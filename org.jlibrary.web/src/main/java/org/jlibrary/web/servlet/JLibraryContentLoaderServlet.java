/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual 
* contributors as indicated by the @authors tag. See copyright.txt in the
* distribution for a full listing of individual contributors.
* All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.web.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.RepositoryInfo;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRRepositoryService;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.jlibrary.web.RepositoryRegistry;
import org.jlibrary.web.WebConstants;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.jlibrary.web.services.ConfigurationService;
import org.jlibrary.web.services.StatsService;
import org.jlibrary.web.services.TicketService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This servlet listens for content requests following a model very similar to REST. 
 * Requests will have the format /webapp/repositories/URL and jLibrary will try to load 
 * the content matching the given URL path.
 * 
 * @author mpermar
 */
@SuppressWarnings("serial")
public class JLibraryContentLoaderServlet extends JLibraryServlet {

	private static Logger logger = Logger.getLogger(JLibraryContentLoaderServlet.class);

	private ServerProfile profile = new LocalServerProfile();
	private StatsService statsService=StatsService.newInstance();
	
	private RepositoryService repositoryService;
	
	@Override
	public void init() throws ServletException {

		super.init();
		
		try {
			ApplicationContext context = 
				WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
			ConfigurationService configService = (ConfigurationService)context.getBean("template");
			TicketService.getTicketService().init(configService);
			
			repositoryService = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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
	/*
	@Override
	protected long getLastModified(HttpServletRequest req) {

		String appURL = req.getContextPath();
		String uri = req.getRequestURI();
		String path = StringUtils.difference(appURL+"/repositories",uri);
		if (path.equals("") || path.equals("/")) {
			// call to /repositories
			return super.getLastModified(req);
		}
		
		String[] pathElements = StringUtils.split(path,"/");
		
		if ((pathElements.length > 1) && (pathElements[1].equals("categories"))) {
			//TODO: Cache categories
			return super.getLastModified(req);	
		}
		
		try {
			String repositoryName = getRepositoryName(req);
			HttpSession session = req.getSession(false);
			if ((repositoryName == null) || (session == null)) {
				return super.getLastModified(req);
			}
			Ticket ticket = (Ticket)session.getAttribute(
					(TicketService.SESSION_TICKET_ID+repositoryName).toLowerCase());

			if (ticket == null) {
				return super.getLastModified(req);
			}
			
			Node node = null;
			Repository repository = loadRepository(repositoryName,ticket);
			String nodePath = StringUtils.difference(appURL+"/repositories/"+repositoryName,uri);
			if (pathElements.length == 1) {
				// TODO: cache repository root
				node = repository.getRoot();
			} else {							
				node = findNode(ticket, repositoryService, nodePath);
			}
			if (node.isResource()) {
				// We will cache all resources
				return node.getDate().getTime();
			} else if (node.isDocument()) {
				
				if (ticket.getUser().getName().equals(WebConstants.ANONYMOUS_WEB_USERNAME) ||
					"true".equals(req.getParameter("download"))) {
					// We will cache downloads or guest pages
					return node.getDate().getTime();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return super.getLastModified(req);
		}
		return super.getLastModified(req);
	}
	*/
	private void processContent(HttpServletRequest req, HttpServletResponse resp) {
		try{
			resp.setCharacterEncoding("ISO-8859-1");
			req.setCharacterEncoding("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		String appURL = req.getContextPath();
		String uri = req.getRequestURI();
		String path = StringUtils.difference(appURL+"/repositories",uri);
		if (path.equals("") || path.equals("/")) {
			// call to /repositories 
			try {
				Ticket ticket = TicketService.getTicketService().getSystemTicket();
				List repoInfo = repositoryService.findAllRepositoriesInfo(ticket);
				Iterator it = repoInfo.iterator();
				List<RepositoryInfo> repositories = new ArrayList<RepositoryInfo>();
				while (it.hasNext()) {
					RepositoryInfo info = (RepositoryInfo)it.next();
					if (info.getName().equals("system") || info.getName().equals("default")) {
						continue;
					}
					repositories.add(info);

				}
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/repositories.jsp");		
				req.setAttribute("repositories", repositories);
				rd.forward(req, resp);

				return;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				//TODO: mandar a una página de error estática
			}
		}
		String[] pathElements = StringUtils.split(path,"/");
		
		String repositoryName = getRepositoryName(req);
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		Repository repository = null;
		try {
			repository = loadRepository(repositoryName,ticket);
	
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
				/*
				resp.setDateHeader("Last-Modified", node.getDate().getTime());
				resp.setDateHeader("Age", 86400); // cache a day
				*/
				if (node.isDocument()) {
					Document document=(Document) node;
					byte[] output;
					if("true".equals(req.getParameter("download"))){
						output = repositoryService.loadDocumentContent(document.getId(), ticket);
						if (document.isImage()) {
							resp.setContentType(Types.getMimeTypeForExtension(
									FileUtils.getExtension(document.getPath())));
						} else {
							resp.setContentType("application/octet-stream");
						}
					}else{
						output = exportDocument(req,ticket,repository,node).getBytes();
					}
					resp.getOutputStream().write(output);
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
			logErrorAndForward(req, resp, repositoryName, nnfe, "The requested page could not be found.");
		} catch (SecurityException se) {
			logErrorAndForward(req, resp, repositoryName, se, "You do not have enough rights for accessing to the requested page.");
		} catch (Exception e) {
			//TODO:mandar a página de error estática
			e.printStackTrace();
		}
	}

	private Repository loadRepository(String repositoryName,
									  Ticket userTicket) throws RepositoryNotFoundException, 
									  							RepositoryException,
									  							SecurityException {
		
		Repository repository;		
		RepositoryRegistry registry = RepositoryRegistry.getInstance();
		repository = registry.getRepository(repositoryName);
		if (repository == null) {
			//TODO: Probably this could be improved. We can use guest tickets 
			// for holding all the information we need (categories and some 
			// user ids). However, note that holding a repository loaded with 
			// a constrained permission set (guest) could be insufficient for 
			// other scenarios.
			repository = repositoryService.findRepository(repositoryName, userTicket);
			repository.setServerProfile(profile);
			RepositoryRegistry.getInstance().addRepository(repository, repositoryName);
		}
		// Change user ticket. This is to use security constraints when applying templates
		repository.setTicket(userTicket);
		return repository;
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
			RepositoryContext context = 
				new RepositoryContext(repository,getServletContext(),getContext());
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request,repository.getName()));
			exporter.setError((String)request.getAttribute("error"));
			exporter.initExportProcess(context);
			statsService.incServedDocuments();
			return exporter.exportDocument((Document)node, context, "document.ftl");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
	}
	
	private String exportDirectory(HttpServletRequest request, 	
								   HttpServletResponse response,
								   Ticket ticket, 
								   Repository repository, 
								   Node node) {
		
		try {
			RepositoryContext context = 
				new RepositoryContext(repository,getServletContext(),getContext());
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request,repository.getName()));
			exporter.setError((String)request.getAttribute("error"));
			exporter.initExportProcess(context);
			statsService.incServedDirectories();
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

			RepositoryContext context = 
				new RepositoryContext(repository,getServletContext(),getContext());
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request,repository.getName()));
			exporter.setError((String)request.getAttribute("error"));
			exporter.initExportProcess(context);
			statsService.incServedCategories();
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
			statsService.incAttachments();
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(e.getMessage(),e);
			return;
		}
	}
}
