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

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.config.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Perez
 *
 * Wizard data page for import fields
 */
public class ImportDataPage extends WizardPage {

	static Logger logger = LoggerFactory.getLogger(ImportDataPage.class);
	
	private Text fileText;
	private Text nameText;
	private Button browseButton;
	private int heightHint;

    public ImportDataPage(String pageName, String description) {
        
        super(pageName);
        setTitle(pageName);
        setPageComplete(false);
        setMessage(description);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_IMPORT_REPOSITORY_WIZARD));
    }

    public ImportDataPage(String pageName, String description, int heightHint) {
        
        this(pageName,description);
        this.heightHint = heightHint;
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
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = heightHint;
        parent.setLayoutData(data);
        
		Label labFile = new Label (parent, SWT.NONE);
		labFile.setText (Messages.getMessage("import_wizard_file"));
		data = new GridData ();
		data.horizontalAlignment = GridData.END;
		labFile.setLayoutData (data);
		
		fileText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 350;
		data.horizontalSpan = 1;
		fileText.setLayoutData (data);
		
		browseButton = new Button(parent, SWT.NONE);
		browseButton.setImage(SharedImages.getImage(SharedImages.IMAGE_DIRECTORY));
		data = new GridData();
		browseButton.setLayoutData(data);
						
		Label labName = new Label (parent, SWT.NONE);
		labName.setText (Messages.getMessage("import_wizard_name"));
		data = new GridData ();
		data.horizontalAlignment = GridData.END;
		labName.setLayoutData (data);
		
		nameText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 350;
		data.horizontalSpan = 1;
		nameText.setLayoutData (data);
		
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
		
		fileText.addModifyListener(modifyListener);
		nameText.addModifyListener(modifyListener);
				
		browseButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent se) {
				
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				String filter = ClientConfig.getValue(ClientConfig.IMPORT_REPOSITORY);
				if (filter != null) {
					File f = new File(filter);
					fd.setFilterPath(f.getAbsolutePath());
				}
				
				fd.setFilterNames (new String [] {Messages.getMessage("jlibrary_extension"), 
						Messages.getMessage("all_extension")
				});
				fd.setFilterExtensions (new String [] {"*.jlib", "*.*"});		
				
				String result = fd.open();
				if (result == null) {
					// Cancel
					return;
				}
				ClientConfig.setValue(ClientConfig.IMPORT_REPOSITORY,result);
				fileText.setText(result);
			}
		});

        setControl(parent);
    }
    
    private void checkButtonsEnabled() {

        setPageComplete(false);
        if (fileText.getText().trim().equals("")) {
            return;
        }
        if (nameText.getText().trim().equals("")) {
            return;
        }       
        
        File file = new File(fileText.getText());
        
        if (!(file.exists())) {
        	setMessage(Messages.getMessage("import_wizard_file_not_found"), IMessageProvider.ERROR);
        } else if (file.isDirectory()) {
        	setMessage(Messages.getMessage("import_wizard_directory"), IMessageProvider.ERROR);        	
        } else if (!file.getName().endsWith(".jlib")) {
        	setMessage(Messages.getMessage("import_wizard_jlib"), IMessageProvider.ERROR);        	        	
        } else {
        	setMessage(Messages.getMessage("import_wizard_next"), IMessageProvider.INFORMATION);
        	setPageComplete(true);
        }    
    }
    
    public String getFilePath() {
    	
    	return fileText.getText();
    }
    
    public String getRepositoryName() {
    	
    	return nameText.getText();
    }
}
