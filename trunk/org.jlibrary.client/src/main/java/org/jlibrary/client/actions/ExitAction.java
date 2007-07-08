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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to exit JLibrary
 */
public class ExitAction extends Action 
					    implements ActionFactory.IWorkbenchAction{

	static Logger logger = LoggerFactory.getLogger(ExitAction.class);

	/**
	 * Constructor
	 */
	public ExitAction(IWorkbenchWindow window) {
		
		if (window == null) {
			throw new IllegalArgumentException();
		}
		
		setText(Messages.getMessage("item_exit"));
		setToolTipText(Messages.getMessage("tooltip_exit"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_EXIT));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_EXIT_DISABLED));
	}
	
	public void run() {
		
		logger.info("Exiting JLibrary");
		
		PlatformUI.getWorkbench().close();
	}
	
	/**
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {

	}
}
