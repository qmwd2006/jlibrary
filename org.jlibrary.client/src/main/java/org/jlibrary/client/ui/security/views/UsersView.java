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
package org.jlibrary.client.ui.security.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.ToolTipHandler;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.client.ui.security.UsersContextMenu;
import org.jlibrary.client.ui.security.actions.UsersActionGroup;
import org.jlibrary.client.ui.security.dnd.UserDragListener;
import org.jlibrary.client.ui.security.providers.UsersLabelProvider;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * View from a repository. It contents a table that will display users
 */
public class UsersView extends ViewPart {
	
	static Logger logger = LoggerFactory.getLogger(UsersView.class);
	
	private static UsersView instance;
	
	private Menu fContextMenu;
	private static UsersActionGroup fActionSet;

	private static ListViewer listViewer;
	private PageBook fViewerbook; 
	private Label fEmptyGroupsViewer;	
	
	public static final String VIEW_ID = "org.jlibrary.client.ui.security.views.UsersView";

	public UsersView() {
	
		instance = this;
	}
	
	public void createPartControl(Composite parent) {
		
		fViewerbook= new PageBook(parent, SWT.NULL);

		fEmptyGroupsViewer= new Label(fViewerbook, SWT.TOP | SWT.LEFT | SWT.WRAP);
		fEmptyGroupsViewer.setText(Messages.getMessage("restricted_list_view"));

		listViewer = new ListViewer(fViewerbook, 
								SharedImages.getImage(SharedImages.IMAGE_USER),
								SWT.MULTI | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		listViewer.setLabelProvider(new UsersLabelProvider());
		getViewSite().setSelectionProvider(listViewer);

		
		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(listViewer);
		
		ToolTipHandler handler = new ToolTipHandler(fViewerbook.getShell());
		handler.activateHoverHelp(listViewer.getTable());
				
		addDragAndDropSupport();
		fillActionBars();
		initEvents();
		fillData();
	}

	private void initEvents() {
		
		listViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				fActionSet.handleOpen(event);
			}
		});
	}	
	
	public void clearSelection() {
		
		listViewer.getTable().select(-1);
	}
	
	private void addDragAndDropSupport() {

		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		listViewer.addDragSupport(operations,types,new UserDragListener(listViewer));
	}

	private void clearView() {
		
		fViewerbook.showPage(listViewer.getControl());
		listViewer.setInput(Collections.EMPTY_LIST);
		return;		
	}
	
	private void fillData() {
		
		fActionSet.updateActionBars();
		
		Repository repository = JLibraryPlugin.getCurrentRepository();
		if (repository == null) {
			clearView();
			return;
		}

		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (securityManager.canPerformAction(
				repository.getId(),
				SecurityManager.VIEW_USERS)) {
			fViewerbook.showPage(listViewer.getControl());
			ServerProfile profile = JLibraryPlugin.getCurrentServerProfile(); 

			SecurityService securityService = 
				JLibraryServiceFactory.getInstance(profile).getSecurityService();
			Ticket ticket = JLibraryPlugin.getCurrentTicket();
			
			
			try {
				List users = new ArrayList(
						securityService.findAllUsers(ticket));
				
				Iterator it = users.iterator();
				while (it.hasNext()) {
					User user = (User) it.next();
					MembersRegistry.getInstance().addMember(user);
				}
				
				if (!(ticket.getUser().isAdmin())) {
					// Only remove this user from view if we aren't server administrator.
					// Otherwise we may be interested in change the Administrator user 
					// information
					users.remove(User.ADMIN_USER);
				}
				listViewer.setInput(users);
			} catch (SecurityException e) {
				
	            logger.error(e.getMessage(),e);
				ErrorDialog.openError(new Shell(),
						"ERROR",
						Messages.getMessage("users_load_error"),
						new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
				StatusLine.setErrorMessage(Messages.getMessage("users_load_error"));
				return;
			}
		} else {
			fViewerbook.showPage(fEmptyGroupsViewer);
		}
	}
	
	public static void refresh() {
		
		if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
			
			instance.fillData();
			listViewer.refresh();
		}
	}

	public static void refreshAndDeselect() {
		
		if (listViewer != null) {
			listViewer.getTable().deselectAll();
		}
		refresh();
	}
	
	public static void setInput(Object object) {
		
		if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
			
			listViewer.setInput(object);
		}
	}

	private void fillActionBars() {
		
		IActionBars actionBars= getViewSite().getActionBars();
		fActionSet = new UsersActionGroup(this);
		fActionSet.fillActionBars(actionBars);
		
		MenuManager manager = new UsersContextMenu(fActionSet,"#PopupUsers");
		getViewSite().registerContextMenu(manager, listViewer);
		
		fContextMenu = manager.createContextMenu(listViewer.getTable());
		listViewer.getTable().setMenu(fContextMenu);

	}		
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		
		if (listViewer != null && !listViewer.getControl().isDisposed()) {
			listViewer.getControl().setFocus();
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {

		if (fContextMenu != null && !fContextMenu.isDisposed())
			fContextMenu.dispose();
		
	}
	
	public static void userCreated(User user) {
		
		if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
			
			refresh();
		}
	}
	
	public static void userUpdated(User user) {
		
		if (instance != null) {
			if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
				
				listViewer.refresh();
			}
		}

	}
	
	public static void userDeleted(User user) {
		
		if (listViewer != null) {
			if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
				
				listViewer.remove(user);
			}
		}
	}

	
	public static User[] getSelectedUsers() {
		
		if ((listViewer == null) || (listViewer.getContentProvider() == null)) {
			return null;
		}
		
 		if (listViewer.getSelection().isEmpty()) {
			return null;
		}
 		User[] users = new User[listViewer.getTable().getSelection().length];
 		for (int i = 0; i < users.length; i++) {
			users[i] = (User)listViewer.getTable().getSelection()[i].getData();
		}
 		return users;
	}

	public static UsersView getInstance() {
		
		return instance;
	}
	
	
}