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

/**
 * @author $Author$
 *
 * Editor for authors. It has only the metadata tab
 */

import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.AuthorEditorInput;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.editor.RelationsEditor;
import org.jlibrary.client.ui.editor.forms.AuthorMetadataFormPage;
import org.jlibrary.client.ui.editor.forms.ContentsFormPage;
import org.jlibrary.client.ui.editor.forms.MetadataFormPage;
import org.jlibrary.core.entities.Author;

public class AuthorEditor extends GenericEditor
{
	public static final String EDITOR_ID = "org.jlibrary.client.ui.editor.impl.AuthorEditor";

	private AuthorMetadataFormPage authorPage;
	
	/**
	 * @see org.jlibrary.client.ui.editor.GenericEditor#createContentsPage()
	 */
	protected ContentsFormPage createContentsPage() {
		
		return null;	
	}
	
	protected MetadataFormPage createMetadataPage() {
		
		authorPage = new AuthorMetadataFormPage(this,Messages.getMessage(PROPERTIES));
		return authorPage;
	}
	
	protected RelationsEditor createRelationsEditor() {

		return null;
	}

	protected void initModel()
	{
		setModel(((AuthorEditorInput)getEditorInput()).getAuthor());
	}

	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#getTitle()
	 */
	public String getTitle()
	{
		try
		{
			Author author = ((Author)getModel());			
			return author.getName();
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
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(SecurityManager.SAVE_AUTHOR)) {
			return false;
		}			
		return true;		
	}	

}
