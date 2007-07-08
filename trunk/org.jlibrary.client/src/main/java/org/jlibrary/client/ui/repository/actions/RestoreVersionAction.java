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
package org.jlibrary.client.ui.repository.actions;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.versions.VersionBrowsingDialog;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to add an entire directory
 */
public class RestoreVersionAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(RestoreVersionAction.class);
	
	private IWorkbenchSite site;
	private DocumentVersion documentVersion;
	
	/**
	 * Constructor
	 */
	public RestoreVersionAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;
		setText(Messages.getMessage("item_restore_version"));
		setToolTipText(Messages.getMessage("tooltip_restore_version"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_RESTORE_VERSION));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_RESTORE_VERSION_DISABLED));
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
		
		if (selection.isEmpty())
			return false;
		
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
		if (!document.hasVersions()) {
			return false;
		}
		
		if (!securityManager.canPerformAction(
				document.getRepository(),
				document,
				SecurityManager.RESTORE_VERSIONS)) {
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

		
		logger.info("Showing version browser");
		
		Document doc = (Document)elements[0];
		
		// Always get the entity registry document
		final Document document = (Document)EntityRegistry.getInstance().
			getNode(doc.getId(),doc.getRepository()); 
		if (document == null) {
			logger.info("No reference to document in Entity Registry");
			return;
		}
		
		VersionBrowsingDialog esd = new VersionBrowsingDialog(site.getShell(),document);
		esd.open();
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}
	
	public DocumentVersion getDocumentVersion() {
		return documentVersion;
	}
}
