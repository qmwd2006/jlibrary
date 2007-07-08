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
package org.jlibrary.client.ui.security.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.security.wizard.ChangePasswordWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be invoked to change the admin password on a server.
 */
public class ChangeAdminPasswordAction extends Action {
	
	static Logger logger = LoggerFactory.getLogger(ChangeAdminPasswordAction.class);
	
	private IWorkbenchWindow window;

	/**
	 * Constructor
	 */
	public ChangeAdminPasswordAction(IWorkbenchWindow window) {
		
		super();
		this.window = window;
		
		setText(Messages.getMessage("item_change_admin_password"));
		setToolTipText(Messages.getMessage("tooltip_change_admin_password"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_SECURITY));
	}

	public boolean isEnabled() {

		return true;
	}
	
	public void run() {
		
		logger.debug("Showing change admin passwor dialog");

		ChangePasswordWizard cpw = new ChangePasswordWizard();
	    WizardDialog wd = new WizardDialog(window.getShell(),cpw) {
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_JLIBRARY));
				return control;
			}
	    };
	    wd.open();
	}
}
