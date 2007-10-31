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
import java.util.Date;
import java.util.HashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.util.Text;
import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.web.captcha.CaptchaService;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.jlibrary.web.services.StatsService;
import org.jlibrary.web.services.TicketService;

import com.octo.captcha.service.CaptchaServiceException;

/**
 * This servlet will forward create/update requests to admin JSF application
 * 
 * @author mpermar
 *
 */
@SuppressWarnings("serial")
public class JLibraryForwardServlet extends JLibraryServlet {

	private static Logger logger = Logger.getLogger(JLibraryForwardServlet.class);
	private StatsService statsService=StatsService.newInstance();

	private ServerProfile profile = new LocalServerProfile();
	
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
		
		String method;
		try {
			method = getField(req, resp, "method");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		if (method.equals("update")) {
			update(req,resp);
		} else if (method.equals("create")) {
			create(req,resp);
		} else if (method.equals("delete")) {
			delete(req,resp);
		} else if (method.equals("comment")) {
			addComment(req,resp);
		} else if (method.equals("login")) {
			login(req,resp);
		} else if (method.equals("signin")) {
			signin(req,resp);
		} else if (method.equals("register")) {
			register(req,resp);
		} else if (method.equals("logout")) {
			logout(req,resp);
		} else if (method.equals("updateform")) {
			updateform(req,resp);
		} else if (method.equals("createform")) {
			createform(req,resp);
		} else {
			try {
				if (logger.isDebugEnabled()) { logger.error("The operation " + method + " is not supported.");}
				String repositoryName = getField(req, resp, "repository");
				String refererURL = getRefererURL(req, repositoryName);
				RequestDispatcher rd = getServletContext().getRequestDispatcher(refererURL);
				req.setAttribute("error", "The operation specified is not supported.");
				rd.forward(req, resp);
			} catch (Exception fe) {
				logger.error(fe.getMessage(),fe);
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		
		return;
	}
	
	private void signin(HttpServletRequest req, HttpServletResponse resp) {
		
		String repositoryName;
		try {
			repositoryName = getField(req, resp, "repository");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				
		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);

			RepositoryContext context = 
				new RepositoryContext(repository, getContext());
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			
			exporter.setRootURL(getRootURL(req));
			exporter.setRepositoryURL(getRepositoryURL(req, repositoryName));
			
			exporter.initExportProcess(context);
			resp.getOutputStream().write(
					exporter.export(context, "register.ftl").getBytes());
			resp.getOutputStream().flush();
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem signing in.");
		}		
	}
	
	private void createform(HttpServletRequest req, HttpServletResponse resp) {
		
		String repositoryName;
		String id;
		String type;
		try {
			repositoryName = getField(req, resp, "repository");
			id = getField(req, resp, "id");
			type = getField(req, resp, "type");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				
		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			Node node = 
				repositoryService.findNode(ticket, id);

			RepositoryContext context = 
				new RepositoryContext(repository,getContext());
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			
			exporter.setRootURL(getRootURL(req));
			exporter.setRepositoryURL(getRepositoryURL(req, repositoryName));
			
			exporter.initExportProcess(context);
			if (type.equals("directory")) {
				resp.getOutputStream().write(
					exporter.exportDirectory((Directory)node, context, "directory-create.ftl").getBytes());
			} else if (type.equals("document")) {
				resp.getOutputStream().write(
					exporter.exportDirectory((Directory)node, context, "document-create.ftl").getBytes());				
			} else if (type.equals("category")) {
				resp.getOutputStream().write(
						exporter.exportDirectory((Directory)node, context, "category-create.ftl").getBytes());
			} else {
				String error = "Invalid operation : " + type;
				logErrorAndForward(req, resp, repositoryName, new InvalidOperationException(error), error);
			}
			resp.getOutputStream().flush();
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to creating the object.");
		}		
	}
	
	private void updateform(HttpServletRequest req, HttpServletResponse resp) {
		
		String repositoryName;
		String id;
		String type;
		try {
			repositoryName = getField(req, resp, "repository");
			id = getField(req, resp, "id");
			type = getField(req, resp, "type");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				
		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);

			RepositoryContext context = 
				new RepositoryContext(repository,getContext());
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			
			exporter.setRootURL(getRootURL(req));
			exporter.setRepositoryURL(getRepositoryURL(req, repositoryName));
			
			exporter.initExportProcess(context);
			
			if (type.equals("node")) {
				Node node = 
					repositoryService.findNode(ticket, id);
				if (node.isDirectory()) {
					resp.getOutputStream().write(
						exporter.exportDirectory((Directory)node, context, "directory-update.ftl").getBytes());
				} else if (node.isDocument()) {
					resp.getOutputStream().write(
							exporter.exportDocument((Document)node, context, "document-update.ftl").getBytes());				
				}
			} else if (type.equals("category")) {
				Category category = repositoryService.findCategoryById(ticket, id);
				resp.getOutputStream().write(
						exporter.exportCategory(category, context, "category-update.ftl").getBytes());				
			} else {
				String error = "Invalid operation : " + type;
				logErrorAndForward(req, resp, repositoryName, new InvalidOperationException(error), error);
			}
			resp.getOutputStream().flush();
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to updating the object.");
		}		
	}
	
