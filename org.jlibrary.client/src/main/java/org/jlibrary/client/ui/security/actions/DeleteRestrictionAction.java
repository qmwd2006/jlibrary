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
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.client.ui.security.views.RestrictionsView;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Restriction;
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
 * This action will be called to delete a restriction
 */
public class DeleteRestrictionAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(DeleteRestrictionAction.class);
	
	/**
	 * Constructor
	 */
	public DeleteRestrictionAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_delete_restriction"));
		setToolTipText(Messages.getMessage("tooltip_delete_restriction"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_DISABLED));
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
		
		RepositoryView repositoryView = RepositoryView.getInstance();
		if (repositoryView == null) {
			return false;
		}
		
		Node currentNode = repositoryView.getCurrentNode();
		if (currentNode == null) {
			return false;
		}
		
		if (!securityManager.canPerformAction(
				currentNode.getRepository(),
				currentNode,
				SecurityManager.DELETE_RESTRICTION)) {
			return false;
		}	
		
		return true;
	}
	
	public void run() {
		
		IStructuredSelection selection = (IStructuredSelection)getSelection();
		if (!selection.isEmpty()) {
			run(selection.toArray());
		}
	}
	
	public void run(Object[] members) {
		
		Repository repository = RepositoryView.getInstance().getCurrentRepository();
		if (repository == null) {
			return;
		}
		Ticket ticket = repository.getTicket();
		
		logger.info("Removing restrictions");
		
		RepositoryView repositoryView = RepositoryView.getInstance();
		if (repositoryView == null) {
			return;
		}
		
		SecurityService ss = JLibraryServiceFactory.getInstance(repository.getServerProfile()).getSecurityService();
		Node nodeReference = repositoryView.getCurrentNode();
		
		// Always get the reference from the registry
		Node node = (Node)EntityRegistry.getInstance().getNode(
				nodeReference.getId(),nodeReference.getRepository());
		if (node == null) {
			logger.info("Invalid reference from Entity Registry");
			return;
		}
		
		for (int i = 0; i < members.length; i++) {
			try {
				Restriction restriction = (Restriction)members[i];
				Member member = MembersRegistry.getInstance().getMember(restriction.getMember());
				if (member instanceof User) {
					UserProperties userProperties = new UserProperties();
					userProperties.addProperty(UserProperties.USER_ID,member.getId());
					userProperties.put(UserProperties.USER_DELETE_RESTRICTION,
							   new PropertyDef(UserProperties.USER_DELETE_RESTRICTION, node.getId()));
					userProperties.put(UserProperties.USER_REPOSITORY,
							   new PropertyDef(UserProperties.USER_REPOSITORY, 
									   node.getRepository()));
					ss.updateUser(ticket,userProperties);
				} else {
					GroupProperties groupProperties = new GroupProperties();
					groupProperties.addProperty(GroupProperties.GROUP_ID,member.getId());
					groupProperties.put(GroupProperties.GROUP_DELETE_RESTRICTION,
							   new PropertyDef(GroupProperties.GROUP_DELETE_RESTRICTION, node.getId()));
					groupProperties.put(GroupProperties.GROUP_REPOSITORY,
							   new PropertyDef(GroupProperties.GROUP_REPOSITORY, 
									   node.getRepository()));
					ss.updateGroup(ticket,groupProperties);
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
			}
		}
		RestrictionsView.refresh();
	}	
}
