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
package org.jlibrary.client.ui.bookmarks.providers;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.User;

/**
 * Repository tree label provider
 */
public class BookmaksLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		
		if (element instanceof Bookmark) {
			return ((Bookmark)element).getName();
		} else if (element instanceof Repository) {
			Repository repository = (Repository)element;
			User user = repository.getTicket().getUser();
			if (user.getName().equals(User.ADMIN_NAME)) {
				return Messages.getMessage(User.ADMIN_NAME) + "("+repository.getName()+")";
			} else {
				return user.getName() + "("+repository.getName()+")";
			}
		}
		return "";
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object node) {
		
		if (node instanceof Repository) {
			return SharedImages.getImage(SharedImages.IMAGE_USER);
		}
		
		Bookmark favorite = (Bookmark)node;
		if (favorite.isFolder()) {
			return SharedImages.getImage(SharedImages.IMAGE_NEW_DIR_BOOKMARK);
		} else {
			return SharedImages.getImage(SharedImages.IMAGE_NEW_BOOKMARK);
		}
	}

	
}
