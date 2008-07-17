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


/**
 * This class represents a relationship between two documents. It will be 
 * instantiated on GEF diagrams as a connection.
 * 
 * @author Phil Zoio
 */
public class Relationship {

	private GEFDocument parent;
	private GEFDocument child;

	/**
	 * Constructor. Takes a parent and a child. Note that this does not means 
	 * that one document is the parent of the other document, it only means 
	 * that in the diagram one document will appear on top of the other 
	 * document just as if it was his parent.
	 * 
	 * @param parent Parent document
	 * @param child Child document
	 */
	public Relationship(GEFDocument parent, GEFDocument child)
	{
		super();
		this.parent = parent;
		this.child = child;
	}

	/**
	 * Returns the parent document
	 * 
	 * @return GEFDocument parent document
	 */
	public GEFDocument getParentDocument()
	{
		return parent;
	}

	/**
	 * Returns the child document
	 * 
	 * @return GEFDocument child document
	 */
	public GEFDocument getChildDocument()
	{
		return child;
	}

	/**
	 * Sets the parent document
	 * 
	 * param parent Parent document
	 */
	public void setParentDocument(GEFDocument parent)
	{
		this.parent = parent;
	}	
	
	/**
	 * Sets the child document
	 * 
	 * param child Child document
	 */
	public void setChildDocument(GEFDocument child)
	{
		this.child = child;
	}
}