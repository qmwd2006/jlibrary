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
package org.jlibrary.client.ui.ccp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.dialogs.DocumentListDialog;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Paster for node paste operations
 */
public class CategoryPaster implements Paster {
	
	static Logger logger = LoggerFactory.getLogger(CategoryPaster.class);
	
	private List notCopied = new ArrayList();
	
	/**
	 * @see org.jlibrary.client.ui.ccp.Paster#paste(java.lang.Object, java.lang.Object)
	 */
	public void paste(Object source, 
					  Object destination,
					  boolean move) throws PasteException {

		paste(source,destination,move,new NullProgressMonitor());
	}

	/**
	 * @see org.jlibrary.client.ui.ccp.Paster#paste(java.lang.Object, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void paste(Object source, 
					  Object destination,
					  boolean move,
					  IProgressMonitor monitor) throws PasteException,
					  								   OperationCanceledException {

		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		
		try {
			Object[] toDrop = (Object[])source;
			if (toDrop[0] instanceof Node) {
				monitor.beginTask(Messages.getMessage("copy_job_name"),calculateLength(toDrop));
			} else {
				monitor.beginTask(Messages.getMessage("copy_job_name"),toDrop.length);
			}
			for (int i = 0; i < toDrop.length; i++) {
				if (toDrop[i] instanceof Category) {
					copyCategory((Category)toDrop[i],destination,monitor);
				} else {
					if ((((Node)toDrop[i]).getRepository().equals(((Category)destination).getRepository()))) {
						if (!isIncluded((Node)toDrop[i],(Object[])toDrop)) {
							copyNode((Node)toDrop[i],(Category)destination,monitor);
						}
					}
				}
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		} catch (RepositoryException e) {
			
            logger.error(e.getMessage(),e);
			throw new PasteException(e);
		} catch (SecurityException e) {
			
            logger.error(e.getMessage(),e);
			throw new PasteException(e);
		}
	}

	private void copyCategory(Category source, 
						  	  Object destination, 
							  IProgressMonitor monitor) throws RepositoryException, 
							  								   SecurityException {

		Repository repository = RepositoryRegistry.getInstance().getRepository(source.getRepository());
		Ticket ticket = repository.getTicket();
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
				
		CategoryProperties categoryProperties;
        try {
            categoryProperties = new CategoryProperties();
            if (destination instanceof Repository) {
	            categoryProperties.put(CategoryProperties.CATEGORY_PARENT, 
		          		   new PropertyDef(CategoryProperties.CATEGORY_PARENT, 
		          		   				   null));
            } else {
	            categoryProperties.put(CategoryProperties.CATEGORY_PARENT, 
		          		   new PropertyDef(CategoryProperties.CATEGORY_PARENT, 
		          		   				   ((Category)destination).getId()));            	
            }
        } catch (Exception e) {
        	
            logger.error(e.getMessage(),e);
            return;
        }

        final Category originalParent = source.getParent();
		try {
			service.updateCategory(ticket,source.getId(),categoryProperties);
						
			if (destination instanceof Repository) {
				source.setParent(null);
			} else {
				source.setParent((Category)destination);
			}
			
			if (originalParent != null) {
				// It seems that hibernate doesn't like .remove on his sets
				Set set = originalParent.getCategories();
				Iterator it = set.iterator();
				while (it.hasNext()) {
					Object obj = (Object)it.next();
					if (obj.equals(source)) {
						it.remove();
						break;
					}
				}
			} else {
				repository.getCategories().remove(source);
			}
			
			if (!(destination instanceof Repository)) {
				Category categoryDestination = (Category)destination;
				if (categoryDestination.getCategories() == null) {
					categoryDestination.setCategories(new HashSet());
				}
				categoryDestination.getCategories().add(source);
			} else {
				repository.getCategories().add(source);
			}			
			source.getCategories().remove(source);
		} catch (final SecurityException se) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(new Shell(),
							  "ERROR",
							  Messages.getMessage("security_exception"),
							  new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
					StatusLine.setErrorMessage(se.getMessage());
				}
			});	
		} catch (final CategoryNotFoundException cnfe) {
			showError(cnfe);
		} catch (final RepositoryException e) {
			showError(e);				
		} finally {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					CategoriesView.refresh(originalParent);
				}
			});	
		}
        
        
	}

	private void showError(final Exception e) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(new Shell(),
						"ERROR",
						Messages.getMessage("category_update_error"),
						new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
				StatusLine.setErrorMessage(Messages.getMessage("category_update_error"));
			}
		});
	}

	private void copyNode(Node source, 
		  	  			  Category destination, 
						  IProgressMonitor monitor) throws RepositoryException, 
			  								   			   SecurityException {
		
		List documents = new ArrayList();
		getAllNodes(source,documents);

		monitor.subTask(Messages.getAndParseValue("copy_category_step","%1",source.getName()));
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(source.getRepository());
		Ticket ticket = repository.getTicket();
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		
		Iterator it = documents.iterator();
		while (it.hasNext()) {
			Document document = (Document) it.next();

			if (document.isDeletedDocument() ||
				((document.getLock() != null) && 
				 (document.getLock().getUserId().equals(ticket.getUser().getId())))) {
				notCopied.add(document);
				continue;
			}
			List categories = service.findCategoriesForNode(ticket, document.getId());
			if (categories.contains(destination)) {
				continue;
			}
			if (categories.size() == 1) {
				Category category = (Category)categories.get(0);
				if (category.isUnknownCategory()) {
					document.removeCategory(category);
				}
			}
			document.addCategory(destination);
			DocumentProperties docProperties = document.dumpProperties();
			
			try {
				service.updateDocument(ticket,docProperties);
			} catch (ResourceLockedException e) {
				notCopied.add(document);
				continue;			
			} catch (final RepositoryException e) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {				
						ErrorDialog.openError(new Shell(),
								"ERROR",
								Messages.getMessage("category_update_error"),
								new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
						StatusLine.setErrorMessage(Messages.getMessage("category_update_error"));
					}
				});
				return;
			} catch (SecurityException e) {
				notCopied.add(document);
				continue;
			} finally {
				monitor.internalWorked(1);
			}
		}
	}	
	
	
	private void getAllNodes(Node source, List list) {

		if (source.isDocument()) {
			list.add(source);
		} else {		
			Iterator it = source.getNodes().iterator();
			while (it.hasNext()) {
				getAllNodes((Node)it.next(),list);
			}
		}
	}

	/**
	 * @see org.jlibrary.client.ui.ccp.Paster#afterPaste(java.lang.Object, java.lang.Object)
	 */
	public void afterPaste(Object source, Object destination) {
		
		Object[] toDrop = (Object[])source;
		
		if (toDrop[0] instanceof Category) {
			Repository repository = RepositoryRegistry.getInstance().getRepository(
					((Category)toDrop[0]).getRepository());
			CategoriesView.refresh(repository);
			for (int i = 0; i< toDrop.length; i++) {
				CategoriesView.categoryUpdated((Category)toDrop[i]);
			}
			if (destination instanceof Category) {
				CategoriesView.categoryUpdated((Category)destination);
			}
		} else {
			if (notCopied.size() > 0) {
				DocumentListDialog listDialog = 
					new DocumentListDialog(null,
					Messages.getMessage("category_add_documents_error_title"),
					Messages.getMessage("category_add_documents_error_detail"));
				listDialog.open(notCopied);
			}
			notCopied.clear();
			CategoriesView.categoryUpdated((Category)destination);
		}
	}
	