	private void logout(HttpServletRequest req, HttpServletResponse resp) {

		String repositoryName;
		try {
			repositoryName = getField(req, resp, "repository");
		} catch (FieldNotFoundException e1) {
			return;
		}	
		
		// Remove ticket from user's session
		TicketService.getTicketService().removeTicket(req, repositoryName);
		req.getSession(true).setAttribute((StatsService.SESSION_LOGGED_USER+repositoryName).toLowerCase(),null);
		String refererURL = req.getHeader("referer");
		try {
			resp.sendRedirect(resp.encodeRedirectURL(refererURL));
		} catch (IOException e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to log out.");
		}
	}	
	
	private void login(HttpServletRequest req, HttpServletResponse resp) {

		String repositoryName;
		String username;
		String password;
		try {
			repositoryName = getField(req, resp, "repository");
			username = getField(req, resp, "username");
			password = getField(req, resp, "password");	
		} catch (FieldNotFoundException e) {
			return;
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		//TODO: Refactor init/end methods to interface and remove this explicit cast
		JCRSecurityService securityService = 
			(JCRSecurityService)JLibraryServiceFactory.getInstance(profile).getSecurityService();
		
		Credentials credentials = new Credentials();
		if (username.equals(User.ADMIN_KEYNAME)) {
			username = User.ADMIN_NAME;
		}
		credentials.setUser(username);
		credentials.setPassword(password);
		try {
			Ticket userTicket = securityService.login(credentials, repositoryName);
			TicketService.getTicketService().putTicket(req, repositoryName, userTicket);
			req.getSession(true).setAttribute((StatsService.SESSION_LOGGED_USER+repositoryName).toLowerCase(),new LoggedUser());
			String refererURL = req.getHeader("referer");
			resp.sendRedirect(resp.encodeRedirectURL(refererURL));
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to log in.");
		}
	}
	
	private void update(HttpServletRequest req, HttpServletResponse resp) {

		String id;
		String repositoryName;
		String name;
		String description;
		String type;
		
		try {
			id = getField(req, resp, "id");
			repositoryName = getField(req, resp, "repository");
			name = getField(req, resp, "name");
			description = getField(req, resp, "description");	
			type = getField(req, resp, "type");
		} catch (FieldNotFoundException e) {
			return;
		}
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			if (type.equals("node")) {
				Node node = 
					repositoryService.findNode(ticket, id);
	
				if (node.isDirectory()) {
					node.setName(name);
					node.setDescription(description);
					DirectoryProperties properties = ((Directory)node).dumpProperties();
					node = repositoryService.updateDirectory(ticket, properties);
					statsService.incUpdatedDirectories();
					String url = getRepositoryURL(req, repositoryName);
					url+=node.getPath();
					resp.sendRedirect(resp.encodeRedirectURL(url));
				} else if (node.isDocument()) {
					node.setName(name);
					node.setDescription(description);
					DocumentProperties properties = ((Document)node).dumpProperties();
					node = repositoryService.updateDocument(ticket, properties);
					statsService.incUpdatedDocuments();
					String content = req.getParameter("FCKEditor");
					repositoryService.updateContent(ticket, node.getId(), content.getBytes());
					
					String url = getRepositoryURL(req, repositoryName);
					url+=node.getPath();
					resp.sendRedirect(resp.encodeRedirectURL(url));
					
				}
			} else if (type.equals("category")) {
				Category category = repositoryService.findCategoryById(ticket, id);
				category.setName(name);
				category.setDescription(description);

				CategoryProperties properties = category.dumpProperties();
				category = repositoryService.updateCategory(ticket, id, properties);
				statsService.incUpdatedCategories();
				String url = getRepositoryURL(req, repositoryName);
				url+="/categories/"+category.getName();
				resp.sendRedirect(resp.encodeRedirectURL(url));	
			} else {
				String error = "Invalid operation : " + type;
				logErrorAndForward(req, resp, repositoryName, new InvalidOperationException(error), error);
			}

			resp.getOutputStream().flush();
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to updating the object.");
		}
	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) {

		String id;
		String repositoryName;
		String type;
		
		try {
			id = getField(req, resp, "id");
			repositoryName = getField(req, resp, "repository");
			type = getField(req, resp, "type");
		} catch (FieldNotFoundException e) {
			return;
		}
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			if (type.equals("node")) {
				Node node = 
					repositoryService.findNode(ticket, id);
				Node parent = 
					repositoryService.findNode(ticket, node.getParent());
				if (node.isDirectory()) {
					repositoryService.removeDirectory(ticket, node.getId());
					statsService.incDeletedDirectories();
				} else if (node.isDocument()) {
					repositoryService.removeDocument(ticket, node.getId());
					statsService.incDeletedDocuments();
				}
				String url = getRepositoryURL(req, repositoryName);
				url+=parent.getPath();
				resp.sendRedirect(resp.encodeRedirectURL(url));
			} else if (type.equals("category")) {
				repositoryService.deleteCategory(ticket, id);
				statsService.incDeletedCategories();
				String url = getRepositoryURL(req, repositoryName);
				resp.sendRedirect(resp.encodeRedirectURL(url));	
			} else {
				String error = "Invalid operation : " + type;
				logErrorAndForward(req, resp, repositoryName, new InvalidOperationException(error), error);
			}

			resp.getOutputStream().flush();
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to delete the object.");
		}
	}

	private void register(HttpServletRequest req, HttpServletResponse resp) {

		String repositoryName;
		String firstName;
		String secondName;
		String email;
		String username;
		String password;
		
		try {
			repositoryName = getField(req, resp, "repository");
			username = getField(req, resp, "username");
			firstName = getField(req, resp, "name");
			secondName = getField(req, resp, "surname");
			email = getField(req,resp,"email");
			password = getField(req,resp,"password");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		// handle captcha
		boolean captchaOk = false;
        String captchaId = req.getSession().getId();
        String response = req.getParameter("j_captcha_response");
        try {
            captchaOk = CaptchaService.getInstance().validateResponseForID(captchaId,response);
        } catch (CaptchaServiceException e) {
             //should not happen, may be thrown if the id is not valid 
        }
        if (!captchaOk) {
			if (logger.isDebugEnabled()) {
				logErrorAndForward(req, resp, repositoryName, 
						new InvalidOperationException("Invalid captcha value"), 
						"Invalid captcha value");
				return;
			}
        }
		
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		SecurityService securityService = 
			JLibraryServiceFactory.getInstance(profile).getSecurityService();

		Credentials credentials = new Credentials();
		credentials.setUser(User.ADMIN_NAME);
		credentials.setPassword(User.DEFAULT_PASSWORD);
		Ticket adminTicket = null;

		try {
			adminTicket = securityService.login(credentials, repositoryName);
			Repository repository = repositoryService.findRepository(repositoryName, adminTicket);
			repository.setServerProfile(profile);
			
			UserProperties userProperties = new UserProperties();
			userProperties.addProperty(UserProperties.USER_ID, username);
			userProperties.addProperty(UserProperties.USER_NAME, username);
			userProperties.addProperty(UserProperties.USER_EMAIL, email);
			userProperties.addProperty(UserProperties.USER_FIRSTNAME, firstName);
			userProperties.addProperty(UserProperties.USER_LASTNAME, secondName);
			userProperties.addProperty(UserProperties.USER_PASSWORD, password);
			userProperties.addProperty(UserProperties.USER_ADMIN, Boolean.FALSE);
			userProperties.addProperty(UserProperties.USER_REPOSITORY, repository.getId());
			securityService.createUser(adminTicket, userProperties);
			statsService.incRegisteredUsers();
			String url = getRepositoryURL(req, repositoryName);
			resp.sendRedirect(resp.encodeRedirectURL(url));

			resp.getOutputStream().flush();
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to register the user.");
		} finally {
			if (adminTicket != null) {
				try {
					securityService.disconnect(adminTicket);
				} catch (SecurityException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
	
	private void create(HttpServletRequest req, HttpServletResponse resp) {

		String id = null;
		String repositoryName;
		String name;
		String description;
		String type;
		String keywords = null;
		
		try {
			type = getField(req, resp, "type");
			if (!type.equals("category")) {
				id = getField(req, resp, "id");
			}
			repositoryName = getField(req, resp, "repository");
			name = getField(req, resp, "name");
			description = getField(req, resp, "description");
			if (type.equals("document")) {
				keywords = getField(req, resp, "keywords");
			}
		} catch (FieldNotFoundException e) {
			return;
		}
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);

			if (type.equals("directory")) {
				Directory directory = new Directory();
				directory.setName(name);
				directory.setDescription(description);
				directory.setParent(id);
				directory.setPosition(new Integer(1));
				DirectoryProperties properties = directory.dumpProperties();
				directory = repositoryService.createDirectory(ticket, properties);
				statsService.incCreatedDirectories();
				String url = getRepositoryURL(req, repositoryName);
				url+=directory.getPath();
				resp.sendRedirect(resp.encodeRedirectURL(url));
			} else if (type.equals("document")) {
				String url = getRepositoryURL(req, repositoryName);
				
				Author author = null;
				try {
					author = repositoryService.findAuthorByName(ticket, ticket.getUser().getName());
				} catch (AuthorNotFoundException anfe) {
					author = Author.UNKNOWN;
				}
				Document document = new Document();
				document.setName(name);
				document.setDescription(description);
				document.setParent(id);
				document.setPosition(new Integer(1));
				document.setImportance(Node.IMPORTANCE_MEDIUM);
				document.setDate(new Date());
				document.setPath(Text.escape(name)+".html");
				document.setTypecode(Types.HTML_DOCUMENT);
				document.setRelations(new HashSet());
				document.addCategory(Category.UNKNOWN);
				
				DocumentMetaData metaData = new DocumentMetaData();
				metaData.setDate(new Date());
				metaData.setTitle(name);
				metaData.setKeywords(keywords);
				metaData.setUrl(url);
				metaData.setAuthor(author);
				document.setMetaData(metaData);
				DocumentProperties properties = document.dumpProperties();
				document = repositoryService.createDocument(ticket, properties);	
				statsService.incCreatedDocuments();
				String content = req.getParameter("FCKEditor");
				if (content == null) {
					content = "";
				}
				repositoryService.updateContent(ticket, document.getId(), content.getBytes());
				
				url+=document.getPath();
				resp.sendRedirect(resp.encodeRedirectURL(url));
			} else if (type.equals("category")) {
				Category category = new Category();
				category.setId(""); //TODO: This is a bug, is to make dumpProperties work propertly
				category.setName(name);
				category.setDescription(description);
				category.setParent(null);
				category.setRepository(repository.getId());
				CategoryProperties properties = category.dumpProperties();
				category = repositoryService.createCategory(ticket, properties);
				statsService.incCreatedCategories();
				String url = getRepositoryURL(req, repositoryName);
				url+="/categories/"+category.getName();
				resp.sendRedirect(resp.encodeRedirectURL(url));				
			} else {
				String error = "Invalid operation : " + type;
				logErrorAndForward(req, resp, repositoryName, new InvalidOperationException(error), error);
			}

			resp.getOutputStream().flush();
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to create the object.");
		}
	}

	private void addComment(HttpServletRequest req, HttpServletResponse resp) {

		String id;
		String repositoryName;
		String text;
		
		try {
			id = getField(req, resp, "document");
			repositoryName = getField(req, resp, "repository");		
			text = getField(req, resp, "text");
		} catch (FieldNotFoundException e) {
			return;
		}
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			Document document = repositoryService.findDocument(ticket, id);
			Note note = new Note();
			note.setCreator(ticket.getUser().getId());
			note.setDate(new Date());
			note.setNode(document);
			note.setNote(processNote(text));
			document.addNote(note);
			repositoryService.updateDocument(ticket, document.dumpProperties());
			
			String refererURL = req.getHeader("referer"); 
			resp.sendRedirect(resp.encodeRedirectURL(refererURL));

		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem adding the comment.");
		}	
	}
	
	private String processNote(String text) {

		return text.replaceAll("\n", "<br/>");
	}
}
