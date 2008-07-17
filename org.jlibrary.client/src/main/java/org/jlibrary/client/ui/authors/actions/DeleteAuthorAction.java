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
package org.jlibrary.client.ui.authors.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.authors.AuthorsView;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to delete an author
 */
public class DeleteAuthorAction extends SelectionDispatchAction {
	
	static Logger logger = LoggerFactory.getLogger(DeleteAuthorAction.class);
	
	/**
	 * Constructor
	 */
	public DeleteAuthorAction(IWorkbenchSite site) {
		
		super(site);
		setText(Messages.getMessage("item_delete_author"));
		setToolTipText(Messages.getMessage("tooltip_delete_author"));
		setImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_DELETE_AUTHOR));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_DELETE_AUTHOR_DISABLED));
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

		Iterator it = selection.iterator();
		while (it.hasNext()) {
			Author author = (Author) it.next();
			if (!securityManager.canPerformAction(
				 author.getRepository(),
				 SecurityManager.DELETE_AUTHOR)) {
				return false;
			}			
			
			if (author.isUnknown()) {
				return false;
			}
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
		
		logger.info("Removing authors");
		
		try {
			for (int i = 0; i < elements.length; i++) {
				Author author = (Author)elements[i];
				JLibraryPlugin.closeEditors(getSite(),author.getId());
				
				Repository repository = JLibraryPlugin.getCurrentRepository();
				ServerProfile serverProfile = repository.getServerProfile();
				RepositoryService service = 
					JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
				
				service.deleteAuthor(repository.getTicket(),
									 author.getId());
					
				AuthorsView.refresh();
			}
		} catch (final SecurityException se) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(new Shell(),
							  "ERROR",
							  Messages.getMessage("security_exception"),
							  new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
					StatusLine.setErrorMessage(se.getMessage());
				}
			});		
		} catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
			ErrorDialog.openError(new Shell(),
					"ERROR",
					Messages.getMessage("delete_author_error"),
					new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
			StatusLine.setErrorMessage(Messages.getMessage("delete_author_error"));
			return;
		}
	}	
}
