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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;

public class UserMetadataSection extends SectionPart {
	
	private Text firstNameText;
	private Text lastNameText;
	private Text nameText;
	private Text passwordText;
	private Text confirmPasswordText;
	private Text emailText;
	
	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
		
			formMetadata.propertiesModified();
		}
	};
	
	private User user;
	private UserFormMetadata formMetadata;
	
	public UserMetadataSection(FormToolkit toolkit,
							   UserFormMetadata formMetadata, 
							   Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED );
		
		this.formMetadata = formMetadata;
		this.user = formMetadata.getUser();
		getSection().clientVerticalSpacing = 4;
		getSection().setData("part", this);

		createClient(toolkit);
	}
	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("metadata_section"));
		section.setDescription(Messages.getMessage("metadata_section_description"));
	
		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.TOP);
		td.grabHorizontal = true;
		td.colspan = 2;
		section.setLayoutData(td);
	
		Composite sectionClient = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.verticalSpacing = 10;
		gridLayout.makeColumnsEqualWidth = true;

		sectionClient.setLayout(gridLayout);
		section.setClient(sectionClient);
		toolkit.paintBordersFor(sectionClient);
	
		Composite leftSection = toolkit.createComposite(sectionClient);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		leftSection.setLayoutData(data);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 10;
		leftSection.setLayout(layout);
		toolkit.paintBordersFor(leftSection);
		
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		
		Label labName = toolkit.createLabel(leftSection,"");
		labName.setText (Messages.getMessage("new_user_alias"));
		labName.setLayoutData (data);
		
		data = new GridData();
		data.widthHint = 200;
		data.horizontalSpan = 3;
		nameText = toolkit.createText(leftSection,"", SWT.WRAP | SWT.FLAT);
		toolkit.adapt(nameText, true, true);
		nameText.setLayoutData (data);
		nameText.setEnabled(false);
		if (formMetadata.canUpdate()) {
			nameText.addModifyListener(modifyListener);
		}

		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		
		Label labPassword = toolkit.createLabel(leftSection,"");
		labPassword.setText (Messages.getMessage("new_user_password"));
		labPassword.setLayoutData (data);
		
		data = new GridData();
		data.widthHint = 200;
		passwordText = toolkit.createText(
				leftSection,"", SWT.FLAT);
		toolkit.adapt(passwordText, true, true);
		passwordText.setLayoutData (data);
		passwordText.setEchoChar('*');
		if (formMetadata.canUpdate()) {
			passwordText.addModifyListener(modifyListener);
		}

		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		
		Label labConfirmPassword = toolkit.createLabel(leftSection,"");
		labConfirmPassword.setText (Messages.getMessage("new_user_confirm_password"));
		labConfirmPassword.setLayoutData (data);
		
		data = new GridData();
		data.widthHint = 200;
		confirmPasswordText = toolkit.createText(
				leftSection,"", SWT.FLAT);
		toolkit.adapt(confirmPasswordText, true, true);
		confirmPasswordText.setEchoChar('*');
		confirmPasswordText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			confirmPasswordText.addModifyListener(modifyListener);
		}
		
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;		

		Label labFirstName = toolkit.createLabel(leftSection,"");
		labFirstName.setText (Messages.getMessage("new_user_firstname"));
		labFirstName.setLayoutData (data);
		
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 200;
		firstNameText = toolkit.createText(leftSection,"", SWT.FLAT);
		firstNameText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			firstNameText.addModifyListener(modifyListener);
		}

		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;		

		Label labLastName = toolkit.createLabel(leftSection,"");
		labLastName.setText (Messages.getMessage("new_user_lastname"));
		labLastName.setLayoutData (data);
		
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 200;
		lastNameText = toolkit.createText(leftSection,"", SWT.FLAT);
		lastNameText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			lastNameText.addModifyListener(modifyListener);
		}
		
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;		

		Label labEmail = toolkit.createLabel(leftSection,"");
		labEmail.setText (Messages.getMessage("new_user_email"));
		labEmail.setLayoutData (data);
		
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.horizontalSpan = 3;
		data.widthHint = 200;
		emailText = toolkit.createText(leftSection,"", SWT.FLAT);
		emailText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			emailText.addModifyListener(modifyListener);
		}		
		
		if (user.equals(User.ADMIN_USER)) {
			nameText.setText(Messages.getMessage(User.ADMIN_NAME));
			if (user.getPassword() !=  null) {
				passwordText.setText(user.getPassword());
				confirmPasswordText.setText(user.getPassword());
			}
			
			Ticket ticket = JLibraryPlugin.getCurrentTicket();
			if (!ticket.getUser().equals(User.ADMIN_USER)) {
				passwordText.setEnabled(false);
			}
			if (!ticket.getUser().equals(User.ADMIN_USER)) {
				confirmPasswordText.setEnabled(false);
			}
			if (!ticket.getUser().equals(User.ADMIN_USER)) {
				emailText.setEnabled(false);
			}
			firstNameText.setText(Messages.getMessage(User.ADMIN_NAME));
			firstNameText.setEnabled(false);
			lastNameText.setText(Messages.getMessage(User.ADMIN_NAME));
			lastNameText.setEnabled(false);
		} else {
			if (user.getName() != null) {
				nameText.setText(user.getName());
			}
			if (user.getPassword() != null) {
				passwordText.setText(user.getPassword());
				confirmPasswordText.setText(user.getPassword());
			}			
			if (user.getFirstName() != null) {
				firstNameText.setText(user.getFirstName());
			}
			if (user.getLastName() != null){
				lastNameText.setText(user.getLastName());
			}
			if (user.getEmail() != null) {
				emailText.setText(user.getEmail());
			}
		}
		
		if (user.getEmail() != null) {
			emailText.setText(user.getEmail());
		}
		
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}

	public void dispose() {

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#setFocus()
	 */
	public void setFocus() {

		if (nameText != null) {
			nameText.setFocus();
		}	
	}

	public String getFirstName() {

		return firstNameText.getText();
	}
	
	public String getName() {
		
		return nameText.getText();
	}
	
	public String getLastName() {

		return lastNameText.getText();
	}

	public String getPassword() {

		return passwordText.getText();
	}

	public String getConfirmPassword() {

		return confirmPasswordText.getText();
	}
	
	public String getEmail() {

		return emailText.getText();
	}	
}
