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
import java.util.Iterator;
import java.util.List;

import org.jlibrary.core.entities.Document;


/**
 * This is the diagram that will contain all the documents structure. It will 
 * load the edited documents relations hierarchy and will be on charge of 
 * refreshing it when necessary
 * <p/>
 * @author martin
 */
public class RelationsSchema {

	private GEFDocument document;
	private RelationsRegistry registry = new RelationsRegistry();
	private ArrayList documents = new ArrayList();
	
	/**
	 * constructor
	 * 
	 * @param rootDocument Edited document
	 */
	public RelationsSchema(Document rootDocument) {
		
		this.document = new GEFDocument(rootDocument);
		
		addRelations(rootDocument);
	}	
	
	private void addRelations(Document document) {
		
		GEFDocument gefDocument = new GEFDocument(document);
		documents.add(gefDocument);
		registry.addDocument(gefDocument);
		
		if (document.getRelations() != null) {
			Iterator it = document.getRelations().iterator();
			while (it.hasNext()){
				Document relation = (Document) it.next();
				if (!registry.isDocumentRegistered(relation.getId())) {
					addRelations(relation);
					GEFDocument gefDestination = registry.getDocument(relation.getId());
					Relationship relationship = 
						new Relationship(gefDocument,gefDestination);
					gefDocument.addSourceConnection(relationship);
					gefDestination.addTargetConnection(relationship);
				}
			}
		}
	}
	/**
	 * Returns the GEF object for the root document
	 * 
	 * @return GEFDocument Root document
	 */
	public GEFDocument getRootDocument() {
		
		return document;
	}
	/**
	 * Returns all the documents that will be rendered on the GEF diagram
	 * 
	 * @return List All the documents
	 */
	public List getDocuments() {
		
		return documents;
	}
	/**
	 * Refreshes the diagram data
	 *
	 */
	public void reloadData() {

		registry.clear();
		documents.clear();
		addRelations(document.getDocument());
	}
}