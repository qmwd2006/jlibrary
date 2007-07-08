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

/**
 * @author $Author: perez_martin $
 *
 * Editor to open categories. It has only the metadata tab
 */

import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.CategoryEditorInput;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.editor.RelationsEditor;
import org.jlibrary.client.ui.editor.forms.CategoryMetadataFormPage;
import org.jlibrary.client.ui.editor.forms.ContentsFormPage;
import org.jlibrary.client.ui.editor.forms.MetadataFormPage;
import org.jlibrary.core.entities.Category;

public class CategoryEditor extends GenericEditor
{
	public static final String EDITOR_ID = "org.jlibrary.client.ui.editor.impl.CategoryEditor";

	private CategoryMetadataFormPage categoryPage;
	
	/**
	 * @see org.jlibrary.client.ui.editor.GenericEditor#createContentsPage()
	 */
	protected ContentsFormPage createContentsPage() {
		
		return null;	
	}
	
	protected MetadataFormPage createMetadataPage() {
		
		categoryPage = new CategoryMetadataFormPage(this,Messages.getMessage(PROPERTIES));
		return categoryPage;
	}
	
	protected RelationsEditor createRelationsEditor() {

		return null;
	}

	protected void initModel()
	{
		setModel(((CategoryEditorInput)getEditorInput()).getCategory());
	}

	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.getTitleImage()
	 */
	public Image getTitleImage()
	{
		return getEditorInput().getImageDescriptor().createImage();
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#getTitle()
	 */
	public String getTitle() {

		try
		{
			Category category = (Category)getModel();
			if (category.isUnknownCategory()) {
				return Messages.getMessage(Category.UNKNOWN_NAME);
			}
			else
				return ((Category)getModel()).getName();
		}
		catch (Exception e)
		{
			return super.getTitle();
		}
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.checkCanSave()
	 */	
	public boolean checkCanSave() {
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Category category = (Category)getModel();
		
		if (!securityManager.canPerformAction(
				category.getRepository(),
				SecurityManager.SAVE_CATEGORY)) {
			return false;
		}			
		return true;
	}
}