	/**
	 * @see org.jlibrary.client.ui.ccp.Paster#beforePaste(java.lang.Object, java.lang.Object)
	 */
	public boolean beforePaste(Object source, Object destination) {
		
		// Nothing to check. CategoryDropListener does now this task
		return true;
	}
	
	private int calculateLength(Object[] nodes) {
		
		int length = 0;
		for (int i = 0; i < nodes.length; i++) {
			Node node = (Node)nodes[i];
			if (!isIncluded(node,(Object[])nodes)) {
				length+=calculateNodeLength(node);
			}
		}
		return length;
	}	
	
	private int calculateNodeLength(Node node) {
		
		if (node.isDocument()) {
			return 1;
		} else {
			int i = 1;
			Iterator it = node.getNodes().iterator();
			while (it.hasNext()) {
				Node child = (Node) it.next();
				i+=calculateNodeLength(child);
			}
			return i;
		}
	}
	
	private boolean isIncluded(Node node, Object[] nodes) {

		// Check if node ancestors are included in the selection
		String parentId = node.getParent();
		while (parentId != null) {
			Node parentNode = EntityRegistry.getInstance().getNode(
					parentId,node.getRepository());
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j] == parentNode) {
					return true;
				}
			}
			parentId = parentNode.getParent();
		}

		return false;
	}
}
