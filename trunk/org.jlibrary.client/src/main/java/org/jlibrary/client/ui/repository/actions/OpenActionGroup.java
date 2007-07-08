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

import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.Page;
import org.jlibrary.client.Messages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Group for open actions
 */
public class OpenActionGroup extends ActionGroup {
	
	static Logger logger = LoggerFactory.getLogger(OpenActionGroup.class);
	
	private IWorkbenchSite fSite;
 	private SelectionDispatchAction[] fActions;

 	private SelectionDispatchAction fOpen;
	private SelectionDispatchAction fOpenSystem;
	
	private MenuManager openWithMenu = new MenuManager(Messages.getMessage("item_open_with")); //$NON-NLS-1$
	
	/**
	 * Creates a new <code>CCPActionGroup</code>. The group requires that
	 * the selection provided by the view part's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param part the view part that owns this action group
	 */
	public OpenActionGroup(IViewPart  part) {

		this(part.getSite());
	}
	
	/**
	 * Creates a new <code>CCPActionGroup</code>.  The group requires that
	 * the selection provided by the page's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param page the page that owns this action group
	 */
	public OpenActionGroup(Page page) {

		this(page.getSite());
	}

	public OpenActionGroup(IWorkbenchSite site) {
		fSite= site;
		fOpen = new OpenAction(fSite);
		fOpenSystem = new OpenSystemAction(fSite);
		fActions= new SelectionDispatchAction[] {	
			fOpen,
			fOpenSystem
		};
		registerActionsAsSelectionChangeListeners();
	}

	private void registerActionsAsSelectionChangeListeners() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		for (int i= 0; i < fActions.length; i++) {
			provider.addSelectionChangedListener(fActions[i]);
		}
	}
	
	private void deregisterActionsAsSelectionChangeListeners() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		for (int i= 0; i < fActions.length; i++) {
			provider.removeSelectionChangedListener(fActions[i]);
		}
	}

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillActionBars(IActionBars actionBars) {

		super.fillActionBars(actionBars);
	}
	
	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {

		if (getContext() == null) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

		super.fillContextMenu(menu);

		if (!menu.isEmpty()) {
			menu.add(new Separator());
		}
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel= (IStructuredSelection) selection;
			if (isOpenTarget(sel.getFirstElement())) {
				
				fillOpenWithMenu(menu, selection);
			}
		}	
	}		
	
	/**
	 * Adds the OpenWith submenu to the context menu.
	 * 
	 * @param menu the context menu
	 * @param selection the current selection
	 */
	private void fillOpenWithMenu(IMenuManager menu, IStructuredSelection selection) {

		Set tools = null;
		
		// Only supported if exactly one file is selected.
		if (selection.size() != 1)
			return;
		Object element = selection.getFirstElement();
		if (!(element instanceof Document) &&
			!(element instanceof ResourceNode))
			return;
		
		String extension = "";
		openWithMenu.removeAll();
		if (element instanceof Document) {
			Document document = (Document)element;
			extension = FileUtils.getExtension(document.getPath());
			openWithMenu.add(fOpen);
		} else {
			
			logger.info("Not implemented yet");
			/*
			Resource resource = (Resource)element;
			String path = FileUtils.getExternalPath(resource);
			extension = FileUtils.getExtension(path);
			openWithMenu.add(fOpenResource);
			*/
		}
		
		Separator separator = new Separator();
		String toolId = ClientConfig.getDefaultToolForExtension(extension);
		boolean checked = false;
		
		if (toolId.equals(ClientConfig.DEFAULT_TOOL)) {
			fOpen.setChecked(true);
			checked = true;
		} else {
			fOpen.setChecked(false);
		}
		openWithMenu.add(separator);

		openWithMenu.add(separator);
		openWithMenu.add(fOpenSystem);
		if (toolId.equals(ClientConfig.SYSTEM_TOOL)) {
			fOpenSystem.setChecked(true);
			checked = true;
		} else {
			fOpenSystem.setChecked(false);
		}
		
		if (!checked) {
			setDefaultAction(extension);
		}
		
		menu.add(openWithMenu);
	}	
	

	private SelectionDispatchAction setDefaultAction(String extension) {

		if (fOpen.isEnabled() ){
			ClientConfig.setDefaultToolForExtension(ClientConfig.DEFAULT_TOOL,extension);
			fOpen.setChecked(true);
			return fOpen;
		} else if (fOpenSystem.isEnabled()) {
			ClientConfig.setDefaultToolForExtension(ClientConfig.SYSTEM_TOOL,extension);
			fOpenSystem.setChecked(true);
			return fOpenSystem;
		}
		return null;
	}

	/*
	 * @see ActionGroup#dispose()
	 */
	public void dispose() {
		
		super.dispose();
		deregisterActionsAsSelectionChangeListeners();
	}

	public SelectionDispatchAction getOpenAction() {
		
		return fOpen;
	}
	
	public SelectionDispatchAction getOpenSystemAction() {
		
		return fOpenSystem;
	}

	/**
	 * Runs the default open action in a selected item
	 * 
	 * @param selection
	 */
	public void runDefaultAction(IStructuredSelection selection) {

		fOpen.update(selection);
		fOpenSystem.update(selection);		
		
		Object selectedObject = selection.getFirstElement();
		if (selectedObject instanceof Repository) {
			fOpen.run(selection);
			return;
		}
		if (!(selectedObject instanceof Node)) {
			return;
		}
		
		Node node = (Node)selectedObject;
		if (node.isDirectory()) {
			fOpen.run(selection);
			return;
		}
		
		String extension = FileUtils.getExtension(node.getPath());
				
		String toolId = ClientConfig.getDefaultToolForExtension(extension);
		if ((toolId != null) && !(toolId.equals(""))) {
			if (toolId.equals(ClientConfig.DEFAULT_TOOL)) {
				fOpen.run(selection);
			} else if (toolId.equals(ClientConfig.SYSTEM_TOOL)) {
				fOpenSystem.run(selection);
			}
		} else {
			SelectionDispatchAction action = setDefaultAction(extension);
			if (action != null) {
				action.run(selection);
			}
		}
	}
	/**
	 * @return Returns the openWithMenu.
	 */
	public MenuManager getOpenWithMenu() {
		return openWithMenu;
	}

	private boolean isOpenTarget(Object element) {
		
		if (element instanceof Node) {
			return true;
		}

		return false;
	}	
}
