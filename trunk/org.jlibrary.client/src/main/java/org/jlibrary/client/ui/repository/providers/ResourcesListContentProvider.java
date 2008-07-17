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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.jlibrary.client.ui.list.ListContentProvider;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;

/**
 * Content provider for repository tree
 */
public class ResourcesListContentProvider extends ListContentProvider {

	private Comparator comparator = new Comparator() {
		
		Collator collator = Collator.getInstance();
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object arg0, Object arg1) {

			Node node1 = (Node)arg0;
			Node node2 = (Node)arg1;
			
			return collator.compare(node1.getName(), node2.getName());
		}
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		
		if (inputElement instanceof Collection) {
			Object [] data = ((Collection)inputElement).toArray();
			Arrays.sort(data,comparator);
			return data;
		}
		
		if (inputElement instanceof Document) {
			Document document = (Document)inputElement;
		
			if (document.getResourceNodes() != null) {
				Object[] resourcesArray = document.getResourceNodes().toArray();
				Arrays.sort(resourcesArray,comparator);
				return resourcesArray;
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
