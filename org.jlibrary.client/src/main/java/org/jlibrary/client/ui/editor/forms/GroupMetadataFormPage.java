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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.ResourceNode;

/**
 * @author martin
 *
 * Metadata page for groups
 */
public class GroupMetadataFormPage extends MetadataFormPage {

	private GroupFormMetadata formMetadata;

	public GroupMetadataFormPage(JLibraryEditor editor, String title) {
		super(editor, title);
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(final IManagedForm form) {
		
		Composite body = form.getForm().getBody();
		form.getForm().setText(Messages.getMessage("editor_group_properties"));

		JLibraryEditor jEditor = (JLibraryEditor)getEditor();
		Group group = (Group)jEditor.getModel();
		formMetadata = new GroupFormMetadata(this,group,body);		
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

		GroupMetadataSection metadataSection = formMetadata.getMetadataSection();
		
		if (metadataSection.getDescription().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_description"));
		} else if (metadataSection.getName().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_name"));
		} else {
			formMetadata.showWarning("");

			Group group = (Group)getModel();
			if (!group.getName().equals(Group.ADMINS_GROUP_NAME) &&
				!group.getName().equals(Group.READERS_GROUP_NAME) &&
				!group.getName().equals(Group.PUBLISHERS_GROUP_NAME)) {
				
				group.setName(metadataSection.getName());
			}
			if (!group.getDescription().equals(Group.ADMINS_GROUP_DESCRIPTION) &&
				!group.getDescription().equals(Group.READERS_GROUP_DESCRIPTION) &&
				!group.getDescription().equals(Group.PUBLISHERS_GROUP_DESCRIPTION)) {
				
				group.setDescription(metadataSection.getDescription());
			}			
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