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
package org.jlibrary.client.ui.versions;

import java.util.WeakHashMap;

import org.jlibrary.core.entities.Document;

/**
 * @author martin
 *
 * Registry that will store document versions with the orignal document that
 * they references. This will be useful when saving documents to detect when 
 * we are saving a restored document version from when we are saving a normal
 * document.
 */
public class EditorVersionRegistry {

	// Weak, so if the version if closed without saving the entry disappears
	private static WeakHashMap versions = new WeakHashMap();
	
	public static void addDocumentVersion(Document version, Document original) {
		
		versions.put(version,original);
	}
	
	public static boolean isDocumentVersion(Document document) {
		
		return versions.containsKey(document);
	}
	
	public static Document getOriginalDocument(Document version) {
		
		return (Document)versions.get(version);
	}
}
