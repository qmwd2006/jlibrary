package org.jlibrary.web.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.PathNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRRepositoryService;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;

@SuppressWarnings("serial")
public class JLibraryContentLoaderServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(JLibraryContentLoaderServlet.class);
	
	private static Ticket ticket;

	private LocalServerProfile profile;

	private Repository repository;

	private String repositoryName;
	
	@Override
	public void init() throws ServletException {

		super.init();
		
		if (ticket == null) {
			profile = new LocalServerProfile();
			SecurityService service = 
				JLibraryServiceFactory.getInstance(profile).getSecurityService();
			Credentials credentials = new org.jlibrary.core.entities.Credentials();
			credentials.setUser("admin_name");
			credentials.setPassword("changeme");
			try {
				ticket = service.login(credentials, "www");
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
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
		
		try {
			RepositoryService repositoryService = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();
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
						String output = exportCategory(req,category);
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
				node = ((JCRRepositoryService)repositoryService).findNodeByPath(ticket,nodePath);
			}
			if (node == null) {
				logger.debug("Node could not be found");
			} else {
				req.setAttribute("node", node);

				if (node.isDocument()) {
					//req.getRequestDispatcher("/document.jsp").forward(req, resp);
					String output = exportDocument(req,node);
					resp.getOutputStream().write(output.getBytes());
					resp.flushBuffer();
				} else if (node.isDirectory()) {
					String output = exportDirectory(req,node);
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
			String categoryName = pathElements[i];
			if (category == null) {
				categoriesSet = repository.getCategories();
			} else {
				categoriesSet = category.getCategories();
			}
			Iterator it = categoriesSet.iterator();
			while (it.hasNext()) {
				Category child = (Category)it.next();
				String childEscapedName = Text.escape(child.getName());
				if (childEscapedName.equals(pathElements[i])) {
					category = child;
					break;
				}
			}
			i++;
		}
		return category;
	}

	
	
	private String exportDocument(HttpServletRequest request,Node node) {
		
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
	
	private String exportDirectory(HttpServletRequest request,Node node) {
		
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
	
	private String exportCategory(HttpServletRequest request,Category category) {
		
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
