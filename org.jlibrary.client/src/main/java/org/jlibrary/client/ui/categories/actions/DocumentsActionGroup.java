/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
package org.jlibrary.client.ui.categories.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.actions.SelectionDispatchAction;

/**
 * @author martin
 *
 * Group of actions for Resources View
 */
public class DocumentsActionGroup extends ActionGroup implements ISelectionChangedListener {

	private DeleteDocumentAction fDelete;
	
	private ViewPart part;
	
	public DocumentsActionGroup(ViewPart part) {
		
		super();
		this.part = part;
		
		fDelete = new DeleteDocumentAction(part.getViewSite());
	}
	
	public void fillActionBars(IActionBars actionBars) {
		
		super.fillActionBars(actionBars);
		
		setGlobalActionHandlers(actionBars);
		fillToolBar(actionBars.getToolBarManager());
		//fillViewMenu(actionBars.getMenuManager());
		
		ISelectionProvider provider = part.getViewSite().getSelectionProvider();
		ISelection selection = provider.getSelection();		
		
		registerAction(fDelete,provider,selection);
	}	
	
	private void setGlobalActionHandlers(IActionBars actionBars) {
		
	}

	void fillToolBar(IToolBarManager toolBar) {
		
		toolBar.add(fDelete);
	}
	
	void fillViewMenu(IMenuManager menu) {
		
		menu.add(fDelete);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
	}
	
	private void registerAction(SelectionDispatchAction action, ISelectionProvider provider, ISelection selection) {
		
		action.update(selection);
		provider.addSelectionChangedListener(action);
	}	
	
}
