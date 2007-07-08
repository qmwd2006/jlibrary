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
package org.jlibrary.client.ui.repository.views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.menu.ResourcesContextMenu;
import org.jlibrary.client.ui.repository.providers.ResourceLabelProvider;
import org.jlibrary.client.ui.repository.providers.ResourcesListContentProvider;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;

/**
 * @author martin
 *
 * View from a repository. It contents a tree that will display repository
 * documents and folders
 */
public class ResourcesView extends ViewPart {
	
	private Menu fContextMenu;
	
	private static ListViewer viewer;
	private static Document document;
	public static final String VIEW_ID = "org.jlibrary.client.ui.repository.views.resourcesView";
	
	public ResourcesView() {}
	
	public void createPartControl(Composite parent) {
		
		viewer = new ListViewer(parent, 
								SWT.MULTI | SWT.H_SCROLL | SWT.BORDER | SWT.BORDER,
								new ResourceLabelProvider(),
								new ResourcesListContentProvider());
		
		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(viewer);	
		
		if (document != null) {
			setDocument(document);
		}
		// Register viewer with site. This must be done before making the actions.
		MenuManager manager = new ResourcesContextMenu(getViewSite(),"#PopupResources");
		site.registerContextMenu(manager, viewer);

		fContextMenu = manager.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(fContextMenu);
		
		fillActionBars();
		
		initEvents();
	}

	private void initEvents() {

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				handleOpen(event);
			}
		});		
	}


	protected void handleOpen(OpenEvent event) {
		
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		RepositoryView.getInstance().getActionManager().handleOpen(selection);
	}

	private void fillActionBars() {}		
	
	
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

		if (fContextMenu != null && !fContextMenu.isDisposed())
			fContextMenu.dispose();
		
	}
	/**
	 * @return Returns the document.
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @param document The document to set.
	 */
	public static void setDocument(Document doc) {
            
		if ((viewer == null) || (viewer.getContentProvider() == null)) {
			return;
        }
        if (viewer.getContentProvider() != null && 
        	viewer.getLabelProvider() != null){
        	
			document = doc;
		
			if (viewer != null) {
				viewer.getTable().removeAll();
				viewer.setInput(doc);
			}
        }
	}
	
	public static void empty() {
		
		setDocument(null);
	}
	
	public static void refresh() {
		
		if ((viewer == null) || (viewer.getContentProvider() == null)) {
			return;
		}
		if (!viewer.getControl().isDisposed()) {
			if (document != null) {
				Node registryNode = EntityRegistry.getInstance().
					getNode(document.getId(),document.getRepository());
				if (registryNode == document) {
					viewer.refresh(document);
				} else {
					setDocument((Document)registryNode);
				}
			}
		}
		
	}
}