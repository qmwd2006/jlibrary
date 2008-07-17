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

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.ResourceNode;

/**
 * @author martin
 *
 * Reusable form for contents
 */
public class FormContents {

	private Browser browser;	
	private FileEditorInput editorInput;
	private Object modelObject;
	
	public FormContents(FileEditorInput editorInput,
						Object modelObject,
						Composite body) {
		
		this.editorInput = editorInput;
		this.modelObject = modelObject;
		
		createFormContent(body);
	}
	
	protected void createFormContent(Composite body) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		body.setLayout(gridLayout);
		
		browser = new Browser(body, SWT.NONE);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);
		
		initBrowser();
	}
	
	public void refreshBrowser() {
		
		if (browser.isDisposed()) {
			return;
		}
		browser.refresh();
	}
	
	public void initBrowser() {

		if (browser.isDisposed()) {
			return;
		}
		
		String path = editorInput.getFile().getFullPath().toString();
		
		if (modelObject instanceof Document) {
			browser.setUrl(path);
		} else if (modelObject instanceof ResourceNode){
			browser.setUrl(path);		
		} else if (modelObject instanceof File) {
			File file = (File)modelObject;
			browser.setUrl(file.getAbsolutePath());
		}		
	}
	
	public void setFocus() {
		
		if(browser != null)
			browser.setFocus();
	}
	
	protected Browser getBrowser() {
		
		return browser;
	}
	
	protected FileEditorInput getEditorInput() {
		
		return editorInput;
	}
}
