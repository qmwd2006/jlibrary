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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.GroupAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nicolasjouanin
 *
 * Wizard for creating new groups
 */
public class NewGroupWizard extends Wizard
{

	static Logger logger = LoggerFactory.getLogger(NewGroupWizard.class);
	
    private Repository repository;
    private GroupWizardDescriptionPage descriptionPage;
    private Group newGroup;

    public NewGroupWizard(Repository repository)
    {
        
        super();
        this.repository = repository;
        
        setWindowTitle(Messages.getMessage("new_group_wizard_title"));
        setNeedsProgressMonitor(true);
    }

    public void addPages()
    {
        
    	descriptionPage = new GroupWizardDescriptionPage(
            Messages.getMessage("new_group_wizard_descpage_title"),
            Messages.getMessage("new_group_wizard_descpage_description"));
                        
        addPage(descriptionPage);
    }

    public boolean performFinish()
	{
	    Ticket ticket = repository.getTicket();
		SecurityService ss = JLibraryServiceFactory.getInstance(
				repository.getServerProfile()).getSecurityService();
		
		
		logger.info("Trying to create a new group");
		
		try
		{
			GroupProperties groupProperties = new GroupProperties();
			groupProperties.put(GroupProperties.GROUP_NAME,
								new PropertyDef(GroupProperties.GROUP_NAME, descriptionPage.getNameText()));
			groupProperties.put(GroupProperties.GROUP_DESCRIPTION,
					new PropertyDef(GroupProperties.GROUP_DESCRIPTION, descriptionPage.getDescriptionText()));
			groupProperties.put(GroupProperties.GROUP_REPOSITORY,
					new PropertyDef(GroupProperties.GROUP_REPOSITORY, repository.getId()));
			
			newGroup = ss.createGroup(ticket,groupProperties);
		} catch (GroupAlreadyExistsException gaee) {
			showError(Messages.getAndParseValue("group_already_exists",
												"%1",
												descriptionPage.getName()));
			return false;			
		} catch (final SecurityException se) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(new Shell(),
							  "ERROR",
							  Messages.getMessage("security_exception"),
							  new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
				}
			});	
		} catch (Exception e) {
			logger.error("Can't create new group", e);
			MessageDialog.openInformation(getShell(),
										  Messages.getMessage("error_create_group"),
										  Messages.getMessage("error_create_group"));
		}
		
		return true;
	}

	private void showError(String message) {
		
		descriptionPage.setMessage(message, IMessageProvider.ERROR);
	}
	
	public Group getNewGroup() {
		return newGroup;
	}

}
