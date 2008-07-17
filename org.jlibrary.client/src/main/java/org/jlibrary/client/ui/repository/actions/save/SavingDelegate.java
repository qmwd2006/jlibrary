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
package org.jlibrary.client.ui.repository.actions.save;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.jlibrary.client.ui.editor.JLibraryEditor;

/**
 * Class to separate object save handling
 * 
 * @author mpermar
 *
 */
public abstract class SavingDelegate {

	public abstract int loadProcessLength(Object object);
	
	public abstract IStatus doSave(IProgressMonitor monitor,JLibraryEditor editor);
	
	public abstract void postJobTasks(JLibraryEditor editor, Object object);
	
	public boolean isEditorDirty(JLibraryEditor editor) {
		
		boolean dirty;
		if (editor instanceof JLibraryEditor) {
			dirty = ((JLibraryEditor)editor).isMarkedDirty();
		} else {
            if (editor != null){
            	dirty = editor.isDirty();
            } else {
                dirty = false;
            }
		}
		return dirty;
	}
}
