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
package org.jlibrary.client.ui.editor.relations.part;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.jlibrary.client.ui.editor.relations.figure.SchemaFigure;
import org.jlibrary.client.ui.editor.relations.layout.GraphLayoutManager;
import org.jlibrary.client.ui.editor.relations.model.RelationsSchema;

/**
 * <p>This is the parent editor part for all documents. It will hold all the 
 * documents that compose the relations tree for the original edited 
 * document.</p>
 * 
 * @author martin
 */
public class RelationsDiagramPart extends AbstractGraphicalEditPart {

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure()
	{
		Figure f = new SchemaFigure();
		GraphLayoutManager layoutManager = new GraphLayoutManager(this);
		f.setLayoutManager(layoutManager);
		return f;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren()
	{
		List children = new ArrayList();
		RelationsSchema schema = (RelationsSchema)getModel();
		children.addAll(schema.getDocuments());
		return children;
		
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refresh()
	 */
	public void refresh() {
		
		RelationsSchema schema = (RelationsSchema)getModel();
		schema.reloadData();
		super.refresh();
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {

		List children = getChildren();
		for (int i=0;i<children.size();i++) {
			DocumentPart documentPart = (DocumentPart)children.get(i);
			documentPart.refreshVisuals();
		}
	}	
}