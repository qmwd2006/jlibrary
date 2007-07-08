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
package org.jlibrary.client.export.freemarker;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.CopyUtils;
import org.jlibrary.client.Messages;
import org.jlibrary.client.export.ExportException;
import org.jlibrary.client.export.RepositoryContext;
import org.jlibrary.client.search.WordCounter;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityService;
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
	
	/**
	 * @see org.jlibrary.client.export.freemarker.FreemarkerTemplateProcessor#processTemplate(org.jlibrary.client.export.freemarker.FreemarkerFactory)
	 */
	public void processTemplate(FreemarkerFactory factory) throws ExportException {

		String path = getFilePath(directory.getPath()) + 
						 File.separator + 
						 "index.html";

		processTemplate(factory,factory.getPage("directory.ftl"),path);
		
		path = getFilePath(directory.getPath()) + 
		 			  	   File.separator + 
						   "pr_index.html";
		
		processTemplate(factory,factory.getPage("directory-print.ftl"),path);
		
	}	
	
	/**
	 * @see org.jlibrary.client.export.freemarker.FreemarkerTemplateProcessor#processTemplate(org.jlibrary.client.export.freemarker.FreemarkerFactory)
	 */
	public void processTemplate(FreemarkerFactory factory,
								Page page,
								String path) throws ExportException {

		// Create the physical directory
		String dirPath = getFilePath(directory.getPath());
		File dirFile = new File(dirPath);
		if (!dirFile.exists()) {
			if (!dirFile.mkdirs()) {
				String message = "[DirectoryTemplateProcessor] The directory " + directory.getName() + " couldn't be created";
				logger.error(message);
				throw new ExportException(message);
			}
		}

		page.expose(FreemarkerVariables.REPOSITORY,context.getRepository());
		page.expose(FreemarkerVariables.DIRECTORY, directory);		
		
		page.expose(FreemarkerVariables.DATE, new Date());
		if (context.getRepository().getTicket().getUser().equals(User.ADMIN_USER)) {
			page.expose(FreemarkerVariables.USER, Messages.getMessage(User.ADMIN_NAME));
		} else {
			page.expose(FreemarkerVariables.USER, context.getRepository().getTicket().getUser().getName());
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
		page.expose(FreemarkerVariables.ROOT_URL,rootURL);
		page.expose(FreemarkerVariables.CATEGORIES_ROOT_URL,rootURL+"/categories");
		page.expose(FreemarkerVariables.LOCATION_URL, exporter.getLocationURL(directory));
		page.expose(FreemarkerVariables.PRINT_FILE, getPrintFile(directory));
		
		page.expose(FreemarkerVariables.PAGE_AUTHOR,"");
	
		Repository repository = context.getRepository();

		String userId = directory.getCreator();
		
		Ticket ticket = repository.getTicket();
		ServerProfile profile = repository.getServerProfile();
		SecurityService ss = 
			JLibraryServiceFactory.getInstance(profile).getSecurityService();
	
		try {
			User user = ss.findUserById(ticket,userId);
			page.expose(FreemarkerVariables.NODE_CREATOR, user.getName());
		} catch (Exception e) {
		   logger.error(e.getMessage(),e);
		}
		
		if (userId.equals(User.ADMIN_CODE)) {
			page.expose(FreemarkerVariables.NODE_CREATOR, 
						Messages.getMessage(User.ADMIN_NAME));
		} else {
			User user = MembersRegistry.getInstance().
								getUser(repository.getId(),userId);
			page.expose(FreemarkerVariables.NODE_CREATOR, user.getName());
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(path);
			CopyUtils.copy(page.getAsString(), fos);
			fos.close();
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
	
	private String getFilePath(String path) {
		
		return context.getOutputDirectory() + path;
	}
	
	public String getPrintFile(Directory directory) {
		
		return "pr_index.html";
	}
}
