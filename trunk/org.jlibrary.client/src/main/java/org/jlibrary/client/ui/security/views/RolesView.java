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
package org.jlibrary.client.ui.security.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.jlibrary.client.ui.list.ListContentProvider;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.security.RolesContextMenu;
import org.jlibrary.client.ui.security.actions.RolesActionGroup;
import org.jlibrary.client.ui.security.dnd.RolDragListener;
import org.jlibrary.client.ui.security.providers.RolesLabelProvider;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;

/**
 * @author martin
 *
 * View from a repository. It contents a table that will display roles
 */
public class RolesView extends ViewPart {
	
	private static RolesView instance;
	
	private Menu fContextMenu;
	private static RolesActionGroup fActionSet;
	
	public static final String VIEW_ID = "org.jlibrary.client.ui.security.views.RolesView";

	private static ListViewer listViewer;
	private PageBook fViewerbook; 
	private Label fEmptyGroupsViewer;	
	
	public RolesView() {
	
		instance = this;
	}
	
	public void createPartControl(Composite parent) {
		
		fViewerbook= new PageBook(parent, SWT.NULL);

		fEmptyGroupsViewer= new Label(fViewerbook, SWT.TOP | SWT.LEFT | SWT.WRAP);
		fEmptyGroupsViewer.setText(Messages.getMessage("restricted_list_view"));

		listViewer = new ListViewer(fViewerbook, 
								SharedImages.getImage(SharedImages.IMAGE_ROL),
								SWT.MULTI | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		listViewer.setLabelProvider(new RolesLabelProvider());
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
	
	private void addDragAndDropSupport() {

		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		listViewer.addDragSupport(operations,types,new RolDragListener(listViewer));
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
				SecurityManager.VIEW_GROUPS)) {
			fViewerbook.showPage(listViewer.getControl());
			ServerProfile profile = JLibraryPlugin.getCurrentServerProfile(); 
	
			Ticket ticket = JLibraryPlugin.getCurrentTicket();
			SecurityService securityService = 
				JLibraryServiceFactory.getInstance(profile).getSecurityService();		
			
			List roles;
			try {
				roles = new ArrayList(securityService.findAllRoles(ticket));
				if (listViewer.getContentProvider() == null) {
					listViewer.setContentProvider(new ListContentProvider());
				}
				listViewer.setInput(roles);
			} catch (SecurityException se) {
				ErrorDialog.openError(new Shell(),
						  "ERROR",
						  Messages.getMessage("security_exception"),
						  new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
				StatusLine.setErrorMessage(se.getMessage());
	
			}
		} else {
			fViewerbook.showPage(fEmptyGroupsViewer);
		}
	}
	
	public static void refresh() {
		
		if ((listViewer != null) &&
			(listViewer.getContentProvider() != null) &&
			!listViewer.getTable().isDisposed()) {		
			
			instance.fillData();
			listViewer.refresh();
		}
	}
	
	public static void refreshAndDeselect() {
		
		if ((listViewer != null) && !listViewer.getTable().isDisposed()) {
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
		fActionSet = new RolesActionGroup(this);
		fActionSet.fillActionBars(actionBars);
		
		MenuManager manager = new RolesContextMenu(fActionSet,"#PopupRoles");
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
	
	public static void rolCreated(Rol rol) {
		
		if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
			
			Collection inputData = (Collection)listViewer.getInput();
			inputData.add(rol);
			listViewer.refresh();
		}
	}
	
	public static void rolUpdated(Rol rol) {
		
		if (instance != null) {
			if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
				
				listViewer.refresh();
			}
		}

	}
	
	public static void rolDeleted(Rol rol) {
		
		if (instance != null) {
			if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
				
				listViewer.remove(rol);
			}
		}
	}

	
	public static Rol getSelectedRol() {
		
 		if (listViewer.getSelection().isEmpty()) {
			return null;
		}
 		Rol rol = (Rol)listViewer.getTable().getSelection()[0].getData();
		return rol;
	}

	public void disableView() {
		
		if (listViewer != null) {
			listViewer.getTable().setEnabled(false);
		}
	}
	
	public void enableView() {
		
		if (listViewer != null) {
			listViewer.getTable().setEnabled(true);
		}
	}
	
	public static RolesView getInstance() {
		
		return instance;
	}
	
	public void clearSelection() {
		
		listViewer.setSelection(new StructuredSelection());
	}
}