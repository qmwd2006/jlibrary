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

/**
 * @author Martin Perez
 *
 * The user will enter the new password on this page
 */
public class AdminPasswordDataPage extends WizardPage {

	private Text passwordText;
	private Text repeatPasswordText;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			
			checkButtonsEnabled();
		}
	};

	
    public AdminPasswordDataPage(String pageName, String description) {
        
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(false);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_CONNECT_WIZARD));        
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite outer) {
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.heightHint = 250;
        outer.setLayoutData(data);

        Composite parent = new Composite(outer, SWT.NONE);
        
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		parent.setLayout (gridLayout);

		Label labPassword = new Label (parent, SWT.NONE);
		labPassword.setText (Messages.getMessage("admin_wizard_password"));		
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labPassword.setLayoutData (data);

		passwordText = new Text (parent, SWT.BORDER);
		passwordText.setEchoChar('*');
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		passwordText.setLayoutData (data);
			
		Label labRepeat = new Label (parent, SWT.NONE);
		labRepeat.setText (Messages.getMessage("admin_wizard_repeat_password"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labRepeat.setLayoutData (data);
		
		repeatPasswordText = new Text (parent, SWT.BORDER);
		repeatPasswordText.setEchoChar('*');
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		repeatPasswordText.setLayoutData (data);
						
		passwordText.addModifyListener(modifyListener);
		repeatPasswordText.addModifyListener(modifyListener);
		
        setControl(parent);
    }
    

    
    private void checkButtonsEnabled() {
        
    	setPageComplete(false);

    	if (passwordText.getText().trim().equals("")) {
			setMessage(Messages.getMessage("admin_wizard_error_empty"), IMessageProvider.ERROR);
			return;
		}

    	if (repeatPasswordText.getText().trim().equals("")) {
			setMessage(Messages.getMessage("admin_wizard_error_repeat"), IMessageProvider.ERROR);
			return;
		}

		if (!passwordText.getText().equals(repeatPasswordText.getText())) {
			setMessage(Messages.getMessage("admin_wizard_error_notequal"), IMessageProvider.ERROR);
			return;
		}

		setMessage(Messages.getMessage("admin_wizard_finish"));
		setPageComplete(true);
    }

	public String getPassword() {
		
		return passwordText.getText();
	}
}
