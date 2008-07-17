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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.repository.wizard.NewResourceWizard;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * This action will be called when the user wants to create
 * a new resource
 */
public class NewResourceAction extends SelectionDispatchAction {
	
	private IWorkbenchSite site;
	private Directory parent;
	
	/**
	 * Constructor
	 */
	public NewResourceAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;
		setText(Messages.getMessage("item_new_resource"));
		setToolTipText(Messages.getMessage("tooltip_new_resource"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_RESOURCE));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_RESOURCE_DISABLED));
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
		if (elements.length > 1) {
			return false;
		}
		
		if (!((elements[0] instanceof Directory) || 
				 (elements[0] instanceof Repository))) {
				return false;
		}
		
		if (elements[0] instanceof Repository) {
			Repository repository = ((Repository)elements[0]);
					
			if (!repository.isConnected()) {
				return false;
			}			
			parent = repository.getRoot();
		} else {
			parent = (Directory)elements[0];
		}		

		if (!securityManager.canPerformAction(
				parent.getRepository(),
				parent,
				SecurityManager.CREATE_RESOURCE)) {
			return false;
		}					
		
		return true;
	}
		
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}
	
	public void run(Directory directory) {
		
		parent = directory;
		run(new Object[]{});
	}
	
	public void run(Object[] elements) {
		
		if (parent == null) {
			return;
		}

		Repository repository = RepositoryRegistry.getInstance().getRepository(parent.getRepository());
		
	    NewResourceWizard nrw = new NewResourceWizard(parent,repository);
	    WizardDialog wd = new WizardDialog(site.getShell(),nrw);
	    wd.open();
		
		 if (wd.getReturnCode() == IDialogConstants.OK_ID) {
		 	if (repository.getRoot().equals(parent)) {
			 	RepositoryView.getRepositoryViewer().refresh(repository);
			 	RepositoryView.getRepositoryViewer().expandToLevel(repository,1);		 		
		 	} else {
			 	RepositoryView.getRepositoryViewer().refresh(parent);
			 	RepositoryView.getRepositoryViewer().expandToLevel(parent,1);
		 	}
		 }
		 
	}
}