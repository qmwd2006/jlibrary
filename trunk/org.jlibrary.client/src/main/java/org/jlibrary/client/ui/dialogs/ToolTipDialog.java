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
package org.jlibrary.client.ui.dialogs;


import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;

/**
 * This is a generic dialog that will show a tip to the user.
 * <p/>
 * The user also will have a check box to not show the dialog again 
 * 
 * @author martin
 *
 */
public class ToolTipDialog extends IconAndMessageDialog {

	public static final String TIP_EXTRACTION = "TIP_EXTRACTION";
	
	private Button notShowAgain;
	private String tipKeyword;
	
	/**
	 * Constructor
	 * 
	 * @param parent the parent shell
	 */
	public ToolTipDialog(Shell parent, String tipKeyword) {
	    
		super(parent);
		
		this.tipKeyword = tipKeyword;
	    // Set the default message
		message = Messages.getMessage("extraction_warning_message");
		
	}
	
	/**
	 * Tells if a tip dialog for the given keyword should be opened
	 * 
	 * @param tipKeyword Keyword for the tip
	 * 
	 * @return boolean <code>true</code> if the dialog should be opened and 
	 * <code>false</code> if the user has choosen to do not show again the 
	 * tip dialog
	 */
	public static boolean mustOpenToolTip(String tipKeyword) {
		
		IDialogSettings settings = JLibraryPlugin.getDefault().
								getDialogSettings().getSection(tipKeyword);
		if (settings == null) {
			return true;
		} else {
			if (settings.get(tipKeyword) == null) {
				return true;
			}
			return false;
		}
	}
	
	protected void configureShell(Shell shell) {
		
		super.configureShell(shell);
		shell.setText(Messages.getMessage("extraction_warning_title"));
	}
	
	  /**
	   * Creates the dialog area
	   * 
	   * @param parent the parent composite
	   * @return Control
	   */
	  protected Control createDialogArea(Composite parent) {
		  
	    createMessageArea(parent);

	    // Create a composite to hold the label
	    Composite composite = new Composite(parent, SWT.NONE);
	    GridData data = new GridData(GridData.FILL_BOTH);
	    data.horizontalSpan = 2; composite.setLayoutData(data);
	    composite.setLayout(new FillLayout());

		notShowAgain = new Button(composite,SWT.CHECK);
		notShowAgain.setText(Messages.getMessage("extraction_warning_notshow"));

		notShowAgain.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				IDialogSettings settings = JLibraryPlugin.getDefault().
									getDialogSettings().getSection(tipKeyword);
				
				if (settings == null) {
					settings = JLibraryPlugin.getDefault().
								getDialogSettings().addNewSection(tipKeyword);
				}
				if (notShowAgain.getSelection()) {
					settings.put(tipKeyword,false);
				} else {
					settings.put(tipKeyword,(String)null);
				}
				
			}
		});
		
	    return composite;
	  }
	
	/**
	 * @see IconAndMessageDialog#getImage()
	 */
	protected Image getImage() {
		
		return getInfoImage();
	}

}
