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
package org.jlibrary.client.ui.repository.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.dialogs.ListDialog;
import org.jlibrary.client.ui.list.ListContentProvider;
import org.jlibrary.client.ui.list.ListLabelProvider;
import org.jlibrary.client.util.ScreenUtils;

/**
 * Dialog for selecting a repository
 */
public class RepositoryListDialog extends ListDialog {

	/**
	 * @param arg0
	 */
	public RepositoryListDialog(Shell shell) {

		super(shell, Messages.getMessage("repository_dialog_title"));
	}
	
	/**
	 * @see org.jlibrary.client.ui.dialogs.ListDialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		
		super.configureShell(shell);	
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_OPEN_REPOSITORY));
		ScreenUtils.centerShell(shell);
	}

	
	/**
	 * @see org.jlibrary.client.ui.dialogs.ListDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		setLabelProvider(new ListLabelProvider(SharedImages.getImage(SharedImages.IMAGE_NODE_REPOSITORY)));
		setContentProvider(new ListContentProvider());
		
		return super.createDialogArea(parent);
	}
}
