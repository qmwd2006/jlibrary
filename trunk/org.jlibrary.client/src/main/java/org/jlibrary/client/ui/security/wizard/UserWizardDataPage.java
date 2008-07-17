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
package org.jlibrary.client.ui.security.wizard;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.entities.User;

/**
 * @author nicolasjouanin
 *
 * Wizard page for creating new users
 */
public class UserWizardDataPage extends WizardPage {

	private Text nickText;
	private Text passwordText;
	private Text confirmPasswordText;
	private Text firstNameText;
	private Text lastNameText;
	private Text emailText;

	public UserWizardDataPage(String pageName, String description)
	{
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(false);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_GENERIC_WIZARD));
    }

	public void createControl(Composite outer)
	{
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 10;
		outer.setLayout (gridLayout);
		
		Label label0 = new Label (outer, SWT.NONE);
		label0.setText (Messages.getMessage("new_user_alias"));
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		label0.setLayoutData (data);
		
		nickText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 120;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		nickText.setLayoutData (data);

		Label sep2 = new Label (outer, SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 2;
		sep2.setLayoutData(data);
		
		Label label2 = new Label (outer, SWT.NONE);
		label2.setText (Messages.getMessage("new_user_password"));
		
		passwordText = new Text (outer, SWT.BORDER);
		passwordText.setEchoChar('*');
		data = new GridData ();
		data.widthHint = 120;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		passwordText.setLayoutData (data);

		Label label4 = new Label (outer, SWT.NONE);
		label4.setText (Messages.getMessage("new_user_confirm_password"));
		
		confirmPasswordText = new Text (outer, SWT.BORDER);
		confirmPasswordText.setEchoChar('*');
		data = new GridData ();
		data.widthHint = 120;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		confirmPasswordText.setLayoutData (data);

		Label sep = new Label (outer, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData ();
		data.horizontalSpan = 4;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		sep.setLayoutData(data);

		Label label6 = new Label (outer, SWT.NONE);
		label6.setText (Messages.getMessage("new_user_firstname"));
		
		firstNameText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 120;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		firstNameText.setLayoutData (data);

		Label label8 = new Label (outer, SWT.NONE);
		label8.setText (Messages.getMessage("new_user_lastname"));
		
		lastNameText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 200;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		lastNameText.setLayoutData (data);
                
		Label label12 = new Label (outer, SWT.NONE);
		label12.setText (Messages.getMessage("new_user_email"));
		
		emailText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 120;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		emailText.setLayoutData (data);


		ModifyListener modifyListener = new ModifyListener()
		{
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
		
		nickText.addModifyListener(modifyListener);
		passwordText.addModifyListener(modifyListener);
		confirmPasswordText.addModifyListener(modifyListener);
		firstNameText.addModifyListener(modifyListener);
		lastNameText.addModifyListener(modifyListener);
		emailText.addModifyListener(modifyListener);
		
		setMessage(Messages.getMessage("new_user_required_fields"), IMessageProvider.INFORMATION);
		
		setControl(outer);
	}
	/**
	 * This method checks if the new user button can be enabled
	 */
	private void checkButtonsEnabled()
	{
		
    	setPageComplete(false);
		if (nickText.getText().equals("")) {
			setMessage(Messages.getMessage("new_user_required_user"), IMessageProvider.ERROR);
			return;
		}
		if (nickText.getText().equals(Messages.getMessage(User.ADMIN_NAME)) ||
			nickText.getText().equals(User.ADMIN_KEYNAME))
		{
			setMessage(Messages.getMessage("new_user_invalid_username"), IMessageProvider.ERROR);
			return;
		}
		if (passwordText.getText().equals("")) {
			setMessage(Messages.getMessage("new_user_required_password"), IMessageProvider.ERROR);
			return;
		}
		if (confirmPasswordText.getText().equals("")) {
			setMessage(Messages.getMessage("new_user_required_confirm"), IMessageProvider.ERROR);
			return;
		}
		
		if (!(passwordText.getText().equals(confirmPasswordText.getText()))) {
			setMessage(Messages.getMessage("new_user_password_error"), IMessageProvider.ERROR);
			return;
		}
		setMessage(getDescription());
    	setPageComplete(true);

		if(firstNameText.getText().equals(""))
		{
			setMessage(Messages.getMessage("new_user_firstname_warning"), IMessageProvider.WARNING);
			return;
		}
		if(lastNameText.getText().equals(""))
		{
			setMessage(Messages.getMessage("new_user_lastname_warning"), IMessageProvider.WARNING);
			return;
		}
		if(emailText.getText().equals(""))
		{
			setMessage(Messages.getMessage("new_user_email_warning"), IMessageProvider.WARNING);
			return;
		}
	}

	public String getEmail() {
		return emailText.getText().trim();
	}

	public String getFirstName() {
		return firstNameText.getText().trim();
	}

	public String getLastName() {
		return lastNameText.getText().trim();
	}

	public String getNickText() {
		return nickText.getText().trim();
	}

	public String getPasswordText() {
		return passwordText.getText().trim();
	}

}
