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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;

/**
 * Categories tree label provider
 */
public class CategoriesLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		
		if (element instanceof Category) {
			Category category = (Category)element;
			if (category.isUnknownCategory()) {
				return Messages.getMessage(Category.UNKNOWN_NAME);
			}
			return ((Category)element).getName();
		}
		// Repository
		Repository repository = (Repository)element; 
		StringBuilder buffer = new StringBuilder(repository.getName());
		buffer.append(" (");
		if (repository.getServerProfile().isLocal()) {
			buffer.append(Messages.getMessage(
					repository.getServerProfile().getLocation()));				
		} else {
			buffer.append(repository.getServerProfile().getLocation());
		}
		buffer.append(")");
		return buffer.toString();
	}

	
	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		
		if (element instanceof Category) {
			Category category = (Category)element;
			if ((category.getCategories() != null) && (!category.getCategories().isEmpty())) {
				return SharedImages.getImage(SharedImages.IMAGE_CATEGORY);			
			} else {
				return SharedImages.getImage(SharedImages.IMAGE_CATEGORY);
			}
		}
		// Repository
		if (((Repository)element).isConnected()) {
			return SharedImages.getImage(SharedImages.IMAGE_NODE_REPOSITORY);
		} else {
			return SharedImages.getImage(SharedImages.IMAGE_REPOSITORY_CLOSED);
		}
		
	}
}
