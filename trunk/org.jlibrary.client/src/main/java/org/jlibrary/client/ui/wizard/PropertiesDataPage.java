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
package org.jlibrary.client.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

/**
 * @author martin
 *
 * Wizard page for creating new directories
 */
public class PropertiesDataPage extends WizardPage {

	private Text directoryText;
	
    public PropertiesDataPage(String pageName, String description) {
        
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
        
        FormToolkit toolkit = new FormToolkit(outer.getDisplay());
        Form form = toolkit.createForm(outer);
        form.getBody().setLayout(new GridLayout());
        
        String labelText = Messages.getMessage("first_time_html");
        FormText rtext = toolkit.createFormText(form.getBody(),true);
        GridData gd = new GridData();
        gd.widthHint = 500;
        rtext.setText(labelText,true,false);
        rtext.setLayoutData(gd);
        setControl(form);
        
		Composite sectionClient = toolkit.createComposite(form.getBody());
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 10;
		sectionClient.setLayout(gridLayout);
		toolkit.paintBordersFor(sectionClient);	
		
		GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.horizontalSpan = 1;
        toolkit.createLabel(sectionClient,Messages.getMessage("first_time_directory"));
		
        data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.horizontalSpan = 1;
        data.widthHint = 300;
		directoryText = toolkit.createText(sectionClient,"",SWT.FLAT);
		toolkit.adapt(directoryText, true, true);
		directoryText.setLayoutData(data);
		directoryText.setEditable(false);
		
		
		Button browseButton = toolkit.createButton(sectionClient,"",SWT.PUSH);
		browseButton.setImage(SharedImages.getImage(SharedImages.IMAGE_DIRECTORY));
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				
				DirectoryDialog dd = new DirectoryDialog(getShell());
				String dir = dd.open();
				if (dir == null) {
					directoryText.setText("");
					setPageComplete(false);
				} else {
					directoryText.setText(dir);
					setPageComplete(true);
				}
			}
		});
		setPageComplete(false);
    }
	
	public String getHomeDirectory() {
		
		return directoryText.getText();
	}	
}
