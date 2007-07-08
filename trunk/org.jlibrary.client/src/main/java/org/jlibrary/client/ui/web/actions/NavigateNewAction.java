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
package org.jlibrary.client.ui.web.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.URLEditorInput;
import org.jlibrary.client.util.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to exit jLibrary
 */
public class NavigateNewAction extends Action implements IWorkbenchWindowActionDelegate {

	static Logger logger = LoggerFactory.getLogger(NavigateNewAction.class);
	
	/**
	 * Constructor
	 */
	public NavigateNewAction() {
		
		super();		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		

	}

	public boolean isEnabled() {

		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				SecurityManager.WEB_BROWSING)) {
			return false;
		}	
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run(URL url) {

		run((IAction)null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		if (logger.isDebugEnabled()) {
			logger.info("new URL");
		}

		URLEditorInput uei = new URLEditorInput();
		
		try {
			IWorkbenchPage page = JLibraryPlugin.getActivePage();

			page.openEditor(uei, "org.jlibrary.client.ui.editor.impl.WebEditor");
			
			// Done in Web Form location listener
			//HistoryTracker.addURL(editor,urlToOpen);
		} catch (PartInitException e) {
            logger.error(e.getMessage(),e);
		}		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {


	}

}
