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
package org.jlibrary.web.freemarker;

import java.util.Collection;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.web.content.WordCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Template processor for category entities
 */
public class CategoryTemplateProcessor extends BaseTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(CategoryTemplateProcessor.class);
	
	private Category category;

	/**
	 * Document template processor
	 * 
	 * @param exporter Main freemarker template exporter
	 * @param category Category to process
	 * @param context Context repository for this processor
	 * @param ftl Template used
	 */
	public CategoryTemplateProcessor(FreemarkerExporter exporter,
									 Category category, 
									 RepositoryContext context,
									 String ftl) {

		super(exporter,context,ftl);
		
		this.category = category;
	}
	
	@Override
	protected void exportContent(Page page) throws ExportException {

		page.expose(FreemarkerVariables.CATEGORY, category);	
		String rootURL = exporter.getRootURL(category);
		page.expose(FreemarkerVariables.ROOT_URL,rootURL);		
		page.expose(FreemarkerVariables.LOCATION_URL, exporter.getLocationURL(category));
		page.expose(FreemarkerVariables.PRINT_FILE, "");
		
		page.expose(FreemarkerVariables.PAGE_AUTHOR,"");
		page.expose(FreemarkerVariables.PAGE_KEYWORDS,
				WordCounter.buildKeywords(category.getDescription(),5));
		
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
	}
}
