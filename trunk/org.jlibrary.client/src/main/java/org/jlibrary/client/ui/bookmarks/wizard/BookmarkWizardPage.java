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
package org.jlibrary.client.ui.bookmarks.wizard;

import java.net.MalformedURLException;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
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
import org.jlibrary.client.util.URL;
import org.jlibrary.core.entities.Bookmark;

public class BookmarkWizardPage extends WizardPage {

	private Text nameText;
	private Text descriptionText;
	private Text urlText;
	
	private String type;
	private Bookmark bookmark;

	public BookmarkWizardPage(String pageName, String description, String type)
	{
		super(pageName);
		this.type = type;
		setTitle(pageName);
		setPageComplete(false);
		setDescription(description);
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_GENERIC_WIZARD));
	}
	public void createControl(Composite outer)
	{
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 10;
		outer.setLayout (gridLayout);

		Label labName = new Label (outer, SWT.NONE);
		labName.setText (Messages.getMessage("new_favorite_name"));
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labName.setLayoutData (data);
		
		nameText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 200;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		nameText.setLayoutData (data);
		if (bookmark != null) {
			nameText.setText(bookmark.getName());
		}
		
		if (type == Bookmark.FAVORITE)
		{
			Label labUrl = new Label (outer, SWT.NONE);
			labUrl.setText (Messages.getMessage("new_favorite_url"));
			data = new GridData ();
			data.horizontalAlignment = GridData.BEGINNING;
			labUrl.setLayoutData (data);
			
			urlText = new Text (outer, SWT.BORDER );
			data = new GridData ();
			data.widthHint = 200;
			data.heightHint = 30;
			data.horizontalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			urlText.setLayoutData (data);
			if (bookmark == null) {
				urlText.setText("http://");
			} else {
				urlText.setText(bookmark.getUrl());
			}
			
		}
		
		Label labDescription = new Label (outer, SWT.NONE);
		labDescription.setText (Messages.getMessage("new_favorite_description"));
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		labDescription.setLayoutData (data);

		descriptionText = new Text (outer, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData ();
		data.widthHint = 200;
		data.heightHint = 100;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		descriptionText.setLayoutData (data);
		if (bookmark != null) {
			descriptionText.setText(bookmark.getDescription());
		}
		
		ModifyListener modifyListener = new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
		
		nameText.addModifyListener(modifyListener);
		if (type == Bookmark.FAVORITE) {
			urlText.addModifyListener(modifyListener);
		}
		
		nameText.addFocusListener(new FocusAdapter() {
			private boolean noNameText = true;

			public void focusLost(FocusEvent e) {
				
				if (noNameText)
				{
					noNameText = false;
					if (descriptionText.getText().equals("")) {
						descriptionText.setText(nameText.getText());
						descriptionText.selectAll();
					}
				}
				
				super.focusLost(e);
			}
		});		
			
		descriptionText.addTraverseListener(new TraverseListener () {
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
	 * This method checks if the new user button can be enabled
	 */
	private void checkButtonsEnabled() {
		
		setPageComplete(false);

		if (nameText.getText().equals(""))
		{
			return;
		}

		if (type == Bookmark.FAVORITE)
		{
			if (urlText.getText().equals(""))
			{
				return;
			}
			try
			{
				new URL(urlText.getText());
			}
			catch (MalformedURLException e)
			{
				setMessage(Messages.getMessage("error_invalid_url"), IMessageProvider.ERROR);
				return;
			}
		}						
		setMessage(getDescription());
		setPageComplete(true);
	}

	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getNameText()
	{
		return nameText.getText().trim();
	}

	public String getDescriptionText()
	{
		return descriptionText.getText().trim();
	}

	public String getUrlText()
	{
		return urlText.getText().trim();
	}
	
	public void setBookmark(Bookmark bookmark) {
		
		this.bookmark = bookmark;
	}
}
