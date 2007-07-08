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
package org.jlibrary.client.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jlibrary.client.Messages;

/**
 * This is a dialog with a list and two buttons for accept and cancel the selection. Because
 * ListViewer doesn't support icons this class uses a TableViewer instead.
 */
public class ListDialog extends Dialog {

	public static final String EMPTY_LIST = Messages.getMessage("empty_list");

	private Object selectedItem;
	private List contentList;
	private TableViewer tv;
	private String title;
	
	private ITableLabelProvider labelProvider;
	private IStructuredContentProvider contentProvider;

	/**
	 * Constructor
	 * 
	 * @param shell Parent shell
	 */
	public ListDialog(Shell shell, String title) {
		
		super(shell);
		this.title = title;
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText(title);
		shell.setSize(350,300);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		
		createButton(parent,IDialogConstants.OK_ID,Messages.getMessage("accept_option"),false);
		createButton(parent,IDialogConstants.CANCEL_ID,Messages.getMessage("cancel_option"),true);
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {

		Composite outer = (Composite)super.createDialogArea(parent);
		outer.setLayout(new FillLayout());
		
		tv = new TableViewer(outer, SWT.FULL_SELECTION);
		
		if (labelProvider != null) {
			tv.setLabelProvider(labelProvider);
		}
		
		if (contentProvider != null) {
			tv.setContentProvider(contentProvider);
		}
		
		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			/**
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent sce) {
				
				if (sce.getSelection().isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}

		});

		tv.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				
				okPressed();
			}
		});
		
		setInput(contentList);

		return outer;
	}




	/**
	 * @param provider
	 */
	public void setContentProvider(IStructuredContentProvider provider) {
		contentProvider = provider;
	}

	/**
	 * @param provider
	 */
	public void setLabelProvider(ITableLabelProvider provider) {
		labelProvider = provider;
	}

	/**
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open(List input) {

		if ((input == null) || (input.size() == 0)) {
			this.contentList = null;
		} else {
			this.contentList = input;
		}
		return super.open();
	}


	/**
	 * Establece el contenido de la lista
	 * 
	 * @param input Contenido de la lista
	 */
	private void setInput(Collection input) {
		
		if ((input == null) || (input == Collections.EMPTY_LIST)) {
			ArrayList list = new ArrayList();
			list.add(EMPTY_LIST);
			tv.setInput(list);
			tv.getControl().setEnabled(false);
		} else {
			tv.setInput(input);
			tv.getControl().setEnabled(true);
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		
		if (tv.getTable().getSelectionCount() == 0) {
			selectedItem = null;
		}
		selectedItem = contentList.get(tv.getTable().getSelectionIndex());
		
		super.okPressed();
	}


	/**
	 * Return the selected item on this list
	 * 
	 * @return Selected item
	 */
	public Object getSelectedItem() {
		
		return selectedItem;
	}
}
