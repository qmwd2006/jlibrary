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

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author martin
 *
 * Exporter progress monitor implementation using IProgressMonitor Eclipse class
 */
public class EclipseExportProgressMonitor implements IExportProgressMonitor {

	private IProgressMonitor monitor;
	
	public EclipseExportProgressMonitor(IProgressMonitor monitor) {
	
		this.monitor = monitor;
	}
	
	public boolean isCanceled() {
		
		return monitor.isCanceled();
	}
	
	/**
	 * @see org.jlibrary.client.export.IExportProgressMonitor#initTask(java.lang.String, int)
	 */
	public void initTask(String name, int steps) {
		
		monitor.beginTask(name,steps);
	}
	
	/**
	 * @see org.jlibrary.client.export.IExportProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String subtask) {

		monitor.subTask(subtask);
	}
	
	/**
	 * @see org.jlibrary.client.export.IExportProgressMonitor#worked(int)
	 */
	public void worked(int steps) {

		monitor.worked(steps);
	}
}
