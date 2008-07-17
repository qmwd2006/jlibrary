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
package org.jlibrary.client.ui.relations;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.jlibrary.client.ui.list.ListContentProvider;
import org.jlibrary.client.ui.repository.providers.RepositoryTreeContentProvider;
import org.jlibrary.core.entities.Document;

/**
 * @author martin
 * 
 * Content provider for categories list viewer
 */
public class RelationsContentProvider extends ListContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		
		if (inputElement == null) {
			return new Object[]{};
		}
		
		if (inputElement instanceof Collection) {
			return ((Collection)inputElement).toArray();
		}
		
		if (inputElement instanceof Document) {
			Document document = (Document)inputElement;
			if (document.getRelations() != null) {
				Object[] nodes = document.getRelations().toArray();
				Arrays.sort(nodes, RepositoryTreeContentProvider.getComparator());
				return nodes;
			} else {
				return new Object[]{};
			}
		}
		
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}
