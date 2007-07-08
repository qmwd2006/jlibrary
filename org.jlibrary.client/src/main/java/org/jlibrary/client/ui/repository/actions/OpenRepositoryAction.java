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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.ActionFactory;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.wizard.OpenRepositoryWizard2;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * This action will be called when the client wants to load a new
 * repository
 */
public class OpenRepositoryAction 	extends Action
									implements ActionFactory.IWorkbenchAction {
	
	private IWorkbenchWindow window;
	
	/**
	 * Constructor
	 */
	public OpenRepositoryAction(IWorkbenchWindow window) {
		
		super();

		this.window = window;
		
		setText(Messages.getMessage("item_open_rep"));
		setToolTipText(Messages.getMessage("tooltip_open_rep"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_OPEN_REPOSITORY));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_OPEN_REPOSITORY_DISABLED));
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

		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		if (!(selection.getFirstElement() instanceof Repository)) {
			return false;
		}
		Repository repository = (Repository)selection.getFirstElement();
		if (!securityManager.canPerformAction(
				repository.getId(),
				repository.getRoot(),
				SecurityManager.OPEN_REPOSITORY)) {
			return false;
		}
		
		return true;
	}
		
	public void run() {

	    //OpenRepositoryWizard orw = new OpenRepositoryWizard();
	    OpenRepositoryWizard2 orw = new OpenRepositoryWizard2();
	    WizardDialog wd = new WizardDialog(window.getShell(),orw) {
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_JLIBRARY));
				return control;
			}	    	
	    };
		wd.open();

		 if (wd.getReturnCode() == IDialogConstants.OK_ID) {
		     
			Repository repository = orw.getRepository();
			
			// Check if the repository already exists
			if (RepositoryRegistry.getInstance().getRepository(repository.getId()) == null) {
			
				String repositoryName = orw.getRepositoryName();
				
			 	// Add repository to tree viewer
			 	RepositoryRegistry.getInstance().addRepository(repository,
			 												   repositoryName);
			 	
			 	// Update views
			 	RelationsView relationsView = JLibraryPlugin.findRelationsView();
			 	if (relationsView != null) {
			 		relationsView.refresh();
			 	}
			 	
			 	CategoriesView categoriesView = JLibraryPlugin.findCategoriesView();
			 	if (categoriesView != null) {
			 		categoriesView.refresh();
			 	}
			 	
			 	BookmarksView.addRepository(repository);
			}
		 }
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {

	}
}
