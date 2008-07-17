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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.SharedImages;

/**
 * Repository tree label provider
 */
public class FileSystemLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		
		File file = (File)element;
		if (file.getName().equals("")) {
			return file.getPath();
		}
		return file.getName();		
	}

	

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object node) {
		
		File file = (File)node;
		if (file.isDirectory()) {
			return SharedImages.getImage(SharedImages.IMAGE_NODE_DIRECTORY);
		} else {
			Image image = SharedImages.getImageForFile((File)node);
			if (image == SharedImages.getImage(SharedImages.IMAGE_UNK)) {
				return  SharedImages.getImage(SharedImages.IMAGE_NODE_DOCUMENT);
			}
			return image;
		}
	}

	
}
