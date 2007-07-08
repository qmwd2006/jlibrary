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
import org.jlibrary.client.ui.favorites.actions.AddToFavoritesAction;
import org.jlibrary.client.ui.favorites.actions.AddToFavoritesDefaultAction;
import org.jlibrary.core.entities.Document;


/**
 * Action group that adds the actions to deal with favorites
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class FavoritesActionGroup extends ActionGroup {

	private AddToFavoritesDefaultAction fAddToFavoritesDefaultAction;
	private AddToFavoritesAction fAddToFavoritesAction;
	
	/**
	 * Creates a new <code>FavoritesActionGroup</code>. The group requires
	 * that the selection provided by the part's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public FavoritesActionGroup(IWorkbenchSite site) {
		fAddToFavoritesDefaultAction = new AddToFavoritesDefaultAction(site);
		fAddToFavoritesAction = new AddToFavoritesAction(site);
		
		ISelectionProvider provider = site.getSelectionProvider();
		ISelection selection = provider.getSelection();
		
		fAddToFavoritesDefaultAction.update(selection);
		provider.addSelectionChangedListener(fAddToFavoritesDefaultAction);
		fAddToFavoritesAction.update(selection);
		provider.addSelectionChangedListener(fAddToFavoritesAction);
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
			if (isNewTarget(sel.getFirstElement())) {
				
				menu.add(fAddToFavoritesDefaultAction);
				menu.add(fAddToFavoritesAction);
			}
		}		
	}
	
	private boolean isNewTarget(Object element) {
		
		if (element instanceof Document) {
			return true;
		}

		return false;
	}	

	public void fillActionBars(IActionBars actionBars) {

		super.fillActionBars(actionBars);
		
		actionBars.setGlobalActionHandler(ActionFactory.ADD_FAVORITES_ID, 
				  						  fAddToFavoritesAction);
		actionBars.setGlobalActionHandler(ActionFactory.ADD_FAVORITES_DEFAULT_ID, 
				  				  		  fAddToFavoritesDefaultAction);
	}
}
