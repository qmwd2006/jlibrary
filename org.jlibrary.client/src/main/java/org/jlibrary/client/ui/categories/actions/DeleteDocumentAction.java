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
package org.jlibrary.client.ui.categories.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.categories.DocumentsView;
import org.jlibrary.client.ui.favorites.FavoritesView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to delete documents from current selected category
 */
public class DeleteDocumentAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(DeleteDocumentAction.class);
	
	private ArrayList notDeleted = new ArrayList();
	
	/**
	 * Constructor
	 */
	public DeleteDocumentAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_delete_category_documents"));
		setToolTipText(Messages.getMessage("tooltip_delete_category_documents"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_DISABLED));
	}
	

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
			
		if (selection.isEmpty()) {
			return false;
		}
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Category category = CategoriesView.getSelectedCategory();
		if (category == null) {
			return false;
		}		
		
		if (category.isUnknownCategory()) {
			return false;
		}

		if (!securityManager.canPerformAction(
				category.getRepository(),
				SecurityManager.CATEGORY_REMOVE_DOCUMENTS
				)) {
			return false;
		}		
		
		return true;
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}
	
	public void run(final Object[] elements) {
		
		
		logger.info("Removing documents from category");
		
		JobTask jobTask = new JobTask(Messages.getMessage("copy_job_name")) {
			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {
				
				IStatus status = Status.OK_STATUS;
				try {
					deleteCategories(elements,monitor);
				} catch (RepositoryException re) {
					throw new JobTaskException(re);
				}
				return status;
			}
			
			public void postJobTasks() throws JobTaskException {
				
				if (DocumentsView.getInstance() != null) {
					DocumentsView.getInstance().refreshDocuments();
				}
				if (FavoritesView.getInstance() != null) {
					FavoritesView.getInstance().refreshCategory();
				}
			}			
		};
		new JobRunner().run(jobTask);
	}	
	
	private void deleteCategories(Object[] elements, 
								  IProgressMonitor monitor) throws RepositoryException {
		
		notDeleted.clear();
		monitor.beginTask(Messages.getMessage("delete_category_document_task"),elements.length);
		Category category = CategoriesView.getSelectedCategory();		
		Repository repository = RepositoryRegistry.getInstance().getRepository(category.getRepository());
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService repositoryService = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		Ticket ticket = repository.getTicket();

		Category unknownCategory = loadUnknownCategory(repository);
		Category selectedCategory = CategoriesView.getSelectedCategory();
		
		for (int i = 0;i <elements.length; i++) {
			Document document = (Document) elements[i];
			monitor.subTask(Messages.getAndParseValue("delete_category_document_step","%1",document.getName()));
			monitor.internalWorked(1);

			if (document.isDeletedDocument() ||
				((document.getLock() != null) && 
				 (document.getLock().getUserId() == ticket.getUser().getId()))) {
				notDeleted.add(document);
				continue;
			}
			
			try {			
				List nodes = repositoryService.findNodesForCategory(ticket, category.getId());
				if (!(nodes.contains(document))) {
					continue;
				}
				document.removeCategory(category);
				List documentCategories = repositoryService.findCategoriesForNode(ticket, document.getId());
				if (documentCategories.size() == 1) {			
					document.addCategory(unknownCategory);
				}
				DocumentProperties docProperties = document.dumpProperties();
				
				repositoryService.updateDocument(ticket,docProperties);
				
//				 Remove the document from the favorites, if necessary
				if ((selectedCategory != null) && selectedCategory.getId().equals(category.getId())) {
					Iterator it2 = selectedCategory.getFavorites().iterator();
					while (it2.hasNext()) {
						Favorite favorite = (Favorite) it2.next();
						if (favorite.getDocument().equals(document.getId())) {
							if (favorite.getUser().equals(ticket.getUser().getId())) {
								it2.remove();
							}
						}
					}
				}
			} catch (ResourceLockedException e) {
				notDeleted.add(document);
				continue;
			} catch (CategoryNotFoundException cnfe) {
				throw new RepositoryException(cnfe);
			} catch (RepositoryException re) {
				throw re;
			} catch (SecurityException e) {
				notDeleted.add(document);
				continue;
			}
		}
	}
	
	private Category loadUnknownCategory(Repository repository) {

		Iterator it = repository.getCategories().iterator();
		while (it.hasNext()) {
			Category category = (Category) it.next();
			if (category.getName().equals(Category.UNKNOWN_NAME)) {
				return category;
			}
		}
		return null;
	}
}
