/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, Blandware (represented by
* Andrey Grebnev), and individual contributors as indicated by the
* @authors tag. See copyright.txt in the distribution for a full listing of
* individual contributors. All rights reserved.
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
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
import org.jlibrary.core.entities.Relation;
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
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.web.captcha.CaptchaService;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.jlibrary.web.services.ConfigurationService;
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
	private Map params=new HashMap();
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
	
	protected String getField(HttpServletRequest req, 
			  HttpServletResponse resp, 
			  String fieldName) throws FieldNotFoundException {
		String ret=null;
		if(params!=null){
			ret=(String) params.get(fieldName);
		}
		if(ret==null){
			ret=super.getField(req, resp, fieldName);
		}
		return ret;
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp) {
		try {
			req.setCharacterEncoding("ISO-8859-1");
			resp.setCharacterEncoding("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		if(ServletFileUpload.isMultipartContent(req)){
			upload(req,resp);
			
		}
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
		} else if (method.equals("upload")) {
			upload(req,resp);
		} else if(method.equals("documentcategories")){
			documentCategoriesForm(req, resp);
		} else if(method.equals("updatedocumentcategories")){
			updateDocumentCategories(req, resp);
		}else if(method.equals("documentdocuments")){
			documentDocumentsForm(req, resp);
		}else if(method.equals("updatedocumentrelations")){
			updateDocumentRelations(req, resp);
		}else{
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
				new RepositoryContext(repository, getServletContext(),getContext());
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
				new RepositoryContext(repository,getServletContext(),getContext());
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
			}else if (type.equals("documentupload")) {
				resp.getOutputStream().write(
					exporter.exportDirectory((Directory)node, context, "document-upload-create.ftl").getBytes());				
			}else if (type.equals("category")) {
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
				new RepositoryContext(repository,getServletContext(),getContext());
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
					Document doc=(Document) node;
					String template;
					if(doc.getTypecode().equals(Types.HTML_DOCUMENT)){
						template="document-update.ftl";
					}else{
						template="document-upload-update.ftl";
					}
					resp.getOutputStream().write(
							exporter.exportDocument((Document)node, context, template).getBytes());
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
		String rootURL = getRepositoryURL(req, repositoryName);
		try {
			resp.sendRedirect(resp.encodeRedirectURL(rootURL));
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

			// Always forward to root
			resp.sendRedirect(resp.encodeRedirectURL(getRepositoryURL(req, repositoryName)));
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
	
				if (node.isDirectory() && node.getParent()!=null) {
					node.setName(name);
					node.setDescription(description);
					DirectoryProperties properties = ((Directory)node).dumpProperties();
					node = repositoryService.updateDirectory(ticket, properties);
					statsService.incUpdatedDirectories();
					StringBuilder url = new StringBuilder(getRepositoryURL(req, repositoryName));
					url.append(node.getPath());
					resp.sendRedirect(resp.encodeRedirectURL(url.toString()));
				} else if (node.isDocument()) {
					Document document=(Document) node;
					document.setName(name);
					document.setDescription(description);
					String content = req.getParameter("content");
					byte[] dataContent=null;
					if(content!=null && !"".equals(content)){
						dataContent=content.getBytes();
					}
					DocumentProperties properties = document.dumpProperties();
					document = repositoryService.updateDocument(ticket, properties);
					if(dataContent!=null){
						repositoryService.updateContent(ticket, document.getId(), dataContent);
					}
					statsService.incUpdatedDocuments();
					String url=getRootURL(req) +"/forward?method=documentcategories&repository="+repositoryName+"&id="+document.getId();
					resp.sendRedirect(resp.encodeRedirectURL(url));
					
				}
			} else if (type.equals("category")) {
				Category category = repositoryService.findCategoryById(ticket, id);
				category.setName(name);
				category.setDescription(description);

				CategoryProperties properties = category.dumpProperties();
				category = repositoryService.updateCategory(ticket, id, properties);
				statsService.incUpdatedCategories();
				String url = getRepositoryURL(req, repositoryName)+"/categories/"+category.getName();
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
				if (node.isDirectory() && node.getParent()!=null) {
					repositoryService.removeDirectory(ticket, node.getId());
					statsService.incDeletedDirectories();
				} else if (node.isDocument()) {
					repositoryService.removeDocument(ticket, node.getId());
					statsService.incDeletedDocuments();
				}
				StringBuilder url = new StringBuilder(getRepositoryURL(req, repositoryName));
				url.append(parent.getPath());
				resp.sendRedirect(resp.encodeRedirectURL(url.toString()));
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
			if (type.equals("document")) {
				keywords = getField(req, resp, "keywords");
			}
			repositoryName = getField(req, resp, "repository");
			name = getField(req, resp, "name");
			description = getField(req, resp, "description");
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
				StringBuilder url = new StringBuilder(getRepositoryURL(req, repositoryName));
				url.append(directory.getPath());
				resp.sendRedirect(resp.encodeRedirectURL(url.toString()));
			} else if (type.equals("document")) {
				StringBuilder url = new StringBuilder(getRepositoryURL(req, repositoryName));
				
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
				document.setRelations(new HashSet());
				document.addCategory(Category.UNKNOWN);
				
				DocumentMetaData metaData = new DocumentMetaData();
				metaData.setDate(new Date());
				metaData.setTitle(name);
				metaData.setKeywords(keywords);
				metaData.setUrl(url.toString());
				metaData.setAuthor(author);
				document.setMetaData(metaData);
				byte[] dataContent=null;
				String content = req.getParameter("content");
				dataContent=content.getBytes();
				document.setPath(Text.escape(name)+".html");
				document.setTypecode(Types.HTML_DOCUMENT);
				DocumentProperties properties = document.dumpProperties();
				document = repositoryService.createDocument(ticket, properties);	
				statsService.incCreatedDocuments();
				if(dataContent!=null){
					repositoryService.updateContent(ticket, document.getId(), dataContent);
				}else{
					logErrorAndForward(req, resp, repositoryName, null, "There was a problem trying to upload the document.");
				}
				String redirectUrl=getRootURL(req) +"/forward?method=documentcategories&repository="+repositoryName+"&id="+document.getId();
				resp.sendRedirect(resp.encodeRedirectURL(redirectUrl));
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
				String url = getRepositoryURL(req, repositoryName)+"/categories/"+category.getName();
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
	
	private void upload(HttpServletRequest req, HttpServletResponse resp) {

		ServletFileUpload upload = new ServletFileUpload();
		boolean sizeExceeded=false;
		String repositoryName=req.getParameter("repository");
		ConfigurationService conf=(ConfigurationService) context.getBean("template");
		upload.setSizeMax(conf.getOperationInputBandwidth());
		
		try {
			params = new HashMap();
			FileItemIterator iter = upload.getItemIterator(req);
			while (iter.hasNext()) {
			    FileItemStream item = iter.next();
			    InputStream stream = item.openStream();
				if(item.isFormField()){
					params.put(item.getFieldName(),Streams.asString(stream));
				} else {
					params.put("filename",item.getName());
					uploadDocumentStructure(req, resp, repositoryName, stream);
				}
			}
		} catch (SizeLimitExceededException e) {
			sizeExceeded=true;
			if( repositoryName==null || "".equals(repositoryName)) {
				repositoryName=(String) params.get("repository");
			}
			logErrorAndForward(req, resp, repositoryName, e, "Bandwith exceeded");
		} catch (FileUploadException e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem uploading the document.");
		} catch (IOException e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem uploading the document.");
		} catch (Exception t) {
			logErrorAndForward(req, resp, repositoryName, t, "There was a problem uploading the document.");
		}
		
		
	}

	private void uploadDocumentStructure(HttpServletRequest req,
										 HttpServletResponse resp, 
										 String repositoryName,
										 InputStream contentInputStream) {
		// This will trigger validation
		String id;
		String name;
		String description;
		String keywords;
		try {
			id = getField(req, resp, "id");
			keywords = checkParameter(params, req, resp, repositoryName, "keywords");
			name = checkParameter(params, req, resp, repositoryName, "name");
			repositoryName = getField(req,resp,"repository");
			description = checkParameter(params, req, resp, repositoryName, "description");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			StringBuilder url = new StringBuilder(getRepositoryURL(req, repositoryName));
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
			document.setRelations(new HashSet());
			document.addCategory(Category.UNKNOWN);
			DocumentMetaData metaData = new DocumentMetaData();
			metaData.setDate(new Date());
			metaData.setTitle(name);
			metaData.setKeywords(keywords);
			metaData.setUrl(url.toString());
			metaData.setAuthor(author);
			document.setMetaData(metaData);

			String path = (String)params.get("filename");
			document.setPath(path);
			document.setTypecode(Types.getTypeForFile(path));
			DocumentProperties properties = document.dumpProperties();
			document = repositoryService.createDocument(ticket, properties);	
			statsService.incCreatedDocuments();
			if( contentInputStream!=null){
				document=(Document) repositoryService.updateContent(ticket, document.getId(), contentInputStream);
				url.append(document.getPath());
				resp.sendRedirect(resp.encodeRedirectURL(url.toString()));
				return;
			}else{
				logErrorAndForward(req, resp, repositoryName, null, "There was a problem trying to upload the document.");
			}
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to create the object.");
		} finally {
			if (contentInputStream != null) {
				try {
					contentInputStream.close();
				} catch (IOException e) {
					logErrorAndForward(req, resp, repositoryName, e, "There was a problem trying to create the object.");
				}
			}
		}
	}

	private String checkParameter (Map params,
								   HttpServletRequest req, 
								   HttpServletResponse resp,
								   String repositoryName, 
								   String parameterName) throws FieldNotFoundException {
		
		String parameter = (String)params.get(parameterName);
		if (parameter == null) {
			FieldNotFoundException fnfe = 
				new FieldNotFoundException("Parameter " + parameter + " has not been found");
			logErrorAndForward(req, resp, repositoryName, fnfe, fnfe.getMessage());
			throw fnfe;
		}
		return parameter;
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
			statsService.incComments();
			String refererURL = req.getHeader("referer"); 
			resp.sendRedirect(resp.encodeRedirectURL(refererURL));
		} catch (RepositoryException e) {
			if ((e.getCause() != null) && (e.getCause() instanceof SecurityException)) {
				logErrorAndForward(req, resp, repositoryName, e, "Sorry, but you need to be logged in to add comments!");
			} else {
				logErrorAndForward(req, resp, repositoryName, e, "There was a problem adding the comment.");
			}
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem adding the comment.");			
		}
	}
	
	private String processNote(String text) {

		return text.replaceAll("\n", "<br/>");
	}
	
	private void updateDocumentCategories(HttpServletRequest req, HttpServletResponse resp){
		String id;
		String repositoryName;
		String[] categories=req.getParameterValues("categories");
		try {
			id = getField(req, resp, "id");
			repositoryName = getField(req, resp, "repository");
		} catch (FieldNotFoundException e) {
			return;
		}
		try{
			Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
			RepositoryService repositoryService = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			Document doc = repositoryService.findDocument(ticket, id);
			DocumentProperties docProperties=doc.dumpProperties();
			List<Category> nodeCategories=repositoryService.findCategoriesForNode(ticket, doc.getId());
			Iterator<Category> ite=nodeCategories.iterator();
			while(ite.hasNext()){
				Category category=ite.next();
				docProperties.addProperty(DocumentProperties.DOCUMENT_DELETE_CATEGORY, category.getId());
			}
			if(categories!=null){
				for(int cont=0;categories.length>cont;cont++){
					docProperties.addProperty(DocumentProperties.DOCUMENT_ADD_CATEGORY, categories[cont]);
				}
			}
			repositoryService.updateDocument(ticket, docProperties);
			
			String url=getRootURL(req) +"/forward?method=documentdocuments&repository="+repositoryName+"&id="+doc.getId();
			resp.sendRedirect(resp.encodeRedirectURL(url));
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem adding categories.");
		}
	}
	
	private void updateDocumentRelations(HttpServletRequest req, HttpServletResponse resp){
		String id;
		String repositoryName;
		String[] documents=req.getParameterValues("relations");
		try {
			id = getField(req, resp, "id");
			repositoryName = getField(req, resp, "repository");
		} catch (FieldNotFoundException e) {
			return;
		}
		try{
			Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
			RepositoryService repositoryService = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			Document doc = repositoryService.findDocument(ticket, id);
			DocumentProperties docProperties=doc.dumpProperties();
			Set<Document> nodeRelations=doc.getRelations();
			Iterator<Document> ite=nodeRelations.iterator();
			while(ite.hasNext()){
				Relation relation = new Relation();
				relation.setBidirectional(true);
				relation.setDestinationNode(ite.next());
				docProperties.addProperty(DocumentProperties.DOCUMENT_DELETE_RELATION, relation);
			}
			if(documents!=null){
				for(int cont=0;documents.length>cont;cont++){
					Relation relation = new Relation();
					relation.setBidirectional(true);
					relation.setDestinationNode(repositoryService.findDocument(ticket, documents[cont]));
					docProperties.addProperty(DocumentProperties.DOCUMENT_ADD_RELATION, relation);
				}
			}
			repositoryService.updateDocument(ticket, docProperties);
			
			String url = getRepositoryURL(req, repositoryName)+doc.getPath();
			resp.sendRedirect(resp.encodeRedirectURL(url));
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem adding relations.");
		}
	}
	
	private void documentCategoriesForm(HttpServletRequest req, HttpServletResponse resp){
		String id;
		String repositoryName;
		try {
			id = getField(req, resp, "id");
			repositoryName = getField(req, resp, "repository");
		} catch (FieldNotFoundException e) {
			return;
		}
		try{
			Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
			RepositoryService repositoryService = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			Document doc = repositoryService.findDocument(ticket, id);
			
			RepositoryContext context = 
				new RepositoryContext(repository,getServletContext(),getContext());
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(req));
			exporter.setRepositoryURL(getRepositoryURL(req, repositoryName));
			exporter.initExportProcess(context);
			resp.getOutputStream().write(
					exporter.exportDocument(doc, context, "document-categories.ftl").getBytes());
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem adding categories.");
		}
	}
	
	private void documentDocumentsForm(HttpServletRequest req, HttpServletResponse resp){
		String id;
		String repositoryName;
		String parentId;
		try {
			id = getField(req, resp, "id");
			repositoryName = getField(req, resp, "repository");
		} catch (FieldNotFoundException e) {
			return;
		}
		try{
			Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
			RepositoryService repositoryService = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			Document doc = repositoryService.findDocument(ticket, id);
			
			RepositoryContext context = 
				new RepositoryContext(repository,getServletContext(),getContext());
			context.setTicket(ticket);
			parentId=req.getParameter("parentId");
			String template="document-documents-ajax.ftl";
			if(parentId==null || "".equals(parentId)){
				parentId=repository.getId();
				template="document-documents.ftl";
			}
			logger.debug(parentId);
			FreemarkerExporter exporter = new FreemarkerExporter();
			exporter.setRootURL(getRootURL(req));
			exporter.setRepositoryURL(getRepositoryURL(req, repositoryName));
			exporter.initExportProcess(context);
			resp.getOutputStream().write(
					exporter.exportDocumentRelation(doc, context, template, parentId).getBytes());
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was a problem adding relations.");
		}
	}
}