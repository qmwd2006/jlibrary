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

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.ResourceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to copy the selection to the clipboard
 */
public class CopyAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(CopyAction.class);
	
	private SelectionDispatchAction pasteAction;
	
	/**
	 * Constructor
	 */
	public CopyAction(IWorkbenchSite site, Clipboard clipboard, SelectionDispatchAction pasteAction) {
		
		super(site);

		this.pasteAction = pasteAction;
		
		setText(Messages.getMessage("item_copy"));
		setToolTipText(Messages.getMessage("tooltip_copy"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_COPY));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_COPY_DISABLED));
		
		update(getSelection());
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

		for (int i = 0; i < elements.length; i++) {
			if (!(elements[i] instanceof Document) &&
				!(elements[i] instanceof Directory) &&
				!(elements[i] instanceof ResourceNode)) {
				return false;
			}
			Node node = ((Node)elements[i]);
			if (!securityManager.canPerformAction(
					node.getRepository(),
					node,
					SecurityManager.COPY)) {
				return false;
			}		
		}
		
	
		
		return true;
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(ITextSelection selection) {}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}
	
	public void run(Object[] elements) {

		
		logger.debug("Copying selection to clipboard");
		

		// update the enablement of the paste action
		// workaround since the clipboard does not support callbacks				
		if (pasteAction != null && pasteAction.getSelection() != null) {
			((PasteAction)pasteAction).setClipboardObjects(elements,false);
			pasteAction.update(pasteAction.getSelection());
		}
	}
}
