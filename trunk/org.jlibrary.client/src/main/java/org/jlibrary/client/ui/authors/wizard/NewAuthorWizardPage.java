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
package org.jlibrary.client.ui.authors.wizard;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

public class NewAuthorWizardPage extends org.eclipse.jface.wizard.WizardPage {
	
	private Text nameText;
	private Text bioText;
	/**
	 * 
	 * @param pageName
	 * @param description
	 */
	public NewAuthorWizardPage(String pageName, String description) {

		super(pageName);
		setTitle(pageName);
		setPageComplete(false);
		setDescription(description);
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_GENERIC_WIZARD));
	}
	/**
	 * 
	 */
	private ModifyListener modifyListener = new ModifyListener()
	{
		public void modifyText(ModifyEvent e) {

			checkButtonsEnabled();
		}
	};
	/**
	 * 
	 */
	public void createControl(Composite outer)
	{
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		outer.setLayout (gridLayout);
		
		Label labName = new Label (outer, SWT.NONE);
		labName.setText (Messages.getMessage("author_name"));
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labName.setLayoutData (data);
		
		nameText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 300;
		data.horizontalSpan = 3;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		nameText.setLayoutData (data);

		Label labDescription = new Label (outer, SWT.NONE);
		labDescription.setText (Messages.getMessage("author_bio"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labDescription.setLayoutData (data);
		
		bioText = new Text (outer, SWT.BORDER | SWT.WRAP);
		data = new GridData ();
		data.widthHint = 370;
		data.heightHint = 150;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		bioText.setLayoutData (data);
		
		nameText.addModifyListener(modifyListener);
		bioText.addModifyListener(modifyListener);
        
		nameText.addFocusListener(new FocusAdapter() {
			private boolean noNameText = true;

			public void focusLost(FocusEvent e) {
				
				if (noNameText) {
					noNameText = false;
					if (bioText.getText().equals("")) {
						bioText.setText(nameText.getText());
						bioText.selectAll();
					}
				}
				
				super.focusLost(e);
			}
		});
		
		bioText.addTraverseListener(new TraverseListener () {
			public void keyTraversed(TraverseEvent e) {
				switch (e.detail) {
					case SWT.TRAVERSE_TAB_NEXT:
						e.doit = true;
					case SWT.TRAVERSE_TAB_PREVIOUS: {
						nameText.setFocus();
						e.doit = true;
					}
				}
			}
		});
		
        setControl(outer);
	}
	/**
	 * 
	 */
	private void checkButtonsEnabled()
	{
    	setPageComplete(false);
		if (nameText.getText().equals("")) {
			setMessage(Messages.getMessage("warning_author"), IMessageProvider.ERROR);
			return;
		}
		if (bioText.getText().equals("")) {
			setMessage(Messages.getMessage("warning_description"), IMessageProvider.ERROR);
			return;
		}
		setErrorMessage(null);
		setMessage(getDescription());
		setPageComplete(true);
    }
	/**
	 * 
	 * @return
	 */
	public String getAuthorName() {
		return nameText.getText();
	}
	/**
	 * 
	 * @return
	 */
	public String getAuthorBioDescription() {
		return bioText.getText();
	}

}
