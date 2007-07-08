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
package org.jlibrary.client.ui.repository.decorators;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.entities.Node;

/**
 * @author Martin Perez
 *
 * This class is the repository tree decorator. It adds icons, prefixes and 
 * suffixes to the nodes in base of their current state.
 */
public class DecoratorNode extends LabelProvider implements ILightweightLabelDecorator, ILabelDecorator {

	public static final String DECORATOR_ID="org.jlibrary.client.ui.repository.decorators.decoratorNode";
	
	public DecoratorNode() {
	    
		super();
	}

	/**
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {

		decoration.addOverlay(SharedImages.getImageDescriptor(SharedImages.IMAGE_USER));
		decoration.addSuffix("*");
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element) {
		
		StringBuffer keyCode = null;
		if (element instanceof Node) {
			
			Node node = (Node)element;
			keyCode = new StringBuffer(node.getTypecode().toString());
			
			boolean decorated = false;
			if (node.isResource() == true) {
				keyCode.append("R");
				decorated = true;
			}
			
			if (node.getLock() != null) {
				keyCode.append("L");
				decorated = true;
			}
			
			if (node.isDeletedDocument()) {
				keyCode.append("D");
				decorated = true;
			}
			
			if (node.isNewDocument()) {
				keyCode.append("N");
				decorated = true;
			}
			
			if (!decorated) {
				return image;
			}
			Image keyImage = SharedImages.getImage(keyCode.toString());
			if (keyImage != null) {
				return keyImage;
			}
		}
		
		if (element instanceof Node) {
			ArrayList imageKeys = new ArrayList();
			Node node = (Node)element;
			
			if (node.isResource()) {
				imageKeys.add(SharedImages.IMAGE_RESOURCE_DECORATOR);				
			}
			
			if (node.getLock() != null) {
				imageKeys.add(SharedImages.IMAGE_LOCK_DECORATOR);
			}
			
			if (node.isDeletedDocument()) {
				imageKeys.add(SharedImages.IMAGE_DELETED_DECORATOR);
			}
			
			if (node.isNewDocument()) {
				imageKeys.add(SharedImages.IMAGE_NEW_DECORATOR);
			}
			
			Image iconImage = drawIconImage(image, imageKeys);
			if (keyCode != null) {
				SharedImages.putImage(keyCode.toString(),iconImage);
			}
			return iconImage;
		}
		return image;
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	public String decorateText(String text, Object element) {
		/*
		if (element instanceof Node) {
			Node node = (Node)element;
			
			if (node.isDocument()) {
				return text + "*";
			}
		}
		*/
		return text;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {

	}
	
	public void dispose() {}	

	
	
	  public void refresh()
	  {
	      fireLabelEvent(
	        new LabelProviderChangedEvent(this));
	  }
	  
	  private void fireLabelEvent(final LabelProviderChangedEvent event)
	  {
	    // We need to get the thread of execution to fire the label provider
	    // changed event , else WSWB complains of thread exception. 
	    Display.getDefault().asyncExec(new Runnable()
	    {
	      public void run()
	      {
	        fireLabelProviderChanged(event);
	      }
	    });
	  }
	  
		/**
		 * Function to draw icon image 
		 * 
		 * @param baseImage base image of the object resource
		 * @param decoratorImageKeys vector of image keys
		 * 
		 * @return icon image with which the resource is to be decorated
		 */
		private Image drawIconImage(Image baseImage, ArrayList decoratorImageKeys) {
			
			Image image;
			OverlayImageIcon overlayIcon = new OverlayImageIcon(baseImage,decoratorImageKeys);
			image = overlayIcon.getImage();
			return image;
		}
}
