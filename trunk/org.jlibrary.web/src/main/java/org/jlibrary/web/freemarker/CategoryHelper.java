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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;

/**
 * Helper class to store information about categories that will be used in the 
 * export process
 * 
 * @author martin
 */
public class CategoryHelper {

	private HashMap mapCategories = new HashMap();
	private HashMap mapCategoryDocuments = new HashMap();
	private HashMap mapDocumentCategories = new HashMap();
	private Repository repository;
	
	/**
	 * Constructor
	 * 
	 * @param repository Repository for extracting data
	 */
	public CategoryHelper(Repository repository) {

		this.repository = repository;
	}
	
	public void loadData() throws RepositoryException {
				
		Collection categories = repository.getCategories();
		Iterator it = categories.iterator();
		while (it.hasNext()) {
			Category category = (Category) it.next();
			loadData(category);
		}
	}
	
	private void loadData(Category category) throws RepositoryException {
		
		mapCategories.put(category.getId(),category);
		
		List nodesInCategory = (List)mapCategoryDocuments.get(category.getId());
		if (nodesInCategory == null) {
			nodesInCategory = new ArrayList();
			mapCategoryDocuments.put(category.getId(), nodesInCategory);
		}
		
		Ticket ticket = repository.getTicket();
		ServerProfile profile = repository.getServerProfile();
		RepositoryService service = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();	
		
		Collection nodes;
		try {
			nodes = service.findNodesForCategory(ticket,category.getId());
		} catch (CategoryNotFoundException e) {
			nodes = new ArrayList();
		}
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			nodesInCategory.add(node);
			
			Set categoriesInNode = (Set)mapDocumentCategories.get(node.getId());
			if (categoriesInNode == null) {
				categoriesInNode = new HashSet();
				mapDocumentCategories.put(node.getId(), categoriesInNode);
			}
			categoriesInNode.add(category);
		}
		
		if (category.getCategories() != null) {
			it = category.getCategories().iterator();
			while (it.hasNext()) {
				Category child = (Category) it.next();
				loadData(child);
			}
		}
	}

	/**
	 * Release some memory
	 */
	public void clear() {
		
		mapCategories.clear();
		mapCategoryDocuments.clear();
		mapDocumentCategories.clear();
	}
	
	/**
	 * Returns a category given an id
	 * 
	 * @param categoryId Category's id
	 * 
	 * @return Category category or <code>null</code> if it not exists
	 */
	public Category findCategory(String categoryId) {
		
		return (Category)mapCategories.get(categoryId);
	}
	
	public Collection findNodesForCategory(String categoryId) {
		
		return (Collection)mapCategoryDocuments.get(categoryId);
	}
	
	public Collection findCategoriesForNode(String nodeId) {
		
		return (Collection)mapDocumentCategories.get(nodeId);
	}
}
