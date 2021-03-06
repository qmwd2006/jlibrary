/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
package org.jlibrary.client.ui.favorites.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.jlibrary.client.ui.categories.dialogs.CategorySelectionDialog;
import org.jlibrary.client.ui.favorites.FavoritesView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to add documents as favourites to a 
 * category.
 */
public class AddToFavoritesAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(AddToFavoritesAction.class);
	
	private IWorkbenchSite site;

	/**
	 * Default constructor
	 */
	public AddToFavoritesAction() {}
	
	/**
	 * Constructor
	 */
	public AddToFavoritesAction(IWorkbenchSite site) {
		
		super(site);
		
		this.site = site;
		
		setText(Messages.getMessage("item_favorites_add"));
		setToolTipText(Messages.getMessage("tooltip_favorites_add"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_ADD_FAVORITE));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_ADD_FAVORITE_DISABLED));
		
		update(getSelection());
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));		
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
		
		if (selection.isEmpty())
			return false;
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			Object object = it.next();
			if (!(object instanceof Document)) {
				return false;
			}
			
			if (!securityManager.canPerformAction(
					((Document)object).getRepository(),
					((Document)object),
					SecurityManager.CREATE_FAVOURITE)) {
				return false;
			}		
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
	
	public void run(Object[] elements) {
		
		
		logger.info("Add to favorites action");
		
		Node firstNode = (Node)elements[0];
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(firstNode.getRepository());

		CategorySelectionDialog csd = CategorySelectionDialog.getInstance(site.getShell());
		csd.open(repository);
		
		if (csd.getReturnCode() != IDialogConstants.OK_ID) {
			return;
		}
		
		Category category = csd.getCategory();
		
		ArrayList nodes = new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			Node nodeReference = (Node)elements[i];
			Node node = EntityRegistry.getInstance().
				getNode(nodeReference.getId(),nodeReference.getRepository());
			nodes.add(node);	
		}
		addToFavorites(nodes,category);
	}
	
	private boolean existFavorite(Category category, Node node) {
		
		Iterator it = category.getFavorites().iterator();
		while(it.hasNext()) {
			Favorite favorite = (Favorite)it.next();
			if (favorite.getDocument().equals(node)) {
				return true;
			}
		}
		return false;
	}
	
	public void addToFavorites(final List documents, 
							   final Category category) {
		
		JobTask jobTask = new JobTask(Messages.getMessage("add_favorites_job")) {

			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

				IStatus status = Status.OK_STATUS;
				try {
					monitor.beginTask(Messages.getMessage("add_favorites_job"),
									  documents.size());
					Iterator it = documents.iterator();
					while (it.hasNext()) {
						Document document = (Document) it.next();
						addDocument(document,category,monitor);
						monitor.internalWorked(1);
					}
				} catch (final SecurityException se) {
					throw new JobTaskException(se);
				} catch (RepositoryException re) {
					throw new JobTaskException(re);
				} catch (Exception e) {
					throw new JobTaskException(e);
				}
				return status;				
			}
			
			public void postJobTasks() throws JobTaskException {

				if (DocumentsView.getInstance() != null) {
					DocumentsView.getInstance().refreshDocuments();
				}
				if (FavoritesView.getInstance() != null) {
					FavoritesView.getInstance().setInput(category);
				}
			}
		};
		new JobRunner().run(jobTask);

	}
	
	
	private void addDocument(Document document, 
						 Category category,
						 IProgressMonitor monitor) throws RepositoryException,
						 						   		  SecurityException {
		
		
		monitor.subTask(Messages.getAndParseValue("add_favorites_job_step","%1",document.getName()));
		if (!document.getRepository().equals(category.getRepository())) {
			return;
		}
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(category.getRepository());
		Ticket ticket = repository.getTicket();
		User user = ticket.getUser();
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();

		
		// Always get the reference from the registry
		if (document == null) {
			logger.info("Invalid reference from Entity Registry");
			return;
		}
		
		if (category.getFavorites() == null) {
			category.setFavorites(new HashSet());
		}
		
		if (existFavorite(category,document)) {
			return;
		}

		try {
			List currentCategories = repositoryService.findCategoriesForNode(ticket,document.getId());
			Category selectedCategory = CategoriesView.getSelectedCategory();
			if (currentCategories.size() == 1) {
				Category unk = (Category)currentCategories.get(0);
				if (unk.getName().equals(Category.UNKNOWN_NAME)) {
					CategoryProperties categoryProperties = new CategoryProperties();
					categoryProperties.put(CategoryProperties.CATEGORY_REMOVE_NODE, 
			        		   new PropertyDef(CategoryProperties.CATEGORY_REMOVE_NODE, document));
					repositoryService.updateCategory(ticket, unk.getId(),categoryProperties);
				}
				if ((selectedCategory != null) && 
					selectedCategory.getId().equals(unk.getId())) {
					// Remove the favorite, if necessary
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
			}
			if (!currentCategories.contains(selectedCategory)) {
				CategoryProperties categoryProperties = new CategoryProperties();
				categoryProperties.put(CategoryProperties.CATEGORY_ADD_NODE, 
						   new PropertyDef(CategoryProperties.CATEGORY_ADD_NODE, document));
				repositoryService.updateCategory(ticket,category.getId(),categoryProperties);
			}
		} catch (CategoryNotFoundException cnfe) {
			throw new RepositoryException(cnfe);
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
			return;
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
			return;
		}					
		
		Favorite favorite = new Favorite();
		favorite.setCategory(category.getId());
		favorite.setUser(user.getId());
		favorite.setDocument(document.getId());
		
		favorite = repositoryService.createFavorite(ticket,
													favorite);
		
		user.getFavorites().add(favorite);
		category.getFavorites().add(favorite);

	}
}
