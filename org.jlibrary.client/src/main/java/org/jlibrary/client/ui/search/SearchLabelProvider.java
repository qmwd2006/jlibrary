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
package org.jlibrary.client.ui.search;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.search.SearchHit;

/**
 * @author martin
 *
 * Search view label provider
 */
public class SearchLabelProvider implements ITableLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		
		SearchHit item = (SearchHit)element;
		switch(columnIndex) {
			case 0 : return "";
			case 1 : 
				return item.getName();
			case 2 :
				Repository repository = RepositoryRegistry.getInstance().
											getRepository(item.getRepository());
				return repository.getName();
			case 3 :
				return item.getPath();
		}
		return "";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {

		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		
		if (columnIndex == 0) {
			SearchHit item = (SearchHit)element;
			Integer importance = item.getImportance();			
			if (!importance.equals(Node.IMPORTANCE_MEDIUM)) {
				return getImageImportance(importance);
			}
			return SharedImages.getImage(SharedImages.IMAGE_EMPTY);
		}
		
		if (columnIndex == 1) {
			SearchHit item = (SearchHit)element;
			return SharedImages.getImageForPath(item.getPath());
		}
		if (columnIndex == 2) {
			return SharedImages.getImage(SharedImages.IMAGE_NODE_REPOSITORY);
		}
		if (columnIndex >= 4) {
			SearchHit item = (SearchHit)element;
			double score = item.getScore()*10;
			int mark = columnIndex-2;
			if ((score >= mark) && (score < mark+0.5)) {
				return SharedImages.getImage(SharedImages.IMAGE_HALF_STAR);
			} else if (score >= mark) {
				return SharedImages.getImage(SharedImages.IMAGE_STAR);
			} else {
				return null;
			}
		}
		
		return null;
	}
	
	private Image getImageImportance(Integer importance) {
		
		if (importance.equals(Node.IMPORTANCE_LOWEST)) {
			return SharedImages.getImage(SharedImages.IMAGE_IMPORTANCE_LOWEST);
		} else if (importance.intValue() < Node.IMPORTANCE_MEDIUM.intValue()) {
			return SharedImages.getImage(SharedImages.IMAGE_IMPORTANCE_LOW);
		} else if (importance.intValue() < Node.IMPORTANCE_HIGHEST.intValue()) {
			return SharedImages.getImage(SharedImages.IMAGE_IMPORTANCE_HIGH);
		} else {
			return SharedImages.getImage(SharedImages.IMAGE_IMPORTANCE_HIGHEST);
		}
	}
}
