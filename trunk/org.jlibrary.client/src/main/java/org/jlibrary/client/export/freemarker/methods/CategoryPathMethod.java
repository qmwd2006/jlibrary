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
package org.jlibrary.client.export.freemarker.methods;

import java.util.List;

import org.jlibrary.client.export.freemarker.CategoryHelper;
import org.jlibrary.client.export.freemarker.FreemarkerExporter;
import org.jlibrary.client.export.freemarker.FreemarkerVariables;
import org.jlibrary.client.export.freemarker.Page;
import org.jlibrary.client.util.CategoryUtils;
import org.jlibrary.core.entities.Category;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * Obtains a path for a category
 * 
 * @author martin
 *
 */
public class CategoryPathMethod implements TemplateMethodModel {

	private FreemarkerExporter exporter;

	public CategoryPathMethod(FreemarkerExporter exporter) {

		this.exporter = exporter;
	}
	
	public Object exec(List args) throws TemplateModelException {

		if ((args.size() != 1) && (args.size() != 2)) {
            throw new TemplateModelException("Wrong arguments");
        }
		
		Object arg = args.get(0);
		if (!(arg instanceof String)) {
			throw new TemplateModelException("Argument should be a string object");
		}

		String id = (String)arg;
		
		Page page = exporter.getCurrentPage();
		CategoryHelper helper = exporter.getCategoryHelper();
		Category category = helper.findCategory(id);
		
		String categoriesRootURL = 
			((SimpleScalar)page.value(FreemarkerVariables.CATEGORIES_ROOT_URL))
			.getAsString();
		
		String categoryPath = CategoryUtils.pathOf(category);
		
		return categoriesRootURL + categoryPath + "/index.html";
	}
}
