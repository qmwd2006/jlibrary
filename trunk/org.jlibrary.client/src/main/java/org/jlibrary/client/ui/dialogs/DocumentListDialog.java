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

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.repository.providers.SimpleNodeContentProvider;
import org.jlibrary.client.ui.repository.providers.SimpleNodeLabelProvider;

/**
 * @author martin
 *
 * This class simply shows a list of documents. 
 */
public class DocumentListDialog extends Dialog {
	
	private String title;
	private String message;
	private List contentList;

	private TableViewer tv;
	
	public DocumentListDialog(Shell shell, 
							  String title,
							  String message) {
		
		super(shell);
		this.title = title;
		this.message = message;
	}	
	
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText(title);
		shell.setSize(260,200);
	}
	
	protected void createButtonsForButtonBar(Composite parent) {

		createButton(parent,IDialogConstants.OK_ID,Messages.getMessage("accept_option"),false);
	}
	
	protected Control createDialogArea(Composite parent) {

		Composite outer = (Composite)super.createDialogArea(parent);
		outer.setLayout(new FillLayout(SWT.VERTICAL));
		
		Label label = new Label(outer,SWT.NONE);
		label.setText(message);
		
		tv = new TableViewer(outer, SWT.NONE);
		tv.setLabelProvider(new SimpleNodeLabelProvider());
		tv.setContentProvider(new SimpleNodeContentProvider());
		
		TableColumn column = new TableColumn(tv.getTable(), SWT.NONE);
		column.setWidth(20);
		column = new TableColumn(tv.getTable(), SWT.NONE);
		column.setWidth(120);
		column.setText(Messages.getMessage("documents_view_name"));
		
		setInput(contentList);

		return outer;
	}

	public int open(List input) {

		if ((input == null) || (input.size() == 0)) {
			this.contentList = null;
		} else {
			this.contentList = input;
		}
		return super.open();
	}
	
	private void setInput(Collection input) {
		
		tv.setInput(input);
		tv.getControl().setEnabled(true);
	}	
}

