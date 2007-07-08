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
package org.jlibrary.client.part;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;

/**
 * Interface for a <code>IStorage</code> input to an editor.
 * <p>
 * Clients implementing this editor input interface should override
 * <code>Object.equals(Object)</code> to answer true for two inputs
 * that are the same. The <code>IWorbenchPage.openEditor</code> APIs
 * are dependent on this to find an editor with the same input.
 * </p><p>
 * Clients should implement this interface to declare new types of 
 * <code>IStorage</code> editor inputs.
 * </p><p>
 * File-oriented editors should support this as a valid input type, and display
 * its content for viewing (but not allow modification).
 * Within the editor, the "save" and "save as" operations should create a new 
 * file resource within the workspace.
 * </p><p>
 * All editor inputs must implement the <code>IAdaptable</code> interface;
 * extensions are managed by the platform's adapter manager.
 * </p>
 */
public interface IStorageEditorInput extends IEditorInput {
/**
 * Returns the underlying IStorage object.
 *
 * @return an IStorage object.
 * @exception CoreException if this method fails
 */
public IStorage getStorage() throws CoreException;
}
