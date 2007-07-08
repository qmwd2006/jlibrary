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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.ITextSelection;
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
import org.jlibrary.client.ui.bookmarks.wizard.UpdateBookmarkWizard;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
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
 * <p>This action will be called when the user wants to update a bookmark</p>
 */
public class UpdateBookmarkAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(UpdateBookmarkAction.class);
	
	/**
	 * Constructor
	 */
	public UpdateBookmarkAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_update_bookmark"));
		setToolTipText(Messages.getMessage("tooltip_update_bookmark"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_BOOKMARK));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_BOOKMARK_DISABLED));
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	protected void selectionChanged(ITextSelection selection) {}

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
			Bookmark bookmark = (Bookmark)element;
			repository = BookmarksView.findRepository(bookmark);
		} else {
			return false;
		}
		if (!securityManager.canPerformAction(
				repository.getId(),
				SecurityManager.SAVE_BOOKMARK)) {
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
		
		
		logger.info("Updating bookmark");
		
		UpdateBookmarkWizard ubw = 
			new UpdateBookmarkWizard((Bookmark)elements[0]);
		WizardDialog wd = new WizardDialog(getShell(), ubw)
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
		if (wd.getReturnCode() == IDialogConstants.OK_ID) {
			Bookmark bookmark = ubw.getBookmark();
			repository = RepositoryRegistry.getInstance().
										getRepository(bookmark.getRepository());
			
			// try to create favorite
			ServerProfile serverProfile = repository.getServerProfile();
			RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
			try {
				service.updateBookmark(repository.getTicket(),bookmark);
							
				BookmarksView.refresh();
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
