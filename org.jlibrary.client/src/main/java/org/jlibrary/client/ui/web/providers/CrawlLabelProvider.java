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
package org.jlibrary.client.ui.web.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.SharedImages;

public class CrawlLabelProvider implements ILabelProvider {
	  
    public Image getImage(Object arg0) {
		
		String url = (String)arg0;
		return SharedImages.getImageForPath(url);
    }

	public String getText(Object arg0) {
	  return arg0.toString();
	}

	public void addListener(ILabelProviderListener arg0) {}

	public void dispose() {}

	public boolean isLabelProperty(Object arg0, String arg1) {
	    return false;
	}

	public void removeListener(ILabelProviderListener arg0) {}
}