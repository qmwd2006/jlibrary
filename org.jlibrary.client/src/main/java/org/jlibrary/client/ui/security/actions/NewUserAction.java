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
import org.jlibrary.client.ui.authors.AuthorsView;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.client.ui.security.views.RestrictionsView;
import org.jlibrary.client.ui.security.views.UsersView;
import org.jlibrary.client.ui.security.wizard.NewUserWizard;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called when the user wants to create
 * a new user
 */
public class NewUserAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(NewUserAction.class);
	
	private IWorkbenchSite site;

	
	/**
	 * Constructor
	 */
	public NewUserAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;
		setText(Messages.getMessage("item_new_user"));
		setToolTipText(Messages.getMessage("tooltip_new_user"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_USER));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_USER_DISABLED));
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
				SecurityManager.CREATE_USER)) {
			return false;
		}
		
		return true;
	}
	
	public void run() {
		
		Repository repository = RepositoryView.getInstance().getCurrentRepository();
		if (repository == null) {
			return;
		}
		
		logger.info("Creating new user");
		
		NewUserWizard nuw = new NewUserWizard(repository);
	    WizardDialog wd = new WizardDialog(site.getShell(),nuw)
	    {
			protected Control createDialogArea(Composite parent)
			{
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_USER));
				return control;
			}
	    };
	    wd.open();
		if (wd.getReturnCode() == IDialogConstants.OK_ID) {
			User user = nuw.getNewUser();
			UsersView.userCreated(user);			
			MembersRegistry.getInstance().addMember(user);
			AuthorsView.refresh();
			RestrictionsView.refresh();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}	

}
