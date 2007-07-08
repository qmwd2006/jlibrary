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
package org.jlibrary.client.ui.web.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.web.providers.CrawlContentProvider;
import org.jlibrary.client.ui.web.providers.CrawlLabelProvider;


/**
 * @author martin
 *
 * A dialog used to check what resources the user wants do download
 */
public class CrawlSelectionDialog extends Dialog {
	
	private CheckboxTableViewer viewer;
		
	private Object[] elements;
	private String[] files;
	private Button changeButton;
	
	/**
	 * Constructor
	 * 
	 * @param window Parent window
	 * @param files Files to select
	 */
	public CrawlSelectionDialog(IWorkbenchWindow window, String[] files) {
		
		super(window.getShell());
		this.files = files;
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		
		shell.setText(Messages.getMessage("crawl_selection_dialog_title"));	
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_CRAWL_MULTIPLE));
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
		
		super.okPressed();
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

		GridData data = new GridData();
		data.horizontalSpan = 3;
		data.horizontalAlignment = GridData.FILL;
		
		changeButton = new Button(outer,SWT.CHECK);
		changeButton.setText(Messages.getMessage("crawl_selection_dialog_check"));
		changeButton.setLayoutData(data);
		changeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if (changeButton.getSelection()) {
					viewer.setAllChecked(true);
				} else {
					viewer.setAllChecked(false);
				}
			}
		});
		
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 250;
		data.heightHint = 200;
		
	    viewer = CheckboxTableViewer.newCheckList(outer, SWT.BORDER);
	    viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	    viewer.setContentProvider(new CrawlContentProvider());
	    viewer.setLabelProvider(new CrawlLabelProvider());
		viewer.getControl().setLayoutData(data);
		
		viewer.setInput(files);
		
		return outer;
	}
	
	public Object[] getCheckedElements() {
		
		return elements;
	}
}
