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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.ui.PlatformUI;

/**
 * @author martin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SharedCursors {

	private static SharedCursors instance = null;
	
	private Cursor waitCursor = null;
	private Cursor normalCursor =  null;
	
	public SharedCursors() {
		
    	waitCursor = new Cursor (PlatformUI.getWorkbench().getDisplay(), SWT.CURSOR_WAIT);
    	normalCursor = new Cursor (PlatformUI.getWorkbench().getDisplay(), SWT.CURSOR_ARROW);

	}
	
	public Cursor getArrowCursor() {
		
		return normalCursor;
	}
	
	public Cursor getWaitCursor() {
		
		return waitCursor;
	}
	
	public void dispose() {
		
		if (waitCursor != null) {
			waitCursor.dispose();
		}
		if (normalCursor != null) {
			normalCursor.dispose();
		}
	}
	
	public static SharedCursors getInstance() {
		
		if (instance == null) {
			instance = new SharedCursors();
		}
		return instance;
	}
}
