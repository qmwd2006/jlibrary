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
package org.jlibrary.client.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.RepositoryViewer;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Dialog used for selecting a resource
 */
public class DirectorySelectionDialog extends Dialog {

	private String repositoryId;
	private Directory directory;
	
	private RepositoryViewer viewer;
		
	private static DirectorySelectionDialog instance;
	private IWorkbenchWindow window;
	
	/**
	 * Constructor
	 * 
	 * @param window Parent window
	 */
	private DirectorySelectionDialog(IWorkbenchWindow window) {
		
		super(window.getShell());
		this.window = window;
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		
		shell.setText(Messages.getMessage("directory_selection_dialog_title"));	
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_DIRECTORY));
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
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		
		outer.setLayout (gridLayout);

		GridData data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 250;
		data.heightHint = 200;
		
		viewer = new RepositoryViewer(outer,
									  window,
									  RepositoryViewer.ONLY_DIRECTORIES);
		
		Collection repositories = new ArrayList();
		if (repositoryId != null) {
			Repository repository = 
				RepositoryRegistry.getInstance().getRepository(repositoryId);
			repositories.add(repository);
		} else {
			repositories = 
				RepositoryRegistry.getInstance().getOpenedRepositories();
		}
		
		viewer.setRepositories(repositories);
		viewer.getControl().setLayoutData(data);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {

				if (event.getSelection().isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
				
				Object item = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (item instanceof Repository) {
					directory = ((Repository)item).getRoot();
				} else {
					directory = (Directory)item;
				}
				getButton(IDialogConstants.OK_ID).setEnabled(true);				
			}
		});
		
		return outer;
	}

	/**
	 * Returns an unique instance of this NewDirectoryDialog
	 *
	 * @return Unique instance of this dialog
	 */
	public static DirectorySelectionDialog getInstance() {
		
		if (instance == null) {
			instance = new DirectorySelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		}
		
		return instance;
	}
	
	/**
	 * Returns the new selected directory
	 * 
	 * @return Directory Selected directory
	 */
	public Directory getDirectory() {
		
		return directory;
	}
		
	public int open() {

		repositoryId = null;
		return super.open();
	}
	
	public int open(Repository repository) {
		
		this.repositoryId = repository.getId();
		return super.open();
	}
}
