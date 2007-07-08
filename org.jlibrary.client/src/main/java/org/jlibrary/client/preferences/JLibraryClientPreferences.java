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

import java.util.Locale;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jlibrary.client.Messages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.i18n.LocaleService;

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
public class JLibraryClientPreferences
	extends PreferencePage
	implements IWorkbenchPreferencePage {
	
	public static final String P_LANGUAGE = "language";

	private String currentLanguage;

	private CCombo languageCombo;
	
	public JLibraryClientPreferences() {
		
		super();

		setDescription(Messages.getMessage("preferences_client_description"));
		setTitle(Messages.getMessage("preferences_client"));
		initializeDefaults();
	}
	
	/**
	 * Sets the default values of the preferences.
	 */
	private void initializeDefaults() {

		Locale locale = ClientConfig.getUserLocale();
		String language = "";
		if (locale != null) {
			language = locale.getLanguage();
		}
				
		currentLanguage = language;
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		
		initializeDefaults();
		super.performDefaults();
	}
	
	private void initializeFields() {

		Locale locale = ClientConfig.getUserLocale();
		String language = "";
		if (locale != null) {
			language = locale.getLanguage();
		}
		
		currentLanguage = language;
	}
	
	protected Control createContents(Composite parent) {
				
		initializeFields();
		
		Composite holder = new Composite(parent, SWT.NONE);
		createGeneralPage(holder);
		return holder;
	}
	
	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		
		setValues();
		
		return super.performOk();
	}
	
	private void setValues() {
		
		String choosenDescription = languageCombo.getText();
		String language = 
			LocaleService.getInstance().getLanguageForDescription(choosenDescription);
		
		ClientConfig.setUserLocale(new Locale(language));
		
		currentLanguage = language;
	}
	
	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performApply()
	 */
	public void performApply() {
		
		setValues();
		
		super.performApply();
	}
	
	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
	 */
	public boolean performCancel() {
		
		String currentDescription = 
			LocaleService.getInstance().getDescriptionForLanguage(currentLanguage);
		languageCombo.setText(currentDescription);
		
		return super.performCancel();
	}
	
	private void createGeneralPage(Composite parent) {
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		parent.setLayout(layout);
		
		Label labelLanguage = new Label(parent, SWT.NONE);
		labelLanguage.setText(Messages.getMessage("preferences_client_language"));
		GridData data = new GridData();
		labelLanguage.setLayoutData(data);
		
		languageCombo = new CCombo(parent, SWT.BORDER);
		
		String[] languages = LocaleService.getInstance().getSupportedUILanguageDescriptions();
		if (ClientConfig.getUserLocale() == null) {
			languageCombo.add("");
		}
		for (int i = 0; i < languages.length; i++) {
			languageCombo.add(languages[i]);
		}
		
		String currentDescription = "";
		if (!currentLanguage.equals("")) {
			currentDescription = 
				LocaleService.getInstance().getDescriptionForLanguage(currentLanguage);
		}
		languageCombo.setText(currentDescription);
		languageCombo.setEditable(false);
		
		data = new GridData();
		data.widthHint = 200;
		languageCombo.setLayoutData(data);
	}		
	
	public void init(IWorkbench workbench) {}
}