/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
package org.jlibrary.client.ui.security.actions;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.security.views.RolesView;
import org.jlibrary.client.ui.security.wizard.NewRolWizard;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called when the user wants to create
 * a new rol
 */
public class NewRolAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(NewRolAction.class);
	
	private IWorkbenchSite site;
	
	/**
	 * Constructor
	 */
	public NewRolAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;
		setText(Messages.getMessage("item_new_rol"));
		setToolTipText(Messages.getMessage("tooltip_new_rol"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_ROL));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_ROL_DISABLED));
	}
		
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
		
		// Check if a repository exists
		if (JLibraryPlugin.getCurrentServerProfile() == null) {
			return false;
		}
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				SecurityManager.CREATE_ROLE)) {
			return false;
		}	
		
		return true;
	}
	
	public void run() {
				
		Repository repository = 
			RepositoryView.getInstance().getCurrentRepository();
		if (repository == null) {
			return;
		}
		
		logger.info("Creating new rol");
		
		NewRolWizard nrw = new NewRolWizard(repository);
	    WizardDialog wd = new WizardDialog(site.getShell(),nrw)
	    {
			protected Control createDialogArea(Composite parent)
			{
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_ROL));
				return control;
			}
	    };
	    wd.open();

		if (wd.getReturnCode() == IDialogConstants.OK_ID)
		{
			Rol rol = nrw.getNewRol();
			RolesView.rolCreated(rol);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}	
}
