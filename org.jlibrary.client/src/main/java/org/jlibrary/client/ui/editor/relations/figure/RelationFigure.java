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
package org.jlibrary.client.ui.editor.relations.figure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;
import org.jlibrary.client.SharedFonts;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Relation figure. It will draw a document and its data.
 */
public class RelationFigure extends Figure {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public static Color relationColor = new Color(null,255,255,206);
	private CompartmentFigure attributes = new CompartmentFigure();
	
	private ArrayList childFigures = new ArrayList();
	
	private int hintWidth;
	private Document document;

	private LineBorder originalBorder;
	
	public RelationFigure(Document document) {
		
		this.document = document;
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setSpacing(5);
		originalBorder = new LineBorder(ColorConstants.black,1);
		setLayoutManager(layout);
		setBorder(originalBorder);
		setBackgroundColor(relationColor);
		setOpaque(true);
		
		Label labName = new Label(document.getName(),
								  SharedImages.getImageForNode(document));
		labName.setFont(SharedFonts.getFont(SharedFonts.RELATION_TITLE));
		add(labName);
		Repository repository = RepositoryRegistry.getInstance().getRepository(document.getRepository());

		Label labRepository = new Label(repository.getName(),
									    SharedImages.getImage(SharedImages.IMAGE_OPEN_REPOSITORY));
		Label labTitle = new Label(document.getMetaData().getTitle(),
								   SharedImages.getImage(SharedImages.IMAGE_ATTRIBUTE));
		Label labDate = new Label(sdf.format(document.getDate()),
				   				  SharedImages.getImage(SharedImages.IMAGE_ATTRIBUTE));
				
		
		attributes.add(labRepository);
		attributes.add(labTitle);
		attributes.add(labDate);
		
		add(attributes);
		
		computeWidth(repository,document);
		setSize(getHintWidth(),100);
	}

	/**
	 * @param document
	 */
	private void computeWidth(Repository repository, Document document) {
		
		int length1 = repository.getName().length();
		int length2 = document.getMetaData().getTitle().length();
		int length3 = sdf.format(document.getDate()).length();
		
		int max = NumberUtils.max(length1,length2,length3);
		
		hintWidth = max*10 + 10;
	}

	/**
	 * @return Returns the childFigures.
	 */
	public ArrayList getChildFigures() {
		return childFigures;
	}
	
	/**
	 * @return Returns the hintWidth.
	 */
	public int getHintWidth() {
		return hintWidth;
	}
	
	public Document getDocument() {
		return document;
	}
	public LineBorder getOriginalBorder() {
		return originalBorder;
	}
	

}
