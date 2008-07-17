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
package org.jlibrary.client.ui.editor.relations.layout;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;


/**
 * Extended version of DirectedGraphLayout which allows DirectedGraphLayout
 * functionality to be used even when graph nodes either have no edges, or when part
 * of clusters isolated from other clusters of Nodes
 * <p/>
 * Took and adapted from the GEF samples. Original authro, Phil Zoio
 * <p/>
 * @author martin
 */
public class NodeJoiningDirectedGraphLayout extends DirectedGraphLayout
{

	/**
	 * @param graph public method called to handle layout task
	 */
	public void visit(DirectedGraph graph)
	{
		
		//add dummy edges so that graph does not fall over because some nodes
		// are not in relationships
		new DummyEdgeCreator().visit(graph);
		
		// create edges to join any isolated clusters
		new ClusterEdgeCreator().visit(graph);	
		
		super.visit(graph);
	}
}