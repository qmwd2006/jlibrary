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

import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.jlibrary.client.Messages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Wizard for creating a new repository
 */
//TODO: Change this class's name
public class OpenRepositoryWizard2 extends Wizard {

	static Logger logger = LoggerFactory.getLogger(OpenRepositoryWizard2.class);
	
    private UserDataPage2 userDataPage;
    private Repository repository;
    
    private boolean status;
	private String repositoryName;
    
    public OpenRepositoryWizard2() {
        
        super();
        setWindowTitle(Messages.getMessage("open_repository_dialog_title"));
        setNeedsProgressMonitor(true);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
        userDataPage = new UserDataPage2(
        		Messages.getMessage("new_repository_wizard_user"),
        		Messages.getMessage("open_repository_wizard_user_desc"));
        addPage(userDataPage);
    }

	/**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        
    	final ServerProfile serverProfile = userDataPage.getServerProfile();    	
    	final String username = userDataPage.getUsername();
    	final String password = userDataPage.getPassword();
    	repositoryName = userDataPage.getRepositoryName();
		
		final boolean autoconnect = userDataPage.isAutoConnect();

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) 
					throws InvocationTargetException, 
						   InterruptedException {

				monitor.beginTask(Messages.getMessage(
						"open_repository_wizard_opening"),2);
				monitor.internalWorked(1);
				
				Repository testRepo = 
					RepositoryRegistry.getInstance().getRepositoryByName(repositoryName);
				if (testRepo !=  null) {
					if (testRepo.getServerProfile().equals(serverProfile)) {
						OpenRepositoryWizard2.this.repository = testRepo;
						status = true;
						monitor.internalWorked(1);
						monitor.done();
						return;
					}
				}
				
				try {
					Ticket ticket = null;
					try {
						SecurityService securityService = 
							JLibraryServiceFactory.getInstance(serverProfile).getSecurityService();
						Credentials credentials = new Credentials();
						credentials.setUser(username);

						if (username.equals(Messages.getMessage(User.ADMIN_NAME)) ||
								username.equals(User.ADMIN_KEYNAME)) {
						    credentials.setUser(User.ADMIN_NAME);
						}
						credentials.setPassword(password);
						ticket = securityService.login(
									credentials,repositoryName);

						// Move the selected profile to the first place on the combo box
						ClientConfig.moveToFirst(serverProfile);
					
					} catch (ConnectException ce) {
						getShell().getDisplay().asyncExec(showError(
								Messages.getMessage("connection_refused")));			
					} catch (UserNotFoundException e1) {
						getShell().getDisplay().asyncExec(showError(
								Messages.getAndParseValue(
										"repository_dialog_user_not_found",
										"%1",
										username)));
					} catch (RepositoryNotFoundException e) {
						getShell().getDisplay().asyncExec(showError(
								Messages.getAndParseValue(
										"repository_not_exists_exception",
										"%1",
										repositoryName)));
						throw new RepositoryNotFoundException(e.getMessage());
					} catch (AuthenticationException e1) {
						getShell().getDisplay().asyncExec(showError(
								Messages.getMessage(
									"repository_dialog_authentication_error")));
					} catch (SecurityException e1) {
			            logger.error(e1.getMessage(),e1);
						getShell().getDisplay().asyncExec(
								showError(e1.getMessage()));
					}			
					repository = loadRepository(ticket,
												repositoryName,
											    serverProfile);
					if (repository == null) {
						userDataPage.setMessage(
								Messages.getMessage("not_enough_permissions"), 
								IMessageProvider.ERROR);
						status = false;
					} else {
						if (autoconnect) {
						    ticket.setAutoConnect(true);
						} else {
						    ticket.setAutoConnect(false);
						}
						repository.setConnected(true);
						repository.setTicket(ticket);
						repository.setServerProfile(serverProfile);
						status = true;
					}
				} catch (SecurityException se) {
					userDataPage.setMessage(
							Messages.getMessage("not_enough_permissions"), 
							IMessageProvider.ERROR);
					status = false;
				} catch (RepositoryNotFoundException rnfe) {
	                logger.error(rnfe.getMessage(),rnfe);
					status = false;
				} catch (RepositoryException e) {
	                logger.error(e.getMessage(),e);
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
		} catch (Exception e) {
			
            logger.error(e.getMessage(),e);
		}		
		return status;
    }
    
    protected Repository loadRepository(Ticket ticket,
    									String name,
    									ServerProfile serverProfile) 
    										throws RepositoryNotFoundException,
    											   RepositoryException,
    										       SecurityException{

    	RepositoryService service = 
    		JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();

    	return service.findRepository(name,ticket);
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
				userDataPage.setMessage(message, IMessageProvider.ERROR);
			}
		};
	}

	/**
	 * Returns the real repository name. This is the name used to connect 
	 * to the repository and not the value returned by 
	 * <code>repository.getName()</code> method.
	 * 
	 * @return String repository name
	 */
	public String getRepositoryName() {

		return repositoryName;
	}    
}
