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
package org.jlibrary.client.ui.repository.wizard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.ui.dialogs.NewServerProfileDialog;
import org.jlibrary.core.entities.ServerProfile;

/**
 * @author Martin Perez
 *
 * user data page
 */
public class UserDataPage2 extends WizardPage {

	private Text repositoryText;
    private Text userText;
    private Text passwordText;
	private CCombo serverCombo;
	private Button serverButton;
	private Button autoConnectButton;
	
    
	private HashMap profiles = new HashMap();
	private int heightHint = 200;

	private static String lastProfile = "";
	
    public UserDataPage2(String pageName, 
    					String description) {
        
        super(pageName);
        setPageComplete(false);
        setDescription(description);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_CONNECT_WIZARD)); 
    }

    public UserDataPage2(String pageName, 
    					String description, 
						int heightHint) {
        
        this(pageName,description);
        this.heightHint = heightHint;
    }

    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(final Composite outer) {
            	
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = heightHint;
        outer.setLayoutData(data);

        Composite parent = new Composite(outer, SWT.NONE);
        
        GridLayout pageLayout = new GridLayout();
        pageLayout.numColumns = 3;
		pageLayout.verticalSpacing = 10;
        parent.setLayout(pageLayout);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = 140;
        parent.setLayoutData(data);
        
		Label labServer = new Label (parent, SWT.NONE);
		labServer.setText (Messages.getMessage("new_repository_dialog_server"));
		data = new GridData();
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		labServer.setLayoutData (data);
		
		serverCombo = new CCombo (parent, SWT.BORDER | SWT.WRAP);
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		data.widthHint = 350;
		data.horizontalSpan = 1;
		serverCombo.setLayoutData (data);
		serverCombo.setEditable(false);
		
		serverButton = new Button(parent, SWT.NONE);
		serverButton.setImage(SharedImages.getImage(
								SharedImages.IMAGE_SERVER_PROFILE));
		data = new GridData();
		serverButton.setLayoutData(data);

		Label labRepository = new Label (parent, SWT.NONE);
		labRepository.setText (Messages.getMessage("repository_wizard_repname"));
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		labRepository.setLayoutData (data);
		
		repositoryText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 2;
		data.widthHint = 350;
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		repositoryText.setLayoutData (data);
		
		
		Label labName = new Label (parent, SWT.NONE);
		labName.setText (Messages.getMessage("repository_wizard_user"));
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		labName.setLayoutData (data);
		
		userText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 2;
		data.widthHint = 350;
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		userText.setLayoutData (data);

		Label labPassword = new Label (parent, SWT.NONE);
		labPassword.setText (Messages.getMessage("repository_wizard_password"));
		data = new GridData ();
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		labPassword.setLayoutData (data);
		
		passwordText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 2;
		data.widthHint = 350;
		passwordText.setLayoutData (data);
		data.horizontalAlignment=GridData.FILL_HORIZONTAL;
		passwordText.setEchoChar('*');
				
		autoConnectButton = new Button(parent, SWT.CHECK | SWT.NONE);
		autoConnectButton.setText (Messages.getMessage(
				"repository_dialog_autoconnect"));
		data = new GridData();
		data.horizontalSpan = 3;
		autoConnectButton.setLayoutData(data);
		
		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
        
		userText.addModifyListener(modifyListener);
		passwordText.addModifyListener(modifyListener);
		repositoryText.addModifyListener(modifyListener);
		
		serverButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				
				NewServerProfileDialog fd = NewServerProfileDialog.getInstance(getShell());
				fd.open();
				if (fd.getReturnCode() == IDialogConstants.OK_ID) {
					ServerProfile profile = fd.getServerProfile();
					if (!profiles.values().contains(profile)) {
						profiles.put(profile.getName(),profile);
						serverCombo.add(profile.getName());
						ClientConfig.addProfile(profile);
					}
					lastProfile = profile.getName();
					serverCombo.select(serverCombo.indexOf(lastProfile));
				}
				checkButtonsEnabled();
			}
		});
		
		serverCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				lastProfile = serverCombo.getItem(serverCombo.getSelectionIndex());				
			}
		});
				
        setControl(parent);
        initData();
    }

	private void checkButtonsEnabled() {
        
        if (serverCombo.getSelectionIndex() == -1) {
        	setPageComplete(false);
            return;
        }
        if (userText.getText().trim().equals("")) {
        	setPageComplete(false);
            return;
        }
        if (passwordText.getText().trim().equals("")) {
        	setPageComplete(false);
            return;
        }
        if (repositoryText.getText().trim().equals("")) {
        	setPageComplete(false);
            return;
        }
        setPageComplete(true);
    }
    
	private void initData() {

		List profiles = ClientConfig.getServerProfiles();
		Iterator it = profiles.iterator();
		while (it.hasNext()) {
			ServerProfile profile = (ServerProfile)it.next();
			if (profile.getName().equals(ClientConfig.PROFILE_LOCAL_KEY)) {			
				serverCombo.add(Messages.getMessage(profile.getName()));
				this.profiles.put(Messages.getMessage(profile.getName()),profile);			
			} else {
				serverCombo.add(profile.getName());
				this.profiles.put(profile.getName(),profile);			
			}
		}
		
		int index = serverCombo.indexOf(lastProfile);
		if (index == -1) {
			index = 0;
			lastProfile = "";
		}
		serverCombo.select(index);
	}
	
    /**
     * @return Returns the serverProfile.
     */
    public ServerProfile getServerProfile() {
        
		final String profile = 
			serverCombo.getItem(serverCombo.getSelectionIndex());
    	return (ServerProfile)profiles.get(profile);
    }	
	
    public String getUsername() {
    	
    	return userText.getText();
    }
    
    public String getPassword() {
    	
    	return passwordText.getText();
    }
    
    public String getRepositoryName() {
    	
    	return repositoryText.getText();
    }
    
    public boolean isAutoConnect() {
        
        return autoConnectButton.getSelection();
    }    
}
