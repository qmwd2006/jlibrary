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
package org.jlibrary.client.ui.repository.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionGroup;
import org.jlibrary.client.Messages;
import org.jlibrary.client.actions.ActionFactory;
import org.jlibrary.client.actions.NewWizardMenu;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Repository;


/**
 * Action group that adds the 'new' menu to a context menu.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class NewWizardsActionGroup extends ActionGroup {

	private IWorkbenchSite fSite;
	private NewWizardMenu fNewMenu;
	
	private NewDirectoryAction fNewDirectory;
	private NewDocumentAction fNewDocument;
	private NewResourceAction fNewResource;
	
	/**
	 * Creates a new <code>NewWizardsActionGroup</code>. The group requires
	 * that the selection provided by the part's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public NewWizardsActionGroup(IViewSite site) {
		fSite= site;
		
		fNewMenu = new NewWizardMenu(fSite.getWorkbenchWindow());

		fNewDirectory = new NewDirectoryAction(site);
		fNewDocument = new NewDocumentAction(site);
		fNewResource = new NewResourceAction(site);

		site.getSelectionProvider().addSelectionChangedListener(fNewDirectory);
		site.getSelectionProvider().addSelectionChangedListener(fNewDocument);
		site.getSelectionProvider().addSelectionChangedListener(fNewResource);		
		
		ISelection selection = site.getSelectionProvider().getSelection();
		fNewDirectory.update(selection);
		fNewDocument.update(selection);
		fNewResource.update(selection);
		
		// Attach concrete actions to global actions
		site.getActionBars().setGlobalActionHandler(ActionFactory.NEW_DIRECTORY_ID, fNewDirectory);
		site.getActionBars().setGlobalActionHandler(ActionFactory.NEW_DOCUMENT_ID, fNewDocument);
		site.getActionBars().setGlobalActionHandler(ActionFactory.NEW_RESOURCE_ID, fNewResource);
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
			if (sel.isEmpty() || isNewTarget(sel.getFirstElement())) {
				
				IMenuManager newMenu = new MenuManager(Messages.getMessage("item_new")); 
				menu.add(newMenu);
				
				newMenu.add(fNewMenu);	
			}
		}		
	}
	
	private boolean isNewTarget(Object element) {
		
		if (element instanceof Repository) {
			return true;
		}
		if (element instanceof Directory) {
			return true;
		}

		return false;
	}	
}
