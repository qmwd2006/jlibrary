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
package org.jlibrary.web.freemarker;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.web.WebConstants;
import org.jlibrary.web.content.WordCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Template processor for directory entities
 */
public class DirectoryTemplateProcessor implements FreemarkerTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(DirectoryTemplateProcessor.class);
	
	private Directory directory;
	private RepositoryContext context;
	private FreemarkerExporter exporter;
	private Document indexDocument;

	/**
	 * Document template processor
	 * 
	 * @param exporter Main freemarker template exporter
	 * @param directory Directory to process
	 * @param context Context repository for this processor
	 */
	public DirectoryTemplateProcessor(FreemarkerExporter exporter,
									  Directory directory, 
									  RepositoryContext context) {

		this.directory = directory;
		this.context = context;
		this.exporter = exporter;
	}
	
	public String processTemplate(FreemarkerFactory factory) throws ExportException {

		return processTemplate(factory,factory.getPage("directory.ftl"));
	}	
	

	public String processTemplate(FreemarkerFactory factory,
								  Page page) throws ExportException {

		loadChildren();
		
		ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
		page.expose("loc",bundle);  
		
		page.expose(FreemarkerVariables.REPOSITORY,context.getRepository());
		page.expose(FreemarkerVariables.DIRECTORY, directory);		
		page.expose(FreemarkerVariables.TICKET, context.getTicket());
		
		page.expose(FreemarkerVariables.DATE, new Date());
		if (context.getRepository().getTicket().getUser().equals(User.ADMIN_USER)) {
			page.expose(FreemarkerVariables.USER, bundle.getString(User.ADMIN_NAME));
		} else {
			String userName = context.getRepository().getTicket().getUser().getName();
			if (userName.equals(WebConstants.ANONYMOUS_WEB_USERNAME)) {
				userName = bundle.getString(WebConstants.ANONYMOUS_WEB_USERNAME);
			}
			page.expose(FreemarkerVariables.USER, userName);
		}

		if (hasIndexDocument()) {
			page.expose(FreemarkerVariables.DIRECTORY_CONTENT, 
						getDirectoryContent());
			page.expose(FreemarkerVariables.PAGE_KEYWORDS,
						indexDocument.getMetaData().getKeywords());

		} else {
			page.expose(FreemarkerVariables.DIRECTORY_CONTENT,"");
			page.expose(FreemarkerVariables.PAGE_KEYWORDS,
						WordCounter.buildKeywords(directory.getDescription(),5));
		}

		String rootURL = exporter.getRootURL(directory);
		String repositoryURL = exporter.getRepositoryURL();
		
		page.expose(FreemarkerVariables.ROOT_URL,rootURL);
		page.expose(FreemarkerVariables.REPOSITORY_URL,repositoryURL);
		page.expose(FreemarkerVariables.CATEGORIES_ROOT_URL,repositoryURL+"/categories");
		page.expose(FreemarkerVariables.LOCATION_URL, exporter.getLocationURL(directory));
		page.expose(FreemarkerVariables.PRINT_FILE, "");
		
		page.expose(FreemarkerVariables.PAGE_AUTHOR,"");
	
		Repository repository = context.getRepository();

		String userId = directory.getCreator();
		
		Ticket ticket = repository.getTicket();
		ServerProfile profile = repository.getServerProfile();
		SecurityService ss = 
			JLibraryServiceFactory.getInstance(profile).getSecurityService();
	
		User user = null;
		try {
			user = ss.findUserById(ticket,userId);
			
			if (user.getName().equals(User.ADMIN_NAME) || 
				user.getName().equals(WebConstants.ANONYMOUS_WEB_USERNAME)) {
				page.expose(FreemarkerVariables.NODE_CREATOR, bundle.getString(user.getName()));
			} else {
				page.expose(FreemarkerVariables.NODE_CREATOR, user.getName());
			}
		} catch (Exception e) {
		   logger.error(e.getMessage(),e);
		}
		
		return page.getAsString();
	}
	
	private void loadChildren() throws ExportException {
		
		try {
			if (directory.hasChildren()) {
				if ((directory == null) || directory.getNodes().size() == 0) {
					ServerProfile profile = context.getRepository().getServerProfile();
					RepositoryService repositoryService = 
						JLibraryServiceFactory.getInstance(profile).getRepositoryService();
					Collection children = repositoryService.findNodeChildren(
							context.getRepository().getTicket(), 
													   directory.getId());
					directory.setNodes(new HashSet(children));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExportException(e);
		}
	}

	private String getDirectoryContent() throws ExportException {
		
		Iterator it = directory.getNodes().iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			if (node.isDocument()) {
				if (node.getPath().toLowerCase().endsWith("index.html")) {
					indexDocument = (Document)node;
				}
			}
		}
		
		return new DocumentTemplateProcessor(exporter,indexDocument,context).loadContent();
	}
	
	private boolean hasIndexDocument() {
		
		Iterator it = directory.getNodes().iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			if (node.isDocument()) {
				if (node.getPath().toLowerCase().endsWith("/index.html")) {
					return true;
				}
			}
		}
		return false;
	}
}
