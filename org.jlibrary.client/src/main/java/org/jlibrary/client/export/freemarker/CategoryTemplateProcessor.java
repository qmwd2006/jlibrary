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
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.CopyUtils;
import org.jlibrary.client.Messages;
import org.jlibrary.client.export.ExportException;
import org.jlibrary.client.export.ExportFilter;
import org.jlibrary.client.export.RepositoryContext;
import org.jlibrary.client.search.WordCounter;
import org.jlibrary.client.util.CategoryUtils;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.User;
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
	
	/**
	 * @see org.jlibrary.client.export.freemarker.FreemarkerTemplateProcessor#processTemplate(org.jlibrary.client.export.freemarker.FreemarkerFactory)
	 */
	public void processTemplate(FreemarkerFactory factory) throws ExportException {

		String path = getCategoryPath(category) +
		 		      File.separator + 
					  "index.html";
		processTemplate(factory,factory.getPage("category.ftl"),path);
		
		path = getCategoryPath(category) +
	      	   File.separator + 
			   "pr_index.html";
		processTemplate(factory,factory.getPage("category-print.ftl"),path);
		
	}	
	
	/**
	 * @see org.jlibrary.client.export.freemarker.FreemarkerTemplateProcessor#processTemplate(org.jlibrary.client.export.freemarker.FreemarkerFactory)
	 */
	public void processTemplate(FreemarkerFactory factory,
								Page page,
								String path) throws ExportException {
				
		page.expose(FreemarkerVariables.REPOSITORY,context.getRepository());
		page.expose(FreemarkerVariables.CATEGORY, category);		
		
		page.expose(FreemarkerVariables.DATE, new Date());
		if (context.getRepository().getTicket().getUser().equals(User.ADMIN_USER)) {
			page.expose(FreemarkerVariables.USER, Messages.getMessage(User.ADMIN_NAME));
		} else {
			page.expose(FreemarkerVariables.USER, context.getRepository().getTicket().getUser().getName());
		}

		String rootURL = exporter.getRootURL(category);
		String categoriesRootURL = "categories/" + rootURL;
		page.expose(FreemarkerVariables.ROOT_URL,rootURL);
		page.expose(FreemarkerVariables.CATEGORIES_ROOT_URL,categoriesRootURL);
		page.expose(FreemarkerVariables.LOCATION_URL, exporter.getLocationURL(category));
		page.expose(FreemarkerVariables.PRINT_FILE, getPrintFile(category));
		
		page.expose(FreemarkerVariables.PAGE_AUTHOR,"");
		page.expose(FreemarkerVariables.PAGE_KEYWORDS,
				WordCounter.buildKeywords(category.getDescription(),5));


		CategoryHelper helper = exporter.getCategoryHelper();
		Collection nodes = helper.findNodesForCategory(category.getId());
		
		ExportFilter filter = exporter.getFilter();
		page.expose(FreemarkerVariables.CATEGORY_DOCUMENTS,
					filter.filterCategoryNodes(category, nodes));
		
		String dirPath = getCategoryPath(category);
		File dirFile = new File(dirPath);
		if (!dirFile.exists()) {
			if (!dirFile.mkdirs()) {
				String message = "[CategoryTemplateProcessor] The directory " + category.getName() + " couldn't be created";
				logger.error(message);
				throw new ExportException(message);
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			CopyUtils.copy(page.getAsString(), fos);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExportException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
	
	private String getCategoryPath(Category category) {
		
		String path = CategoryUtils.pathOf(category);
		return context.getOutputDirectory() + "/categories" + path.toString();
	}
	
	public String getPrintFile(Category category) {
		
		return "pr_index.html";
	}
}
