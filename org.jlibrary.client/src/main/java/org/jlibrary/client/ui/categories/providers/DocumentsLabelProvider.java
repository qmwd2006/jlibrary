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
package org.jlibrary.client.ui.categories.providers;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.categories.DocumentsView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 * 
 * Documents view label provider
 */
public class DocumentsLabelProvider implements ITableLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 */
	public String getColumnText(Object element, int columnIndex) {

		Document document = (Document) element;
		switch (columnIndex) {
		case 0: return "";
		case 1: return "";
		case 2:
			return document.getName();
		case 3:
			Repository repository = RepositoryRegistry.getInstance().getRepository(document.getRepository());
			return repository.getName();
		case 4:
			return document.getPath();
		case 5:
			return document.getDescription();
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {

		Document document = (Document) element;

		if (columnIndex == 0) {
			if (!document.getImportance().equals(Node.IMPORTANCE_MEDIUM)) {
				return getImageImportance(document.getImportance());
			} else {
				return SharedImages.getImage(SharedImages.IMAGE_EMPTY);
			}
		}
		if (columnIndex == 1) {
			Category category = DocumentsView.getInstance().getCurrentCategory();

			if (category.getFavorites() != null) {
				Iterator it = category.getFavorites().iterator();
				while (it.hasNext()) {
					//System.out.println(category.getName()+"-" + document.getId());					
					Favorite favorite = (Favorite) it.next();
					if (favorite.getDocument().equals(document.getId())) {
						return SharedImages.getImage(SharedImages.IMAGE_FAVORITE);
					}
				}
			}
			return SharedImages.getImage(SharedImages.IMAGE_EMPTY);
		}

		if (columnIndex == 2) {
			return SharedImages.getImageForNode(document);
		} else if (columnIndex == 3) {
			return SharedImages.getImage(SharedImages.IMAGE_OPEN_REPOSITORY);
		}

		return SharedImages.getImage(SharedImages.IMAGE_EMPTY);
	}

	/**
	 * 
	 * @param importance
	 * @return
	 */
	private Image getImageImportance(Integer importance) {

		if (importance.equals(Node.IMPORTANCE_LOWEST)) {
			return SharedImages.getImage(SharedImages.IMAGE_IMPORTANCE_LOWEST);
		} else if (importance.intValue() < Node.IMPORTANCE_MEDIUM.intValue()) {
			return SharedImages.getImage(SharedImages.IMAGE_IMPORTANCE_LOW);
		} else if (importance.intValue() < Node.IMPORTANCE_HIGH.intValue()) {
			return SharedImages.getImage(SharedImages.IMAGE_EMPTY);
		} else if (importance.intValue() < Node.IMPORTANCE_HIGHEST.intValue()) {
			return SharedImages.getImage(SharedImages.IMAGE_IMPORTANCE_HIGH);
		} else {
			return SharedImages.getImage(SharedImages.IMAGE_IMPORTANCE_HIGHEST);
		}
	}
}
