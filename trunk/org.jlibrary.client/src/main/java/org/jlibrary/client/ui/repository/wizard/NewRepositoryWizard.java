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
import java.net.ConnectException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RecentlyRemovedRepositoryException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Wizard for creating a new repository
 */
public class NewRepositoryWizard extends Wizard {

	static Logger logger = LoggerFactory.getLogger(NewRepositoryWizard.class);
	
    private UserDataPage userDataPage;
    private RepositoryDataPage repositoryDataPage;
    private Repository repository;
    
    private boolean status = false;

    public NewRepositoryWizard() {
        
        super();
        
        setWindowTitle(Messages.getMessage("new_repository_dialog_title"));
        setNeedsProgressMonitor(true);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
        userDataPage = new UserDataPage(Messages.getMessage("new_repository_wizard_user"),
        								Messages.getMessage("new_repository_wizard_user_desc"),
										false);
        repositoryDataPage = new RepositoryDataPage(
        								Messages.getMessage("new_repository_wizard_repository"),
										Messages.getMessage("new_repository_wizard_repository"));
        
        addPage(userDataPage);
        addPage(repositoryDataPage);
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        
		final String name = repositoryDataPage.getName();
		final String description = repositoryDataPage.getDescription();
		final User creator = repositoryDataPage.getUser();
		final ServerProfile serverProfile = userDataPage.getServerProfile();
		
		final Ticket ticket = userDataPage.getTicket();
		if (repositoryDataPage.isAutoConnect()) {
		    ticket.setAutoConnect(true);
		} else {
		    ticket.setAutoConnect(false);
		}
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, 
															 InterruptedException {

				monitor.beginTask(Messages.getMessage("new_repository_wizard_creating"),5);
				monitor.internalWorked(1);
				try {
					RepositoryService service = 
						JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
					monitor.internalWorked(1);
					repository = service.createRepository(ticket,name,description,creator);
					
					// Now log in in that repository with estándar admin credentials
					Credentials credentials = new Credentials();
					credentials.setUser(User.ADMIN_USER.getName());
					credentials.setPassword(User.ADMIN_USER.getPassword());
					Ticket newTicket = 
						JLibraryServiceFactory.getInstance(serverProfile).getSecurityService().login(
												credentials,
												repository.getName());
					monitor.internalWorked(1);
					newTicket.setAutoConnect(ticket.isAutoConnect());
					repository = service.findRepository(repository.getName(), 
														newTicket);
					repository.setConnected(true);
					repository.setServerProfile(serverProfile);
					repository.setTicket(newTicket);
					monitor.internalWorked(1);
				 	// Add repository to repository registry
				 	RepositoryRegistry.getInstance().addRepository(
				 			repository,repository.getName());
					
					status = true;
				} catch (final RecentlyRemovedRepositoryException rrre) {
					getShell().getDisplay().asyncExec(showError(
							Messages.getAndParseValue("repository_removed_exception",
														  "%1",
														  name)));
					status = false;					
				} catch (final RepositoryNotFoundException rnfe) {
					getShell().getDisplay().asyncExec(showError(rnfe.getMessage()));
				    status = false;
				} catch (RepositoryAlreadyExistsException raee) {
					getShell().getDisplay().asyncExec(showError(
						Messages.getAndParseValue("repository_exists_exception",
													  "%1",
													  name)));
					status = false;
				} catch (final AuthenticationException ae) {
					getShell().getDisplay().asyncExec(showError(Messages.getMessage("security_exception")));
				    status = false;
				} catch (final ConnectException ce) {		    
					getShell().getDisplay().asyncExec(showError(Messages.getMessage("security_exception")));
				    status = false;
				} catch (final UserNotFoundException ce) {		    
					getShell().getDisplay().asyncExec(showError(Messages.getMessage("security_exception")));
				    status = false;
				} catch (final SecurityException se) {		    
					getShell().getDisplay().asyncExec(showError(Messages.getMessage("security_exception")));
				    status = false;
				} catch (RepositoryException e) {
				    getShell().getDisplay().asyncExec(showError(Messages.getMessage("new_repository_dialog_can't_create")));
				    status = false;
				} finally {
					monitor.internalWorked(1);
					monitor.done();
				}
			}
		};		

		WizardDialog wd = (WizardDialog)getContainer();
		try {
			wd.run(true,true,runnable);
		} catch (InvocationTargetException e) {
			
            logger.error(e.getMessage(),e);
		} catch (InterruptedException e) {
			
            logger.error(e.getMessage(),e);
		}

		return status;
    }
    
    
    
    /**
     * @return Returns the repository.
     */
    public Repository getRepository() {
        return repository;
    }
    
	private Runnable showError(final String message) {
		
		return new Runnable() {
			public void run() {
				repositoryDataPage.setMessage(message, IMessageProvider.ERROR);
			}
		};
	}

}
