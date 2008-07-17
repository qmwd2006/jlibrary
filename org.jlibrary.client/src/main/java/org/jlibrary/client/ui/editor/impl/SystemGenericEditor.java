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

import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.editor.forms.ContentsFormPage;
import org.jlibrary.core.entities.Node;

/**
 * @author martin
 *
 * This is an editor used to open all the external files. As the files are 
 * rendered on an external application, this editor does not need a contents
 * section
 */
public class SystemGenericEditor extends GenericEditor {
	
	public static final String EDITOR_ID = "org.jlibrary.client.ui.editor.impl.SystemGenericEditor";
	
	/**
	 * @see org.jlibrary.client.ui.editor.GenericEditor#createContentsPage()
	 */
	protected ContentsFormPage createContentsPage() {

		return null;	
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
