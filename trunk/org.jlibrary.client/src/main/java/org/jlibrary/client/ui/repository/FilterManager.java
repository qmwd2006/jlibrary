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
package org.jlibrary.client.ui.repository;

import org.eclipse.jface.action.IAction;
import org.jlibrary.client.ui.repository.filter.ViewerSorter;

/**
 * This class manages filter states
 * 
 * @author martin
 *
 */
public class FilterManager {

	private RepositoryViewer viewer;
	private IAction lastAction;

	/**
	 * Builds a new filter manager
	 * 
	 * @param viewer Associated repository viewer
	 */
	public FilterManager(RepositoryViewer viewer) {
		
		this.viewer = viewer;
	}
	
	/**
	 * Adds an active filter to the repository viewer
	 * 
	 * @param action Executed action
	 * @param filter Filter to be added
	 */
	public void addFilter(IAction action, ViewerSorter filter) {
		
		if (lastAction != null) {
			lastAction.setChecked(false);
		}
		action.setChecked(true);
		viewer.addSortFilter(filter);
		this.lastAction = action;
	}
}
