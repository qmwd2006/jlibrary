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
package org.jlibrary.client.ui.authors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.swt.SWT;
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
import org.jlibrary.client.ui.authors.actions.AuthorsActionGroup;
import org.jlibrary.client.ui.authors.menu.AuthorsContextMenu;
import org.jlibrary.client.ui.authors.providers.AuthorsContentProvider;
import org.jlibrary.client.ui.authors.providers.AuthorsLabelProvider;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;

/**
 * @author martin
 *
 * This view will show repository authors
 */
public class AuthorsView extends ViewPart {
	
	private Menu fContextMenu;
	
	private static ListViewer listViewer;
	private static PageBook fViewerbook; 
	private static Label fEmptyGroupsViewer;	
	

	private static AuthorsActionGroup fActionSet;
	public static final String VIEW_ID = 
		"org.jlibrary.client.ui.authors.authorsView";
	
	public AuthorsView() {}
	
	public void createPartControl(Composite parent) {
		
		fViewerbook= new PageBook(parent, SWT.NULL);

		fEmptyGroupsViewer= new Label(fViewerbook, SWT.TOP | SWT.LEFT | SWT.WRAP);
		fEmptyGroupsViewer.setText(Messages.getMessage("restricted_list_view"));
		
		listViewer = new ListViewer(fViewerbook, 
								SWT.MULTI | SWT.H_SCROLL | SWT.BORDER,
								new AuthorsLabelProvider(SharedImages.getImage(
										SharedImages.IMAGE_AUTHOR)),
								new AuthorsContentProvider());
		
		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(listViewer);
		
		fActionSet = new AuthorsActionGroup(this);
		
		// Register viewer with site. This must be done before making the actions.
		MenuManager manager = new AuthorsContextMenu(fActionSet,"#PopupAuthors");
		site.registerContextMenu(manager, listViewer);

		fContextMenu = manager.createContextMenu(listViewer.getTable());
		listViewer.getTable().setMenu(fContextMenu);
		
		ToolTipHandler handler = new ToolTipHandler(fViewerbook.getShell());
		handler.activateHoverHelp(listViewer.getTable());
		
		fillActionBars();		
		initEvents();
		fillData();
	}

	private void initEvents() {
		//open the update Author dialog when double-clicked on an Author 
		listViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				fActionSet.handleOpen(event);
			}
		});
		
	}

	private void fillActionBars() {
		
		IActionBars actionBars= getViewSite().getActionBars();
		fActionSet.fillActionBars(actionBars);
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

	private static void fillData() {
		
		Repository repository = JLibraryPlugin.getCurrentRepository();
		if (fActionSet != null) {
			fActionSet.updateActionBars();
		}
		
        if ((listViewer == null) || 
        	(listViewer.getContentProvider() == null) ||
        	(repository == null)) {        	

        	empty();
            return;
        }        
        
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (securityManager.canPerformAction(
			repository.getId(),
			SecurityManager.VIEW_AUTHORS)) {
			fViewerbook.showPage(listViewer.getControl());

			// Load repository Authors
	        Collection authors = loadRepositoryAuthors(repository);
	        
	        authors.remove(Author.UNKNOWN);
	        listViewer.setInput(authors);        	
	    	fActionSet.updateActionBars();
		} else {
			fViewerbook.showPage(fEmptyGroupsViewer);
		}
	}	
	
	private static List loadRepositoryAuthors(Repository repository) {
		
		Ticket ticket = repository.getTicket();
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		
		try {
			return repositoryService.findAllAuthors(ticket);
		} catch (RepositoryException e) {
			ErrorDialog.openError(new Shell(),
					  "ERROR",
					  e.getMessage(),
					  new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
			StatusLine.setErrorMessage(e.getMessage());
			return Collections.EMPTY_LIST;
		}
	}
	
	public static void authorUpdated(Author author) {
		
		if ((listViewer != null) && (listViewer.getContentProvider() != null)) {
			
			listViewer.refresh(author); //refresh the category in the view
		}
	}	
	
	public static void empty() {
				
		if ((listViewer != null) && (listViewer.getContentProvider() != null)) {			
			fViewerbook.showPage(listViewer.getControl());
			listViewer.setInput(null);
			fActionSet.updateActionBars();
		}
	}
	
	public static void refresh() {
		
		if ((listViewer == null) || (listViewer.getContentProvider() == null)) {
			return;
		}
		if (!listViewer.getControl().isDisposed()) {
			fillData();
		}
		
	}
}