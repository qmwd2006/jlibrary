/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.User;

/**
 * @author martin
 *
 * Metadata page for users
 */
public class UserMetadataFormPage extends MetadataFormPage {

	private UserFormMetadata formMetadata;

	public UserMetadataFormPage(JLibraryEditor editor, String title) {
		super(editor, title);
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(final IManagedForm form) {
		
		Composite body = form.getForm().getBody();
		form.getForm().setText(Messages.getMessage("editor_user_properties"));

		JLibraryEditor jEditor = (JLibraryEditor)getEditor();
		User user = (User)jEditor.getModel();
		formMetadata = new UserFormMetadata(this,user,body);		
	}

	
	public void initialize(FormEditor editor) {
		
		super.initialize(editor);		
	}
		
	public void setFocus() {
		
		if (formMetadata != null) {
			formMetadata.setFocus();
		}		
	}
	
	public void propertiesModified() {

		UserMetadataSection metadataSection = formMetadata.getMetadataSection();
		
		if (metadataSection.getName().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_name"));
		} else if (metadataSection.getPassword().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_password"));
		} else if (metadataSection.getConfirmPassword().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_confirm_password"));
		} else if (!metadataSection.getPassword().equals(
				   metadataSection.getConfirmPassword())) {
			formMetadata.showWarning(Messages.getMessage("warning_wrong_password"));
		} else {
			formMetadata.showWarning("");

			User user = (User)getModel();
			if (!user.getName().equals(User.ADMIN_NAME)) {
				user.setName(metadataSection.getName());
			}
			user.setPassword(metadataSection.getPassword());
			user.setFirstName(metadataSection.getFirstName());
			user.setLastName(metadataSection.getLastName());
			user.setEmail(metadataSection.getEmail());
		}	
		
		((JLibraryEditor)getEditor()).updateTitle();
		((JLibraryEditor)getEditor()).setDirty(true);
	}

	public void editorUpdated() {
		
		((JLibraryEditor)getEditor()).updateTitle();
	}
	
	public void closeEditors(ResourceNode resource) {
		
		super.closeEditors(resource);
	}

}