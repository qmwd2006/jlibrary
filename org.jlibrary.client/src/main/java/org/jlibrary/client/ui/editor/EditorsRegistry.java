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
package org.jlibrary.client.ui.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.ui.IEditorPart;

/**
 * @author martin
 *
 * Registry for editor parts
 */
public class EditorsRegistry {

	private HashMap editors = new HashMap();
	
	private static EditorsRegistry instance = null;
	
	private EditorsRegistry() {}
	
	public IEditorPart getEditor(String key) {
		return (IEditorPart)editors.get(key);
	}
	
	public Object put(String key, IEditorPart editor) {
		return editors.put(key,editor);
	}
	
	public Object remove(String key) {
		
		return editors.remove(key);
	}
	
	public void remove(IEditorPart editor) {
		
		Iterator it = editors.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue().equals(editor)) {
				it.remove();
				return;
			}
		}
	}
	
	/**
	 * Returns the number of opened editors
	 * 
	 * @return int Number of opened editors
	 */
	public int size() {
		
		return editors.size();
	}
	
	public static EditorsRegistry getInstance() {
		
		if (instance == null) {
			instance = new EditorsRegistry();
		}
		return instance;
	}
}
