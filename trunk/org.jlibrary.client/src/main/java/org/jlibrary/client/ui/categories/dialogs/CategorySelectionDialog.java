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
package org.jlibrary.client.ui.categories.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.categories.providers.CategoriesContentProvider;
import org.jlibrary.client.ui.categories.providers.CategoriesLabelProvider;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;

/**
 * @author Martï¿½n Pï¿½rez
 *
 * Dialog used for creating a new repository
 */
public class CategorySelectionDialog extends Dialog {

	private TreeViewer viewer;
	private Category category;
		
	private static CategorySelectionDialog instance;

	private String repositoryId;
	

	private CategorySelectionDialog(Shell shell) {
		
		super(shell);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText(Messages.getMessage("categories_label"));	
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_CATEGORY));
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		
		createButton(parent,IDialogConstants.OK_ID,Messages.getMessage("accept_option"),true);
		createButton(parent,IDialogConstants.CANCEL_ID, Messages.getMessage("cancel_option"),false);
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);	
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(final Composite parent) {

		Composite outer = (Composite)super.createDialogArea(parent);
		
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 1;
		outer.setLayout (gridLayout);

		viewer = new TreeViewer(parent);
		viewer.setLabelProvider(new CategoriesLabelProvider());
		viewer.setContentProvider(new CategoriesContentProvider());
		
		GridData data = new GridData (GridData.FILL_BOTH);
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.heightHint = 200;
		data.widthHint = 200;
		viewer.getTree().setLayoutData(data);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {
				
				if (event.getSelection().isEmpty()) {
					category = null;
				}
				Object selectedObject = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if (selectedObject instanceof Repository) {
					category = null;
					return;
				}
				category = (Category)selectedObject;
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		});
		
		viewer.addOpenListener(new IOpenListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IOpenListener#open(org.eclipse.jface.viewers.OpenEvent)
			 */
			public void open(OpenEvent event) {
				
				Object selection = ((IStructuredSelection)
								viewer.getSelection()).getFirstElement();
				if (selection instanceof Repository) {
					category = null;
				} else {
					category = (Category)selection;
				}

				okPressed();
			}
		});
		
		fillData(repositoryId);
		
		return outer;
	}




	public static CategorySelectionDialog getInstance(Shell shell) {
		
		if (instance == null) {
			instance = new CategorySelectionDialog(shell);
		}
		return instance;
	}
	
	private void fillData(String repositoryId) {
		
		Repository repository = 
			RepositoryRegistry.getInstance().getRepository(repositoryId);
		viewer.setInput(new Repository[]{repository});
	}
	
	public int open() {
		
		throw new NoSuchMethodError("Not supported operation use open(repository) instead");
	}
	
	public int open(Repository repository) {
		
		this.repositoryId = repository.getId();
		return super.open();
	}
	
	/**
	 * Returns the new created category
	 * 
	 * @return Category that was created
	 */
	public Category getCategory() {
		
		return category;
	}

}
