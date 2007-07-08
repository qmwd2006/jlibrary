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
package org.jlibrary.client.export;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;

/**
 * @author martin
 *
 * Empty base implementation for a sample exporter. Use it if you wants as a 
 * base for your exporters
 */
public class BaseExporter implements Exporter {

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
	
	public void exportDocument(Document document, RepositoryContext context) 
		throws ExportException {}
	
	public void exportResource(ResourceNode resource, RepositoryContext context) 
		throws ExportException {}

	public void exportCategory(Category category, RepositoryContext context) 
		throws ExportException {}
	
	public void exportDirectory(Directory directory, RepositoryContext context) 
		throws ExportException {}
	
	public void exportRepository(RepositoryContext context) 
		throws ExportException {}
	
	public void filterRepository(RepositoryContext context) 
		throws ExportException {}

	public final IStatus export(RepositoryContext context,
					   		    IExportProgressMonitor monitor) 
										throws ExportException, 
											   OperationCanceledException {

		this.context = context;
		
		Repository repository = context.getRepository();
		
		// Repository can be lazy 
		if (repository.getRepositoryConfig().isEnabledLazyLoading()) {
			monitor.subTask(Messages.getMessage("export_html_lazy"));
			checkLazyDirectory(repository,repository.getRoot());
			monitor.worked(1);
		}
		
		filterRepository(context);
		repository = context.getRepository();
		
		monitor.initTask(Messages.getMessage("export_html_begin"),
					     loadTaskLength(repository));
		
		monitor.subTask(Messages.getMessage("export_html_begin"));
		initExportProcess(context);
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);
		
		monitor.subTask(Messages.getAndParseValue(
				"export_html_repository","%1",repository.getName()));		
		exportRepository(context);
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);
	
		try {
			internalExportDirectory(repository.getRoot(),context,monitor);
			
			// double check
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			Iterator it = repository.getCategories().iterator();
			while (it.hasNext()) {
				Category category = (Category) it.next();
				internalExportCategory(category,context,monitor);
			}			
		} catch (OperationCanceledException oce) {
			return Status.CANCEL_STATUS;
		}
		
		monitor.subTask(Messages.getMessage("export_html_end"));
		endExportProcess(context);
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);
		
		return Status.OK_STATUS;
	}
	
	private void internalExportDirectory(Directory directory, 
										 RepositoryContext context,
										 IExportProgressMonitor monitor) 
			throws ExportException, OperationCanceledException {
		
		monitor.subTask(Messages.getAndParseValue(
				"export_html_directory","%1",directory.getName()));
		
		exportDirectory(directory,context);
		monitor.worked(1);
		
		Iterator it = directory.getNodes().iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			if (node.isDirectory()) {
				internalExportDirectory((Directory)node,context,monitor);
			} else if (node.isDocument()) {
				monitor.subTask(Messages.getAndParseValue(
						"export_html_document","%1",node.getName()));		
				exportDocument((Document)node,context);
				monitor.worked(1);
			} else if (node.isResource()) {
				monitor.subTask(Messages.getAndParseValue(
						"export_html_resource","%1",node.getName()));		
				exportResource((ResourceNode)node,context);
				monitor.worked(1);				
			}
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}
	
	private void checkLazyDirectory(Repository repository,
									Directory directory) throws ExportException {
		
		if (((directory.getNodes() == null) ||
			 (directory.getNodes().isEmpty())) && directory.hasChildren()) {
			// load children
			Ticket ticket = repository.getTicket();
			ServerProfile profile = repository.getServerProfile();
			RepositoryService service = 
				JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			Collection children;
			try {
				children = service.findNodeChildren(ticket,directory.getId());
				directory.setNodes(new HashSet(children));
				EntityRegistry.getInstance().addNodeWithChildren(directory,false);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new ExportException(e);
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new ExportException(e);
			}
		}
		
		Iterator it = directory.getNodes().iterator();
		while (it.hasNext()) {
			Node child = (Node) it.next();
			if (child.isDirectory()) {
				checkLazyDirectory(repository,(Directory)child);
			}
		}
	}

	private void internalExportCategory(Category category,
										RepositoryContext context,
										IExportProgressMonitor monitor) 
			throws ExportException, OperationCanceledException {
		
		monitor.subTask(Messages.getAndParseValue(
				"export_html_category","%1",category.getName()));
		exportCategory(category,context);
		monitor.worked(1);
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		
		if (category.getCategories() != null) {
			Iterator it = category.getCategories().iterator();
			while (it.hasNext()) {
				Category child = (Category) it.next();
				internalExportCategory(child,context,monitor);
			}
		}
	}
	
	private int loadTaskLength(Repository repository) {
		
		// init, repository, end
		int initialLength = 1 + 1 + 1;
		
		int length = calculateLength(repository.getRoot());
		Iterator it = repository.getCategories().iterator();
		while (it.hasNext()) {
			Category category = (Category) it.next();
			length += calculateLength(category);
		}
		
		return length + initialLength;
	}
	
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

	public RepositoryContext getContext() {
		return context;
	}	
}
