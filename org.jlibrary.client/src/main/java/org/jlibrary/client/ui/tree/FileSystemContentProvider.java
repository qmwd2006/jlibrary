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
package org.jlibrary.client.ui.tree;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for repository tree
 */
public class FileSystemContentProvider implements ITreeContentProvider {

	/**
	 * @author Liße, Jan
	 * 
	 * This field is required for workaround. See hasChildren()-method.
	 * Flag is only necessary on Windows 2000. What about Win98/ME etc...?
	 */
	private boolean isFirstElement = System.getProperty("os.name").equals("Windows 2000") ? true : false;
	
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element) {
	
		File file = (File)element;		
		return file.listFiles();
		
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		
		File file = (File)element;
		return file.getParentFile();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
	        
        /**
         * This condition is a workaround to prevent the popup of
         * a "no disk in floppy"-window on a Windows 2000 system when
         * initially filling a TreeViewer with the filesystem-root delivered by 
         * the java.io.File.listRoots() method. Now the a:\-floppy-node will initially be expandable.
         * When you try to actually expand that node without any children, the "[]" sign will disappear
         * and of course now the popup window shows up.
         * 
         * This check is based on the assumption that Floppy-disk a:\
         * will be the first element that is passed to this method.
         * Since a:\ will always be the first element in the File-Array 
         * delivered by listRoots() this assumption will be true unless Windows-filesystem
         * naming changes. 
         */
        if (isFirstElement) {
            isFirstElement = false;
            return true;       
        }
        // the regular part  
        else {
            File file = (File) element;
            if (file.list() == null) {
                return false;
            }
            return file.list().length > 0;
        }
    }


	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object element) {
		
		//File[] file = (File[])element;
		return (File[])element;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}


}
