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

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.profiles.HTTPServerProfile;
import org.jlibrary.core.profiles.LocalServerProfile;


/**
 * @author martin
 *
 * Dialog used for creating a new repository
 */
public class NewServerProfileDialog extends Dialog {

	private Button localButton;
	private Button remoteButton;
	private Button browseButton;
	private Text locationText;
	private Text fileLocationText;
	
	private ServerProfile serverProfile;
	
	private static NewServerProfileDialog instance;
	
	/**
	 * Constructor
	 * 
	 * @param window Parent window
	 */
	private NewServerProfileDialog(Shell shell) {
		
		super(shell);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText(Messages.getMessage("profile_create"));	
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_USER));
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		
		createButton(parent,IDialogConstants.OK_ID,Messages.getMessage("accept_option"),true);
		createButton(parent,IDialogConstants.CANCEL_ID, Messages.getMessage("cancel_option"),false);
		
		getButton(IDialogConstants.OK_ID).setEnabled(true);	
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		
		// Save server profile into user preferences

		if (localButton.getSelection()) {
			serverProfile = new LocalServerProfile();
			serverProfile.setName(ClientConfig.PROFILE_LOCAL_KEY);
			serverProfile.setLocation(ClientConfig.PROFILE_LOCAL_KEY);
		} else if (remoteButton.getSelection()){
			serverProfile = new HTTPServerProfile();
			serverProfile.setName(locationText.getText());
			serverProfile.setLocation(locationText.getText());
		}
		
		super.okPressed();
	}


	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(final Composite parent) {

		Composite outer = (Composite)super.createDialogArea(parent);
		
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		
		outer.setLayout (gridLayout);

		localButton = new Button (outer, SWT.RADIO);
		localButton.setText (Messages.getMessage(
				ClientConfig.PROFILE_LOCAL_KEY));
		localButton.setSelection(true);
		GridData data = new GridData ();
		data.horizontalSpan = 3;
		data.horizontalAlignment = GridData.BEGINNING;
		localButton.setLayoutData (data);

		Label labFileLocation = new Label (outer, SWT.NONE);
		labFileLocation.setText (Messages.getMessage("profile_location"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labFileLocation.setLayoutData (data);
		
		fileLocationText = new Text (outer, SWT.BORDER);
		fileLocationText.setText("");
		fileLocationText.setEnabled(false);
		data = new GridData ();
		data.widthHint = 200;
		data.horizontalAlignment = GridData.FILL;
		fileLocationText.setLayoutData (data);
		fileLocationText.setEditable(false);
		
		data = new GridData();
		browseButton = new Button(outer, SWT.NONE);
		browseButton.setImage(SharedImages.getImage(SharedImages.IMAGE_DIRECTORY));
		browseButton.setLayoutData(data);
		browseButton.setEnabled(false);
		
		remoteButton = new Button (outer, SWT.RADIO);
		remoteButton.setText (Messages.getMessage("profile_remote"));
		remoteButton.setSelection(false);
		data = new GridData ();
		data.horizontalSpan = 3;
		data.horizontalAlignment = GridData.BEGINNING;
		remoteButton.setLayoutData (data);

		Label labLocation = new Label (outer, SWT.NONE);
		labLocation.setText (Messages.getMessage("profile_location"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labLocation.setLayoutData (data);
		
		locationText = new Text (outer, SWT.BORDER);
		locationText.setText(ClientConfig.REMOTE_PROFILE_LOCATION);
		locationText.setEnabled(false);
		data = new GridData ();
		data.widthHint = 200;
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		locationText.setLayoutData (data);
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				
				DirectoryDialog dd = new DirectoryDialog(getShell());
				String filter = ClientConfig.getValue(ClientConfig.EXPORT_WEB);
				if (filter != null) {
					File f = new File(filter);
					dd.setFilterPath(f.getAbsolutePath());
				}
				String dir = dd.open();
				if (dir == null) {
					fileLocationText.setText("");
				} else {
					fileLocationText.setText(dir);
				}
				
				checkButtonsEnabled();
			}
		});		
		
		localButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent se) {
				
				localButton.setSelection(true);
				
				remoteButton.setSelection(false);
				locationText.setEnabled(false);
				fileLocationText.setEnabled(false);
				browseButton.setEnabled(false);
				
			}
		});
		
		remoteButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent se) {
				
				remoteButton.setSelection(true);
				locationText.setEnabled(true);

				localButton.setSelection(false);
				fileLocationText.setEnabled(false);
				browseButton.setEnabled(false);
			}
		});
	
		
		locationText.addModifyListener(new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		});
		
		return outer;
	}


	protected void checkButtonsEnabled() {
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		if (remoteButton.getSelection()) {
			if (!locationText.getText().startsWith("http://")) {
				return;
			}
		}
		getButton(IDialogConstants.OK_ID).setEnabled(true);		
	}

	private boolean checkDirectory() {
		
		File f = new File(fileLocationText.getText());
		if (!f.exists()) return false;
		
		int found = 0;
		String[] filenames = f.list();
		for (int i = 0; i < filenames.length; i++) {
			if (filenames[i].equals("repository")) found++;
			if (filenames[i].startsWith("version")) found++;
			if (filenames[i].equals("workspaces")) found++;
		}
		return (found == 3);
	}

	public static NewServerProfileDialog getInstance(Shell shell) {
		
		if (instance == null) {
			instance = new NewServerProfileDialog(shell);
		}
		return instance;
	}
	
	
	/**
	 * Returns the new created author
	 * 
	 * @return Author that was created
	 */
	public ServerProfile getServerProfile() {
		
		return serverProfile;
	}

}
