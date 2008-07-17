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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Wizard for creating a new custom property within a repository
 */
public class NewPropertyWizard extends Wizard {

	static Logger logger = LoggerFactory.getLogger(NewPropertyWizard.class);
	
    private PropertyDescriptionDataPage descriptionDataPage;
    
    private Ticket ticket;
    private Repository repository;
    
    public NewPropertyWizard(Repository repository) {
        
        super();

        this.repository = repository;
        this.ticket = repository.getTicket();
        
        setWindowTitle(Messages.getMessage("new_directory_wizard_title"));
        setNeedsProgressMonitor(true);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
    	descriptionDataPage = new PropertyDescriptionDataPage(
            Messages.getMessage("repository_properties_custom"),
            Messages.getMessage("repository_properties_custom_description"));
                        
        addPage(descriptionDataPage);
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
    	
    	return createProperty();
    }
    
	private boolean createProperty() {
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, 
															 InterruptedException {
				try {
					int taskLength = 2;
					monitor.beginTask(Messages.getMessage("repository_properties_custom_job"),taskLength);
					monitor.internalWorked(1);					
					monitor.internalWorked(1);
					monitor.done();
				} catch (final Exception e) {
					
                    logger.error(e.getMessage(),e);
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							ErrorDialog.openError(new Shell(),
									  "ERROR",
									  Messages.getMessage("repository_properties_custom_error"),
									  new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
							StatusLine.setErrorMessage(Messages.getMessage("repository_properties_custom_error"));
						}
					});												
					return;
				}

				// Save preferences and update
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						
						StatusLine.setOKMessage(Messages.getMessage("repository_properties_custom_created"));
					}
				});																
			}
		};		
		
		
		WizardDialog wd = (WizardDialog)getContainer();
			try {
				wd.run(false,true,runnable);
			} catch (InvocationTargetException e) {			
				logger.error(e.getMessage(),e);
			} catch (InterruptedException e) {			
				logger.error(e.getMessage(),e);
			}
		
		return true;
	}
}
