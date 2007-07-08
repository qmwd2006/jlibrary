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
package org.jlibrary.client.ui.versions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jlibrary.client.Messages;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.ui.editor.forms.DocumentFormMetadata;
import org.jlibrary.client.ui.editor.forms.FormContents;
import org.jlibrary.core.entities.Document;

/**
 * Used whenever the input is null or no viewer can be found.
 */
public class DocumentViewer extends AbstractViewer {

	private Form form;

	public DocumentViewer(Composite parent, 
						  FileEditorInput editorInput, 
						  Document document) {
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.getBody().setLayout(new FillLayout());
		
		CTabFolder tabFolder = new CTabFolder(form.getBody(), SWT.BOTTOM);		

		Form fc = toolkit.createForm(tabFolder);
		fc.getBody().setLayout(new FillLayout());		
		CTabItem tabContents = new CTabItem(tabFolder,SWT.BOTTOM);
		tabContents.setText(Messages.getMessage("editor_contents"));
		new FormContents(editorInput,document,fc.getBody());
		tabContents.setControl(fc);
		
		ScrolledForm sf = toolkit.createScrolledForm(tabFolder);
		sf.getBody().setLayout(new FillLayout());
		CTabItem tabMetadata = new CTabItem(tabFolder,SWT.BOTTOM);
		tabMetadata.setText(Messages.getMessage("editor_properties"));
		new DocumentFormMetadata(document,sf.getBody());
		tabMetadata.setControl(sf);

		tabFolder.setSelection(tabContents);
		CompareViewerPane.clearToolBar(parent);
	}

	public Control getControl() {
		
		return form;
	}
}
