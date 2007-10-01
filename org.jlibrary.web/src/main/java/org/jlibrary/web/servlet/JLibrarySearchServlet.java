package org.jlibrary.web.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.SearchResult;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRSearchService;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.jlibrary.web.services.TicketService;

@SuppressWarnings("serial")
public class JLibrarySearchServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(JLibrarySearchServlet.class);

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
		
		if (logger.isDebugEnabled()) {
			logger.debug("Received search request");
		}
		repositoryName = req.getParameter("repository");
		String text = req.getParameter("text");
		if ((repositoryName == null) || (text == null)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid request: repositoryName=" + repositoryName + ", text=" + text);
			}
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String type = req.getParameter("type");
		if (type == null) {
			type = SearchService.SEARCH_CONTENT;
		}
		int init = JCRSearchService.NO_PAGING;
		String initParameter = req.getParameter("init");
		if (initParameter != null) {
			try {
				init = Integer.parseInt(initParameter);
			} catch (NumberFormatException nfe) {
				if (logger.isDebugEnabled()) {
					logger.debug("Wrong value for init parameter=" + initParameter);
				}
			}
		}
		int end = JCRSearchService.NO_PAGING;
		String endParameter = req.getParameter("end");
		if (endParameter != null) {
			try {
				end = Integer.parseInt(endParameter);
			} catch (NumberFormatException nfe) {
				if (logger.isDebugEnabled()) {
					logger.debug("Wrong value for end parameter=" + endParameter);
				}
			}
		}		
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		//TODO: Refactor init/end methods to interface and remove this explicit cast
		JCRSearchService searchService = 
			(JCRSearchService)JLibraryServiceFactory.getInstance(profile).getSearchService();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			
			Repository repository = 
				repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(new LocalServerProfile());
			repository.setTicket(ticket);
			
			SearchResult result = searchService.search(ticket, text, type, init, end);
			String output = exportResults(req,ticket,repository,result);
			resp.getOutputStream().write(output.getBytes());
			resp.flushBuffer();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		return;
	}
	private String exportResults(HttpServletRequest request, 
								  Ticket ticket, 
								  Repository repository, 
								  SearchResult result) {
		
		try {
			String templatesDirectory = 
				"/software/apache-tomcat-6.0.13-pruebas/webapps/jlibrary/templates/terrafirma";
			RepositoryContext context = 
				new RepositoryContext(repository,templatesDirectory,null);
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request));
			
			exporter.initExportProcess(context);
			return exporter.exportSearchResults(result, context);
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
}
