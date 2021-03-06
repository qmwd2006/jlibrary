/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
package org.jlibrary.web.freemarker;

import java.util.Collection;
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
public class DocumentTemplateProcessor extends BaseTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(DocumentTemplateProcessor.class);
	
	private Document document;
	private ResourceBundle bundle;
	

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

		super(exporter,context,ftl);
		this.document = document;
	}
	
	@Override
	protected void exportContent(Page page) throws ExportException {

		bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
		page.expose("loc",bundle);  
		
		page.expose(FreemarkerVariables.DOCUMENT, document);		
		String rootURL = exporter.getRootURL(document);
		
		page.expose(FreemarkerVariables.ROOT_URL,rootURL);
		page.expose(FreemarkerVariables.LOCATION_URL, exporter.getLocationURL(document));
		page.expose(FreemarkerVariables.PRINT_FILE, getPrintFile(document));
		
		page.expose(FreemarkerVariables.PAGE_KEYWORDS,document.getMetaData().getKeywords());
		
		page.expose(FreemarkerVariables.DOCUMENT_CATEGORIES, loadCategories());
		page.expose(FreemarkerVariables.DOCUMENT_CONTENT, loadContent());
		
		loadDocumentInformation(page);
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
			if (Types.isTextFile(document.getTypecode())) {
				String body = HTMLUtils.extractBody(new String(content));
				if (document.getTypecode().equals(Types.TEXT_DOCUMENT)) {
					body = "<pre>" + body + "</pre>";
				} 
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

		try {
			Repository repository = context.getRepository();
			Ticket ticket = context.getTicket(); 
			ServerProfile profile = repository.getServerProfile();
			RepositoryService service = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();	
			return service.findCategoriesForNode(ticket, document.getId());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExportException(e);	
		}
	}
	
	public String getPrintFile(Document document) {
		
		String path = document.getPath();
		int index = path.lastIndexOf("/");
		return "pr_" + path.substring(index+1,path.length());
	}
}
