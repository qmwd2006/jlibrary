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

import java.net.MalformedURLException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.browser.Browser;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.editor.forms.WebFormPage;
import org.jlibrary.client.util.URL;
import org.jlibrary.client.web.HistoryTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to exit JLibrary
 */
public class NavigateStopAction extends Action implements IWorkbenchWindowActionDelegate {

	static Logger logger = LoggerFactory.getLogger(NavigateStopAction.class);
	
	/**
	 * Constructor
	 */
	public NavigateStopAction() {
		
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
	public void init(IWorkbenchWindow window) {
		

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		
		logger.info("Stop");

		JLibraryEditor editor = (JLibraryEditor)JLibraryPlugin.getActiveEditor();
		WebFormPage formPage = (WebFormPage)editor.getActivePageInstance();
		Browser browser = formPage.getBrowser();
		browser.stop();
		
		try {
			URL url = new URL(browser.getUrl());
			HistoryTracker.remove(editor,url);
		} catch (MalformedURLException e) {
            logger.error(e.getMessage(),e);
		}
		setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		HistoryTracker.setStopAction(action);
		IEditorPart editor = JLibraryPlugin.getActiveEditor();
		if (editor == null) {
			action.setEnabled(false);
			return;
		}
	}

}
