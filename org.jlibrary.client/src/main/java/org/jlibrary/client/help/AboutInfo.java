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
package org.jlibrary.client.help;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

/**
 * The information within this object is obtained from the about INI file.
 * This file resides within an install configurations directory and must be a 
 * standard java property file.  
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 * @author martin
 */
public final class AboutInfo {
	
	private String featurePluginLabel;
	private ImageDescriptor windowImage;
	private ImageDescriptor aboutImage;
	private String aboutText;

	/**
	 * Returns the configuration information for the feature with the 
	 * given id.
	 * 
	 */
	public static AboutInfo readInfo() {

		AboutInfo info = new AboutInfo();
		info.featurePluginLabel = Messages.getMessage("about_title");
		info.aboutText = Messages.getMessage("about_text");
		info.aboutImage = SharedImages.getImageDescriptor(SharedImages.IMAGE_ABOUT_BIG);
		return info;
	}
	
	/**
	 * Returns the descriptor for an image which can be shown in an "about" dialog 
	 * for this product. Products designed to run "headless" typically would not 
	 * have such an image.
	 * 
	 * @return the descriptor for an about image, or <code>null</code> if none
	 */
	public ImageDescriptor getAboutImage() {
		return aboutImage;
	}

		
	/**
	 * Returns a label for the feature plugn, or <code>null</code>.
	 */
	public String getFeatureLabel() {
		return featurePluginLabel;
	}

	/**
	 * Returns the text to show in an "about" dialog for this product.
	 * Products designed to run "headless" typically would not have such text.
	 * 
	 * @return the about text, or <code>null</code> if none
	 */
	public String getAboutText() {
		return aboutText;
	}

	/**
	 * Returns the product name or <code>null</code>.
	 * This is shown in the window title and the About action.
	 *
	 * @return the product name, or <code>null</code>
	 */
	public String getProductName() {
		return featurePluginLabel;
	}




	/**
	 * Returns the image descriptor for the window image to use for this product.
	 * Products designed to run "headless" typically would not have such an image.
	 * 
	 * @return the image descriptor for the window image, or <code>null</code> if none
	 */
	public ImageDescriptor getWindowImage() {
		return windowImage;
	}
}
