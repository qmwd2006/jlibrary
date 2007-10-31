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

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jlibrary.core.entities.User;
import org.jlibrary.web.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Template processor for category entities
 */
public abstract class BaseTemplateProcessor implements FreemarkerTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(BaseTemplateProcessor.class);
	
	RepositoryContext context;
	FreemarkerExporter exporter;
	String ftl;

	/**
	 * Document template processor
	 * 
	 * @param exporter Main freemarker template exporter
	 * @param category Category to process
	 * @param context Context repository for this processor
	 */
	public BaseTemplateProcessor(FreemarkerExporter exporter,
								 RepositoryContext context,
								 String ftl) {

		this.context = context;
		this.exporter = exporter;
		this.ftl = ftl;
	}
	
	/**
	 * @see FreemarkerTemplateProcessor#processTemplate(FreemarkerFactory)
	 */
	public String processTemplate(FreemarkerFactory factory) throws ExportException {


		return processTemplate(factory,factory.getPage(ftl));		
	}	
	
	/**
	 * Processes a freemarker template
	 * 
	 * @param factory Freemarker factory
	 * @param page Freemarker page
	 * 
	 * @return String Parsed content
	 * 
	 * @throws ExportException If there is any exception exporting the content
	 */
	public String processTemplate(FreemarkerFactory factory,
								  Page page) throws ExportException {
			
		ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
		page.expose("loc",bundle);  
		
		page.expose(FreemarkerVariables.REPOSITORY,context.getRepository());
		page.expose(FreemarkerVariables.TICKET, context.getTicket());
		page.expose(FreemarkerVariables.ERROR_MESSAGE, exporter.getError());		
		page.expose(FreemarkerVariables.DATE, new Date());
		page.expose(FreemarkerVariables.PAGE_KEYWORDS,"");
		page.expose(FreemarkerVariables.LOCATION_URL, "");

		
		if (context.getRepository().getTicket().getUser().equals(User.ADMIN_USER)) {
			page.expose(FreemarkerVariables.USER, bundle.getString(User.ADMIN_NAME));
		} else {
			String userName = context.getRepository().getTicket().getUser().getName();
			if (userName.equals(WebConstants.ANONYMOUS_WEB_USERNAME)) {
				userName = bundle.getString(WebConstants.ANONYMOUS_WEB_USERNAME);
			}
			page.expose(FreemarkerVariables.USER, userName);
		}

		String repositoryURL = exporter.getRepositoryURL();
		String categoriesRootURL = repositoryURL+"/categories";
		page.expose(FreemarkerVariables.REPOSITORY_URL,repositoryURL);
		page.expose(FreemarkerVariables.CATEGORIES_ROOT_URL,categoriesRootURL);

		page.expose(FreemarkerVariables.REGISTRATION_ENABLED, new Boolean(context.isRegistrationEnabled()));
		
		exportContent(page);
		
		return page.getAsString();
	}
	
	/**
	 * This method gives the subclasses an oportunity to export their own content
	 * 
	 * @param page Freemarker page object
	 * @throws ExportException
	 */
	protected abstract void exportContent(Page page) throws ExportException;
}
