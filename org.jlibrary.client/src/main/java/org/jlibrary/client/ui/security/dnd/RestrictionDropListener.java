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
package org.jlibrary.client.ui.security.dnd;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.security.views.RestrictionsView;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Restriction;
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
 * JFace restrictions drop adapter
 * 
 * @author martin
 */
public class RestrictionDropListener extends ViewerDropAdapter {

	static Logger logger = LoggerFactory.getLogger(RestrictionDropListener.class);
	
   public RestrictionDropListener(ListViewer viewer) {
	   
	   super(viewer);
   }
         
   /**
    * Method declared on ViewerDropAdapter
    */
   public boolean performDrop(Object data) {

		Object[] items = DNDItems.getItems();
		RepositoryView repositoryView = RepositoryView.getInstance();
		if (repositoryView == null) {
			return false;
		}
		Node nodeReference = repositoryView.getCurrentNode();
		
		// Always get the reference from the registry
		Node node = (Node)EntityRegistry.getInstance().getNode(
				nodeReference.getId(),nodeReference.getRepository());
		if (node == null) {
			
			logger.info("Invalid reference from Entity Registry");
			return false;
		}
		
		Ticket ticket = JLibraryPlugin.getCurrentTicket();
		ServerProfile profile = JLibraryPlugin.getCurrentServerProfile();
		SecurityService ss = 
			JLibraryServiceFactory.getInstance(profile).getSecurityService();
		try {
			for (int i = 0; i < items.length; i++) {
				Member member = (Member)items[i];
				if (existsMember(member)) {
					continue;
				}
				
				if (member instanceof User) {
					UserProperties userProperties = new UserProperties();
					userProperties.addProperty(UserProperties.USER_ID,member.getId());
					userProperties.put(UserProperties.USER_ADD_RESTRICTION,
							   new PropertyDef(UserProperties.USER_ADD_RESTRICTION, node.getId()));
					userProperties.put(UserProperties.USER_REPOSITORY,
							   new PropertyDef(UserProperties.USER_REPOSITORY, node.getRepository()));
					ss.updateUser(ticket,userProperties);
				} else {
					GroupProperties groupProperties = new GroupProperties();
					groupProperties.addProperty(GroupProperties.GROUP_ID,member.getId());
					groupProperties.put(GroupProperties.GROUP_ADD_RESTRICTION,
							   new PropertyDef(GroupProperties.GROUP_ADD_RESTRICTION, node.getId()));
					groupProperties.put(GroupProperties.GROUP_REPOSITORY,
							   new PropertyDef(GroupProperties.GROUP_REPOSITORY, node.getRepository()));
					ss.updateGroup(ticket,groupProperties);
				}
			}
			RestrictionsView.refresh();
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
		StructuredSelection selection = new StructuredSelection(node);
		getViewer().setSelection(selection);
		
		DNDItems.clear();
		
	   return true;
   }	
	
   /**
    * Method declared on ViewerDropAdapter
    */
   public boolean validateDrop(Object target, int op, TransferData type) {
	   
	   SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
	   boolean isValid = TextTransfer.getInstance().isSupportedType(type);
	   if (!isValid) {
		   return false;
	   }

		Object[] items = DNDItems.getItems();
		for (int i = 0; i < items.length; i++) {
			if (!(items[i] instanceof Member)) {
				return false;
			}
			if (!securityManager.canPerformAction(
					((Member)items[i]).getRepository(),
					SecurityManager.CREATE_RESTRICTION)) {
				return false;
			}
			
		}
	   return true;
   }		
   
    private boolean existsMember(Member member) {
	   
    	Collection data = (Collection)getViewer().getInput();
    	Iterator it = data.iterator();
    	while (it.hasNext()) {
    		Restriction restriction = (Restriction) it.next();
    		if (restriction.getMember().equals(member.getId())) {
    			return true;
    		}
    	}
    	return false;
    }
}
