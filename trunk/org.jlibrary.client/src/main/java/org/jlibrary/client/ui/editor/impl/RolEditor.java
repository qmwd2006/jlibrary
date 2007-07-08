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
package org.jlibrary.client.ui.editor.impl;

import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.editor.RelationsEditor;
import org.jlibrary.client.ui.editor.RolEditorInput;
import org.jlibrary.client.ui.editor.forms.ContentsFormPage;
import org.jlibrary.client.ui.editor.forms.MetadataFormPage;
import org.jlibrary.client.ui.editor.forms.RolMetadataFormPage;
import org.jlibrary.core.entities.Rol;

/**
 * @author martin
 *
 * Editor to open roles. It has only the metadata tab
 */
public class RolEditor extends GenericEditor {

	public static final String EDITOR_ID = "org.jlibrary.client.ui.editor.impl.RolEditor";
	
	private RolMetadataFormPage rolPage;
	
	/**
	 * @see org.jlibrary.client.ui.editor.GenericEditor#createContentsPage()
	 */
	protected ContentsFormPage createContentsPage() {
		
		return null;	
	}
	
	protected MetadataFormPage createMetadataPage() {
		
		rolPage = new RolMetadataFormPage(this,Messages.getMessage(PROPERTIES));
		return rolPage;
	}
	
	protected RelationsEditor createRelationsEditor() {

		return null;
	}
	
	protected void initModel()
	{
		setModel(((RolEditorInput)getEditorInput()).getRol());
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#getTitle()
	 */
	public String getTitle()
	{
		try
		{
			Rol rol = (Rol)getModel();
			if ((rol.getName().equals(Rol.ADMIN_ROLE_NAME)) ||
				(rol.getName().equals(Rol.READER_ROLE_NAME)) ||
				(rol.getName().equals(Rol.PUBLISHER_ROLE_NAME)))
			{
				return Messages.getMessage(rol.getName());
			}
			else
				return ((Rol)getModel()).getName();
		}
		catch (Exception e)
		{
			return super.getTitle();
		}
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.getTitleImage()
	 */
	public Image getTitleImage()
	{
		return getEditorInput().getImageDescriptor().createImage();
	}

	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.checkCanSave()
	 */	
	public boolean checkCanSave() {
		
		Rol role = (Rol)getModel();
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				role.getRepository(),
				SecurityManager.SAVE_ROLE)) {
			return false;
		}			
		return true;		
	}	
}
