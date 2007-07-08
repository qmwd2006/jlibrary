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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.editor.EditorsRegistry;
import org.jlibrary.client.ui.editor.impl.SystemGenericEditor;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to open a document with operating system default defined tool
 */
public class OpenSystemAction extends SelectionDispatchAction {
	
	static Logger logger = LoggerFactory.getLogger(OpenSystemAction.class);
	
	private IEditorPart openedEditorPart;
	
	/**
	 * Constructor
	 * 
	 * @param Window application window
	 */
	public OpenSystemAction(IWorkbenchSite site) {
		
		super(site);
		
		setText(Messages.getMessage("item_open_system"));
		setToolTipText(Messages.getMessage("tooltip_open_system"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_OPEN_SYSTEM));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_OPEN_SYSTEM_DISABLED));
		
		update(getSelection());
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(canOperateOn(selection));
	}
	
	private boolean canOperateOn(IStructuredSelection selection) {
		
		if (selection.isEmpty()) {
			return false;
		}

		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Object element = selection.toArray()[0];
		
		if (!(element instanceof Document)) {
			return false;
		}
		
		Document document = ((Document)element);
		if (!securityManager.canPerformAction(
				document.getRepository(),
				document,
				SecurityManager.OPEN_DOCUMENT)) {
			return false;
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
		
		
		Document doc = (Document)elements[0];

		// Always get reference from the registry
		Document document = (Document)EntityRegistry.getInstance().
			getNode(doc.getId(),doc.getRepository());
		if (document == null) {
			logger.info("Invalid reference from Entity Registry");
			return;
		}
		
		logger.debug("Opening document with system editor");
		
		String path = document.getPath();
		String extension = FileUtils.getExtension(path);
		ClientConfig.setDefaultToolForExtension(ClientConfig.SYSTEM_TOOL,extension);
		
		try {
			FileEditorInput fei = 
				FileEditorInput.createFileEditorInput(document);
						
			// Open the document in an SYSTEM editor without a content viewer
			openedEditorPart = JLibraryPlugin.getActivePage().openEditor(
					fei,SystemGenericEditor.EDITOR_ID);	
			
			// Open the document in the system editor
			JLibraryPlugin.getActivePage().openEditor(
					fei,IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
			
			
			EditorsRegistry.getInstance().put(document.getId(),openedEditorPart);
			
			// updateViews
			RelationsView relationsView = JLibraryPlugin.findRelationsView();
			if (relationsView != null) {
				relationsView.refresh();
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
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
		}				
	}	
}
