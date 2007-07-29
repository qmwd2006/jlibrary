package org.jlibrary.web.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
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
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRRepositoryService;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.jlibrary.web.services.TicketService;

@SuppressWarnings("serial")
public class JLibraryContentLoaderServlet extends HttpServlet {

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
						String output = exportCategory(req,repository,category);
						resp.getOutputStream().write(output.getBytes());
						resp.flushBuffer();
					}
					return;
				}
			}
			
			Node node = null;

			if (pathElements.length == 1) {
				node = repository.getRoot();
			} else {
				String nodePath = StringUtils.difference(appURL+"/repositories/"+repositoryName,uri);
				//node = repositoryService.findNode(ticket, nodeId);			
				try {
					node = ((JCRRepositoryService)repositoryService).findNodeByPath(ticket,nodePath);
				} catch (NodeNotFoundException nnfe) {
					// Perhaps somebody has unescaped the name
					String unescapedPath = Text.unescape(nodePath);
					node = ((JCRRepositoryService)repositoryService).findNodeByPath(ticket,unescapedPath);					
				}
			}
			if (node == null) {
				logger.debug("Node could not be found");
			} else {
				req.setAttribute("node", node);

				if (node.isDocument()) {
					String output = exportDocument(req,repository,node);
					resp.getOutputStream().write(output.getBytes());
					resp.flushBuffer();
				} else if (node.isDirectory()) {
					String output = exportDirectory(req,repository,node);
					resp.getOutputStream().write(output.getBytes());
					resp.flushBuffer();
				}
			}			
		} catch (NodeNotFoundException nnfe) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
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

	
	
	private String exportDocument(HttpServletRequest request, Repository repository, Node node) {
		
		try {
			String templatesDirectory = 
				"/software/apache-tomcat-6.0.13-pruebas/webapps/jlibrary/templates/andreas";
			RepositoryContext context = 
				new RepositoryContext(repository,templatesDirectory,null);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request));
			exporter.initExportProcess(context);
			return exporter.exportDocument((Document)node, context);
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
	
	private String exportDirectory(HttpServletRequest request, Repository repository, Node node) {
		
		try {
			String templatesDirectory = 
				"/software/apache-tomcat-6.0.13-pruebas/webapps/jlibrary/templates/andreas";
			RepositoryContext context = 
				new RepositoryContext(repository,templatesDirectory,null);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request));
			exporter.initExportProcess(context);
			return exporter.exportDirectory((Directory)node, context);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
	}
	
	private String exportCategory(HttpServletRequest request, Repository repository, Category category) {
		
		try {
			String templatesDirectory = 
				"/software/apache-tomcat-6.0.13-pruebas/webapps/jlibrary/templates/andreas";
			RepositoryContext context = 
				new RepositoryContext(repository,templatesDirectory,null);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request));
			exporter.initExportProcess(context);
			return exporter.exportCategory(category, context);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
	}	
}
