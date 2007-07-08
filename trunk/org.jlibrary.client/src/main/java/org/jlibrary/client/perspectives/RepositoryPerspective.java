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
package org.jlibrary.client.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.repository.views.WorkingSetView;
import org.jlibrary.client.ui.search.SearchView;

/**
 * @author Martin
 *
 * Repository perspective
 */
public class RepositoryPerspective implements IPerspectiveFactory
{
	
	public static final String PERSPECTIVE_ID = "org.jlibrary.client.perspectives.RepositoryPerspective";
	
	public RepositoryPerspective() {}
	
	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
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
		topLeft.addView(RepositoryView.VIEW_ID);
		
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float)0.5, "topLeft");
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);
		bottomLeft.addView(WorkingSetView.VIEW_ID);
		bottomLeft.addView(RelationsView.VIEW_ID);
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.7, editorArea);
		bottom.addView(SearchView.VIEW_ID);
		bottom.addView("org.eclipse.ui.views.ProgressView");
		/*
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float)0.35, "bottom");
		bottom.addView("org.eclipse.ui.cheatsheets.views.CheatSheetView");
		*/
	}
}
