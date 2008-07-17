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
package org.jlibrary.client.ui.bookmarks.menu;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.jlibrary.client.ui.bookmarks.actions.BookmarksActionGroup;

/**
 * @author Martín Pérez
 *
 * Context menu for favorites view widget
 */
public class BookmarksContextMenu extends MenuManager {

	private BookmarksActionGroup fActionGroup;
	
	/**
	 * Constructor
	 */
	public BookmarksContextMenu(BookmarksActionGroup bag, String title) {
		
		super(title);
		
		fActionGroup = bag;
		add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
		fActionGroup.fillContextMenu(this);		
	}
}
