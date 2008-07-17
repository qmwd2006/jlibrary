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
package org.jlibrary.client.ui.editor.impl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.editor.editors.SimpleEditor;
import org.jlibrary.client.ui.editor.forms.ContentsFormPage;
import org.jlibrary.core.entities.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Editor to open all Text documents. It uses the standard Eclipse Text Editor
 * 
 * 
 * Feel free to replace this text editor with any other improved Text editor.
 */
public class TextEditor extends GenericEditor {

	static Logger logger = LoggerFactory.getLogger(TextEditor.class);
	
	private SimpleEditor textEditor;
	
	/**
	 * @see org.jlibrary.client.ui.editor.GenericEditor#createContentsPage()
	 */
	protected ContentsFormPage createContentsPage() {
		
		return null;
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.GenericEditor#addStartPages()
	 */
	protected void addStartPages() {

		try {
			textEditor = new SimpleEditor(true,this);
			int index = addPage(textEditor,getEditorInput());
			setPageText(index,Messages.getMessage(CONTENTS));
		} catch (PartInitException e) {
			
            logger.error(e.getMessage(),e);
		}			
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#pageChange(int)
	 */
	protected void pageChange(int index) {

		super.pageChange(index);
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		
		if (textEditor.isMarkedDirty()) {
			textEditor.doSave(null);
			textEditor.setMarkedDirty(false);
		}
		
		super.doSave(monitor);
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.checkCanSave()
	 */	
	public boolean checkCanSave() {
		
		Node node = (Node)getModel();
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				node.getRepository(),
				node,
				SecurityManager.SAVE_DOCUMENT)) {
			return false;
		}			
		return true;		
	}	
}
