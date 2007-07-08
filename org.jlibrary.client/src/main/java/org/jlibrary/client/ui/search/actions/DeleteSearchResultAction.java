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
package org.jlibrary.client.ui.search.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.ui.search.SearchView;
import org.jlibrary.core.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to close a repository
 */
public class DeleteSearchResultAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(DeleteSearchResultAction.class);
	
	/**
	 * Constructor
	 */
	public DeleteSearchResultAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_delete"));
		setToolTipText(Messages.getMessage("tooltip_delete"));
		setImageDescriptor(
				SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_SEARCH_RESULT));
		setDisabledImageDescriptor(
				SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_SEARCH_RESULT_DISABLED));
	}
	

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
		
		if (selection.isEmpty())
			return false;

		Object[] elements = selection.toArray();
		if (elements.length > 1) {
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
		
		
		logger.info("Removing selected search results");

		SearchHit item = (SearchHit)elements[0];
		SearchView.getSearchViewer().remove(item);
	}
	
}
