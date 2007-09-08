package org.jlibrary.web.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.web.admin.content.DocumentsManager;
import org.jlibrary.web.services.TicketService;

/**
 * This servlet will forward create/update requests to admin JSF application
 * 
 * @author mpermar
 *
 */
@SuppressWarnings("serial")
public class JLibraryForwardServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(JLibraryForwardServlet.class);

	private ServerProfile profile = new LocalServerProfile();

	private String repositoryName;
	
	@Override
	public void init() throws ServletException {

		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processRequest(req,resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processRequest(req,resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Received content upload request");
		}
		
		String method = req.getParameter("method");
		if (method == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid upload request. Method not defined.");
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}
		
		if (method.equals("create")) {
			createDocument(req,resp);
		} else if (method.equals("update")) {
			updateDocument(req,resp);
		} else {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		return;
	}
	
	private void updateDocument(HttpServletRequest req, HttpServletResponse resp) {

		String id = req.getParameter("id");
		if (id == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid update request. Document id not found.");
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}

		String repositoryName = req.getParameter("repository");
		if (repositoryName == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid update request. Repository name not found.");
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			Node node = repositoryService.findNode(ticket, id);
			
			// Now update the JSF documents manager with the requested node
			DocumentsManager documentsManager = new DocumentsManager();
			documentsManager.setNode(node);
			req.setAttribute("documentsManager", documentsManager);
			RequestDispatcher dispatcher = 
				req.getRequestDispatcher("/admin/content/document_form.jsf");
			dispatcher.forward(req, resp);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}	
	}

	private void createDocument(HttpServletRequest req, HttpServletResponse resp) {

		/*
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
			
			Collection results = searchService.search(ticket, text, type, init, end);
			String output = exportResults(req,ticket,repository,results);
			resp.getOutputStream().write(output.getBytes());
			resp.flushBuffer();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		*/		
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
