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
package org.jlibrary.client.ui;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author martin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class StatusLine {

	private static IStatusLineManager getStatusLineManager() {
		
		IStatusLineManager manager = null;
		IWorkbenchWindow window = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if (window != null) {
			IWorkbenchPage activePage = window.getActivePage();
			if (activePage != null) {
				IWorkbenchPart activePart = activePage.getActivePart();
				if (activePart != null) {
					if (activePart instanceof IViewPart) {
						manager = ((IViewPart)activePart).getViewSite().
										getActionBars().getStatusLineManager();
					}
				} else if (activePart instanceof IEditorPart) {
						manager = ((IEditorPart)activePart).getEditorSite().
										getActionBars().getStatusLineManager();
				}
			}
		}
		
		return manager;
	}

	/**
	 * Muestra un mensaje de acción cancelada
	 * 
	 * @param message Mensaje a mostrar
	 */
	public static void setErrorMessage(String message) {
		
		IStatusLineManager manager = getStatusLineManager();
		if (manager != null) {
			manager.setErrorMessage(message);
		}
	}

	public static void setText(String text) {
		
		IStatusLineManager manager = getStatusLineManager();
		if (manager != null) {
			manager.setMessage(text);
		}
	}

	/**
	 * Muestra un mensaje de acción realizada correctamente
	 * 
	 * @param message Mensaje a mostrar
	 */
	public static void setOKMessage(String message) {
		
		IStatusLineManager manager = getStatusLineManager();
		if (manager != null) {
			manager.setMessage(message);
		}				
	}
	
}
