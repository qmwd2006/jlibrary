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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.security.RestrictionsContextMenu;
import org.jlibrary.client.ui.security.actions.RestrictionsActionGroup;
import org.jlibrary.client.ui.security.dnd.RestrictionDragListener;
import org.jlibrary.client.ui.security.dnd.RestrictionDropListener;
import org.jlibrary.client.ui.security.providers.RestrictionsLabelProvider;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * View from a repository. It contents a table that will display restrictions
 */
public class RestrictionsView extends ViewPart {
	
	static Logger logger = LoggerFactory.getLogger(RestrictionsView.class);
	
	private static RestrictionsView instance;
	
	private Menu fContextMenu;
	private static RestrictionsActionGroup fActionSet;
	private static ListViewer listViewer;
	
	private PageBook fViewerbook; 
	private Label fEmptyGroupsViewer;	
	
	public static final String VIEW_ID = "org.jlibrary.client.ui.security.views.RestrictionsView";

	public RestrictionsView() {
	
		instance = this;
	}
	
	public void createPartControl(Composite parent) {
		
		fViewerbook= new PageBook(parent, SWT.NULL);

		fEmptyGroupsViewer= new Label(fViewerbook, SWT.TOP | SWT.LEFT | SWT.WRAP);
		fEmptyGroupsViewer.setText(Messages.getMessage("restricted_list_view"));

		listViewer = new ListViewer(fViewerbook, 
                    SharedImages.getImage(SharedImages.IMAGE_RESTRICTION),
                    SWT.MULTI | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		listViewer.setLabelProvider(new RestrictionsLabelProvider(SharedImages.getImage(SharedImages.IMAGE_RESTRICTION)));
		getViewSite().setSelectionProvider(listViewer);

		
		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(listViewer);
		
		ToolTipHandler handler = new ToolTipHandler(fViewerbook.getShell());
		handler.activateHoverHelp(listViewer.getTable());
		

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {
				
			}
		});
				
		addDragAndDropSupport();
		fillActionBars();
		fillRestrictions();
	}

	private void addDragAndDropSupport() {

		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		listViewer.addDragSupport(operations,types,new RestrictionDragListener(listViewer));
		listViewer.addDropSupport(operations,types,new RestrictionDropListener(listViewer));
	}

	private void clearView() {
		
		fViewerbook.showPage(listViewer.getControl());
		listViewer.setInput(Collections.EMPTY_LIST);
		return;		
	}

	private void fillRestrictions() {

		Repository repository = JLibraryPlugin.getCurrentRepository();
		RepositoryView repositoryView = RepositoryView.getInstance();
		Node node = null;
		if (repositoryView != null) {
			node = repositoryView.getCurrentNode();
		}
	
		if ((repository == null) || 
			(node == null)) {
			clearView();
			return;
		}
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (securityManager.canPerformAction(
				repository.getId(),
				SecurityManager.VIEW_RESTRICTIONS)) {
			fViewerbook.showPage(listViewer.getControl());
		
			// Load restrictions from the server
			ServerProfile profile = repository.getServerProfile();

			SecurityService securityService = 
				JLibraryServiceFactory.getInstance(profile).getSecurityService();
			Ticket ticket = JLibraryPlugin.getCurrentTicket();
	
			try {
				Collection restrictions = 
					securityService.findAllRestrictions(ticket,node.getId());
				listViewer.setInput(restrictions);
			} catch (SecurityException e) {
				
	            logger.error(e.getMessage(),e);
				ErrorDialog.openError(new Shell(),
						"ERROR",
						Messages.getMessage("restrictions_load_error"),
						new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
				StatusLine.setErrorMessage(Messages.getMessage("restrictions_load_error"));
				return;
			}		
		} else {
			fViewerbook.showPage(fEmptyGroupsViewer);
		}
	}
	
	public static void refresh() {
		
		if ((listViewer != null) &&
			(listViewer.getContentProvider() != null) &&
			!listViewer.getTable().isDisposed()) {
			
			instance.fillRestrictions();
			listViewer.refresh();			
		}
	}
	
	public static void refreshAndDeselect() {
		
		if ((listViewer != null) && !listViewer.getTable().isDisposed()) {
			listViewer.getTable().deselectAll();
		}
		refresh();
	}

	private void fillActionBars() {
		
		IActionBars actionBars= getViewSite().getActionBars();
		fActionSet = new RestrictionsActionGroup(this);
		fActionSet.fillActionBars(actionBars);
		
		MenuManager manager = new RestrictionsContextMenu(fActionSet,"#PopupRestrictions");
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
	
	public static Group getSelectedGroup() {
		
 		if (listViewer.getSelection().isEmpty()) {
			return null;
		}
		Group group = (Group)listViewer.getTable().getSelection()[0].getData();
		return group;
	}

	public static RestrictionsView getInstance() {
		
		return instance;
	}

	/**
	 * Clears this view
	 */
	public static void empty() {
		
		if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
			
			listViewer.getTable().clearAll();
		}
	}
}