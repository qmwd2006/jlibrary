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
package org.jlibrary.client.ui.security.wizard;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nicolasjouanin
 *
 * Wizard for creating new users
 */
public class NewUserWizard extends Wizard
{

	static Logger logger = LoggerFactory.getLogger(NewUserWizard.class);
	
    private Ticket ticket;
    private Repository repository;
	private User newUser;

	private UserWizardDataPage dataPage;

	public NewUserWizard(Repository repository)
    {
        
        super();
        this.repository = repository;
        this.ticket = repository.getTicket();
        
        setWindowTitle(Messages.getMessage("new_user_wizard_title"));
        setNeedsProgressMonitor(true);
    }

    public void addPages()
    {
        
    	dataPage = new UserWizardDataPage(
            Messages.getMessage("new_user_wizard_datapage_title"),
            Messages.getMessage("new_user_wizard_datapage_description"));
                        
        addPage(dataPage);
    }
	public boolean performFinish()
	{
		ServerProfile profile = repository.getServerProfile();
		SecurityService ss = JLibraryServiceFactory.getInstance(profile).getSecurityService();
		
		
		logger.info("Trying to create a new user");
		
		String repositoryId = JLibraryPlugin.getCurrentRepository().getId();
		try {
			UserProperties userProperties = new UserProperties();
			userProperties.put(UserProperties.USER_NAME,
					new PropertyDef(UserProperties.USER_NAME, dataPage.getNickText()));
			userProperties.put(UserProperties.USER_FIRSTNAME,
					new PropertyDef(UserProperties.USER_FIRSTNAME, dataPage.getFirstName()));
			userProperties.put(UserProperties.USER_LASTNAME,
					new PropertyDef(UserProperties.USER_LASTNAME, dataPage.getLastName()));
			userProperties.put(UserProperties.USER_EMAIL,
					new PropertyDef(UserProperties.USER_EMAIL, dataPage.getEmail()));                        
			userProperties.put(UserProperties.USER_PASSWORD,
					new PropertyDef(UserProperties.USER_PASSWORD, dataPage.getPasswordText()));
			userProperties.put(UserProperties.USER_ADMIN,
					new PropertyDef(UserProperties.USER_ADMIN, Boolean.FALSE));
			userProperties.put(UserProperties.USER_REPOSITORY, 
					new PropertyDef(UserProperties.USER_REPOSITORY, repositoryId));

			newUser = ss.createUser(ticket,userProperties);
		} catch (UserAlreadyExistsException uaee) {
			showError(Messages.getAndParseValue("user_already_exists",
												"%1",
												dataPage.getNickText()));
			return false;
		} catch (Exception e) {
                        logger.error(e.getMessage(),e);
			MessageDialog.openInformation(getShell(),
                            Messages.getMessage("new_user_wizard_error_create_user_title"),
                            Messages.getMessage("new_user_wizard_error_create_user_text"));
		}
		
		return true;
	}

	private void showError(String message) {
		
		dataPage.setMessage(message, IMessageProvider.ERROR);
	}	
	
	public User getNewUser() {
		return newUser;
	}

}
