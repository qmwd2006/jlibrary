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
package org.jlibrary.client.ui.bookmarks.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to close a repository
 */
public class DeleteBookmarkAction extends SelectionDispatchAction {
	
	static Logger logger = LoggerFactory.getLogger(DeleteBookmarkAction.class);
	
	/**
	 * Constructor
	 */
	public DeleteBookmarkAction(IWorkbenchSite site) {
		
		super(site);
		setText(Messages.getMessage("item_delete_bookmark"));
		setToolTipText(Messages.getMessage("tooltip_delete_bookmark"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_DISABLED));
	}
	

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
			
		if (selection.isEmpty()) {
			return false;
		}
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Object item = selection.getFirstElement();
		if (item instanceof Repository) {
			return false;
		}
		Bookmark bookmark = (Bookmark)item;
		if (!securityManager.canPerformAction(
				bookmark.getRepository(),
				SecurityManager.DELETE_BOOKMARK)) {
			return false;
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}
	
	public void run(Object[] elements) {
		
		
		logger.info("Removing bookmarks");
		
		try {
			for (int i = 0; i < elements.length; i++) {
				Bookmark favorite = (Bookmark)elements[i];
				JLibraryPlugin.closeEditors(getSite(),favorite.getId());
				
				Repository repository = BookmarksView.findRepository(favorite);
				ServerProfile serverProfile = repository.getServerProfile();
				RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
				
				service.removeBookmark(repository.getTicket(),
									   favorite.getId());
					
				if (favorite.getParent() == null) {
					User currentUser = repository.getTicket().getUser();
					currentUser.getBookmarks().remove(favorite);
				} else {
					favorite.getParent().getBookmarks().remove(favorite);
				}
				BookmarksView.refresh();
			}
		} catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
			ErrorDialog.openError(new Shell(),
					"ERROR",
					Messages.getMessage("delete_bookmark_error"),
					new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
			StatusLine.setErrorMessage(Messages.getMessage("delete_bookmark_error"));
			return;
		}
		BookmarksView.refresh();
	}	
}
