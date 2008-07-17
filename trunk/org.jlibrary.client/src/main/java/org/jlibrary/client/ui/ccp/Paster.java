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
package org.jlibrary.client.ui.ccp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * @author martin
 *
 * Interface that nows how to paste objects
 */
public interface Paster {

	/**
	 * Performs a paste operation
	 * 
	 * @param source Source object
	 * @param destination Destination object
	 * @param move <code>true</code> if the source element has to be moved, i.e. 
	 * the source element has to be deleted
	 * 
	 * @throws PasteException If the operation can't be performed
	 * @throws OperationCanceledException If the operation is canceled
	 */
	public void paste(Object source, 
					  Object destination,
					  boolean move) throws PasteException,
					  					   OperationCanceledException;
	
	/**
	 * Performs a paste operation
	 * 
	 * @param source Source object
	 * @param destination Destination object
	 * @param move <code>true</code> if the source element has to be moved, i.e. 
	 * the source element has to be deleted
	 * @param monitor Monitor to track paste operation progress
	 * 
	 * @throws PasteException If the operation can't be performed
	 */
	public void paste(Object source, 
					  Object destination,
					  boolean move,
					  IProgressMonitor monitor) throws PasteException;

	/**
	 * Performs some tasks after paste operation
	 * 
	 * @param source Source object
	 * @param destination Destination object
	 */
	public void afterPaste(Object source, Object destination);
	
	/**
	 * Performs some tasks before paste operation. This method also gives a last 
	 * opportunity to not perform the paste operation
	 * 
	 * @param source Source object
	 * @param destination Destination object
	 * 
	 * @return boolean If <code>false</code> then the paste operation shouldn't 
	 * be performed. 
	 */
	public boolean beforePaste(Object source, Object destination);
}
