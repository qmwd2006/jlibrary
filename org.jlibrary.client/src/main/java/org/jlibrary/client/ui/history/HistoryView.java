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
package org.jlibrary.client.ui.history;

import java.net.MalformedURLException;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.ToolTipHandler;
import org.jlibrary.client.ui.web.actions.NavigateNewAction;
import org.jlibrary.client.util.URL;

/**
 * @author martin
 *
 * View from a repository. It contents a tree that will display repository
 * documents and folders
 */
public class HistoryView extends ViewPart {
	
	private Menu fContextMenu;	
	private static TreeViewer viewer;

	public static final String VIEW_ID = "org.jlibrary.client.ui.history.historyView";

	
	public HistoryView() {}
	
	public void createPartControl(Composite parent) {
		
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new HistoryContentProvider());
		viewer.setLabelProvider(new HistoryLabelProvider());
		
		viewer.setInput(new HistoryBook[]{HistoryBook.getInstance()});
		
		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(viewer);
		
		// Register viewer with site. This must be done before making the actions.
		MenuManager manager = new HistoryContextMenu(getViewSite(),"#PopupHistory");
		site.registerContextMenu(manager, viewer);
		
		fContextMenu = manager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(fContextMenu);
		
		fillActionBars();
		
		initEvents();
		
		ToolTipHandler handler = new ToolTipHandler(parent.getShell());
		handler.activateHoverHelp(viewer.getTree());
	}

	public static void refresh() {
		
		if ((viewer != null) && (viewer.getContentProvider() != null)) {
			
			viewer.refresh();
		}
	}

	public static void setInput(Object object) {
		
		if ((viewer != null) && (viewer.getContentProvider() != null)) {
			
			viewer.setInput(object);
		}
	}
	
	
	private void initEvents() {

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				handleOpen(event);
			}
		});
	}

	protected void handleOpen(OpenEvent event) {

		// Open a document
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		
		Object selectedObject = selection.getFirstElement();
		if (!(selectedObject instanceof History)) {
			return;
		}
		History item = (History)selectedObject;
		try {
			URL url = new URL(item.getUrl());
			new NavigateNewAction().run(url);
		} catch (MalformedURLException e) {
			StatusLine.setErrorMessage(Messages.getAndParseValue("error_open_bookmark","%1",item.getUrl()));
			return;
		}
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
}