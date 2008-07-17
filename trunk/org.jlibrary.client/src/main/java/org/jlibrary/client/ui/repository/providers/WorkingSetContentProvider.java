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
package org.jlibrary.client.ui.repository.providers;

import java.text.Collator;
import java.util.Comparator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jlibrary.client.ui.repository.LockRegistry;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * Content provider for repository tree
 */
public class WorkingSetContentProvider implements ITreeContentProvider {

	
	private static Comparator comparator = new Comparator() {
		
		Collator collator = Collator.getInstance();
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object arg0, Object arg1) {

			Node node1 = (Node)arg0;
			Node node2 = (Node)arg1;
			
			if (node1.isDirectory()) {
				if (node2.isDirectory()) {
					return collator.compare(node1.getName(), node2.getName());
				} else {
					return -1;
				}
			} else {
				if (node2.isDirectory()) {
					return 1;
				} else {
					return collator.compare(node1.getName(), node2.getName());
				}
			}
		}
	};
	
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element) {

		if (element instanceof Repository) {
			LockRegistry lockRegistry = LockRegistry.getInstance();
			return lockRegistry.getLocks((Repository)element).toArray();
		} else {
			return new Object[]{};
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		
		if (element instanceof Repository) { 
			return true;
		}
		
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object element) {

		LockRegistry lockRegistry = (LockRegistry)element;
		return lockRegistry.getRepositoriesWithLockedDocuments().toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}

	public static Comparator getComparator() {
		
		return comparator;
	}
}
