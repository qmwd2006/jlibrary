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
package org.jlibrary.client.ui.repository.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to export repository contents
 */
public class SaveContentAction extends SelectionDispatchAction {
 
	static Logger logger = LoggerFactory.getLogger(SaveContentAction.class);
	
	private IWorkbenchSite site;
	
	/**
	 * Constructor
	 */
	public SaveContentAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;

		setText(Messages.getMessage("item_save_content"));
		setToolTipText(Messages.getMessage("tooltip_save_content"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_SAVE_CONTENT));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_SAVE_CONTENT_DISABLED));
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
		
		Object[] elements = selection.toArray();
		if (elements.length > 1) {
			return false;
		}
		
		if (!(elements[0] instanceof Document)) {
			return false;
		}
		
		Document document = ((Document)elements[0]);
		if (!securityManager.canPerformAction(
				document.getRepository(),
				document,
				SecurityManager.SAVE_CONTENT)) {
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

		
		logger.debug("Saving document's contents");
		
		final Document document = (Document)elements[0];
		
		FileDialog fd = new FileDialog(site.getShell(), SWT.SAVE);
		String filter = ClientConfig.getValue(ClientConfig.SAVE_DOCUMENT);
		if (filter != null) {
			File f = new File(filter);
			fd.setFilterPath(f.getAbsolutePath());
		}
		String extension = FileUtils.getExtension(document.getPath());
		fd.setFilterNames (
				new String [] {Messages.getAndParseValue("load_content_extension","%1",extension)
		});
		fd.setFilterExtensions (new String [] {"*"+extension});	
		
		String result = fd.open();
		if (result == null) {
			// Cancel
			return;
		}
		ClientConfig.setValue(ClientConfig.SAVE_DOCUMENT,result);
		
		final File f = new File(result);
		if (f.exists()) {
			// Overwrite ?
			// Show ask dialog
			boolean confirm = MessageDialog.openYNConfirm(
					site.getWorkbenchWindow().getShell(),
					Messages.getMessage("overwrite_confirmation_title"),
					Messages.getMessage("export_overwrite_confirmation_text"));
			
			if (confirm) {
				// Delete original file
				f.delete();
			} else {
				// Cancel
				return;
			} 
		}
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(document.getRepository());
		Ticket ticket = repository.getTicket();
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(f);
			repositoryService.loadDocumentContent(document.getId(), ticket, fos);
		} catch (final SecurityException se) {
            logger.error(se.getMessage(),se);
			ErrorDialog.openError(site.getShell(),
					"ERROR",
					Messages.getMessage("not_enough_permissions"),
					new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			ErrorDialog.openError(site.getShell(),
					"ERROR",
					Messages.getMessage("error_saving_content"),
					new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
			StatusLine.setErrorMessage(Messages.getMessage("error_saving_content"));
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}

	}

	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}
}
