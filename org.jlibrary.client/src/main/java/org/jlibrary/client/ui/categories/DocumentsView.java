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
package org.jlibrary.client.ui.categories;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.categories.actions.DocumentsActionGroup;
import org.jlibrary.client.ui.categories.dnd.DocumentDragListener;
import org.jlibrary.client.ui.categories.menu.DocumentsContextMenu;
import org.jlibrary.client.ui.categories.providers.DocumentsContentProvider;
import org.jlibrary.client.ui.categories.providers.DocumentsLabelProvider;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.actions.OpenAction;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * View from a repository. It contents a tree that will display repository
 * documents and folders
 */
public class DocumentsView extends ViewPart {
	
	static Logger logger = LoggerFactory.getLogger(DocumentsView.class);
	
	private Menu fContextMenu;
	private DocumentsActionGroup fActionSet;
	
	private static TableViewer viewer;
	private static DocumentsView instance;
		
	public static final String VIEW_ID = "org.jlibrary.client.ui.categories.DocumentsView";
	private Category category;
	
	public DocumentsView() {
	
		instance = this;
	}

	
	public void createPartControl(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		viewer.setContentProvider(new DocumentsContentProvider());
		viewer.setLabelProvider(new DocumentsLabelProvider());
		
		TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setWidth(20);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setWidth(20);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setWidth(120);
		column.setText(Messages.getMessage("documents_view_name"));
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText(Messages.getMessage("documents_view_repository"));
		column.setWidth(80);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText(Messages.getMessage("documents_view_path"));
		column.setWidth(200);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText(Messages.getMessage("documents_view_description"));
		column.setWidth(220);

		getViewSite().setSelectionProvider(viewer);
		
		// Register viewer with site. This must be done before making the actions.
		MenuManager manager = new DocumentsContextMenu(getViewSite(),"#PopupCategoryDocuments");
		IWorkbenchPartSite site = getSite();
		site.registerContextMenu(manager, viewer);
		
		fContextMenu = manager.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(fContextMenu);
		
		fillActionBars();

		initEvents();
	}
	
	private void fillActionBars() {
		
		IActionBars actionBars= getViewSite().getActionBars();
		fActionSet = new DocumentsActionGroup(this);
		fActionSet.fillActionBars(actionBars);
		
		viewer.getTable().setMenu(fContextMenu);

	}		
	
	
	private void initEvents() {

		viewer.addOpenListener(new IOpenListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IOpenListener#open(org.eclipse.jface.viewers.OpenEvent)
			 */
			public void open(OpenEvent event) {
				
				if (event.getSelection().isEmpty()) {
					return;
				}
				
				Document document = (Document)((IStructuredSelection)event.getSelection()).getFirstElement();
				if (document == null) {
					return;
				}
				new OpenAction(getViewSite()).run(document);
			}
		});

		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDragSupport(operations,types,new DocumentDragListener(viewer));
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		
		if (viewer != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().setFocus();
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {

	}
	
	public void refresh() {
		
		viewer.refresh();
	}
	
	public static DocumentsView getInstance() {
		return instance;
	}

	public void loadDocuments(Category category) {
		
        if ((viewer != null) && 
        	(viewer.getContentProvider() != null)) {
        	
			this.category = category;
			Repository repository = RepositoryRegistry.getInstance().getRepository(category.getRepository());
			ServerProfile serverProfile = repository.getServerProfile();
			RepositoryService repositoryService = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
			Ticket ticket = repository.getTicket();
			
			try {
				List documents = repositoryService.findNodesForCategory(ticket, category.getId());
				if (documents != null) {
					viewer.setInput(documents.toArray());
				} else {
					viewer.setInput(new Object[]{});
				}
				viewer.refresh();
			} catch (CategoryNotFoundException cnfe) {
				
	            logger.error(cnfe.getMessage(),cnfe);
			} catch (RepositoryException e) {
				
	            logger.error(e.getMessage(),e);
			}
        }
	}

	public void removeDocuments(Object[] elements) {
		
		viewer.remove(elements);
	}
	
	public Category getCurrentCategory() {
		
		return category;
	}

	public void clearInput() {
		
		this.category = null;
		viewer.getTable().removeAll();
	}


	/**
	 * 
	 */
	public void refreshDocuments() {
		
		if (category != null) {
			loadDocuments(category);
		}
	}
}