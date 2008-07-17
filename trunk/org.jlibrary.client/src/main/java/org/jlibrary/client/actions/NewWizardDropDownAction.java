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
package org.jlibrary.client.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.actions.ActionFactory;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

/**
 * Invoke the resource creation wizard selection Wizard.
 * This action will retarget to the active view.
 */
public class NewWizardDropDownAction
		extends Action
		implements ActionFactory.IWorkbenchAction,
			IMenuCreator,
			IWorkbenchWindowPulldownDelegate2 {

	/**
	 * The workbench window; or <code>null</code> if this
	 * action has been <code>dispose</code>d.
	 */
	private IWorkbenchWindow workbenchWindow;

	private IAction newWizardAction;
	
	private MenuManager dropDownMenuMgr;
	/**
	 *	Create a new instance of this class
	 */
	public NewWizardDropDownAction(IWorkbenchWindow window) {
		
		super(Messages.getMessage("item_new")); //$NON-NLS-1$
		if (window == null) {
			throw new IllegalArgumentException();
		}
		this.workbenchWindow = window;
		setToolTipText(Messages.getMessage("item_new"));

		// @issues should be IDE-specific images
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW));		
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DISABLED));		

		setMenuCreator(this);
	}
	/**
	 * create the menu manager for the drop down menu.
	 */
	protected void createDropDownMenuMgr() {
		
		if (dropDownMenuMgr == null) {
			dropDownMenuMgr = new MenuManager();
			dropDownMenuMgr.add(new NewWizardMenu(workbenchWindow));
		}
	}
	/**
	 * dispose method comment.
	 */
	public void dispose() {
		if (workbenchWindow == null) {
			// action has already been disposed
			return;
		}
		if (dropDownMenuMgr != null) {
			dropDownMenuMgr.dispose();
			dropDownMenuMgr = null;
		}
	}
	/**
	 * getMenu method comment.
	 */
	
	public Menu getMenu(Control parent) {
		/*
		createDropDownMenuMgr();
		return dropDownMenuMgr.createContextMenu(parent);
		*/
		createDropDownMenuMgr();
		Menu menu = new Menu(parent);
		IContributionItem[] items = dropDownMenuMgr.getItems();
		for (int i = 0; i < items.length; i++) {
			IContributionItem item = items[i];
			item.fill(menu, 0);
		}
		return menu;		
	}

	/**
	 * Create the drop down menu as a submenu of parent.  Necessary
	 * for CoolBar support.
	 */	
	public Menu getMenu(Menu parent) {
		
		createDropDownMenuMgr();
		Menu menu = new Menu(parent);
		IContributionItem[] items = dropDownMenuMgr.getItems();
		for (int i = 0; i < items.length; i++) {
			IContributionItem item = items[i];
			item.fill(menu, 0);
		}
		return menu;
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#init(IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
	}
	public void run() {
		if (workbenchWindow == null) {
			// action has been disposed
			return;
		}
		newWizardAction.run();
	}
	/**
	 * @see runWithEvent(IAction, Event)
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
	}
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
}

