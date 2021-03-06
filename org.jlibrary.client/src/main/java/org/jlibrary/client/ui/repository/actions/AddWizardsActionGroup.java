/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
import org.jlibrary.core.entities.Repository;


/**
 * Action group that adds the actions that allow adding nodes to the repository
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class AddWizardsActionGroup extends ActionGroup {

	private AddDirectoryAction fAddDirectory;
	private AddResourcesAction fAddResources;
	
	/**
	 * Creates a new <code>AddWizardsActionGroup</code>. The group requires
	 * that the selection provided by the part's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public AddWizardsActionGroup(IWorkbenchSite site) {
		
		fAddDirectory = new AddDirectoryAction(site);
		fAddResources = new AddResourcesAction(site);
		
		ISelectionProvider provider = site.getSelectionProvider();
		ISelection selection = provider.getSelection();
		
		fAddResources.update(selection);
		fAddDirectory.update(selection);
		
		provider.addSelectionChangedListener(fAddResources);
		provider.addSelectionChangedListener(fAddDirectory);
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
			if (sel.size() <= 1 && isNewTarget(sel.getFirstElement())) {
				
				menu.add(fAddDirectory);
				menu.add(fAddResources);
			}
		}		
	}
	
	private boolean isNewTarget(Object element) {
		
		if ((element instanceof Directory) || (element instanceof Repository)) {
			return true;
		}

		return false;
	}	

	public void fillActionBars(IActionBars actionBars) {
		
		super.fillActionBars(actionBars);
		
		actionBars.setGlobalActionHandler(ActionFactory.ADD_DIRECTORIES_ID, 
										  fAddDirectory);
		actionBars.setGlobalActionHandler(ActionFactory.ADD_RESOURCES_ID,
										  fAddResources);

	}
}
