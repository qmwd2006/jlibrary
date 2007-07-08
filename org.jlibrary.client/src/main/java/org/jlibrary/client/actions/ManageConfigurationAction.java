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
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.update.ui.UpdateManagerUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

public class ManageConfigurationAction extends Action implements IWorkbenchAction {
	
	private IWorkbenchWindow workbenchWindow;

	public ManageConfigurationAction(IWorkbenchWindow window){
		if (window == null) {
			throw new IllegalArgumentException();
		}
		this.workbenchWindow = window;
		setText(Messages.getMessage("update_manage"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_UPDATE_APP_CONF));
		setDisabledImageDescriptor(
				SharedImages.getImageDescriptor(SharedImages.IMAGE_UPDATE_APP_CONF_DISABLED));
	}
	
	public void run(){
		if (workbenchWindow == null){
			return;
		}
		UpdateManagerUI.openConfigurationManager(workbenchWindow.getShell());
	}
	
	public void dispose() {
		if (workbenchWindow == null){
			return;
		}
		workbenchWindow = null;
	}
}
