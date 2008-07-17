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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Template processor for generic pages
 */
public class GenericTemplateProcessor extends BaseTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(GenericTemplateProcessor.class);
	
	/**
	 * Document template processor
	 * 
	 * @param exporter Main freemarker template exporter
	 * @param context Context repository for this processor
	 */
	public GenericTemplateProcessor(FreemarkerExporter exporter, 
								    RepositoryContext context,
								    String template) {

		super(exporter,context,template);
	}

	@Override
	protected void exportContent(Page page) throws ExportException {
		
		String rootURL = exporter.getRootURL(context.getRepository().getRoot());
		page.expose(FreemarkerVariables.ROOT_URL,rootURL);
	}
}
