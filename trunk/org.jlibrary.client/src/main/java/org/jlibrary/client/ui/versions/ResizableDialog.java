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
package org.jlibrary.client.ui.versions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;


/**
 * Base class for resizable Dialogs with persistent window bounds.
 */
public abstract class ResizableDialog extends Dialog {

	// dialog store id constants
	private final static String DIALOG_BOUNDS_KEY= "ResizableDialogBounds"; //$NON-NLS-1$
	private static final String X= "x"; //$NON-NLS-1$
	private static final String Y= "y"; //$NON-NLS-1$
	private static final String WIDTH= "width"; //$NON-NLS-1$
	private static final String HEIGHT= "height"; //$NON-NLS-1$
	
	private Rectangle fNewBounds;

	public ResizableDialog(Shell parent) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}
	
	public void setHelpContextId(String contextId) {

	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}
	
	protected Point getInitialSize() {
		
		int width= 0;
		int height= 0;
		
		final Shell s= getShell();
		if (s != null) {
			s.addControlListener(
				new ControlListener() {
					public void controlMoved(ControlEvent arg0) {
						fNewBounds= s.getBounds();
					}
					public void controlResized(ControlEvent arg0) {
						fNewBounds= s.getBounds();
					}
				}
			);
		}
		
		IDialogSettings bounds = null;
		if (bounds == null) {
			Shell shell= getParentShell();
			if (shell != null) {
				Point parentSize= shell.getSize();
				width= parentSize.x-100;
				height= parentSize.y-100;
			}
			if (width < 700)
				width= 700;
			if (height < 500)
				height= 500;
		} else {
			try {
				width= bounds.getInt(WIDTH);
			} catch (NumberFormatException e) {
				width= 700;
			}
			try {
				height= bounds.getInt(HEIGHT);
			} catch (NumberFormatException e) {
				height= 500;
			}
		}	
	
		return new Point(width, height);
	}
	
	protected Point getInitialLocation(Point initialSize) {
		Point loc= super.getInitialLocation(initialSize);
		
		IDialogSettings bounds = null;
		if (bounds != null) {
			try {
				loc.x= bounds.getInt(X);
			} catch (NumberFormatException e) {
			}
			try {
				loc.y= bounds.getInt(Y);
			} catch (NumberFormatException e) {
			}
		}
		return loc;
	}
	
	public boolean close() {
		boolean closed= super.close();
		if (closed && fNewBounds != null)
			saveBounds(fNewBounds);
		return closed;
	}

	private void saveBounds(Rectangle bounds) {
		IDialogSettings dialogBounds = null;
		if (dialogBounds == null) {
			dialogBounds= new DialogSettings(DIALOG_BOUNDS_KEY);
		}
		dialogBounds.put(X, bounds.x);
		dialogBounds.put(Y, bounds.y);
		dialogBounds.put(WIDTH, bounds.width);
		dialogBounds.put(HEIGHT, bounds.height);
	}
}
