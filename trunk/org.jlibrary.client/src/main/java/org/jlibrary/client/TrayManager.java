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

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.jlibrary.client.actions.ActionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin
 *
 * Tray manager class. Inits a tray icon and its events
 */
public class TrayManager {
	
	static Logger logger = LoggerFactory.getLogger(TrayManager.class);
	
	private static Image image = null;
	private static Tray tray = null;
	private static TrayItem trayItem = null;
	
	/**
	 * Inits tray icon and its events
	 */
	public static void initTray(IWorkbenchWindow window) {

		tray = PlatformUI.getWorkbench().getDisplay().getSystemTray();
		trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setToolTipText(Messages.getMessage("jlibrary_title"));
		
		image = SharedImages.getImage(SharedImages.IMAGE_JLIBRARY);
		trayItem.setImage(image);
		
		trayItem.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event) {
				logger.debug("Show");
			}
		});
		
		trayItem.addListener(SWT.Hide, new Listener() {
			public void handleEvent(Event event) {
				logger.debug("Hide");
			}
		});
		
		trayItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				logger.debug("Selection");
			}
		});
		
		trayItem.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event event) {
				logger.debug("DefaultSelection");
			}
		});
		
		final MenuManager menuManager = new MenuManager(Messages.getMessage("item_help"), IWorkbenchActionConstants.M_HELP);
		final IWorkbenchAction aboutAction = ActionFactory.ABOUT.create(window);
		menuManager.add(aboutAction);
		
		final Menu menu = menuManager.createContextMenu(window.getShell()); 

		trayItem.addListener(SWT.MenuDetect, new Listener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
			 */
			public void handleEvent(Event event) {
				
				menu.setVisible(true);
			}
		});
	}
	
	public static void disposeTray() {
		
		image.dispose();
		if (tray != null) {
			tray.dispose();
		}
		if (trayItem != null) {
			trayItem.dispose();
		}
	}
}
