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
package org.jlibrary.client.ui.repository.providers;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * Repository tree label provider
 */
public class WorkingSetLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		
		if (element instanceof Repository) {
			return ((Repository)element).getName();
		}
		Lock lock = (Lock)element;
		Node node = EntityRegistry.getInstance().getNode(lock.getId(),lock.getRepository());
		if (node == null) {
			// This is for debug purposes. Theorically never will happen.
			return lock.getId();
		} else {
			return node.getName();
		}
	}

	

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		
		if (element instanceof Repository) {
			return SharedImages.getImage(SharedImages.IMAGE_NODE_REPOSITORY);
		}
		Lock lock = (Lock)element;
		Node node = EntityRegistry.getInstance().
			getNode(lock.getId(),lock.getRepository());
		if (node == null) {
			// This is for debug purposes. Theorically never will happen.
			return SharedImages.getImage(SharedImages.IMAGE_LOCK);
		} else {
			if (node.isDocument()) {
				return SharedImages.getImageForNode(node);
			} else {
				return SharedImages.getImage(SharedImages.IMAGE_LOCK);				
			}
		}
	}	
}
