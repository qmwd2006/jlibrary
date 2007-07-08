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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

/**
 * @author nicolasjouanin
 *
 * Wizard page for creating new roles
 */
public class RolWizardDescriptionPage extends WizardPage {

	private Text nameText;
	private Text descriptionText;

	private ModifyListener modifyListener = new ModifyListener()
	{
		public void modifyText(ModifyEvent e) {

			checkButtonsEnabled();
		}
	};

	public RolWizardDescriptionPage(String pageName, String description)
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
		
		Label labName = new Label (parent, SWT.NONE);
		labName.setText (Messages.getMessage("new_rol_dialog_name"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labName.setLayoutData (data);
		
		nameText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		nameText.setLayoutData (data);

		Label labDescription = new Label (parent, SWT.NONE);
		labDescription.setText (Messages.getMessage("new_rol_dialog_description"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labDescription.setLayoutData (data);
		
		descriptionText = new Text (parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		descriptionText.setLayoutData (data);
		
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
		
        setControl(parent);
	}
	   
	private void checkButtonsEnabled()
	{
        
    	setPageComplete(false);

		if (nameText.getText().equals("")) {
			setMessage(Messages.getMessage("new_rol_insert_name"), IMessageProvider.ERROR);
			return;
		}

		if (descriptionText.getText().equals("")) {
			setMessage(Messages.getMessage("new_rol_insert_description"), IMessageProvider.ERROR);
			return;
		}

		setMessage(getDescription());
		setPageComplete(true);
    }
	public String getName() {
		
		return nameText.getText();
	}
		
	public String getRolDescription() {
		
		return descriptionText.getText();
	}

}
