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
package org.jlibrary.client.ui.security.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.security.views.GroupsView;
import org.jlibrary.client.ui.security.views.RolesView;
import org.jlibrary.client.ui.security.views.UsersView;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to delete a rol
 */
public class DeleteRolAction extends SelectionDispatchAction {
	
	static Logger logger = LoggerFactory.getLogger(DeleteRolAction.class);
	
	/**
	 * Constructor
	 */
	public DeleteRolAction(IWorkbenchSite site) {
		
		super(site);
		setText(Messages.getMessage("item_delete_rol"));
		setToolTipText(Messages.getMessage("tooltip_delete_rol"));
		setImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_DELETE_ROL));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_DELETE_ROL_DISABLED));
	}
		
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
			
		if (selection.isEmpty()) {
			return false;
		}
					
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		if ((UsersView.getSelectedUsers() != null) || (GroupsView.getSelectedGroups() != null)) {
			return true;
		}
		
		Iterator it = selection.iterator();
		while (it.hasNext()) {
			Rol rol = (Rol)it.next();
			if (!securityManager.canPerformAction(
					rol.getRepository(),
					SecurityManager.DELETE_ROLE)) {
				return false;
			}	
			if ((rol.getName().equals(Rol.ADMIN_ROLE_NAME)) || 
				 rol.getName().equals(Rol.READER_ROLE_NAME) || 
				 rol.getName().equals(Rol.PUBLISHER_ROLE_NAME)) {
				
				return false;
			}
		}
		
		return true;
	}
	
	public void run() {
		
		IStructuredSelection selection = (IStructuredSelection)getSelection();
		if (!selection.isEmpty()) {
			run(selection.toArray());
		}
	}
	
	public void run(Object[] roles) {
		
		Repository repository = RepositoryView.getInstance().getCurrentRepository();
		if (repository == null) {
			return;
		}
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
	
		logger.info("Removing roles");
		
		SecurityService securityService = 
			JLibraryServiceFactory.getInstance(profile).getSecurityService();
		
		// Check if we have to delete a user or we have to remove it from a troup
		User[] users = UsersView.getSelectedUsers();
		Group[] groups = GroupsView.getSelectedGroups();
		boolean deleted = false;
		
		for (int i = 0; i < roles.length; i++) {
			Rol rol = (Rol)roles[i];
			JLibraryPlugin.closeEditors(getSite(),rol.getId());
			try {
				if (users == null) {
					if (groups == null) {
						// remove rol from repository
						securityService.removeRol(ticket,rol.getId());
						deleted = true;
					} else {
//						 remove rol from the groups
						for (int j = 0; j < groups.length; j++) {
							if (groups[j].getRoles().contains(rol)) {
								GroupProperties groupProperties = new GroupProperties();
								groupProperties.addProperty(GroupProperties.GROUP_ID,groups[j].getId());
								groupProperties.put(GroupProperties.GROUP_DELETE_ROL,
												   new PropertyDef(GroupProperties.GROUP_DELETE_ROL, rol));
								groupProperties.put(GroupProperties.GROUP_REPOSITORY,
										   new PropertyDef(GroupProperties.GROUP_REPOSITORY, rol.getRepository()));
								securityService.updateGroup(ticket,groupProperties);
								groups[j].getRoles().remove(rol);
								rol.getMembers().remove(groups[j]);
								deleted = true;
							}
						}
					}
				} else {
					// remove rol from the users
					for (int j = 0; j < users.length; j++) {
						if (users[j].getRoles().contains(rol)) {
							UserProperties userProperties = new UserProperties();
							userProperties.addProperty(UserProperties.USER_ID,users[j].getId());
							userProperties.put(UserProperties.USER_DELETE_ROL,
											   new PropertyDef(UserProperties.USER_DELETE_ROL, rol));
							userProperties.put(UserProperties.USER_REPOSITORY,
									   new PropertyDef(UserProperties.USER_REPOSITORY, rol.getRepository()));
							securityService.updateUser(ticket,userProperties);
							users[j].getRoles().remove(rol);
							if (rol.getMembers() != null) {
								rol.getMembers().remove(users[j]);
							}
							deleted = true;
						}
					}
				}
			} catch (final SecurityException se) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						ErrorDialog.openError(new Shell(),
								  "ERROR",
								  Messages.getMessage("security_exception"),
								  new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
						StatusLine.setErrorMessage(se.getMessage());
					}
				});	
			} catch (Exception e) {
                logger.error(e.getMessage(),e);
				continue;
			}
			if (deleted) {
				RolesView.rolDeleted(rol);
			}
		}
	}	
}
