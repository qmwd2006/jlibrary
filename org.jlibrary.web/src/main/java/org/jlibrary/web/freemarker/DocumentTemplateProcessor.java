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
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.web.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Template processor for document entities
 */
public class DocumentTemplateProcessor implements FreemarkerTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(DocumentTemplateProcessor.class);
	
	private Document document;
	private RepositoryContext context;
	private FreemarkerExporter exporter;

	private ResourceBundle bundle;
	private String ftl;
	

	/**
	 * Document template processor
	 * 
	 * @param exporter Main freemarker template exporter
	 * @param document Document to process
	 * @param context Context repository for this processor
	 * @param ftl Template to use
	 */
	public DocumentTemplateProcessor(FreemarkerExporter exporter,
									 Document document, 
									 RepositoryContext context,
									 String ftl) {

		this.document = document;
		this.context = context;
		this.exporter = exporter;
		this.ftl = ftl;
	}
	
	public String processTemplate(FreemarkerFactory factory) throws ExportException {
				
		return processTemplate(factory,factory.getPage(ftl));
		
	}
	
	private String processTemplate(FreemarkerFactory factory, 
								   Page page) throws ExportException {
		
		/*
		if (Types.isImageFile(document.getTypecode())) {
			loadContent();
			return;
		}
		*/
		bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
		page.expose("loc",bundle);  
		
		page.expose(FreemarkerVariables.REPOSITORY,context.getRepository());
		page.expose(FreemarkerVariables.DOCUMENT, document);
		page.expose(FreemarkerVariables.TICKET, context.getTicket());
		page.expose(FreemarkerVariables.ERROR_MESSAGE, exporter.getError());
		
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
		
		String rootURL = exporter.getRootURL(document);
		String repositoryURL = exporter.getRepositoryURL();
		
		page.expose(FreemarkerVariables.ROOT_URL,rootURL);
		page.expose(FreemarkerVariables.REPOSITORY_URL,repositoryURL);
		page.expose(FreemarkerVariables.CATEGORIES_ROOT_URL,repositoryURL+"/categories");
		page.expose(FreemarkerVariables.LOCATION_URL, exporter.getLocationURL(document));
		page.expose(FreemarkerVariables.PRINT_FILE, getPrintFile(document));
		
		page.expose(FreemarkerVariables.PAGE_KEYWORDS,document.getMetaData().getKeywords());
		
		page.expose(FreemarkerVariables.DOCUMENT_CATEGORIES, loadCategories());
		page.expose(FreemarkerVariables.DOCUMENT_CONTENT, loadContent());
		
		loadDocumentInformation(page);
		
		return page.getAsString();
	}
	
	public void loadDocumentInformation(Page page) throws ExportException {
		
		if (document.getMetaData().getAuthor().isUnknown()) {
			page.expose(FreemarkerVariables.NODE_CREATOR, 
						bundle.getString(Author.UNKNOWN_NAME));
			page.expose(FreemarkerVariables.PAGE_AUTHOR, 
					bundle.getString(Author.UNKNOWN_NAME));
			page.expose(FreemarkerVariables.NODE_AUTHOR, 
					bundle.getString(Author.UNKNOWN_NAME));			
		} else {
			String authorName = document.getMetaData().getAuthor().getName();
			if (authorName.equals(User.ADMIN_NAME) || 
				authorName.equals(WebConstants.ANONYMOUS_WEB_USERNAME)) {
				authorName = bundle.getString(authorName);
			}
			page.expose(FreemarkerVariables.NODE_CREATOR, authorName);
			page.expose(FreemarkerVariables.NODE_AUTHOR, authorName);
			page.expose(FreemarkerVariables.PAGE_AUTHOR, authorName);
		}
		
		page.expose(FreemarkerVariables.DOCUMENT_UPDATE_DATE, 
					document.getDate());
	}
	
	public String loadContent() throws ExportException {
		
		Repository repository = context.getRepository();
		ServerProfile profile = repository.getServerProfile();
		final Ticket ticket = repository.getTicket();
		final RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		try {
			byte[] content = repositoryService.loadDocumentContent(document.getId(), ticket);
			if (document.getTypecode().equals(Types.HTML_DOCUMENT)) {				
				String body = HTMLUtils.extractBody(new String(content));
				return body;
			} else {
				//TODO: Implement binary content return
				return "";			
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExportException(e);
		}
	}
	
	private Collection loadCategories() throws ExportException {

		return Collections.EMPTY_LIST;
		//CategoryHelper helper = exporter.getCategoryHelper();
		//return helper.findCategoriesForNode(document.getId());
	}

	private String getFilePath(String path) {
		
		return context.getOutputDirectory() + path;
	}
	
	public String getPrintFile(Document document) {
		
		String path = document.getPath();
		int index = path.lastIndexOf("/");
		return "pr_" + path.substring(index+1,path.length());
	}
}
