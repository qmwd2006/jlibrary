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
package org.jlibrary.client.export;

import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Holder class for export operation needed data
 */
public class RepositoryContext {

	private Repository repository;
	private String templatesDirectory;
	private String outputDirectory;
	
	/**
	 * Constructor
	 * 
	 * @param repository Repository for this context
	 * @param templatesDirectory Directory where the templates are stored
	 * @param outputDirectory Output directory for the export process
	 */	
	public RepositoryContext(Repository repository, 
							 String templatesDirectory,
							 String outputDirectory) {
		
		this.repository = repository;
		this.templatesDirectory = templatesDirectory;
		this.outputDirectory = outputDirectory;
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public String getTemplatesDirectory() {
		return templatesDirectory;
	}
	
	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setTemplatesDirectory(String templatesDirectory) {
		this.templatesDirectory = templatesDirectory;
	}
	
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	/**
	 * Applies a filter. The new repository in this context will be the 
	 * filtered repository
	 * 
	 * @param filter Filter to apply
	 */
	public void applyFilter(ExportFilter filter) {
		
		setRepository(filter.filter(getRepository()));
	}	
}
