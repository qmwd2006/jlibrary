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
package org.jlibrary.client.ui.editor.editors;

import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.jlibrary.client.ui.editor.JLibraryEditor;


public class SimpleEditor extends AbstractTextEditor {

	private boolean editable = true;
	private boolean markedDirty;
	private JLibraryEditor jLibraryEditor;
	
	public SimpleEditor(boolean editable, JLibraryEditor jLibraryEditor) {
		
		super();
		
		this.jLibraryEditor = jLibraryEditor;
		internal_init();
		this.editable = editable;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded() {
		
		return false;
	}
	
	public boolean isMarkedDirty() {
		
		return markedDirty;
	}
	
	/**
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#isEditable()
	 */
	public boolean isEditable() {
		
		return editable;
	}
	
	/**
	 * Initializes the document provider and source viewer configuration.
	 * Called by the constructor. Subclasses may replace this method.
	 */
	protected void internal_init() {
		configureInsertMode(SMART_INSERT, false);
		setDocumentProvider(new SimpleDocumentProvider(this));
	}


	public void setMarkedDirty(boolean markedDirty) {
		this.markedDirty = markedDirty;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#close(boolean)
	 */
	public void close(boolean save) {

		super.close(false);
	}
	
	public JLibraryEditor getJLibraryEditor() {
		return jLibraryEditor;
	}
}
