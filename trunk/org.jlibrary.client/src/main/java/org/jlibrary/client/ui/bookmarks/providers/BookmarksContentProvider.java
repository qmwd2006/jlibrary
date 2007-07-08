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
package org.jlibrary.client.ui.bookmarks.providers;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Repository;

/**
 * Content provider for favorites tree
 */
public class BookmarksContentProvider implements ITreeContentProvider {

	//TODO: The bookmarks must be grouped by server profile -> user
	public BookmarksContentProvider() {}
		
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element) {

		Object[] favorites = null;
		if (element instanceof Repository) {
			favorites = ((Repository)element).getTicket().getUser().getFilteredBookmarks().toArray();
		} else if (element instanceof Bookmark) {
			favorites = ((Bookmark)element).getBookmarks().toArray();
		}
		return favorites;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		
		if (element instanceof Repository) {
			return null;
		}
		Bookmark favorite = (Bookmark)element;
		if (favorite.getParent() == null) {
			return BookmarksView.findRepository(favorite);
		} else {
			return favorite.getParent();
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		
		if (element instanceof Repository) {
			return !((Repository)element).getTicket().getUser().getFilteredBookmarks().isEmpty();
		}
		if (((Bookmark)element).getBookmarks() == null) return false;
		return !((Bookmark)element).getBookmarks().isEmpty();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object element) {
		
		return ((Collection)element).toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}

}
