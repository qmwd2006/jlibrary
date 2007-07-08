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
package org.jlibrary.client.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */
public class JLibraryPreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private static JLibraryPreferences instance;
	
	public JLibraryPreferences() {
		
		super(GRID);
		
		new JLibraryRepositoryPreferences();
		new JLibraryExtractionPreferences();
		
		setPreferenceStore(JFacePreferences.getPreferenceStore());
		setTitle(Messages.getMessage("preferences_jlibrary"));
		initializeDefaults();
		noDefaultAndApplyButton();
	}
	
	/**
	 * Sets the default values of the preferences.
	 */
	private void initializeDefaults() {}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {}
	

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		
		Composite composite = createComposite(parent);

		Label imageLabel = new Label(composite, SWT.NONE);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		imageLabel.setLayoutData(data);
		
		imageLabel.setImage(SharedImages.getImage(SharedImages.IMAGE_PRODUCT));

		Label text1 = new Label(composite,SWT.NONE);
		text1.setText(Messages.getMessage("preferences_product"));
		Label text2 = new Label(composite,SWT.NONE);
		text2.setText(Messages.getMessage("preferences_version"));
		Label text3 = new Label(composite,SWT.NONE);
		text3.setText(Messages.getMessage("preferences_author"));
		
		return composite;
		
	}
	
	
	
	protected Composite createComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		composite.setFont(parent.getFont());
		return composite;
	}	
	
	public void init(IWorkbench workbench) {}
	
	public static JLibraryPreferences getInstance() {
		
		if (instance == null) {
			newInstance();
		}
		return instance;
	}
	
	public static void newInstance() {
		
		instance = new JLibraryPreferences();
	}
}