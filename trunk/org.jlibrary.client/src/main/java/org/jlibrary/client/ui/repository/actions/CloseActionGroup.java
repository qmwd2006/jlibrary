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
package org.jlibrary.client.ui.repository.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionGroup;
import org.jlibrary.client.actions.ActionFactory;
import org.jlibrary.core.entities.Repository;


/**
 * Action group that adds the actions that close repositories
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class CloseActionGroup extends ActionGroup {

	private ConnectAction fClose;
	private CloseRepositoryAction fRemoveFromView;
	
	/**
	 * Creates a new <code>CloseActionGroup</code>. The group requires
	 * that the selection provided by the part's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public CloseActionGroup(IWorkbenchSite site) {
		
		fClose = new ConnectAction(site);
		fRemoveFromView = new CloseRepositoryAction(site);
		
		ISelectionProvider provider = site.getSelectionProvider();
		ISelection selection = provider.getSelection();
		
		fClose.update(selection);
		provider.addSelectionChangedListener(fClose);
		fRemoveFromView.update(selection);
		provider.addSelectionChangedListener(fRemoveFromView);
	}
	
	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		
		super.fillContextMenu(menu);
		
		if (!menu.isEmpty()) {
			menu.add(new Separator());
		}
		
		ISelection selection= getContext().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel= (IStructuredSelection) selection;
			if (isDisconnectTarget(sel.getFirstElement())) {				
				menu.add(fClose);
			}
			if (isCloseTarget(sel.getFirstElement())) {				
				menu.add(fRemoveFromView);
			}			
		}		
	}
	
	private boolean isDisconnectTarget(Object element) {
		
		if (element instanceof Repository) {
			return true;
		}

		return false;
	}	

	private boolean isCloseTarget(Object element) {
		
		if (element instanceof Repository) {
			return true;
		}

		return false;
	}	
	
	public void fillActionBars(IActionBars actionBars) {

		super.fillActionBars(actionBars);
		
		actionBars.setGlobalActionHandler(ActionFactory.CLOSE_ID, 
				  						  fRemoveFromView);
		actionBars.setGlobalActionHandler(ActionFactory.CONNECT_ID,
				  						  fClose);
		
	}
}
