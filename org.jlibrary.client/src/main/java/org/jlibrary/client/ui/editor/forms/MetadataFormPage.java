/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
package org.jlibrary.client.ui.editor.forms;

import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.editor.JLibraryFormPage;

public abstract class MetadataFormPage extends JLibraryFormPage {

	public MetadataFormPage(JLibraryEditor editor, String title) {
		super(editor, title);
	}

	/**
	 * Returns the categories section
	 * 
	 * @return CategoriesSection Categories section
	 */
	public CategoriesSection getCategoriesSection() {
		
		return null;
	}
	
	/**
	 * Returns the resources section
	 * 
	 * @return ResourcesSection resources section
	 */
	public ResourcesSection getResourcesSection() {
		
		return null;
	}
	
	/**
	 * Returns the relations section
	 * 
	 * @return RelationsSection relations section
	 */
	public RelationsSection getRelationsSection() {
		
		return null;
	}	
	
	public NotesSection getNotesSection() {
		
		return null;
	}

    /**
     * Returns the custom properties section.
     *
     * @return section with custom properties
     */
    public CustomPropertiesSection getCustomPropertiesSection() {
        return null;
    }

}
