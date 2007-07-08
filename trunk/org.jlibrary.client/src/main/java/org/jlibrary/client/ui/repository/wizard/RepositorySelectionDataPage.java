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
package org.jlibrary.client.ui.repository.wizard;

import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.RepositoryInfo;
import org.jlibrary.core.entities.Ticket;

/**
 * @author Martin Perez
 *
 * open repository data page
 */
public class RepositorySelectionDataPage extends WizardPage {

	private RepositoryInfo repositoryInfo;
	private Button autoConnectButton;
	private ListViewer viewer;
	private UserDataPage userDataPage;

    public RepositorySelectionDataPage(UserDataPage userDataPage,
    								   String pageName, 
									   String description) {
        
        super(pageName);
        setTitle(pageName);
        setPageComplete(false);
        setDescription(description);
        this.userDataPage = userDataPage;
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_NEW_REPOSITORY_WIZARD)); 
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite outer) {

        Composite parent = new Composite(outer, SWT.NONE);
        
        GridLayout pageLayout = new GridLayout();
        pageLayout.numColumns = 1;
		pageLayout.verticalSpacing = 10;

        parent.setLayout(pageLayout);
	
		viewer = new ListViewer(parent, SharedImages.getImage(
				SharedImages.IMAGE_OPEN_REPOSITORY));
		
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 100;
		data.grabExcessHorizontalSpace = true;
		viewer.getControl().setLayoutData(data);
        
		autoConnectButton = new Button(parent, SWT.CHECK | SWT.NONE);
		autoConnectButton.setText (Messages.getMessage(
				"repository_dialog_autoconnect"));
		data = new GridData();
		data.heightHint = 50;
		autoConnectButton.setLayoutData(data);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				repositoryInfo = (RepositoryInfo)((StructuredSelection)
						viewer.getSelection()).getFirstElement();
				String id = repositoryInfo.getId();
				if (event.getSelection().isEmpty()) {
					setPageComplete(false);
					setErrorMessage(null);
					return;
				}	
				Repository repository = 
					RepositoryRegistry.getInstance().getRepository(id); 
				if (repository != null) {
					// Debemos estar conectándonos con el mismo usuario
					Ticket ticket = userDataPage.getTicket();
					if (ticket.getUser().equals(
							repository.getTicket().getUser())) {					
						setMessage(Messages.getMessage(
								"open_repository_wizard_repository_opened"), IMessageProvider.INFORMATION);
						setPageComplete(false);
						return;
					}
				}
				setMessage(getDescription());
				setPageComplete(true);
			}
		});
		
        setControl(parent);
    }
    
    public boolean isAutoConnect() {
        
        return autoConnectButton.getSelection();
    }

	public void setRepositories(List repositories) {
		
		viewer.getTable().removeAll();
        if (repositories== null){
        	setMessage(Messages.getMessage(
        			"open_repository_wizard_error"), IMessageProvider.ERROR);
        } else if (repositories.size() == 0) {
			setMessage(Messages.getMessage(
					"open_repository_wizard_repository_empty"), IMessageProvider.ERROR);
		} else {
			setMessage(getDescription());
			viewer.add(repositories.toArray());
		}
	}
	
	/**
	 * @return Returns the repository.
	 */
	public RepositoryInfo getRepositoryInfo() {
		return repositoryInfo;
	}
}
