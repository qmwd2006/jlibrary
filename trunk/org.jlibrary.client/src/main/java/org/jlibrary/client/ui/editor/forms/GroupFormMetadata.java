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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.jlibrary.client.ui.editor.JLibraryFormPage;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.ResourceNode;

/**
 * @author martin
 *
 * Reusable form for metadata
 */
public class GroupFormMetadata {

	private boolean init;

	private Label labWarning;
	private Group group;
	private GroupMetadataFormPage formPage;
	
	private GroupMetadataSection metadataSection;
	private GroupUsersSection usersSection;
	private GroupRolesSection rolesSection;
	
	public GroupFormMetadata(GroupMetadataFormPage formPage,
						     Group group,
						     Composite body) {
		
		
		this.formPage = formPage;
		this.group = group;
		createFormContent(body);
	}
	
	public GroupFormMetadata(Group group,
						     Composite body) {
		
		this.group = group;
		createFormContent(body);
	}
	
	protected void createFormContent(Composite body) {

		Display display = PlatformUI.getWorkbench().getDisplay();
		FormToolkit toolkit = new FormToolkit(display);
		

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.bottomMargin = 10;
		layout.verticalSpacing = 10;
		layout.makeColumnsEqualWidth = false;
		body.setLayout (layout);
		
		labWarning = new Label (body, SWT.NONE);
		labWarning.setImage(null);
		labWarning.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED));
		labWarning.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		TableWrapData td = new TableWrapData (TableWrapData.FILL_GRAB);
		td.colspan = 2;
		
		labWarning.setLayoutData (td);	
		
		td = new TableWrapData ();
		td.colspan = 2;
		Label labPath = toolkit.createLabel(body,"");
		labPath.setLayoutData(td);
		
		init = true; // Begin initialization, ignore changes on widgets
		createMetaDataSection(body, display, toolkit);
		createUsersSection(body, display, toolkit);
		createRolesSection(body, display, toolkit);
				
		init = false; // End of initialization
	}


	private void createMetaDataSection(Composite body, 
									   Display display, 
									   FormToolkit toolkit) {
		
		metadataSection = new GroupMetadataSection(toolkit,this,body);		
	}

	private void createUsersSection(Composite body, 
									Display display, 
									FormToolkit toolkit) {
		
		usersSection = new GroupUsersSection(toolkit,this,body);		
	}
	
	private void createRolesSection(Composite body, 
									Display display, 
									FormToolkit toolkit) {
		
		rolesSection = new GroupRolesSection(toolkit,this,body);		
	}	
	
	public void setFocus() {
		
		if (metadataSection != null) {
			metadataSection.setFocus();
		}
	}
	
	public boolean canUpdate() {
		
		return (formPage != null);
	}
	
	public JLibraryFormPage getFormPage() {
		
		return formPage;
	}
	
	public void propertiesModified() {

		if (!init) {
			if (formPage != null) {
				formPage.propertiesModified();
			}
		}
	}
	
	public void showWarning(String text) {
		
		if (text.equals("")) {
			labWarning.setImage(null);
		}
		labWarning.setText(text);
	}
	
	public Group getGroup() {
		
		return group;
	}

	public void editorUpdated() {

		if (formPage != null) {
			formPage.editorUpdated();
		}		
	}

	public void closeEditors(ResourceNode resource) {

		if (formPage != null) {
			formPage.closeEditors(resource);
		}			
	}
	
	public void setWarning(String text) {
		
		if ((labWarning != null) && (!labWarning.isDisposed())) {
			labWarning.setText(text);
		}
	}
	
	public GroupMetadataSection getMetadataSection() {
		
		return metadataSection;
	}
	
	public GroupUsersSection getUsersSection() {
		
		return usersSection;
	}
	
	public GroupRolesSection getRolesSection() {
		
		return rolesSection;
	}	
}
