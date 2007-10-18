package org.jlibrary.web.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRRepositoryService;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.jlibrary.web.RepositoryRegistry;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.jlibrary.web.services.TicketService;

@SuppressWarnings("serial")
public class JLibraryContentLoaderServlet extends JLibraryServlet {

	private static Logger logger = Logger.getLogger(JLibraryContentLoaderServlet.class);

	private ServerProfile profile = new LocalServerProfile();

	private String repositoryName;
	
	@Override
	public void init() throws ServletException {

		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processContent(req,resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processContent(req,resp);
	}

	private void processContent(HttpServletRequest req, HttpServletResponse resp) {
		
		String appURL = req.getContextPath();
		String uri = req.getRequestURI();
		String path = StringUtils.difference(appURL+"/repositories",uri);
		
		String[] pathElements = StringUtils.split(path,"/");
		
		repositoryName = pathElements[0];
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		Repository repository = null;
		try {
			RepositoryService repositoryService = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			//TODO: Check if we really need to have this instance. Perhaps some of the methods that 
			// use a RepositoryContext object don't need to get a whole repository object
			repository = repositoryService.findRepository(repositoryName, ticket);
			RepositoryRegistry.getInstance().addRepository(repository, repositoryName);
			repository.setServerProfile(new LocalServerProfile());
			repository.setTicket(ticket);
	
			if (pathElements.length > 1) {
				if (pathElements[1].equals("categories")) {
					String categoryPath = 
						StringUtils.difference(appURL+"/repositories/"+repositoryName+"/categories",uri);
					Category category = findCategory(repository,pathElements);
					if (category == null) {
						resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
						resp.flushBuffer();
					} else {
						String output = exportCategory(req,resp,ticket,repository,category);
						resp.getOutputStream().write(output.getBytes());
						resp.flushBuffer();
					}
					return;
				}
			}
			
			Node node = null;
			String nodePath = StringUtils.difference(appURL+"/repositories/"+repositoryName,uri);
			if (pathElements.length == 1) {
				node = repository.getRoot();
			} else {							
				node = findNode(ticket, repositoryService, nodePath);
			}
			if (node == null) {
				logger.debug("Node could not be found");
			} else {
				req.setAttribute("node", node);

				if (node.isDocument()) {
					String output = exportDocument(req,ticket,repository,node);
					resp.getOutputStream().write(output.getBytes());
					resp.flushBuffer();
				} else if (node.isDirectory()) {
					// Search for a root document (index.html)
					String output = exportDirectory(req,resp,ticket,repository,node);
					resp.getOutputStream().write(output.getBytes());
					resp.flushBuffer();
				} else if (node.isResource()) {
					exportResouce(req,resp,repositoryService,ticket,node);
					
				}
			}			
		} catch (NodeNotFoundException nnfe) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (SecurityException se) {
			String refererURL = req.getHeader("referer");
			if (refererURL == null) {
				refererURL = getRepositoryURL(req);
			}
			req.setAttribute("refererURL", refererURL);
			req.setAttribute("rootURL",getRootURL(req));
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/security-error.jsp");
			try {
				rd.forward(req, resp);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private Node findNode(Ticket ticket, 
						  RepositoryService repositoryService,
						  String nodePath) throws RepositoryException,
						  						  SecurityException, 
						  						  NodeNotFoundException {
		
		Node node;
		try {
			node = ((JCRRepositoryService)repositoryService).findNodeByPath(ticket,nodePath);
		} catch (NodeNotFoundException nnfe) {
			// Perhaps somebody has unescaped the name
			String unescapedPath = Text.unescape(nodePath);
			node = ((JCRRepositoryService)repositoryService).findNodeByPath(ticket,unescapedPath);					
		}
		return node;
	}
	private Category findCategory(Repository repository, String[] pathElements) {

		int i = 2;
		Category category = null;
		Set categoriesSet = null;
		while (i < pathElements.length) {
			String categoryName = Text.unescape(pathElements[i]);
			
			if (category == null) {
				categoriesSet = repository.getCategories();
			} else {
				categoriesSet = category.getCategories();
			}
			Iterator it = categoriesSet.iterator();
			while (it.hasNext()) {
				Category child = (Category)it.next();
				//String childEscapedName = Text.escape(child.getName());
				if (child.getName().equals(categoryName)) {
					category = child;
					break;
				}
			}
			i++;
		}
		return category;
	}

	
	
	private String exportDocument(HttpServletRequest request, 
							      Ticket ticket,
								  Repository repository, 
							      Node node) {
		
		try {
//			String templatesDirectory = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_TEMPLATES);
//				"/javaconganas/jlibrary/org.jlibrary.web/src/main/webapp/templates/terrafirma";
			RepositoryContext context = 
				new RepositoryContext(repository,getTemplate(),null);
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request));
			exporter.initExportProcess(context);
			return exporter.exportDocument((Document)node, context, "document.ftl");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
	}
	
	private String getRootURL(HttpServletRequest request) {

		return request.getScheme( ) + "://"
				+ request.getLocalAddr( )  + ":"
				+ request.getLocalPort( )
				+ request.getContextPath( );
	}

	private String getRepositoryURL(HttpServletRequest request) {
		
		return getRootURL(request) + "/repositories/" + repositoryName;
	}
	
	private String exportDirectory(HttpServletRequest request, 	
								   HttpServletResponse response,
								   Ticket ticket, 
								   Repository repository, 
								   Node node) {
		
		try {
//			String templatesDirectory = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_TEMPLATES);
//				"/javaconganas/jlibrary/org.jlibrary.web/src/main/webapp/templates/terrafirma";
			RepositoryContext context = 
				new RepositoryContext(repository,getTemplate(),null);
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request));
			exporter.initExportProcess(context);
			
			if ((request.getParameter("rss") != null) && 
				(request.getParameter("rss").equals("true"))) {
				response.setContentType("application/rss+xml");
				return exporter.exportDirectory((Directory)node, context, "directory-rss.ftl");
			} else {			
				return exporter.exportDirectory((Directory)node, context, "directory.ftl");
			}
				
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
	}
	
	private String exportCategory(HttpServletRequest request, 
								  HttpServletResponse response,
								  Ticket ticket, 
								  Repository repository, 
								  Category category) {
		
		try {
//			String templatesDirectory = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_TEMPLATES);
//				"/javaconganas/jlibrary/org.jlibrary.web/src/main/webapp/templates/terrafirma";
			RepositoryContext context = 
				new RepositoryContext(repository,getTemplate(),null);
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request));
			exporter.initExportProcess(context);
			if ((request.getParameter("rss") != null) && 
				(request.getParameter("rss").equals("true"))) {
				response.setContentType("application/rss+xml");
				return exporter.exportCategory(category, context, "category-rss.ftl");
			} else {			
				return exporter.exportCategory(category, context, "category.ftl");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
	}	
	
	private void exportResouce(HttpServletRequest req,
							   HttpServletResponse resp, 
							   RepositoryService repositoryService,
							   Ticket ticket, 
							   Node node) {

		try {
			String extension = FileUtils.getExtension(node.getPath());
			String mime = Types.getMimeTypeForExtension(extension);
			resp.setContentType(mime);  
			repositoryService.loadResourceNodeContent(ticket, node.getId(), resp.getOutputStream());
			resp.getOutputStream().flush();
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(e.getMessage(),e);
			return;
		}
	}
}
