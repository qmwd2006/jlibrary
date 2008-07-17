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
package org.jlibrary.client.ui.bookmarks.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.jlibrary.client.Messages;
import org.jlibrary.core.entities.Bookmark;

/**
 * @author Nico
 * 
 * Wizard to update bookmarks
 */
public class UpdateBookmarkWizard extends Wizard {

	private BookmarkWizardPage bookmarkPage;

	private Bookmark favorite;
	private String type;

	/**
	 * 
	 * @param repository
	 */
	public UpdateBookmarkWizard(Bookmark bookmark)
	{
	
		super();

		this.favorite = bookmark;
		this.type = bookmark.getType();
		setWindowTitle(Messages.getMessage("update_bookmark_wizard_title"));
		setNeedsProgressMonitor(false);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		bookmarkPage = new BookmarkWizardPage(
				Messages.getMessage("update_bookmark_wizard_name"),
				Messages.getMessage("update_bookmark_wizard_description"),
				type);
		addPage(bookmarkPage);
		bookmarkPage.setBookmark(favorite);
	}

	public boolean performFinish()
	{
		
		favorite.setDescription(bookmarkPage.getDescriptionText());
		favorite.setName(bookmarkPage.getNameText());
		if (type == Bookmark.FAVORITE)
		{
			favorite.setUrl(bookmarkPage.getUrlText());
		}
		return true;
	}
	
	/**
	 * @return Returns the favorite.
	 */
	public Bookmark getBookmark() {
		return favorite;
	}


}
