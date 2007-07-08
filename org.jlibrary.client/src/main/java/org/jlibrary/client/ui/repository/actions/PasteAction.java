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

import java.io.File;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.ccp.PasteException;
import org.jlibrary.client.ui.ccp.PasteService;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to paste clipboard contents
 */
public class PasteAction extends SelectionDispatchAction {
	
	static Logger logger = LoggerFactory.getLogger(PasteAction.class);
	
	private Object[] clipboardObjects;
	private boolean remove;
	private String[] filenames;

	/**
	 * Constructor
	 * 
	 * @param Window application window
	 */
	public PasteAction(IWorkbenchSite site, Clipboard clipboard) {
		
		super(site);
		
		setText(Messages.getMessage("item_paste"));
		setToolTipText(Messages.getMessage("tooltip_paste"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_PASTE));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_PASTE_DISABLED));
		
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
		
		setEnabled(canOperateOn(selection));
	}
	
	private boolean canOperateOn(IStructuredSelection selection) {

		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(SecurityManager.PASTE)) {
			return false;
		}
		
		Clipboard clipboard = new Clipboard(getSite().getShell().getDisplay());
		filenames = (String[])clipboard.getContents(FileTransfer.getInstance());

		if ((clipboardObjects == null) || selection.isEmpty()) {
			if ((filenames!= null) && (filenames.length > 0)) {
				return true;
			} else {
				return false;
			}
		}
		
		Object element = selection.toArray()[0];
		
		if ((element instanceof Document) || (element instanceof ResourceNode)) {
			if ((filenames!= null) && (filenames.length > 0)) {
				return true;
			} else {
				return false;
			}
		} else if (element instanceof Repository) {
			element = ((Repository)element).getRoot();
		}
		
		Node node = (Node)element;
		
		for (int i = 0; i < clipboardObjects.length; i++) {
			Node node2 = (Node)clipboardObjects[i];
			
			if (node.equals(node2.getParent())) {
				if ((filenames!= null) && (filenames.length > 0)) {
					return true;
				} else {
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		
		if (!canOperateOn(selection))
			return;
		run(selection.toArray());
	}
	
	public void run(Object[] elements) {
		
		PasteService pasteService = PasteService.getInstance();
		Object destination = elements[0];
		
		if (elements[0] instanceof Repository) {
			destination = ((Repository)elements[0]).getRoot();
		}
		
		try {
			if (clipboardObjects == null) {
				File[] files = new File[filenames.length];
				for (int i = 0; i < files.length; i++) {
					files[i] = new File(filenames[i]);
				}
				pasteService.paste(files,destination,remove);
			} else {
				pasteService.paste(clipboardObjects, destination, remove);
			}
		} catch (PasteException e) {
			
            logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * Sets clipboard contents
	 * 
	 * @param clipboardObjects The clipboard objects to set.
	 * @param remove Indicates if the original object has to be removed
	 */
	public void setClipboardObjects(Object[] clipboardObjects, boolean remove) {
		
		PasteService.getInstance().setClipboardObjects(clipboardObjects);
		PasteService.getInstance().setCutOperation(remove);
		this.clipboardObjects = clipboardObjects;
		this.remove = remove;
	}	
}
