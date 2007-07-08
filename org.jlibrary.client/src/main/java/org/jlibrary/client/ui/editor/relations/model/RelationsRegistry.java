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
package org.jlibrary.client.ui.editor.relations.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This simply registry will store the already drawn documents.
 * 
 * @author martin
 *
 */
public class RelationsRegistry {

	private Map registry = new HashMap();

	/**
	 * Adds a document to the registry
	 * 
	 * @param gefDocument Document
	 */
	public void addDocument(GEFDocument gefDocument) {
		
		registry.put(gefDocument.getDocument().getId(),gefDocument);
	}
	/**
	 * Tells if a document is loaded on the registry
	 * 
	 * @param documentId Document's id
	 * 
	 * @return boolean <code>true</code> if the document is already registered 
	 * and <code>false</code> otherwise
	 */
	public boolean isDocumentRegistered(String documentId) {
		
		return registry.get(documentId) != null;
	}
	
	/**
	 * Clears the registry
	 *
	 */
	public void clear() {
		
		registry.clear();
	}
	
	/**
	 * Looks for a document on the registry
	 * 
	 * @param documentId Document's id
	 * 
	 * @return GEFDocument Document or <code>null</code> if it cannot be found
	 */
	public GEFDocument getDocument(String documentId) {
		
		return (GEFDocument)registry.get(documentId);
	}
}
