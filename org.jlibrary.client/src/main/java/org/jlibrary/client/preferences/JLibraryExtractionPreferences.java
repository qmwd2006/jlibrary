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
package org.jlibrary.client.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jlibrary.client.Messages;

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
public class JLibraryExtractionPreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	public static final String P_FIND_KEYWORDS = "findKeywords";
	public static final String P_NUMBER_OF_KEYWORDS = "numberOfKeywords";

	public JLibraryExtractionPreferences() {
		
		super(GRID);
		setPreferenceStore(JFacePreferences.getPreferenceStore());
		setDescription(Messages.getMessage("preferences_extraction_description"));
		setTitle(Messages.getMessage("preferences_extraction"));
		initializeDefaults();
	}
/**
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		
		IPreferenceStore store = JFacePreferences.getPreferenceStore();
		store.setDefault(P_FIND_KEYWORDS, true);
		store.setDefault(P_NUMBER_OF_KEYWORDS, String.valueOf(5));
	}
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	public void createFieldEditors() {
		
		BooleanFieldEditor field =	new BooleanFieldEditor(	
				P_FIND_KEYWORDS, 
				Messages.getMessage("preferences_extraction_find"), 
				getFieldEditorParent());		
		addField(field);
		
		IntegerFieldEditor fieldNumber = new IntegerFieldEditor(	
				P_NUMBER_OF_KEYWORDS, 
				Messages.getMessage("preferences_extraction_noKeywords"), 
				getFieldEditorParent());		
		fieldNumber.setTextLimit(2);
		addField(fieldNumber);
		
	}
	
	public void init(IWorkbench workbench) {}
}