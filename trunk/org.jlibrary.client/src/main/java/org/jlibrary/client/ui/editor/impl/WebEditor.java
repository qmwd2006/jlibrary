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

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.editor.IURLEditorInput;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.editor.URLEditorInput;
import org.jlibrary.client.ui.editor.editors.xml.XMLEditor;
import org.jlibrary.client.ui.editor.forms.WebFormPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Generic editor to navigate through HTML pages. 
 * 
 * It will only contain a browser and the source code pages 
 */
public class WebEditor extends JLibraryEditor {

	static Logger logger = LoggerFactory.getLogger(WebEditor.class);
	
	private XMLEditor xmlEditor;
	
	public static final String WEB_SOURCE = "web_source";	
	public static final String CONTENTS = "editor_contents";
	public static final String RELATIONS = "editor_relations";
	public static final String PROPERTIES = "editor_properties";
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#addPages()
	 */
	final protected void addPages() {

		try {
			
			addPage(new WebFormPage(this,Messages.getMessage(CONTENTS)));
			xmlEditor = new XMLEditor(false,this);
			int index = addPage(xmlEditor,getEditorInput());
			setPageText(index,Messages.getMessage(WEB_SOURCE));
			
		} catch (PartInitException e) {
			
            logger.error(e.getMessage(),e);
		}			
	}
	
	public Image getTitleImage() {
		
		return SharedImages.getImage(SharedImages.IMAGE_HTML);
	}
	
	public String getTitle() {
		
		if (getModel() == null) {
			return Messages.getMessage("blank_page");
		}
		return super.getTitle();
	}
	
	public String getTitleToolTip() {

		if (getModel() == null) {
			return Messages.getMessage("blank_page_description");
		}
		return super.getTitleToolTip();		
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {}

	public Object getModel() {
		
		URLEditorInput editorInput = (URLEditorInput)getEditorInput();
		if (editorInput == null) {
			return null;			
		}
		return editorInput.getURL();
	}
	
	protected void initModel() {
		setModel(((IURLEditorInput)getEditorInput()).getURL());
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.checkCanSave()
	 */	
	public boolean checkCanSave() {

		return false;
	}
}
