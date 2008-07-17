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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.repository.wizard.NewDirectoryWizard;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called when the user wants to create
 * a new directory
 */
public class NewDirectoryAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(NewDirectoryAction.class);
	
	private IWorkbenchSite site;
	private Directory parent;
	
	/**
	 * Constructor
	 */
	public NewDirectoryAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;
		setText(Messages.getMessage("item_new_directory"));
		setToolTipText(Messages.getMessage("tooltip_new_directory"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DIRECTORY));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DIRECTORY_DISABLED));
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
				SecurityManager.CREATE_DIRECTORY)) {
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
	
	public void run(Object[] elements) {
		
		if (parent == null) {
			return;
		}
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(parent.getRepository());
		
		
		logger.debug("Creating new directory");
		
	    NewDirectoryWizard ndw = new NewDirectoryWizard(parent,repository);
	    WizardDialog wd = new WizardDialog(site.getShell(),ndw) {
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_JLIBRARY));
				return control;
			}
	    };
	    wd.open();
		
	    if (wd.getReturnCode() == IDialogConstants.OK_ID) {
			RepositoryView.getRepositoryViewer().refresh(parent);
			if (repository.getRoot().equals(parent)) {
				RepositoryView.getRepositoryViewer().refresh(repository);
				RepositoryView.getRepositoryViewer().expandToLevel(repository,1);		 		
			} else {
				RepositoryView.getRepositoryViewer().expandToLevel(parent,1);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}	
}
