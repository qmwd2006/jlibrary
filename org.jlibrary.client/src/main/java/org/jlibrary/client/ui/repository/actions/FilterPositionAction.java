/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.repository.FilterManager;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.filter.PositionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to filter a viewer by node positions
 */
public class FilterPositionAction extends Action {
	
	static Logger logger = LoggerFactory.getLogger(FilterPositionAction.class);
	
	private FilterManager manager;
	private PositionFilter filter;

	/**
	 * Constructor
	 * 
	 * @param viewer visor
	 */
	public FilterPositionAction(FilterManager manager) {
		
		super(null,IAction.AS_CHECK_BOX);
		
		this.manager = manager;
		this.filter = new PositionFilter();
		
		setText(Messages.getMessage("filter_position"));
		setToolTipText(Messages.getMessage("filter_position_tooltip"));
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
				"[FilterPositionAction] Filtering by position");
		manager.addFilter(this,filter);
	}
}
