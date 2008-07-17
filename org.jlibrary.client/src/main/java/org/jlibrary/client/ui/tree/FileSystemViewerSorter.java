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
import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author martin
 *
 * Sorter for file system viewer
 */
public class FileSystemViewerSorter extends ViewerSorter {

	private Collator collator;
	
	public FileSystemViewerSorter() {
		
		this.collator = Collator.getInstance();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		File file1 = (File)e1;
		File file2 = (File)e2;
		
		if (file1.isDirectory()) {
			if (!file2.isDirectory()) {
				return -1;
			} else {
				return collator.compare(file1.getName(),file2.getName());
			}
		} else {
			if (file2.isDirectory()) {
				return 1;
			} else {
				return collator.compare(file1.getName(),file2.getName());
			}
		}
	}
}
