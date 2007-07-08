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

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.search.actions.SearchAction;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to search into a repository 
 * from the repositories view 
 */
public class SearchRepositoryAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(SearchRepositoryAction.class);
	
	private SearchAction delegateAction;
	
	/**
	 * Constructor
	 */
	public SearchRepositoryAction(IWorkbenchSite site) {

		super(site);

		delegateAction = new SearchAction(site.getWorkbenchWindow());
		
		setText(Messages.getMessage("item_search"));
		setToolTipText(Messages.getMessage("tooltip_search"));
		setImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_SEARCH));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_SEARCH_DISABLED));
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	protected void selectionChanged(ITextSelection selection) {}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {

		setEnabled(checkEnabled(selection));
	}

	private boolean checkEnabled(IStructuredSelection selection) {

		if (selection.isEmpty()) {
			return false;
		}

		Object item = selection.getFirstElement();
		if (!(item instanceof Repository)) {
			return false;
		}
		Repository repository = (Repository)item;
		if (!repository.isConnected()) {
			return false;
		}
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				repository.getId(),
				SecurityManager.SEARCH)) {
			return false;
		}		
						
		return true;
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {

		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}

	public void run(Object[] elements) {

		
		logger.debug("Launching search dialog");

		final Repository repository = (Repository)elements[0];
		
		delegateAction.run(repository);
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}
}
