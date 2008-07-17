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
package org.jlibrary.client.ui.editor.relations.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.entities.Document;

/**
 * This model objects encapsulates a document adding additional data over it to
 * show documents on GEF diagrams.
 * 
 * @author martin
 */
public class GEFDocument {

	private List sourceConnections = new ArrayList();
	private List targetConnections = new ArrayList();
	private Document document;

	/**
	 * Constructor
	 * 
	 * @param document wrapped document
	 */
	public GEFDocument(Document document) {
		
		this.document = document;
	}
	/**
	 * Returns the icon for this document
	 * 
	 * @return Image Icon for this document
	 */
	public Image getIcon() {
	
			return SharedImages.getImageForPath(document.getPath());
	}

	/**
	 * Return a List of outgoing Connections.
	 * 
	 * @return List List of outgoing Connections.
	 */
	public List getSourceConnections() {
		return new ArrayList(sourceConnections);
	}
	
	/**
	 * Return a List of incoming Connections.
	 * 
	 * @return List List of incoming Connections.
	 */
	public List getTargetConnections() {
		return new ArrayList(targetConnections);
	}

	/**
	 * Returns the wrapped document
	 * 
	 * @return Document Wrapped document
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Adds a source connection
	 * 
	 * @param relation Source connection
	 */
	public void addSourceConnection(Relationship relation) {
		sourceConnections.add(relation);
	}
	
	/**
	 * Adds a target connection
	 * 
	 * @param relation Target connection
	 */
	public void addTargetConnection(Relationship relation) {
		targetConnections.add(relation);
	}
}