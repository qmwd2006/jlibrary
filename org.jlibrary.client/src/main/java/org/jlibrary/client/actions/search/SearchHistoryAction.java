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
package org.jlibrary.client.actions.search;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.search.SearchHistory;
import org.jlibrary.client.ui.search.SearchView;
import org.jlibrary.core.search.SearchResultSet;

/**
 * Drop down action that holds the currently registered sort actions.
 */
public class SearchHistoryAction extends Action implements IMenuCreator {


	private SearchHistory history;
	private Menu fMenu;

	public SearchHistoryAction(SearchHistory history) {
		
		super(Messages.getMessage("search_previous"));
		
		setToolTipText(Messages.getMessage("search_previous")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_SEARCH_HISTORY));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_SEARCH_HISTORY_DISABLED));
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
		
		Iterator it = history.getSearchResults().iterator();
		while(it.hasNext()) {
			final SearchResultSet searchResult = (SearchResultSet)it.next();

			final Action action= new Action() {
				public void run() {
					SearchView.setInput(searchResult);
				}
			};
			
			action.setText(searchResult.getDescription());
			action.setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_SEARCH));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_SEARCH_DISABLED));
			action.setToolTipText(searchResult.getDescription());
			addActionToMenu(fMenu, action);			
		}
		if (history.getSearchResults().size() > 0) {
			Separator separator = new Separator();
			separator.fill(fMenu,-1);
			
			final Action removeAllAction = new Action() {
				public void run() {
					SearchView.setInput(new SearchResultSet());
					history.clear();
				}
			};
			removeAllAction.setText(Messages.getMessage("search_remove_all"));
			removeAllAction.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_ALL));
			removeAllAction.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_ALL_DISABLED));
			removeAllAction.setToolTipText(Messages.getMessage("search_remove_all"));
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
	public SearchHistoryAction renew() {
		
		SearchHistoryAction action= new SearchHistoryAction(history);
		dispose();
		return action;
	}


	public int getSearchCount() {
		
		return history.getSearchResults().size();
	}
}
