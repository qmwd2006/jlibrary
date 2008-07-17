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
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Interface that defines operations to filter repository content
 */
public interface ExportFilter {

	/**
	 * Filters a repository and returns a new repository filtered instance
	 * 
	 * @param repository Repository to be filtered
	 * 
	 * @return Repository Filtered repository
	 */
	public Repository filter(Repository repository);

	/**
	 * Filters a node and returns a new node filtered instance
	 * 
	 * @param node Node to be filtered
	 * 
	 * @return Node Filtered node
	 */
	public Node filter(Node node);
	
	/**
	 * Filters a list of nodes that are members of a given category
	 * 
	 * @param category Category
	 * @param nodes Collection of nodes of that category
	 * 
	 * @return Collection Filtered node list.
	 */
	public Collection filterCategoryNodes(Category category, Collection nodes);
}
