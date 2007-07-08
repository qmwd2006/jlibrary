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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.LockRegistry;
import org.jlibrary.client.ui.repository.actions.OpenAction;
import org.jlibrary.client.ui.repository.dnd.WorkingSetDropListener;
import org.jlibrary.client.ui.repository.menu.WorkingSetActionGroup;
import org.jlibrary.client.ui.repository.menu.WorkingSetContextMenu;
import org.jlibrary.client.ui.repository.providers.WorkingSetContentProvider;
import org.jlibrary.client.ui.repository.providers.WorkingSetLabelProvider;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Node;

/**
 * @author martin
 *
 * View from a repository working set
 */
public class WorkingSetView extends ViewPart {
	
	private static TreeViewer viewer;
	public static final String VIEW_ID = "org.jlibrary.client.ui.repository.views.workingSetView";
	
	private Menu fContextMenu;
	private WorkingSetActionGroup fActionSet;
	
	public WorkingSetView() {}
	
	public void createPartControl(Composite parent) {
		
		viewer = new TreeViewer(parent, 
								SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new WorkingSetContentProvider());
		viewer.setLabelProvider(new WorkingSetLabelProvider());
		
		viewer.setInput(LockRegistry.getInstance());
		
		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(viewer); 
		
		IActionBars actionBars= getViewSite().getActionBars();
		fActionSet = new WorkingSetActionGroup(this);
		fActionSet.fillActionBars(actionBars);
		
		MenuManager manager = new WorkingSetContextMenu(fActionSet,"#PopupWorkingSet");
		site.registerContextMenu(manager, viewer);
		
		fContextMenu = manager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(fContextMenu);
		
		fillActionBars();
		initEvents();
		addDragAndDropSupport();
	}

	private void addDragAndDropSupport() {

		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDropSupport(operations,types,new WorkingSetDropListener(viewer,getSite()));
		
	}
	
	private void initEvents() {

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				handleOpen(event);
			}
		});		
	}


	protected void handleOpen(OpenEvent event) {
				
		if (event.getSelection().isEmpty()) {
			return;
		}
		Object selectedObject = ((IStructuredSelection)event.getSelection()).getFirstElement();
		if (!(selectedObject instanceof Lock)) {
			return;
		}
		Lock lock = (Lock)selectedObject;
		Node node = EntityRegistry.getInstance().getNode(
				lock.getId(),lock.getRepository());
		if (!node.isDocument()) {
			return;
		}
		new OpenAction(getViewSite()).run((Document)node);
	}
	
	private void fillActionBars() {
		
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
		
		if (fContextMenu != null && !fContextMenu.isDisposed())
			fContextMenu.dispose();
	}
	
	public static void refresh() {
		
		if ((viewer == null) || (viewer.getContentProvider() == null)) {
			return;
		}
		if (!viewer.getControl().isDisposed()) {
			viewer.refresh();
		}
		
	}
}