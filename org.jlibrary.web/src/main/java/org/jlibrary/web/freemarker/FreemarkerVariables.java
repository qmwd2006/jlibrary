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

/**
 * @author martin
 *
 * Freemarker defined variables holder class
 */
public class FreemarkerVariables {
	
	/**
	 * Error string
	 */
	public static final String ERROR_MESSAGE = "error";
	
	/**
	 * Search results
	 */
	public static final String SEARCH_RESULTS = "results";
	
	/**
	 * Ticket
	 */
	public static final String TICKET = "ticket";
	
	/**
	 * Exported category
	 */
	public static final String CATEGORY = "category";
	
	/**
	 * Exported document
	 */
	public static final String DOCUMENT = "document";
	
	/**
	 * Exported directory
	 */
	public static final String DIRECTORY = "directory";
	
	/**
	 * Exported repository
	 */
	public static final String REPOSITORY = "repository";
	
	/**
	 * Current date
	 */
	public static final String DATE = "date";
	
	/**
	 * User that have created the processed element
	 */
	public static final String USER = "user";
	
	/**
	 * Tracks the root URL for each template processed
	 */
	public static final String ROOT_URL = "root_url";

	/**
	 * Tracks the root URL for each repository processed
	 */
	public static final String REPOSITORY_URL = "repository_url";
	
	/**
	 * Tracks the relative URL for the categories directory for each template
	 * processed
	 */
	public static final String CATEGORIES_ROOT_URL = "categories_root_url";
	
	/**
	 * String that contains the current path. Each element of the path has a 
	 * link that allow us browse to that item. 
	 */
	public static final String LOCATION_URL = "location_url"; 
	
	/**
	 * Key string for the document's categories list
	 */
	public static final String DOCUMENT_CATEGORIES = "document_categories";
	
	/**
	 * Key string for the document's contents
	 */
	public static final String DOCUMENT_CONTENT = "document_content";
	
	/**
	 * Key to expose the creator of the node. It applies to both documents and 
	 * directories
	 */
	public static final String NODE_CREATOR = "node_creator";

	/**
	 * Key to expose the creator of the node. It applies only to documents
	 */
	public static final String NODE_AUTHOR = "node_author";
	
	/**
	 * Exposes the last time when the document were used
	 */
	public static final String DOCUMENT_UPDATE_DATE = "document_update_date";
	
	/**
	 * Name of the author, if exists
	 */
	public static final String PAGE_AUTHOR = "page_author";
	
	/**
	 * Keywords for the document
	 */
	public static final String PAGE_KEYWORDS = "page_keywords";
	
	/**
	 * Exposes the document list of a category
	 */
	public static final String CATEGORY_DOCUMENTS = "category_documents";
	
	/**
	 * Exposes the name of the file print view
	 */
	public static final String PRINT_FILE = "print_file";
	
	/**
	 * Explicit HTML content for the directory. Typically it will be 
	 * an index.html document under the directory.
	 */
	public static final String DIRECTORY_CONTENT = "directory_content";
}
