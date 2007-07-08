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
package org.jlibrary.client.actions.console;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.console.ConsoleHistory;
import org.jlibrary.client.ui.console.ConsoleItem;

/**
 * Drop down action that holds the currently registered sort actions.
 */
public class ConsoleHistoryAction extends Action implements IMenuCreator {


	private ConsoleHistory history;
	private Menu fMenu;

	public ConsoleHistoryAction(ConsoleHistory history) {
		
		super(Messages.getMessage("console_items"));
		
		setToolTipText(Messages.getMessage("console_items")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_CONSOLE_HISTORY));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_CONSOLE_HISTORY_DISABLED));
		this.history = history;
		setMenuCreator(this);
	}

	public void dispose() {
		
		if (fMenu != null && !fMenu.isDisposed())
			fMenu.dispose();
		fMenu= null;
	}

	public Menu getMenu(Control parent) {
		
		dispose(); // ensure old menu gets disposed
		fMenu= new Menu(parent);
		
		Iterator it = history.getConsoles().iterator();
		while(it.hasNext()) {
			final ConsoleItem consoleItem = (ConsoleItem)it.next();

			final Action action= new Action() {
				public void run() {
					
				}
			};
			
			action.setText(consoleItem.getDescription());
			action.setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_CONSOLE));
			action.setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_CONSOLE_DISABLED));
			action.setToolTipText(consoleItem.getDescription());
			addActionToMenu(fMenu, action);			
		}
		if (history.getConsoles().size() > 0) {
			Separator separator = new Separator();
			separator.fill(fMenu,-1);
			
			final Action removeAllAction = new Action() {
				public void run() {
					history.clear();
				}
			};
			removeAllAction.setText(Messages.getMessage("console_remove_all"));
			removeAllAction.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_ALL));
			removeAllAction.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_ALL_DISABLED));
			removeAllAction.setToolTipText(Messages.getMessage("console_remove_all"));
			addActionToMenu(fMenu, removeAllAction);
		}		
		
		return fMenu;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	public Menu getMenu(Menu parent) {
		
		dispose(); // ensure old menu gets disposed
		fMenu= new Menu(parent);
		
		return fMenu;	
	}

	protected void addActionToMenu(Menu parent, Action action) {
		
		ActionContributionItem item= new ActionContributionItem(action);
		item.fill(parent, -1);
	}

    public void run() {
		// nothing to do
	}


	/**
	 * Disposes this action's menu and returns a new unused instance.
	 */
	public ConsoleHistoryAction renew() {
		
		ConsoleHistoryAction action= new ConsoleHistoryAction(history);
		dispose();
		return action;
	}


	public int getConsoleCount() {
		
		return history.getConsoles().size();
	}
}
