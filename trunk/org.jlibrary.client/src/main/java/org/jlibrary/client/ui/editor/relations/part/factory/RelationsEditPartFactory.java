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
package org.jlibrary.client.ui.editor.relations.part.factory;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.jlibrary.client.ui.editor.relations.model.GEFDocument;
import org.jlibrary.client.ui.editor.relations.model.RelationsSchema;
import org.jlibrary.client.ui.editor.relations.model.Relationship;
import org.jlibrary.client.ui.editor.relations.part.DocumentPart;
import org.jlibrary.client.ui.editor.relations.part.RelationsDiagramPart;
import org.jlibrary.client.ui.editor.relations.part.RelationshipPart;

/**
 * Factory that maps model elements to edit parts.
 * 
 * @author martin
 */
public class RelationsEditPartFactory implements EditPartFactory {

	/**
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object modelElement) {
		
		EditPart part = getPartForElement(modelElement);
		part.setModel(modelElement);
		return part;
	}

	/**
	 * Maps an object to an EditPart. 
	 * 
	 * @throws RuntimeException If the part cannot be found
	 */
	private EditPart getPartForElement(Object modelElement) {
		
		if (modelElement instanceof GEFDocument) {
			return new DocumentPart();
		}
		
		if (modelElement instanceof RelationsSchema) {
			return new RelationsDiagramPart();
		}
		
		if (modelElement instanceof Relationship) {
			return new RelationshipPart();
		}
		
		throw new RuntimeException(
				"Can't create part for model element: "
				+ ((modelElement != null) ? modelElement.getClass().getName() : "null"));
	}
}