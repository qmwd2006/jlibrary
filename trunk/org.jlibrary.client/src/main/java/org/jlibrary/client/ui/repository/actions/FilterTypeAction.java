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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.repository.FilterManager;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.filter.TypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to filter a viewer by node types
 */
public class FilterTypeAction extends Action {
	
	static Logger logger = LoggerFactory.getLogger(FilterTypeAction.class);
	
	private FilterManager manager;
	private TypeFilter filter;

	/**
	 * Constructor
	 * 
	 * @param viewer visor
	 */
	public FilterTypeAction(FilterManager manager) {
		
		super(null,IAction.AS_CHECK_BOX);
		
		this.manager = manager;
		this.filter = new TypeFilter();
		
		setText(Messages.getMessage("filter_type"));
		setToolTipText(Messages.getMessage("filter_type_tooltip"));
	}

	/**
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	public boolean isEnabled() {
		
		return 
		 (RepositoryRegistry.getInstance().getConnectedRepositoriesCount() > 0);
	}	
	
	public void run() {

		logger.info(
				"[FilterTypeAction] Filtering by type");
		manager.addFilter(this,filter);
	}
}
