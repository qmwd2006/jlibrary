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
package org.jlibrary.client.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * A <code>NewWizardMenu</code> is used to populate a menu manager with
 * New Wizard actions.  The visible actions are determined by user preference
 * from the Perspective Customize dialog.
 */
public class NewWizardMenu extends ContributionItem {
	
	private IAction newDirectoryAction;
	private IAction newDocumentAction;
	private IAction newRepositoryAction;
	private IAction newResourceAction;

	private boolean enabled = true;
	private IWorkbenchWindow window;


	public NewWizardMenu(MenuManager innerMgr, IWorkbenchWindow window) {
		
		this(window);
		fillMenu(innerMgr);
	}

	public NewWizardMenu(IWorkbenchWindow window) {
		super();
		this.window = window;
		newDirectoryAction = ActionFactory.NEW_DIRECTORY.create(window);
		newDocumentAction = ActionFactory.NEW_DOCUMENT.create(window); 
		newRepositoryAction = ActionFactory.NEW_REPOSITORY.create(window);
		newResourceAction = ActionFactory.NEW_RESOURCE.create(window);
	}
	
	/* (non-Javadoc)
	 * Fills the menu with New Wizards.
	 */
	private void fillMenu(IContributionManager innerMgr) {
		// Remove all.
		innerMgr.removeAll();
		innerMgr.add(newRepositoryAction);
		innerMgr.add(new Separator());
		innerMgr.add(newDirectoryAction);
		innerMgr.add(newDocumentAction);		
		innerMgr.add(newResourceAction);		
	}
	
	/* (non-Javadoc)
	 * Method declared on IContributionItem.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/* (non-Javadoc)
	 * Method declared on IContributionItem.
	 */
	public boolean isDynamic() {
		return true;
	}
	
	/**
	 * Sets the enabled state of the receiver.
	 * 
	 * @param enabledValue if <code>true</code> the menu is enabled; else
	 * 		it is disabled
	 */
	public void setEnabled(boolean enabledValue) {
		this.enabled = enabledValue;
	}
	

	/* (non-Javadoc)
	 * Method declared on IContributionItem.
	 */
	public void fill(Menu menu, int index) {
	
	
		MenuManager manager = new MenuManager();
		fillMenu(manager);
		
		IContributionItem items[] = manager.getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].fill(menu, index++);
		}
		
	}
	
	
	/**
	 * @return Returns the newDirectoryAction.
	 */
	public IAction getNewDirectoryAction() {
		return newDirectoryAction;
	}
	/**
	 * @return Returns the newDocumentAction.
	 */
	public IAction getNewDocumentAction() {
		return newDocumentAction;
	}
	/**
	 * @return Returns the newRepositoryAction.
	 */
	public IAction getNewRepositoryAction() {
		return newRepositoryAction;
	}
	/**
	 * @return Returns the newResourceAction.
	 */
	public IAction getNewResourceAction() {
		return newResourceAction;
	}	
}
