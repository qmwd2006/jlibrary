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

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.jlibrary.client.Messages;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RecentlyRemovedRepositoryException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Wizard for importing a new repository
 */
public class ImportRepositoryWizard extends Wizard {

	static Logger logger = LoggerFactory.getLogger(ImportRepositoryWizard.class);
	
	public static final String EXPORT_VERSION_KEY="EXPORT_VERSION";
	public static final String EXPORT_VERSION_1_0="1.0";
	
    private UserDataPage userDataPage;
    private ImportDataPage importDataPage;
    private Repository repository;
    
    private boolean status = false;
	private String repositoryName;
    
    public ImportRepositoryWizard() {
        
        super();
        setWindowTitle(Messages.getMessage("import_wizard_title"));
        setNeedsProgressMonitor(true);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
        userDataPage = new UserDataPage(
        		Messages.getMessage("new_repository_wizard_user"),
        		Messages.getMessage("new_repository_wizard_user_desc"),
				false,
				140);
        importDataPage = new ImportDataPage(
        		Messages.getMessage("import_wizard_repository"),
				Messages.getMessage("import_wizard_repository_desc"),
				140);
        
        addPage(importDataPage);
        addPage(userDataPage);
        
        userDataPage.setNextMessage(
        		Messages.getMessage("import_wizard_finish"));
    }
    
    /**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
	    
		repositoryName = importDataPage.getRepositoryName();
		final String filePath = importDataPage.getFilePath();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(filePath);
					monitor.beginTask(
							Messages.getMessage("import_wizard_progress"), 
							IProgressMonitor.UNKNOWN);
					ServerProfile serverProfile = 
						userDataPage.getServerProfile();

					Ticket ticket = userDataPage.getTicket();
					Credentials credentials = userDataPage.getCredentials();
					RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
	
					service.importRepository(ticket,repositoryName,fis);
					ticket = JLibraryServiceFactory.getInstance(serverProfile).getSecurityService().login(
							credentials,repositoryName);
					repository = service.findRepository(repositoryName, ticket);
					userDataPage.setTicket(ticket);
					repository.setConnected(true);
					repository.setServerProfile(serverProfile);
					repository.setTicket(ticket);
					
					status = true;
				} catch (final RecentlyRemovedRepositoryException rrre) {
					getShell().getDisplay().asyncExec(showError(
							Messages.getAndParseValue("repository_removed_exception",
														  "%1",
														  repositoryName)));
					status = false;														
				} catch (RepositoryAlreadyExistsException raee) {
					getShell().getDisplay().asyncExec(showError(
						Messages.getAndParseValue("repository_exists_exception",
												  "%1",repositoryName)));
					status = false;
				} catch (final SecurityException se) {		    
					getShell().getDisplay().asyncExec(showError(
							Messages.getMessage("security_exception")));
				    status = false;
				} catch (Exception e) {
	                logger.error(e.getMessage(),e);
				    getShell().getDisplay().asyncExec(showError(
				    		Messages.getMessage("error_import_repository")));
				    status = false;
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							logger.debug(e.getMessage(),e);
						}
					}
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
				userDataPage.setMessage(message, IMessageProvider.ERROR);
			}
		};
	}

	/**
	 * Returns the name that the user has choosen for this repository. 
	 * Take in mind that this name can be different from the original 
	 * repository name.
	 * 
	 * @return String Choosen name
	 */
	public String getRepositoryName() {
		
		return repositoryName;
	}
}
