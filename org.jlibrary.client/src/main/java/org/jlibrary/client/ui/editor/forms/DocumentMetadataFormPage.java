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
package org.jlibrary.client.ui.editor.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.ResourceNode;

public class DocumentMetadataFormPage extends MetadataFormPage {

	private DocumentFormMetadata formMetadata;

	public DocumentMetadataFormPage(JLibraryEditor editor, String title) {
		super(editor, title);
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(final IManagedForm form) {
		
		Composite body = form.getForm().getBody();
		form.getForm().setText(Messages.getMessage("editor_document_properties"));

		JLibraryEditor jEditor = (JLibraryEditor)getEditor();
		Document document = (Document)jEditor.getModel();
		formMetadata = new DocumentFormMetadata(this,document,body);
		
		form.addPart(formMetadata.getMetadataSection());
		form.addPart(formMetadata.getKeywordsSection());
		form.addPart(formMetadata.getCategoriesSection());
		form.addPart(formMetadata.getResourcesSection());
		form.addPart(formMetadata.getNotesSection());
	}

	
	public void initialize(FormEditor editor) {
		
		super.initialize(editor);		
	}
	
	/**
	 * Returns the categories section
	 * 
	 * @return CategoriesSection Categories section
	 */
	public CategoriesSection getCategoriesSection() {
		
		if (formMetadata != null) {
			return formMetadata.getCategoriesSection();
		}
		return null;
	}
	
	/**
	 * Returns the resources section
	 * 
	 * @return ResourcesSection resources section
	 */
	public ResourcesSection getResourcesSection() {
		
		if (formMetadata != null) {
			return formMetadata.getResourcesSection();
		}
		return null;
	}
	
	/**
	 * Returns the relations section
	 * 
	 * @return RelationsSection relations section
	 */
	public RelationsSection getRelationsSection() {
		
		if (formMetadata != null) {
			return formMetadata.getRelationsSection();
		}
		return null;
	}	
	
	public NotesSection getNotesSection() {
		
		if (formMetadata != null) {
			return formMetadata.getNotesSection();
		}
		return null;
	}
	
	public void setFocus() {
		
		if (formMetadata != null) {
			formMetadata.setFocus();
		}		
	}
	
	public void propertiesModified() {

		MetadataSection metadataSection = formMetadata.getMetadataSection();
		KeywordsSection keywordsSection = formMetadata.getKeywordsSection();
		
		Document document = (Document)getModel();
		
		if (metadataSection.getAuthorText().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_author"));
		} else if (metadataSection.getDescription().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_description"));
		} else if (metadataSection.getName().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_name"));
		} else if (metadataSection.getPosition().equals("")) {
			formMetadata.showWarning(Messages.getMessage("warning_position"));
		} else {
			formMetadata.showWarning("");
			//saveManager.addEditor((JLibraryEditor)getEditor(),document);

			document.getMetaData().setTitle(metadataSection.getTitle());
			document.getMetaData().setUrl(metadataSection.getUrl());
			document.getMetaData().setAuthor(metadataSection.getAuthor());			
			document.getMetaData().setKeywords(keywordsSection.getKeywords());
			document.setName(metadataSection.getName());
			document.setDescription(metadataSection.getDescription());
			document.setImportance(new Integer(metadataSection.getImportance()));
			document.getMetaData().setLanguage(metadataSection.getLanguage());
			document.setTypecode(metadataSection.getTypecode());
			document.setPosition(new Integer(metadataSection.getPosition()));			
		}	
		
		((JLibraryEditor)getEditor()).updateTitle();
		((JLibraryEditor)getEditor()).setDirty(true);
	}

	public void editorUpdated() {
		
		JLibraryEditor editor = (JLibraryEditor)getEditor();
		editor.setDirty(true);
		
		((JLibraryEditor)getEditor()).updateTitle();
	}
	
	public void closeEditors(ResourceNode resource) {
		
		super.closeEditors(resource);
	}

}