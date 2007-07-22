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

import org.jlibrary.core.entities.ResourceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Template processor for document entities
 */
public class ResourceTemplateProcessor implements FreemarkerTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(ResourceTemplateProcessor.class);
	
	private ResourceNode resource;
	private RepositoryContext context;
	
	/**
	 * Document template processor
	 * 
	 * @param exporter Main freemarker template exporter
	 * @param document Document to process
	 * @param context Context repository for this processor
	 */
	public ResourceTemplateProcessor(ResourceNode resource, 
									 RepositoryContext context) {

		this.resource = resource;
		this.context = context;
	}
	
	public String processTemplate(FreemarkerFactory factory) 
										throws ExportException {

		return loadContent();
	}
	
	public String loadContent() throws ExportException {
		
		//TODO: Implement resource loading
		return "";
	}
		
	private String getFilePath(String path) {
		
		return context.getOutputDirectory() + path;
	}
}
