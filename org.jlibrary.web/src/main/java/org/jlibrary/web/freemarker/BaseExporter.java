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
package org.jlibrary.web.freemarker;

import java.util.Collection;
import java.util.Iterator;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;

/**
 * @author martin
 *
 * Empty base implementation for a sample exporter. Use it if you wants as a 
 * base for your exporters
 */
public class BaseExporter {

	private static final ExportFilter EMPTY_EXPORT_FILTER = new ExportFilter() {
		public Repository filter(Repository repository) {return repository;}
		public Node filter(Node node) {return node;}
		public Collection filterCategoryNodes(Category category, 
											  Collection nodes) {
			return nodes;
		}
	};

	private RepositoryContext context;
	
	public ExportFilter getFilter() {
		return EMPTY_EXPORT_FILTER;
	}
	
	public void initExportProcess(RepositoryContext context) 
		throws ExportException {}
	
	public void endExportProcess(RepositoryContext context) 
		throws ExportException {}
	
	public String exportDocument(Document document, RepositoryContext context) 
		throws ExportException {return "";}
	
	public String exportResource(ResourceNode resource, RepositoryContext context) 
		throws ExportException {return "";}

	public String exportCategory(Category category, RepositoryContext context) 
		throws ExportException {return "";}
	
	public String exportDirectory(Directory directory, RepositoryContext context) 
		throws ExportException {return "";}
	
	public void exportRepository(RepositoryContext context) 
		throws ExportException {}
	
	public void filterRepository(RepositoryContext context) 
		throws ExportException {}
	
	private int calculateLength(Node node) {
		
		int length = 1;
		if (node.getNodes() != null) {
			Iterator it = node.getNodes().iterator();
			while (it.hasNext()) {
				length+=calculateLength((Node)it.next());
			}
		}
		return length;
	}
	
	private int calculateLength(Category category) {
		
		int length = 1;
		if (category.getCategories() != null) {
			Iterator it = category.getCategories().iterator();
			while (it.hasNext()) {
				length+=calculateLength((Category)it.next());
			}
		}
		return length;
	}

	public void setRepositoryContext(RepositoryContext context) {
		
		this.context = context;
	}
	
	public RepositoryContext getContext() {
		return context;
	}	
}
