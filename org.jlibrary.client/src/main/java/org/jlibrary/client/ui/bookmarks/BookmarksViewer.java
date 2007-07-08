/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
package org.jlibrary.client.ui.bookmarks;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Repository;


public class BookmarksViewer extends TreeViewer {

	public BookmarksViewer(Composite parent) {

		super(parent);
	}
	
	public Repository findRepository(Bookmark bookmark) {
		
		TreeItem item = (TreeItem)findItem(bookmark);
		if (item == null) {
			return null;
		}
		
		while (!(item.getData() instanceof Repository)) {
			item = item.getParentItem();
		}
		
		return (Repository)item.getData();
	}
	
}
