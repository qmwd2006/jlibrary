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

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.help.AboutInfo;

/**
 * Displays information about the product.
 *
 * @author martin
 */
public class AboutDialog extends ProductInfoDialog {
	private final static int MAX_IMAGE_WIDTH_FOR_TEXT = 250;

	/**
	 * About info for the primary feature.
	 * Private field used in inner class.
	 * @issue org.eclipse.ui.internal.AboutInfo - illegal reference to generic workbench internals
	 */
	/* package */ AboutInfo primaryInfo;

	private Image image; //image to display on dialog

	private ArrayList images = new ArrayList();
	private StyledText text;

	/**
	 * Create an instance of the AboutDialog
	 */
	
	public AboutDialog(
		IWorkbenchWindow window,
		AboutInfo primaryInfo) {
		
		super(window.getShell());
		this.primaryInfo = primaryInfo;

	}
	

	public boolean close() {
		//get rid of the image that was displayed on the left-hand side of the Welcome dialog
		if (image != null) {
			image.dispose();
		}
		for (int i = 0; i < images.size(); i++) {
			((Image) images.get(i)).dispose();
		}
		return super.close();
	}
	/* (non-Javadoc)
	 * Method declared on Window.
	 */
	protected void configureShell(Shell newShell) {
		
		super.configureShell(newShell);
		String name = null;
		if (primaryInfo != null) {
			name = primaryInfo.getProductName();
		}
		newShell.setText(name);
		
	}
	/**
	 * Add buttons to the dialog's button bar.
	 *
	 * Subclasses should override.
	 *
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = (GridLayout) parent.getLayout();
		layout.numColumns++;
		layout.makeColumnsEqualWidth = false;

		Button b =
			createButton(
				parent,
				IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL,
				true);
		b.setFocus();
	}
	/**
	 * Creates and returns the contents of the upper part 
	 * of the dialog (above the button bar).
	 *
	 * Subclasses should overide.
	 *
	 * @param the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		
		getShell().setImage(SharedImages.getImage(SharedImages.IMAGE_ABOUT_BIG));
		
		setHandCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_HAND));
		setBusyCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_WAIT));
		getShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (getHandCursor() != null) {
					getHandCursor().dispose();
				}
				if (getBusyCursor() != null) {
					getBusyCursor().dispose();
				}
			}
		});

		ImageDescriptor imageDescriptor = null;
		if (primaryInfo != null) {
			imageDescriptor = primaryInfo.getAboutImage(); // may be null
		}
		if (imageDescriptor != null) {
			image = imageDescriptor.createImage();
		}
		if (image == null
			|| image.getBounds().width <= MAX_IMAGE_WIDTH_FOR_TEXT) {
			// show text
			String aboutText = null;
			if (primaryInfo != null) {
				aboutText = primaryInfo.getAboutText(); // may be null
			}
			if (aboutText != null) {
				// get an about item
				setItem(scan(aboutText));
			}
		}

		// page group
		Composite outer = (Composite) super.createDialogArea(parent);
		outer.setSize(outer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		GridLayout layout = new GridLayout();
		outer.setLayout(layout);
		outer.setLayoutData(new GridData(GridData.FILL_BOTH));

		// the image & text	
		Composite topContainer = new Composite(outer, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = (image == null || getItem() == null ? 1 : 2);
		layout.marginWidth = 0;
		topContainer.setLayout(layout);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		topContainer.setLayoutData(data);

		//image on left side of dialog
		if (image != null) {
			Label imageLabel = new Label(topContainer, SWT.NONE);
			data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.BEGINNING;
			data.grabExcessHorizontalSpace = false;
			imageLabel.setLayoutData(data);
			imageLabel.setImage(image);
		}

		if (getItem() != null) {
			// text on the right
			text = new StyledText(topContainer, SWT.MULTI | SWT.READ_ONLY);
			text.setCaret(null);
			text.setFont(parent.getFont());
			data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.BEGINNING;
			data.grabExcessHorizontalSpace = true;
			text.setText(getItem().getText());
			text.setLayoutData(data);
			text.setCursor(null);
			text.setBackground(topContainer.getBackground());
			setLinkRanges(text, getItem().getLinkRanges());
			addListeners(text);
		}

		// horizontal bar
		Label bar = new Label(outer, SWT.HORIZONTAL | SWT.SEPARATOR);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		bar.setLayoutData(data);

		// feature images
		Composite featureContainer = new Composite(outer, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;
		featureContainer.setLayout(rowLayout);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		featureContainer.setLayoutData(data);

		// spacer
		bar = new Label(outer, SWT.NONE);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		bar.setLayoutData(data);

		return outer;
	}
}
