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
package org.jlibrary.client.export;

/**
 * @author martin
 *
 * Simple interface for tracking export operation progress
 */
public interface IExportProgressMonitor {

	/**
	 * Marks the start of the progress operation
	 * 
	 * @param name Name of the operation
	 * @param steps Number of steps of this operation
	 */
	public void initTask(String name, int steps);
	
	/**
	 * Mark the beginning of a subtask
	 * 
	 * @param step Description of the steps performed
	 * @param worked Number of steps
	 */
	public void subTask(String subtask);
	
	/**
	 * Mark that some steps have been performed
	 * 
	 * @param steps Steps performed
	 */
	public void worked(int steps);
	
	/**
	 * Returns <code>true</code> if the work has been cancelled and 
	 * <code>false</code> otherwise
	 * 
	 * @return boolean Job status
	 */
	public boolean isCanceled();
}
