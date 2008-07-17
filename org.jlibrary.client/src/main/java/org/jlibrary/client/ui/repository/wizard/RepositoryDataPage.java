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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.entities.User;

/**
 * @author Martin Perez
 *
 * repository data page
 */
public class RepositoryDataPage extends WizardPage {

	private Text nameText;
	private Text descriptionText;
	private Text userText;
	private Button autoConnectButton;

    public RepositoryDataPage(String pageName, String description) {
        
        super(pageName);
        setTitle(pageName);
        setPageComplete(false);
        setMessage(description);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_NEW_REPOSITORY_WIZARD)); 
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite outer) {

        Composite parent = new Composite(outer, SWT.NONE);
        
        GridLayout pageLayout = new GridLayout();
        pageLayout.numColumns = 3;
		pageLayout.verticalSpacing = 10;
        parent.setLayout(pageLayout);
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.heightHint = 170;
        parent.setLayoutData(data);
        
		Label labUser = new Label (parent, SWT.NONE);
		labUser.setText (Messages.getMessage("new_repository_wizard_creator"));
		data = new GridData ();
		data.horizontalAlignment = GridData.END;
		labUser.setLayoutData (data);
		
		userText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		userText.setLayoutData (data);
		userText.setEditable(false);
		userText.setText(Messages.getMessage(User.ADMIN_NAME));
		
		Label labName = new Label (parent, SWT.NONE);
		labName.setText (Messages.getMessage("new_repository_dialog_name"));
		data = new GridData ();
		data.horizontalAlignment = GridData.END;
		labName.setLayoutData (data);
		
		nameText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		nameText.setLayoutData (data);
		
		
		Label labDescription = new Label (parent, SWT.NONE);
		labDescription.setText (Messages.getMessage("new_repository_dialog_description"));
		data = new GridData ();
		data.horizontalAlignment = GridData.END;
		labDescription.setLayoutData (data);
		
		descriptionText = new Text (parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		descriptionText.setLayoutData (data);
		
		descriptionText.addTraverseListener(new TraverseListener () {
			public void keyTraversed(TraverseEvent e) {
				switch (e.detail) {
					case SWT.TRAVERSE_TAB_NEXT:
						e.doit = true;
					case SWT.TRAVERSE_TAB_PREVIOUS: {
						nameText.setFocus();
						e.doit = true;
					}
				}
			}
		});
				
		autoConnectButton = new Button(parent, SWT.CHECK | SWT.NONE);
		autoConnectButton.setText (Messages.getMessage("repository_dialog_autoconnect"));
		data = new GridData();
		data.horizontalSpan = 3;
		autoConnectButton.setLayoutData(data);
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
		
		userText.addModifyListener(modifyListener);
		nameText.addModifyListener(modifyListener);
		descriptionText.addModifyListener(modifyListener);
				
		nameText.addFocusListener(new FocusAdapter() {
			private boolean noNameText = true;

			public void focusLost(FocusEvent e) {
				
				if (noNameText) {
					noNameText = false;
					if (descriptionText.getText().equals("")) {
						descriptionText.setText(nameText.getText());
						descriptionText.selectAll();
					}
				}
				
				super.focusLost(e);
			}
		});		
		
        setControl(parent);
    }
    
    private void checkButtonsEnabled() {

        setPageComplete(false);
        if (nameText.getText().trim().equals("")) {
            return;
        }
        if (descriptionText.getText().trim().equals("")) {
            return;
        }
        if (userText.getText().trim().equals("")) {
            return;
        }        
        setPageComplete(true);
    }
    
    public String getName() {
        
        return nameText.getText();
    }
    
    public String getDescription() {
        
        return descriptionText.getText();
    }
    
    public User getUser() {
        
        return User.ADMIN_USER;
    }
    
    public boolean isAutoConnect() {
        
        return autoConnectButton.getSelection();
    }
}
