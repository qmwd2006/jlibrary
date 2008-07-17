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
package org.jlibrary.client.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.history.HistoryView;
import org.jlibrary.client.ui.repository.views.RepositoryView;

public class NavigatorPerspective implements IPerspectiveFactory {

	public static final String PERSPECTIVE_ID = 
		"org.jlibrary.client.perspectives.NavigatorPerspective"; 
	
	public void createInitialLayout(IPageLayout layout) {

		layout.addPerspectiveShortcut(CategoriesPerspective.PERSPECTIVE_ID);
		layout.addPerspectiveShortcut(NavigatorPerspective.PERSPECTIVE_ID);
		layout.addPerspectiveShortcut(RepositoryPerspective.PERSPECTIVE_ID);
		layout.addPerspectiveShortcut(SecurityPerspective.PERSPECTIVE_ID);
		
		layout.addShowViewShortcut(RepositoryView.VIEW_ID);
		layout.addShowViewShortcut("org.eclipse.ui.cheatsheets.views.CheatSheetView");		
		layout.addShowViewShortcut(BookmarksView.VIEW_ID);
		layout.addShowViewShortcut(CategoriesView.VIEW_ID);
		
		String editorArea = layout.getEditorArea();
		
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, (float)0.35, editorArea);
		topLeft.addView(BookmarksView.VIEW_ID);
		
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float)0.5, "topLeft");
		bottomLeft.addView(HistoryView.VIEW_ID);
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.7, editorArea);
		bottom.addView("org.eclipse.ui.views.ProgressView");
				
		layout.addActionSet("org.jlibrary.client.ui.web.actions.NavigationActionSet");
		layout.setEditorAreaVisible(true);
		
	}

}
