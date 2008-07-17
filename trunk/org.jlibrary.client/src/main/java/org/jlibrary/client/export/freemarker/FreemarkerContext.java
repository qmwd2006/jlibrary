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
package org.jlibrary.client.export.freemarker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jlibrary.client.export.RepositoryContext;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Repository context with some freemarker utility methods
 */
public class FreemarkerContext extends RepositoryContext {

	private final static String DOCUMENT_TEMPLATE="document.ftl";
	private final static String CATEGORY_TEMPLATE="category.ftl";
	private final static String DIRECTORY_TEMPLATE="directory.ftl";
	private final static String DOCUMENT_TEMPLATE_PRINT="document-print.ftl";
	private final static String CATEGORY_TEMPLATE_PRINT="category-print.ftl";
	private final static String DIRECTORY_TEMPLATE_PRINT="directory-print.ftl";
	
	
	/**
	 * Constructor
	 * 
	 * @param repository Repository for this context
	 * @param templatesDirectory Directory where the templates are stored
	 * @param outputDirectory Output directory for the export process
	 */
	public FreemarkerContext(Repository repository, 
							 String templatesDirectory,
							 String outputDirectory) {

		super(repository, templatesDirectory, outputDirectory);
	}
	
	/**
	 * Returns a String list with the paths of all the valid freemarker
	 * template files. This method also check that the templates are valid.
	 * If a template directory not have all the required templates, then 
	 * it will not be returned.
	 * 
	 * @return List of template paths
	 */
	public List getTemplateFilesList() {
		
		ArrayList list = new ArrayList();
		
		File outputFile = new File(getTemplatesDirectory());
		if (!outputFile.exists() || !outputFile.isDirectory()) {
			return Collections.EMPTY_LIST;
		}
		
		File[] child = outputFile.listFiles();
		for (int i = 0; i < child.length; i++) {
			if (!child[i].isDirectory()) continue;
			if (!isValidTemplateDirectory(child[i].getAbsolutePath())) {
				continue;
			}
			list.add(child[i]);
		}
		
		return list;
	}
	
	private boolean isValidTemplateDirectory(String path) {
		
		int templatesFound = 0;
		File dir = new File(path);
		String childNames[] = dir.list();
		for (int i = 0; i < childNames.length; i++) {
			if ((childNames[i].equalsIgnoreCase(DOCUMENT_TEMPLATE)) ||
				(childNames[i].equalsIgnoreCase(DOCUMENT_TEMPLATE_PRINT)) ||
				(childNames[i].equalsIgnoreCase(DIRECTORY_TEMPLATE)) ||
				(childNames[i].equalsIgnoreCase(DIRECTORY_TEMPLATE_PRINT)) ||
				(childNames[i].equalsIgnoreCase(CATEGORY_TEMPLATE)) ||
				(childNames[i].equalsIgnoreCase(CATEGORY_TEMPLATE_PRINT))) {
				
				templatesFound++;
				continue;
			}
		}
		
		return templatesFound == 6;
	}
}
