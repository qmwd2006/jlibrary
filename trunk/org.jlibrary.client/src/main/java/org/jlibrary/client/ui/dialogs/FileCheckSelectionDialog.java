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

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.tree.FileSystemNode;
import org.jlibrary.client.ui.tree.FileSystemViewer;


/**
 * @author Martín Pérez
 *
 * Dialog used for selecting a document from a repository
 */
public class FileCheckSelectionDialog extends Dialog {
	
	private static final String FILE_CHECK_DIALOG = "FILE_CHECK_DIALOG";
	
	private FileSystemViewer viewer;
		
	private IWorkbenchWindow window;
	private Object[] elements;

	private Button resourcesButton;
	private boolean extractResources;
	
	private IDialogSettings settings;

	private boolean showExtractResourcesOption;
	
	/**
	 * Constructor
	 * 
	 * @param window Parent window
	 */
	public FileCheckSelectionDialog(IWorkbenchWindow window,
									boolean showExtractResourcesOption) {
		
		super(window.getShell());
		this.window = window;
		this.showExtractResourcesOption = showExtractResourcesOption;
		
		settings = JLibraryPlugin.getDefault().
		getDialogSettings().getSection(FILE_CHECK_DIALOG);
		if (settings == null) {
			settings = JLibraryPlugin.getDefault().
				getDialogSettings().addNewSection(FILE_CHECK_DIALOG);
		}
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		
		shell.setText(Messages.getMessage("document_selection_dialog_title"));	
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_NEW_DOCUMENT));
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		
		createButton(parent,IDialogConstants.OK_ID,Messages.getMessage("accept_option"),true);
		createButton(parent,IDialogConstants.CANCEL_ID, Messages.getMessage("cancel_option"),false);	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		
		elements = viewer.getCheckedElements();
		if (showExtractResourcesOption) {
			extractResources = resourcesButton.getSelection();
		}
		
		FileSystemNode node = (FileSystemNode)elements[0];
		settings.put(FILE_CHECK_DIALOG,node.getFile().getAbsolutePath());
		
		super.okPressed();
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(final Composite parent) {

		Composite outer = (Composite)super.createDialogArea(parent);
		
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		
		outer.setLayout (gridLayout);

		GridData data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 250;
		data.heightHint = 200;
		
		viewer = new FileSystemViewer(outer,window);
		viewer.getControl().setLayoutData(data);
		
		if (showExtractResourcesOption) {
			data = new GridData ();
			data.horizontalAlignment = GridData.FILL;
			resourcesButton = new Button(outer,SWT.CHECK);
			resourcesButton.setText(Messages.getMessage("add_extract_resources"));
		}
		
		// Reload latest opened directory
		String lastFile = settings.get(FILE_CHECK_DIALOG);
		if (lastFile != null) {
			File file = new File(lastFile);
			if (file.exists()) {
				if (file.isDirectory()) {
					viewer.reveal(file);
				} else {
					viewer.reveal(file.getParentFile());
				}
			}
		}
		
		return outer;
	}
	
	public boolean shouldExtractResources() {
		
		return extractResources;
	}
	
	public Object[] getCheckedElements() {
		
		return elements;
	}
}
