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
package org.jlibrary.client.ui.repository.wizard;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

/**
 * @author martin
 *
 * Wizard page for creating new custom property
 */
public class PropertyDescriptionDataPage extends WizardPage {

	private Text keyText;
	private Text defaultValueText;
	private Button autoCreateButton;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			
			checkButtonsEnabled();
		}
	};

	private SelectionListener selectionListener = new SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			checkButtonsEnabled();
		};
	};
	
    public PropertyDescriptionDataPage(String pageName, String description) {
        
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(false);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_NEW_DIRECTORY_WIZARD));        
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
		
		Label labkey = new Label (parent, SWT.NONE);
		labkey.setText (Messages.getMessage("repository_properties_key"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labkey.setLayoutData (data);
		
		keyText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		keyText.setLayoutData (data);

		Label labDefaultValue = new Label (parent, SWT.NONE);
		labDefaultValue.setText (Messages.getMessage("repository_properties_default"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labDefaultValue.setLayoutData (data);
		
		defaultValueText = new Text (parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		defaultValueText.setLayoutData (data);
		
		Label labAutoCreate = new Label (parent, SWT.NONE);
		labAutoCreate.setText (Messages.getMessage("repository_properties_auto"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labAutoCreate.setLayoutData (data);
		
		autoCreateButton = new Button (parent, SWT.CHECK);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		autoCreateButton.setLayoutData (data);		
		
		keyText.addModifyListener(modifyListener);
		defaultValueText.addModifyListener(modifyListener);
        autoCreateButton.addSelectionListener(selectionListener);
		
        setControl(parent);
    }
    

    
    private void checkButtonsEnabled() {
        
    	setPageComplete(false);

		if (keyText.getText().equals("")) {
			setMessage(Messages.getMessage("new_directory_insert_name"), IMessageProvider.ERROR);
			return;
		}

		if (defaultValueText.getText().equals("")) {
			setMessage(Messages.getMessage("new_directory_insert_description"), IMessageProvider.ERROR);
			return;
		}

		setMessage(getDescription());
		setPageComplete(true);
    }
	
	public String getKey() {
		
		return keyText.getText();
	}
	
	public String getDefaultValue() {
		
		return defaultValueText.getText();
	}
	
	public boolean isAutoCreated() {
		
		return autoCreateButton.getSelection();
	}
}
