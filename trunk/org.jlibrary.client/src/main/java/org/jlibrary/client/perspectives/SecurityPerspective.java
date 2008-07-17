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

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.jlibrary.client.ui.authors.AuthorsView;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.security.views.GroupsView;
import org.jlibrary.client.ui.security.views.RestrictionsView;
import org.jlibrary.client.ui.security.views.RolesView;
import org.jlibrary.client.ui.security.views.UsersView;

public class SecurityPerspective implements IPerspectiveFactory {
	
	public static final String PERSPECTIVE_ID = "org.jlibrary.client.perspectives.SecurityPerspective";
	
	/* (non-Javadoc)
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
		
		String relativePartId= IPageLayout.ID_EDITOR_AREA;		
		int relativePos= IPageLayout.TOP;
		layout.addView(GroupsView.VIEW_ID, relativePos, 0.25f, relativePartId);
		
		relativePartId= GroupsView.VIEW_ID;
		relativePos= IPageLayout.RIGHT;
		layout.addView(UsersView.VIEW_ID, relativePos, 0.25f, relativePartId);
		
		relativePartId= UsersView.VIEW_ID;
		relativePos= IPageLayout.RIGHT;		
		layout.addView(RolesView.VIEW_ID, relativePos, 0.33f, relativePartId);
		
		relativePartId= RolesView.VIEW_ID;
		relativePos= IPageLayout.RIGHT;
		layout.addView(AuthorsView.VIEW_ID, relativePos, 0.50f, relativePartId);
		
		relativePartId= IPageLayout.ID_EDITOR_AREA;
		relativePos= IPageLayout.LEFT;
		layout.addView(RepositoryView.VIEW_ID, relativePos, 0.33f, relativePartId);

		relativePartId= RepositoryView.VIEW_ID;
		relativePos= IPageLayout.BOTTOM;
		layout.addView(RestrictionsView.VIEW_ID, relativePos, 0.50f, relativePartId);
		
		layout.setEditorAreaVisible(true);
	}


}
