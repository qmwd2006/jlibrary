/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
import org.jlibrary.client.ui.security.views.RestrictionsView;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.GroupNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to delete a group
 */
public class DeleteGroupAction extends SelectionDispatchAction {
	
	static Logger logger = LoggerFactory.getLogger(DeleteGroupAction.class);
	
	/**
	 * Constructor
	 */
	public DeleteGroupAction(IWorkbenchSite site) {
		
		super(site);
		setText(Messages.getMessage("item_delete_group"));
		setToolTipText(Messages.getMessage("tooltip_delete_group"));
		setImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_DELETE_GROUP));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_DELETE_GROUP_DISABLED));
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
		
		Iterator it = selection.iterator();
		while (it.hasNext()) {
			Group group = (Group)it.next();
			if (!securityManager.canPerformAction(
					group.getRepository(),
					SecurityManager.DELETE_GROUP)) {
				return false;
			}	
			
			if ((group.getName().equals(Group.ADMINS_GROUP_NAME)) || 
				 group.getName().equals(Group.PUBLISHERS_GROUP_NAME) || 
				 group.getName().equals(Group.READERS_GROUP_NAME)) {
				
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
	
	public void run(Object[] groups) {
		
		Repository repository = RepositoryView.getInstance().getCurrentRepository();
		if (repository == null) {
			return;
		}
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		
		logger.info("Removing groups");
		
		SecurityService securityService = JLibraryServiceFactory.getInstance(profile).getSecurityService();
		
		for (int i = 0; i < groups.length; i++) {
			Group group = (Group)groups[i];
			JLibraryPlugin.closeEditors(getSite(),group.getId());
			try {
				securityService.removeGroup(ticket,group.getId());
			}  catch (GroupNotFoundException onfe) {
				continue;
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
				continue;
			}
			GroupsView.groupDeleted(group);
			RestrictionsView.refresh();
		}
		
	}	
}
