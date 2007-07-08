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
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;


/**
 * Action group that adds the actions that allow different node operations
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class NodeOperationsActionGroup extends ActionGroup {

	private RefreshRepositoryAction fRefresh;
	private RenameNodeAction fRenameNode;
	
	/**
	 * Creates a new <code>NodeOperationsActionGroup</code>. The group requires
	 * that the selection provided by the part's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public NodeOperationsActionGroup(IWorkbenchSite site) {
		
		fRefresh = new RefreshRepositoryAction(site);
		fRenameNode = new RenameNodeAction(site);
		
		ISelectionProvider provider = site.getSelectionProvider();
		ISelection selection = provider.getSelection();
		
		fRefresh.update(selection);
		provider.addSelectionChangedListener(fRefresh);
		fRenameNode.update(selection);
		provider.addSelectionChangedListener(fRenameNode);		
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
			if (isRenameTarget(sel.getFirstElement())) {
				menu.add(fRenameNode);
			}
			if (isRefreshTarget(sel.getFirstElement())) {
				menu.add(fRefresh);
			}
		}		
	}
	
	private boolean isRefreshTarget(Object element) {
		
		if (element instanceof Repository) {
			return true;
		}
		if (element instanceof Directory) {
			return true;
		}
		if (element instanceof Document) {
			return true;
		}
		return false;
	}	
	
	private boolean isRenameTarget(Object element) {
		
		if (element instanceof Repository) {
			return true;
		}
		if (element instanceof Directory) {
			return true;
		}
		if (element instanceof Document) {
			return true;
		}
		return false;
	}

	RenameNodeAction getRenameNodeAction() {
		
		return fRenameNode;
	}
	
	/**
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	public void fillActionBars(IActionBars actionBars) {

		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ActionFactory.REFRESH_ID,fRefresh);
	}
}
