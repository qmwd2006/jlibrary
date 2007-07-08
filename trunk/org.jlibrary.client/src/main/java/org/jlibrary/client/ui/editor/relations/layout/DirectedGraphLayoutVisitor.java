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
package org.jlibrary.client.ui.editor.relations.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.jlibrary.client.ui.editor.relations.figure.RelationFigure;
import org.jlibrary.client.ui.editor.relations.part.DocumentPart;
import org.jlibrary.client.ui.editor.relations.part.RelationsDiagramPart;
import org.jlibrary.client.ui.editor.relations.part.RelationshipPart;

/**
 * Visitor with support for populating nodes and edges of DirectedGraph
 * from model objects
 * <p/>
 * Took and adapted from the GEF samples. Original by Phil Zoio
 * <p/>
 * @author martin
 */
public class DirectedGraphLayoutVisitor
{

	Map partToNodesMap;
	DirectedGraph graph;

	/**
	 * Public method for reading graph nodes
	 */
	public void layoutDiagram(RelationsDiagramPart diagram)
	{

		partToNodesMap = new HashMap();
		
		graph = new DirectedGraph();
		addNodes(diagram);
		if (graph.nodes.size() > 0)
		{	
			addEdges(diagram);
			new NodeJoiningDirectedGraphLayout().visit(graph);
			applyResults(diagram);
		}

	}

	//******************* SchemaDiagramPart contribution methods **********/

	protected void addNodes(RelationsDiagramPart diagram)
	{
		GraphAnimation.recordInitialState(diagram.getFigure());

		for (int i = 0; i < diagram.getChildren().size(); i++)
		{
			DocumentPart tp = (DocumentPart) diagram.getChildren().get(i);
			addNodes(tp);
		}
	}

	/**
	 * Adds nodes to the graph object for use by the GraphLayoutManager
	 */
	protected void addNodes(DocumentPart tablePart)
	{
		Node n = new Node(tablePart);
		n.width = tablePart.getFigure().getPreferredSize(400, 300).width;
		n.height = tablePart.getFigure().getPreferredSize(400, 300).height;
		n.setPadding(new Insets(10, 8, 10, 12));
		partToNodesMap.put(tablePart, n);
		graph.nodes.add(n);
	}

	protected void addEdges(RelationsDiagramPart diagram)
	{
		for (int i = 0; i < diagram.getChildren().size(); i++)
		{
			DocumentPart tablePart = (DocumentPart) diagram.getChildren().get(i);
			addEdges(tablePart);
		}
	}

	//******************* TablePart contribution methods **********/

	protected void addEdges(DocumentPart tablePart)
	{
		List outgoing = tablePart.getSourceConnections();
		for (int i = 0; i < outgoing.size(); i++)
		{
			RelationshipPart relationshipPart = (RelationshipPart) tablePart.getSourceConnections().get(i);
			addEdges(relationshipPart);
		}
	}

	//******************* RelationshipPart contribution methods **********/

	protected void addEdges(RelationshipPart relationshipPart)
	{
		GraphAnimation.recordInitialState((Connection) relationshipPart.getFigure());
		Node source = (Node) partToNodesMap.get(relationshipPart.getSource());
		Node target = (Node) partToNodesMap.get(relationshipPart.getTarget());
		Edge e = new Edge(relationshipPart, source, target);
		e.weight = 2;
		graph.edges.add(e);
		partToNodesMap.put(relationshipPart, e);
	}

	//******************* SchemaDiagramPart apply methods **********/

	protected void applyResults(RelationsDiagramPart diagram)
	{
		applyChildrenResults(diagram);
	}

	protected void applyChildrenResults(RelationsDiagramPart diagram)
	{
		for (int i = 0; i < diagram.getChildren().size(); i++)
		{
			DocumentPart tablePart = (DocumentPart) diagram.getChildren().get(i);
			applyResults(tablePart);
		}
	}

	protected void applyOwnResults(RelationsDiagramPart diagram)
	{
	}

	//******************* TablePart apply methods **********/

	public void applyResults(DocumentPart tablePart)
	{

		Node n = (Node) partToNodesMap.get(tablePart);
		RelationFigure tableFigure = (RelationFigure) tablePart.getFigure();

		Rectangle bounds = new Rectangle(n.x, n.y, tableFigure.getPreferredSize().width,
				tableFigure.getPreferredSize().height);

		tableFigure.setBounds(bounds);

		for (int i = 0; i < tablePart.getSourceConnections().size(); i++)
		{
			RelationshipPart relationship = (RelationshipPart) tablePart.getSourceConnections().get(i);
			applyResults(relationship);
		}
	}

	//******************* RelationshipPart apply methods **********/

	protected void applyResults(RelationshipPart relationshipPart)
	{

		Edge e = (Edge) partToNodesMap.get(relationshipPart);
		NodeList nodes = e.vNodes;

		PolylineConnection conn = (PolylineConnection) relationshipPart.getConnectionFigure();
		conn.setTargetDecoration(new PolygonDecoration());
		if (nodes != null)
		{
			List bends = new ArrayList();
			for (int i = 0; i < nodes.size(); i++)
			{
				Node vn = nodes.getNode(i);
				int x = vn.x;
				int y = vn.y;
				if (e.isFeedback)
				{
					bends.add(new AbsoluteBendpoint(x, y + vn.height));
					bends.add(new AbsoluteBendpoint(x, y));

				}
				else
				{
					bends.add(new AbsoluteBendpoint(x, y));
					bends.add(new AbsoluteBendpoint(x, y + vn.height));
				}
			}
			conn.setRoutingConstraint(bends);
		}
		else
		{
			conn.setRoutingConstraint(Collections.EMPTY_LIST);
		}

	}

}