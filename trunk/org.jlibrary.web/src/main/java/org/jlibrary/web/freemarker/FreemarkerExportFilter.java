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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.web.content.RepositoryHelper;
import org.jlibrary.web.i18n.Messages;

/**
 * @author martin
 *
 * filter that clones a repository and reorganizes its data
 */
public class FreemarkerExportFilter implements ExportFilter {
	
	private Comparator comparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			Node node1 = (Node)o1;
			Node node2 = (Node)o2;
			
			int i = node1.getPosition().compareTo(node2.getPosition());
			if (i == 0) {
				return node1.getName().compareTo(node2.getName());
			}
			
			if (node1.getPosition().intValue() == 0) {
				return 1;
			} else if (node2.getPosition().intValue() == 0) {
				return -1;
			}
			
			return i;
		}
	};
	
	/**
	 * @see org.jlibrary.client.export.ExportFilter#filter(org.jlibrary.core.entities.Repository)
	 */
	public Repository filter(Repository repository) {

		Repository newRepository = (Repository)SerializationUtils.clone(repository);
		
		// transient
		newRepository.setServerProfile(repository.getServerProfile());
		
		filterPaths(newRepository.getRoot());
		reorderNodes(newRepository.getRoot());
		
		Category unknown = 
			RepositoryHelper.findUnknownCategory(newRepository);
		
		unknown.setName(Messages.getMessage(Category.UNKNOWN_NAME));
		unknown.setDescription(Messages.getMessage(Category.UNKNOWN_DESCRIPTION));
		
		return newRepository;
	}
	
	/**
	 * @see org.jlibrary.client.export.ExportFilter#filter(org.jlibrary.core.entities.Node)
	 */
	public Node filter(Node node) {
		
		Node newNode = (Node)SerializationUtils.clone(node);
		filterPaths(newNode);
		return node;
	}
	
	public Collection filterCategoryNodes(Category category, Collection nodes) {
		
		List favorites = new ArrayList();
		List notFavorites = new ArrayList();
		
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Node newNode = filter((Node)it.next());
			if (category.getFavorites().contains(newNode)) {
				favorites.add(newNode);
			} else {
				notFavorites.add(newNode);
			}
		}
		
		Collections.sort(favorites, comparator);
		Collections.sort(notFavorites, comparator);
		
		favorites.addAll(notFavorites);
		
		return favorites;
	}

	/**
	 * Reorders the nodes by creation date
	 * 
	 * @param node Node to reorder
	 */
	private void reorderNodes(Node node) {
		
		if (node.getNodes() == null) {
			return;
		}
		
		if (!(node.getNodes() instanceof TreeSet)) {
			Collection children = node.getNodes();
			node.setNodes(new TreeSet(comparator));
			node.getNodes().addAll(children);
		}
		Iterator it = node.getNodes().iterator();
		while (it.hasNext()) {
			Node child = (Node) it.next();
			reorderNodes(child);
		}
	}

	/**
	 * Removes possible wrong paths
	 * 
	 * @param node Node to filter
	 */
	private void filterPaths(Node node) {
		
		String path = StringUtils.replace(node.getPath(),"\\\\","/");
		path = StringUtils.replace(path,"\\","/");
		node.setPath(path);
		
		if (node.isDocument()) {
			if (((Document)node).getRelations() != null) {
				Iterator it = ((Document)node).getRelations().iterator();
				while (it.hasNext()) {
					Document document = (Document) it.next();
					path = StringUtils.replace(document.getPath(),"\\\\","/");
					path = StringUtils.replace(path,"\\","/");
					document.setPath(path);	
				}
			} else {
				((Document)node).setRelations(new HashSet());
			}
		} else {
			Iterator it = node.getNodes().iterator();
			while (it.hasNext()) {
				filterPaths((Node)it.next());
			}
		}
	}

}
