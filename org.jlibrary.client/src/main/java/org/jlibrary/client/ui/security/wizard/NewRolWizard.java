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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.RolProperties;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.RoleAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nicolasjouanin
 *
 * Wizard for creating new roles
 */
public class NewRolWizard extends Wizard
{
	static Logger logger = LoggerFactory.getLogger(NewRolWizard.class);
	
    private RolWizardDescriptionPage descriptionDataPage;

    private Ticket ticket;
    private Repository repository;
	private Rol rol;

    public NewRolWizard(Repository repository)
    {
        
        super();
        this.repository = repository;
        this.ticket = repository.getTicket();
        
        setWindowTitle(Messages.getMessage("new_rol_wizard_title"));
        setNeedsProgressMonitor(true);
    }
    
    public void addPages()
    {
        
    	descriptionDataPage = new RolWizardDescriptionPage(
            Messages.getMessage("new_rol_wizard_name"),
            Messages.getMessage("new_rol_wizard_description"));
                        
        addPage(descriptionDataPage);
    }
 
	public boolean performFinish()
	{
		if (repository == null)
		{
			return false;
		}
		SecurityService ss = JLibraryServiceFactory.getInstance(
				repository.getServerProfile()).getSecurityService();

		
		logger.info("Trying to create a new rol");
		try
		{			
			RolProperties rolProperties = new RolProperties();
			rolProperties.put(RolProperties.ROL_NAME, 
							  new PropertyDef(RolProperties.ROL_NAME, descriptionDataPage.getName()));
			rolProperties.put(RolProperties.ROL_DESCRIPTION, 
					  new PropertyDef(RolProperties.ROL_DESCRIPTION, descriptionDataPage.getRolDescription()));
			
			rolProperties.put(RolProperties.ROL_REPOSITORY, 
					  new PropertyDef(RolProperties.ROL_REPOSITORY, repository.getId()));
			
			rol = ss.createRol(ticket,rolProperties);
		} catch (RoleAlreadyExistsException uaee) {
			showError(Messages.getAndParseValue("rol_already_exists",
												"%1",
												descriptionDataPage.getName()));
			return false;
		} catch (final SecurityException se) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
			{
				public void run() {
					ErrorDialog.openError(new Shell(),
							  "ERROR",
							  Messages.getMessage("security_exception"),
							  new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
				}
			});	
		}
		catch (Exception e)
		{
			logger.error("Can't create new rol", e);
			MessageDialog.openInformation(getShell(),
					Messages.getMessage("error_create_rol"),
					Messages.getMessage("error_create_rol"));
		}
		
		return true;
	}
	
	private void showError(String message) {
		
		descriptionDataPage.setMessage(message, IMessageProvider.ERROR);
	}	
	
	/**
	 * Returns the new created rol
	 * 
	 * @return New created rol
	 */
	public Rol getNewRol() {
		
		return rol;
	}

}
