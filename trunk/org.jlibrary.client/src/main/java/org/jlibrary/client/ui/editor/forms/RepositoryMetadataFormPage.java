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
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Metadata page for repositories
 */
public class RepositoryMetadataFormPage extends MetadataFormPage {

	private RepositoryFormMetadata formMetadata;

	public RepositoryMetadataFormPage(JLibraryEditor editor, String title) {
		super(editor, title);
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(final IManagedForm form) {
		
		Composite body = form.getForm().getBody();
		form.getForm().setText(Messages.getMessage("editor_repository_properties"));

		JLibraryEditor jEditor = (JLibraryEditor)getEditor();
		Repository repository = (Repository)jEditor.getModel();
		formMetadata = new RepositoryFormMetadata(this,repository,body);		
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

		RepositoryMetadataSection metadataSection = formMetadata.getMetadataSection();
		RepositoryAdvancedSection advancedSection = formMetadata.getAdvancedSection();
		
		if (metadataSection.getDescription().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_description"));
		} else if (metadataSection.getName().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_name"));
		} else {
			formMetadata.showWarning("");
		}	
		
		Repository repository = (Repository)getModel();
		repository.setName(metadataSection.getName());
		repository.setDescription(metadataSection.getDescription());
		repository.getRepositoryConfig().setExtractMetadata(
				advancedSection.isMetadataExtractionEnabled());
		repository.getRepositoryConfig().setPhysicalDeleteDocuments(
				!advancedSection.isLogicalDeleteEnabled());
		repository.getRepositoryConfig().setEnabledLazyLoading(
				advancedSection.isLazyLoadingEnabled());
		
		((JLibraryEditor)getEditor()).updateTitle();
		((JLibraryEditor)getEditor()).setDirty(true);
		
	}

	public void editorUpdated() {
		
		((JLibraryEditor)getEditor()).updateTitle();
	}
}