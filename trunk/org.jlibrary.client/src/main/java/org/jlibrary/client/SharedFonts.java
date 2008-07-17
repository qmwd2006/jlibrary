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
package org.jlibrary.client;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * @author martin
 *
 * Class that will be used to access to shared images
 */
public class SharedFonts {

	public final static String RELATION_TITLE = "font.relation.title";
	public final static String DIALOG_BOLD = "font.bold";
	
	private static boolean init = false;

	
	private static void initFonts() {
		
		FontRegistry registry = JFaceResources.getFontRegistry();

		FontData data = new FontData("Serif",10,SWT.BOLD);
		registry.put(DIALOG_BOLD,new FontData[]{data});

		data = new FontData("Arial",10,SWT.BOLD);
		registry.put(RELATION_TITLE, new FontData[]{data});

		
		init = true;
	}
	
	public static Font getFont(String key) {
		
		if (!init) {
			initFonts();
		}
		
		FontRegistry registry = JFaceResources.getFontRegistry();
		return registry.get(key);
	}
	
}
