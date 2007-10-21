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
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.ccp.PasteService;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;

/**
 * Repository tree label provider
 */
public class RepositoryLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		
		if (element instanceof Repository) {
			Repository repository = (Repository)element; 
			StringBuilder buffer = new StringBuilder(repository.getName());
			buffer.append(" (");
			if ((repository.getServerProfile().isLocal())) {
				buffer.append(Messages.getMessage(
						repository.getServerProfile().getLocation()));				
			} else {
				buffer.append(repository.getServerProfile().getLocation());
			}
			buffer.append(")");
			return buffer.toString();
		} else if (element instanceof Node) {
			Node node = (Node)element;
			return node.getName();
		}
		return "";
	}

	

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object node) {
		
		Image image = null;
		if (node instanceof Directory) {
			image = SharedImages.getImage(SharedImages.IMAGE_NODE_DIRECTORY);
		} else if ((node instanceof Document) || (node instanceof ResourceNode)) {
			image = SharedImages.getImageForNode((Node)node);
		} else if (node instanceof Repository) {
			if (((Repository)node).isConnected()) {
				image = SharedImages.getImage(SharedImages.IMAGE_NODE_REPOSITORY);
			} else {
				image = SharedImages.getImage(SharedImages.IMAGE_REPOSITORY_CLOSED);
			}
		}
		
		// Check node clipboard status
		
		if (PasteService.getInstance().isCutOperation()) {
			Object[] clipboardObjects = PasteService.getInstance().getClipboardObjects();
			if (clipboardObjects != null) {
				for (int i = 0; i < clipboardObjects.length; i++) {
					if (clipboardObjects[i] == node) {
						return SharedImages.getGreyedImageForNode((Node)node);
					}
				}
			}
		}
		
		return image;
	}

	
}
