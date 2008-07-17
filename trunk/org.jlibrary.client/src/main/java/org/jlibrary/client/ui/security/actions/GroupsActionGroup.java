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
package org.jlibrary.client.ui.security.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.ui.repository.actions.OpenAction;

/**
 * @author martin
 *
 * Group of actions for Groups View
 */
public class GroupsActionGroup extends ActionGroup implements ISelectionChangedListener {
	
	private NewGroupAction fNew;
	private DeleteGroupAction fRemove;
	
	private OpenAction fOpen;
	
	private ViewPart part;
	
	public GroupsActionGroup(ViewPart part) {
		
		super();
		this.part = part;
		
		fNew = new NewGroupAction(part.getViewSite());
		fRemove = new DeleteGroupAction(part.getViewSite());
		fOpen = new OpenAction(part.getViewSite());
	}
	
	public void fillActionBars(IActionBars actionBars) {
		
		super.fillActionBars(actionBars);
		
		setGlobalActionHandlers(actionBars);
		fillToolBar(actionBars.getToolBarManager());
		
		ISelectionProvider provider = part.getViewSite().getSelectionProvider();
		ISelection selection = provider.getSelection();		
		
		registerAction(fNew,provider,selection);
		registerAction(fRemove,provider,selection);
		registerAction(fOpen,provider,selection);
	}	
	
	public void updateActionBars() {

		fNew.update(fNew.getSelectionProvider().getSelection());
		fRemove.update(fRemove.getSelectionProvider().getSelection());
		fOpen.update(fOpen.getSelectionProvider().getSelection());		
	}	
	
	/**
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {

		menu.add(fNew);
		menu.add(fRemove);
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(fOpen);

		super.fillContextMenu(menu);
	}
	
	private void setGlobalActionHandlers(IActionBars actionBars) {
		
		/*
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.GO_INTO, fZoomInAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.BACK, fBackAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.FORWARD, fForwardAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.UP, fUpAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.GO_TO_RESOURCE, fGotoResourceAction);
		actionBars.setGlobalActionHandler(JdtActionConstants.GOTO_TYPE, fGotoTypeAction);
		actionBars.setGlobalActionHandler(JdtActionConstants.GOTO_PACKAGE, fGotoPackageAction);
		*/
	}

	void fillToolBar(IToolBarManager toolBar) {
		
		toolBar.add(fNew);
		toolBar.add(fRemove);
	}
	
	void fillViewMenu(IMenuManager menu) {
		
		menu.add(fNew);
		menu.add(fRemove);
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
	
	public NewGroupAction getNewGroupAction() {		
		
		return fNew;
	}
	
	public void handleOpen(OpenEvent event) {

		fOpen.run((IStructuredSelection)event.getSelection());
	}	
}
