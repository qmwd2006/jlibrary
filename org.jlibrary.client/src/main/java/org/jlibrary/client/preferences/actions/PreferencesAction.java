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
package org.jlibrary.client.preferences.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to save a document state
 */
public class PreferencesAction extends Action {
	
	static Logger logger = LoggerFactory.getLogger(PreferencesAction.class);
	
	/**
	 * Constructor
	 */
	public PreferencesAction() {
		
		super();
		
		setText(Messages.getMessage("item_preferences"));
		setToolTipText(Messages.getMessage("tooltip_preferences"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_PREFERENCES));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_PREFERENCES_DISABLED));
	}

	public void run() {

		logger.info("Showing preferences dialog");

		PreferenceManager manager = 
			PlatformUI.getWorkbench().getPreferenceManager();
		
		final PreferenceDialog dialog = new PreferenceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), manager);

		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), new Runnable() {
			public void run() {
				
				dialog.create();				
				dialog.open();
			}
		});	
		
	}
}
