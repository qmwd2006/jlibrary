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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.entities.Node;

/**
 * Simple provider for nodes. It only shows name and description
 *
 * @author martin
 * 
 */
public class SimpleNodeLabelProvider implements ITableLabelProvider {

	public void addListener(ILabelProviderListener listener) {}
	
	public void removeListener(ILabelProviderListener listener) {}
	
	public void dispose() {}
	
	public boolean isLabelProperty(Object element, String property) {return false;}
	
	public String getColumnText(Object element, int columnIndex) {
		
		if (columnIndex == 0) return "";
		if (columnIndex == 1) return ((Node)element).getName();
		return "";
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		
		if (columnIndex == 0) {
			return SharedImages.getImageForNode((Node)element);
		}
		return null;
	}
}
