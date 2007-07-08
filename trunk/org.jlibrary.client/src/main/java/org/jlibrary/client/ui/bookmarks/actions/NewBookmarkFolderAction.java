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
package org.jlibrary.client.ui.bookmarks.actions;

import java.util.HashSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.client.ui.bookmarks.wizard.NewBookmarkWizard;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called when the user wants to create
 * a new bookmark folder
 */
public class NewBookmarkFolderAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(NewBookmarkFolderAction.class);
	
	/**
	 * Constructor
	 */
	public NewBookmarkFolderAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_new_bookmark_folder"));
		setToolTipText(Messages.getMessage("tooltip_new_bookmark_folder"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DIR_BOOKMARK));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DIR_BOOKMARK_DISABLED));
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
		
		Repository repository = null;
		Object element = selection.getFirstElement();
		if (element instanceof Bookmark) {
			repository = BookmarksView.findRepository((Bookmark)element);
		} else {
			repository = (Repository)element;
		}
		if (!securityManager.canPerformAction(
				repository.getId(),
				SecurityManager.CREATE_BOOKMARK)) {
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
		
		
		logger.info("Creating new favorite folder");
		
		NewBookmarkWizard nbw = new NewBookmarkWizard(Bookmark.FOLDER);
		WizardDialog wd = new WizardDialog(getShell(), nbw)
	    {
			protected Control createDialogArea(Composite parent)
			{
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_BOOKMARK));
				return control;
			}
	    };
		wd.open();

		Repository repository = null;
		if (wd.getReturnCode() == IDialogConstants.OK_ID)
		{
			Bookmark bookmark = nbw.getBookmark();
			Bookmark parent = null;
			if (elements.length > 0) {
				if (elements[0] instanceof Bookmark) {
					if (((Bookmark)elements[0]).isFolder()) {
						parent = (Bookmark)elements[0];
					}
					repository = BookmarksView.findRepository((Bookmark)elements[0]);
				} else {
					repository = (Repository)elements[0];
				}
			}
			bookmark.setRepository(repository.getId());
			bookmark.setType(Bookmark.FOLDER);
			bookmark.setParent(parent);
			bookmark.setUser(repository.getTicket().getUser().getId());
			
			// try to create favorite
			ServerProfile serverProfile = repository.getServerProfile();
			RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
			try {
				bookmark = service.createBookmark(repository.getTicket(),bookmark);
				if (parent != null) {
					if (parent.getBookmarks() == null) {
						parent.setBookmarks(new HashSet());
					}					
					parent.getBookmarks().add(bookmark);
				} else {
					repository.getTicket().getUser().getBookmarks().add(bookmark);
				}	

				BookmarksView.getBookmarksViewer().expandToLevel(elements[0],1);
				BookmarksView.getBookmarksViewer().refresh();

			} catch (RepositoryException e) {
                logger.error(e.getMessage(),e);
				ErrorDialog.openError(new Shell(),
						"ERROR",
						Messages.getMessage("new_bookmark_error"),
						new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
				StatusLine.setErrorMessage(Messages.getMessage("new_bookmark_error"));
				return;
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}	
}
