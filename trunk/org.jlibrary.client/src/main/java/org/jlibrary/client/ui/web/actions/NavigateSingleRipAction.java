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
package org.jlibrary.client.ui.web.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.dialogs.DirectorySelectionDialog;
import org.jlibrary.client.ui.editor.URLEditorInput;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.web.CrawlManager;
import org.jlibrary.client.util.URL;
import org.jlibrary.core.entities.Directory;

/**
 * @author martin
 *
 * This action will be called to download a single page within a repository
 */
public class NavigateSingleRipAction extends Action 
									 implements IWorkbenchWindowActionDelegate {

	private URL url = null;
	private Directory currentDirectory;
	
	/**
	 * Constructor
	 */
	public NavigateSingleRipAction() {
		
		super();		
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		
	}

	public boolean isEnabled() {

		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				SecurityManager.WEB_CRAWLING)) {
			return false;
		}	
		return true;
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run(URL url) {
		
		this.url = url;
		run((IAction)null);
	}
	
	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
				
		URLEditorInput urlInput = (URLEditorInput)JLibraryPlugin.getActiveEditor().getEditorInput();
		url = urlInput.getURL();
		
		// Show spider dialog
		DirectorySelectionDialog dsd = DirectorySelectionDialog.getInstance();
		dsd.open();
		if (dsd.getReturnCode() == IDialogConstants.CANCEL_ID) {
			return;
		}
		currentDirectory = dsd.getDirectory();
		
		// CRAWL !!
		CrawlManager.getInstance().crawl(new URL[]{url},currentDirectory);		
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		action.setEnabled(false);
		
		IEditorPart editor = JLibraryPlugin.getActiveEditor();
		if (editor == null) {
			return;
		}
		
		IEditorInput input = editor.getEditorInput();
		if (!(input instanceof URLEditorInput)) {
			return;
		}
		
		// If there isn't any opened repository, disable action
		if (RepositoryRegistry.getInstance().getRepositoryCount() == 0) {
			return;
		}
		
		action.setEnabled(true);
	}

}
