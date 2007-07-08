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
package org.jlibrary.client.ui.categories.actions;

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
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.categories.wizard.NewCategoryWizard;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;

/**
 * @author Martï¿½n Pï¿½rez
 *
 * This action will be called when the user wants to create
 * a new category
 */
public class NewCategoryAction extends SelectionDispatchAction {

	private IWorkbenchSite site;
	
	/**
	 * Constructor
	 */
	public NewCategoryAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;
		setText(Messages.getMessage("item_new_category"));
		setToolTipText(Messages.getMessage("tooltip_new_category"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_CATEGORY));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_CATEGORY_DISABLED));
	}
		
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
			
		if (selection.isEmpty()) {
			return false;
		}
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Object selectedObject = selection.getFirstElement();
		if (selectedObject instanceof Category) {
			Category category = (Category)selectedObject;
			if (category.isUnknownCategory()) {
				return false;
			}
			if (!securityManager.canPerformAction(
					category.getRepository(),
					SecurityManager.CREATE_CATEGORY
					)) {
				return false;
			}		
			
		}
		return true;
	}
	
	public void run()
	{
		
		Object selectedObject = CategoriesView.getSelectedObject();
		Category parentCategory = null;
		String repositoryId = null;
		
		if (selectedObject instanceof Category) {
			parentCategory = ((Category)selectedObject);
			repositoryId = ((Category)selectedObject).getRepository();
		} else {
			repositoryId = ((Repository)selectedObject).getId();
		}
		Repository repository = RepositoryRegistry.getInstance().getRepository(repositoryId);
		NewCategoryWizard ncw = new NewCategoryWizard(repository, parentCategory);
	    WizardDialog wd = new WizardDialog(site.getShell(),ncw)
	    {
			protected Control createDialogArea(Composite parent)
			{
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_CATEGORY));
				return control;
			}
	    };
	    wd.open();

		if ((wd.getReturnCode() == IDialogConstants.OK_ID) &&
			(ncw.getCategory() != null))
		{
			CategoriesView.categoryCreated(ncw.getCategory());
		}			
	}
	
	/**
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}	
}
