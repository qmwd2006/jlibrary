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
package org.jlibrary.client.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.help.AboutInfo;
import org.jlibrary.client.ui.dialogs.AboutDialog;

/**
 * Creates an About dialog and opens it.
 */
public class AboutAction
		extends Action 
		implements ActionFactory.IWorkbenchAction {
			
	/**
	 * The workbench window; or <code>null</code> if this
	 * action has been <code>dispose</code>d.
	 */
	private IWorkbenchWindow workbenchWindow;
	private AboutInfo primaryInfo;

	/**
	 * Creates a new <code>AboutAction</code> with the given label
	 */
	public AboutAction(IWorkbenchWindow window) {
		
		if (window == null) {
			throw new IllegalArgumentException();
		}
		this.workbenchWindow = window;
		
		primaryInfo = AboutInfo.readInfo();
		
		setText(Messages.getMessage("item_about"));
		setToolTipText(Messages.getMessage("tooltip_about"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_ABOUT));
		
	}
	
	/* (non-Javadoc)
	 * Method declared on IAction.
	 */
	public void run() {
		
		if (workbenchWindow == null) {
			// action has been disposed
			return;
		}
		AboutDialog ab = new AboutDialog(workbenchWindow, primaryInfo);
		ab.open();
	}
	
	/* (non-Javadoc)
	 * Method declared on ActionFactory.IWorkbenchAction.
	 */
	public void dispose() {
		
		if (workbenchWindow == null) {
			// action has already been disposed
			return;
		}
		workbenchWindow = null;
	}
}




/*
package org.jlibrary.client.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.JLibrary;
import org.jlibrary.core.jci.ClientInterface;


public class AboutAction 	extends Action
							implements ActionFactory.IWorkbenchAction {

	private IWorkbenchWindow window;
	

	public AboutAction(IWorkbenchWindow window) {
		
		super();
		this.window = window;
		
		setText(Messages.getMessage("item_about"));
		setToolTipText(Messages.getMessage("tooltip_about"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_ABOUT));
		
	}


	protected void selectionChanged(ITextSelection selection) {}


	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
		
		return true;
	}
	

	public void run(ITextSelection selection) {}
	

	public void run(IStructuredSelection selection) {
		
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}
	
	public void run(Object[] elements) {

		ClientInterface jci = JLibrary.getJCI();
		jci.getLoggingService().debug("[AboutAction] Opening about dialog");
		/*
		 AboutDialog dialog = AboutDialog.getInstance(window.getShell());
		 dialog.open();
		 */
/*
	}
	

	public void dispose() {

	}
}
*/