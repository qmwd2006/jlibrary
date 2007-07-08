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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.repository.wizard.UserDataPage;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This wizard will be used to change the password for the global admin user
 */
public class ChangePasswordWizard extends Wizard {

	static Logger logger = LoggerFactory.getLogger(ChangePasswordWizard.class);
	
    private UserDataPage userDataPage;
    private AdminPasswordDataPage adminPasswordDataPage;

    
    private boolean status = false;

    public ChangePasswordWizard() {
        
        super();
        
        setWindowTitle(Messages.getMessage("admin_wizard_title"));
        setNeedsProgressMonitor(true);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
        userDataPage = new UserDataPage(Messages.getMessage("admin_wizard_connect"),
        								Messages.getMessage("admin_wizard_connect_desc"),
										false);
        adminPasswordDataPage = new AdminPasswordDataPage(
        								Messages.getMessage("admin_wizard_change"),
										Messages.getMessage("admin_wizard_change_description"));
        
        addPage(userDataPage);
        addPage(adminPasswordDataPage);
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        
		final ServerProfile serverProfile = userDataPage.getServerProfile();

		final String newPassword = adminPasswordDataPage.getPassword();
		
		final Ticket ticket = userDataPage.getTicket();
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, 
															 InterruptedException {

				monitor.beginTask(Messages.getMessage("admin_wizard_job"),2);
				monitor.internalWorked(1);
				try {
					ticket.getUser().setPassword(newPassword);
					UserProperties userProperties = 
						ticket.getUser().dumpProperties();
					SecurityService service = 
						JLibraryServiceFactory.getInstance(serverProfile).getSecurityService();
					service.updateUser(ticket,userProperties);	
					monitor.internalWorked(1);
					monitor.done();
					status = true;
				} catch (final AuthenticationException ae) {
					getShell().getDisplay().asyncExec(showError(Messages.getMessage("security_exception")));
				    status = false;
				} catch (final SecurityException se) {		    
					getShell().getDisplay().asyncExec(showError(Messages.getMessage("security_exception")));
				    status = false;
				} catch (final Exception e) {		    
					getShell().getDisplay().asyncExec(showError(e.getMessage()));
				    status = false;				    
				} finally {
					monitor.internalWorked(1);
					monitor.done();
				}
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

		return status;
    }
    
	private Runnable showError(final String message) {
		
		return new Runnable() {
			public void run() {
				adminPasswordDataPage.setMessage(message, IMessageProvider.ERROR);
			}
		};
	}

}
