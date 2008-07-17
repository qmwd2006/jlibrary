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
package org.jlibrary.client.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.authors.AuthorsView;
import org.jlibrary.client.ui.authors.providers.AuthorsLabelProvider;
import org.jlibrary.client.ui.authors.wizard.NewAuthorWizard;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.entities.Author;

/**
 * @author martin
 *
 * Dialog used for creating a new repository
 */
public class AuthorSelectionDialog extends Dialog {

	private ListViewer viewer;
	private Button newAuthor;
	private Author author;
		
	private List authors;
	
	/**
	 * Constructor
	 * 
	 * @param window Parent window
	 */
	public AuthorSelectionDialog(Shell shell) {
		
		super(shell);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText(Messages.getMessage("author_title"));	
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_USER));
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
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		
		outer.setLayout (gridLayout);

		viewer = new ListViewer(outer);
		viewer.setLabelProvider(new AuthorsLabelProvider(
				SharedImages.getImage(SharedImages.IMAGE_AUTHOR)));
		viewer.add(authors.toArray());
		
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.END;
		data.heightHint = 200;
		data.widthHint = 200;
		viewer.getTable().setLayoutData(data);
		
		
		newAuthor = new Button (outer, SWT.NONE);
		newAuthor.setText(Messages.getMessage("author_new"));
		newAuthor.setToolTipText(Messages.getMessage("author_new_tooltip"));
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessVerticalSpace=true;
		data.verticalAlignment = GridData.BEGINNING;
		newAuthor.setLayoutData(data);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				if (event.getSelection().isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				author = (Author)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		});
		
		viewer.addOpenListener(new IOpenListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IOpenListener#open(org.eclipse.jface.viewers.OpenEvent)
			 */
			public void open(OpenEvent event) {
				
				author = (Author)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				okPressed();
			}
		});
		
		newAuthor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				NewAuthorWizard naw = new NewAuthorWizard(RepositoryView.getInstance().getCurrentRepository());
				WizardDialog wd = new WizardDialog(getShell(), naw);
				wd.open();
				if (wd.getReturnCode() == IDialogConstants.OK_ID)
				{	
					viewer.add(naw.getAuthor());
					AuthorsView.refresh();;
				}

			}
		});
		
		return outer;
	}
	
	public void open(List authors) {
		
		this.authors = authors;
		super.open();
	}
	
	/**
	 * Returns the new created author
	 * 
	 * @return Author that was created
	 */
	public Author getAuthor() {
		
		return author;
	}

}
