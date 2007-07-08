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
package org.jlibrary.client.ui.dialogs;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * @author martin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class YesNoCancelConfirmDialog extends MessageDialog {

	public static int YES = 0;
	public static int NO = 1;
	public static int CANCEL = 2;
	
	public YesNoCancelConfirmDialog(
			Shell parentShell,
			String dialogTitle,
			Image dialogTitleImage,
			String dialogMessage,
			int dialogImageType,
			String[] dialogButtonLabels,
			int defaultIndex) {
		
		super(	parentShell,
				dialogTitle,
				dialogTitleImage,
				dialogMessage,
				dialogImageType,
				dialogButtonLabels,
				defaultIndex);
	}
	
	/**
	 * Convenience method to open a simple confirm (Yes/No/Cancel) dialog.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if
	 *            none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 * @return <code>true</code> if the user presses the OK button, <code>false</code>
	 *         otherwise
	 */
	public static int openYNCConfirm(Shell parent,
									 String title,
									 String message) {
		
		MessageDialog dialog = new MessageDialog(
				parent,
				title,
				null,
				// accept the default window icon
				message,
				QUESTION,
				new String[]{ 	YES_LABEL,
								NO_LABEL,
						   		CANCEL_LABEL,
							},
						   		0
		);
		
		// OK is the default
		return dialog.open();
	}
}
