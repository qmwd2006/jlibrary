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
package org.jlibrary.client.ui.editor.forms;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.editor.JLibraryFormPage;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.ResourceNode;


public class ContentsFormPage extends JLibraryFormPage {

	private FormContents formContents;
	private IManagedForm form;

	public ContentsFormPage(JLibraryEditor editor, String title) {

		super(editor, title);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm form) {

		this.form = form;
		Composite body = form.getForm().getBody();
		formContents = new FormContents((FileEditorInput)getEditor().getEditorInput(),
										((JLibraryEditor)getEditor()).getModel(),
										body);
		updateFormTitle();
	}

	public void initialize(FormEditor editor) {

		super.initialize(editor);
	}

	public void setFocus() {

		if(formContents != null)
			formContents.setFocus();
	}

	public void updateContent() {

		if(formContents != null) {
			formContents.initBrowser();
		}
	}

	public void refreshContents() {

		if(formContents != null) {
			formContents.refreshBrowser();
		}
	}

	private void updateFormTitle() {

		Object modelObject = ((JLibraryEditor)getEditor()).getModel();

		if (modelObject instanceof Document) {
			Document document = (Document)modelObject;
			form.getForm().setText(document.getName());
		} else if (modelObject instanceof ResourceNode){
			ResourceNode resource = (ResourceNode)modelObject;
			form.getForm().setText(resource.getName());
		} else if (modelObject instanceof File) {
			File file = (File)modelObject;
			form.getForm().setText(file.getName());
		}
	}

	protected IManagedForm getForm() {
		return form;
	}

	protected FormContents getFormContents() {
		return formContents;
	}

	protected void setForm(IManagedForm form) {
		this.form = form;
	}

	protected void setFormContents(FormContents formContents) {
		this.formContents = formContents;
	}
}
