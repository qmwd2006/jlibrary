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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.providers.FileLabelProvider;

/**
 * @author martin
 *
 * <p>This is the wizard page to show up document's resources. The user will
 * be able to check if he wants to add resources, or if he do not want to 
 * add resources.</p>
 */
public class DocumentResourcesPage extends WizardPage {
	
	private ListViewer listResources;
	
	private Button ignoreResourcesButton;

	/**
	 * Inits this wizard
	 * 
	 * @param pageName Page name
	 * @param description Page description
	 */
	public DocumentResourcesPage(String pageName, String description) {
        
        super(pageName);
        setTitle(pageName);
        setPageComplete(true);
        setDescription(description);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_NEW_DOCUMENT_WIZARD));         
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = 250;
        parent.setLayoutData(data);

        Composite outer = new Composite(parent, SWT.NONE);
                
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		outer.setLayout (gridLayout);

		listResources = new ListViewer(outer,SharedImages.getImage(SharedImages.IMAGE_RESOURCES));
		listResources.setLabelProvider(new FileLabelProvider());
		
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		listResources.getControl().setLayoutData (data);

		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		ignoreResourcesButton = new Button(outer, SWT.CHECK);
		ignoreResourcesButton.setText(
				Messages.getMessage("new_document_wizard_ignore_resources"));
		ignoreResourcesButton.setLayoutData(data);
        setControl(outer);
    }

    /**
     * Returns all the resources needed by the document or an empty list if 
     * the document does not need any resources or if the user has choosen to 
     * do not add the resources.
     * 
     * @return Collection Resources that the document needs.
     */
	public Collection getResources() {
		
		if (!ignoreResourcesButton.getSelection()) {
			return listResources.getItems();
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	/**
	 * Adds a resource to the list of resources needed by the document
	 * 
	 * @param resource Path of the resource to be added
	 */
	public void addResource(String resource) {
		
		listResources.add(resource);
	}
}
