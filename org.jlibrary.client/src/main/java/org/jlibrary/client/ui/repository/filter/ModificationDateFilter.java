/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
package org.jlibrary.client.ui.repository.filter;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Filter to sort by modification date
 */
public class ModificationDateFilter extends ViewerSorter {

	private Comparator dateComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			
			if (o1 instanceof Repository) {
				Repository r1 = (Repository)o1;
				if (o2 instanceof Repository) {
					Repository r2 = (Repository)o2;
					return r1.getName().compareTo(r2.getName());
				} else {
					Node node2 = (Node)o2;
					Repository r2 = RepositoryRegistry.getInstance().getRepository(node2.getRepository());
					return r1.getName().compareTo(r2.getName());
				}
			} 
			if (o2 instanceof Repository) {
				Repository r2 = (Repository)o2;
				if (o1 instanceof Repository) {
					Repository r1 = (Repository)o1;
					return r2.getName().compareTo(r1.getName());
				} else {
					Node node1 = (Node)o1;
					Repository r1 = RepositoryRegistry.getInstance().getRepository(node1.getRepository());
					return r2.getName().compareTo(r1.getName());
				}				
			}			
			
			Node node1 = (Node)o1;
			Node node2 = (Node)o2;
			
			if (!reversed) {
				return node1.getDate().compareTo(node2.getDate());
			} else {
				return node2.getDate().compareTo(node1.getDate());
			}
		}
	};
	
	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		
		return true;
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#filter(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object[])
	 */
	public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
		
		Arrays.sort(elements, dateComparator);
		return elements;
	}
}
