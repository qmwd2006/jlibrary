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

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.web.content.WordCounter;
import org.jlibrary.web.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Template processor for category entities
 */
public class CategoryTemplateProcessor implements FreemarkerTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(CategoryTemplateProcessor.class);
	
	private Category category;
	private RepositoryContext context;
	private FreemarkerExporter exporter;

	/**
	 * Document template processor
	 * 
	 * @param exporter Main freemarker template exporter
	 * @param category Category to process
	 * @param context Context repository for this processor
	 */
	public CategoryTemplateProcessor(FreemarkerExporter exporter,
									 Category category, 
									 RepositoryContext context) {

		this.category = category;
		this.context = context;
		this.exporter = exporter;
	}
	
	public String processTemplate(FreemarkerFactory factory) throws ExportException {


		return processTemplate(factory,factory.getPage("category.ftl"));		
	}	
	
	public String processTemplate(FreemarkerFactory factory,
								  Page page) throws ExportException {
				
		page.expose(FreemarkerVariables.REPOSITORY,context.getRepository());
		page.expose(FreemarkerVariables.CATEGORY, category);		
		
		page.expose(FreemarkerVariables.DATE, new Date());
		if (context.getRepository().getTicket().getUser().equals(User.ADMIN_USER)) {
			page.expose(FreemarkerVariables.USER, Messages.getMessage(User.ADMIN_NAME));
		} else {
			page.expose(FreemarkerVariables.USER, context.getRepository().getTicket().getUser().getName());
		}

		String rootURL = exporter.getRootURL(category);
		String repositoryURL = exporter.getRepositoryURL();
		
		String categoriesRootURL = repositoryURL+"/categories";
		page.expose(FreemarkerVariables.ROOT_URL,rootURL);		
		page.expose(FreemarkerVariables.REPOSITORY_URL,repositoryURL);
		page.expose(FreemarkerVariables.CATEGORIES_ROOT_URL,categoriesRootURL);
		page.expose(FreemarkerVariables.LOCATION_URL, exporter.getLocationURL(category));
		page.expose(FreemarkerVariables.PRINT_FILE, "");
		
		page.expose(FreemarkerVariables.PAGE_AUTHOR,"");
		page.expose(FreemarkerVariables.PAGE_KEYWORDS,
				WordCounter.buildKeywords(category.getDescription(),5));


		//CategoryHelper helper = exporter.getCategoryHelper();		
		//Collection nodes = helper.findNodesForCategory(category.getId());
		
		Repository repository = context.getRepository();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(repository.getServerProfile()).getRepositoryService();
		Collection nodes;
		try {
			nodes = repositoryService.findNodesForCategory(repository.getTicket(), category.getId());
		} catch (Exception e) {
			throw new ExportException(e);
		}
		ExportFilter filter = exporter.getFilter();
		page.expose(FreemarkerVariables.CATEGORY_DOCUMENTS,
					filter.filterCategoryNodes(category, nodes));
		
		return page.getAsString();		
	}
}
