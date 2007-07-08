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
package org.jlibrary.client.actions;

import org.eclipse.jface.action.Action;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

public class StartServerAction extends Action {
	
	/**
	 * Constructor
	 */
	public StartServerAction() {
		
		super();
		
		setText(Messages.getMessage("item_start_server"));
		setToolTipText(Messages.getMessage("tooltip_start_server"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_JLIBRARY));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_JLIBRARY_DISABLED));
	}

	public void run() {
		/*
		System.out.println("[StartServerAction] Starting jLibrary server");
		
		TomcatAppServer tas = new TomcatAppServer();
		try {
			tas.start(8888,"localhost");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		*/
	}
}
