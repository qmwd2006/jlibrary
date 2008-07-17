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

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.jlibrary.client.ui.editor.relations.figure.RelationFigure;
import org.jlibrary.client.ui.editor.relations.model.GEFDocument;
import org.jlibrary.client.ui.repository.actions.OpenAction;
import org.jlibrary.core.entities.Document;

/**
 * <p>This is the editor part used to draw documents on GEF.</p>
 * 
 * @author martin
 */
public class DocumentPart extends AbstractGraphicalEditPart {

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		
		GEFDocument gefDocument = (GEFDocument)getModel();
	
		RelationFigure documentFigure = 
			new RelationFigure(gefDocument.getDocument());
		addListeners(documentFigure);
		return documentFigure;
	}


	private void addListeners(final RelationFigure documentFigure) {
	
		documentFigure.addMouseListener(new MouseListener() {
			
			public void mouseDoubleClicked(MouseEvent arg0) {}
			
			public void mousePressed(MouseEvent me) {
				
				Document document = documentFigure.getDocument();
				OpenAction action = new OpenAction();
				action.run(document);	
			}
			
			public void mouseReleased(MouseEvent me) {}
			
			
		});
	
		documentFigure.addMouseMotionListener(new MouseMotionListener() {
			public void mouseEntered(MouseEvent arg0) {
				documentFigure.setBorder(new LineBorder(ColorConstants.orange,2));
			}
			public void mouseExited(MouseEvent arg0) {
				documentFigure.setBorder(documentFigure.getOriginalBorder());
				
			}
			public void mouseDragged(MouseEvent me) {}
			public void mouseHover(MouseEvent arg0) {}
			public void mouseMoved(MouseEvent arg0) {}
		});
		
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	public List getModelSourceConnections() {
		
		GEFDocument document = (GEFDocument)getModel();
		return document.getSourceConnections();
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	public List getModelTargetConnections() {
		
		GEFDocument document = (GEFDocument)getModel();
		return document.getTargetConnections();	
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {

		RelationFigure relationFigure = (RelationFigure) getFigure();
		Point location = relationFigure.getLocation();
		RelationsDiagramPart parent = (RelationsDiagramPart) getParent();
		Rectangle constraint = new Rectangle(location.x, location.y, -1, -1);
		parent.setLayoutConstraint(this, relationFigure, constraint);
	}
}