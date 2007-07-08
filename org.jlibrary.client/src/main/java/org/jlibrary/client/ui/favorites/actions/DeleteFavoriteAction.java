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
package org.jlibrary.client.ui.favorites.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.categories.DocumentsView;
import org.jlibrary.client.ui.favorites.FavoritesView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * <p>This action will be called to delete a favourite.</p>
 */
public class DeleteFavoriteAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(DeleteFavoriteAction.class);
	
	/**
	 * Constructor
	 */
	public DeleteFavoriteAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_delete_favorite"));
		setToolTipText(Messages.getMessage("tooltip_delete_favorite"));
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
		
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			Object object = it.next();
			if (!(object instanceof Favorite)) {
				return false;
			}
			Favorite favorite = (Favorite)object;
			String nodeId = favorite.getDocument();
			Node node = EntityRegistry.getInstance().
							getNode(nodeId,favorite.getRepository());
			if (!securityManager.canPerformAction(
					((Favorite)object).getRepository(),
					node,
					SecurityManager.CREATE_FAVOURITE)) {
				return false;
			}			
		}
		
		if (!securityManager.canPerformAction(SecurityManager.DELETE_FAVOURITE)) {
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
	
	public void run(Object[] elements) {
		
		
		logger.info("Removing favorites");
		
		Category category = CategoriesView.getSelectedCategory();
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(category.getRepository());
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		Ticket ticket = repository.getTicket();
		
		try {
			for (int i = 0; i < elements.length; i++) {
				Favorite favorite = (Favorite)elements[i];
				JLibraryPlugin.closeEditors(getSite(),favorite.getId());
				repositoryService.deleteFavorite(ticket,
												 favorite.getId());
				category.getFavorites().remove(favorite);

				FavoritesView.getInstance().removeFavorite(favorite);
			}
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
		} catch (RepositoryException e) {
            logger.error(e.getMessage(),e);
			ErrorDialog.openError(new Shell(),
					"ERROR",
					Messages.getMessage("delete_favorite_error"),
					new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
			StatusLine.setErrorMessage(Messages.getMessage("delete_favorite_error"));
			return;
		}
		DocumentsView.getInstance().refresh();
		
	}	
}
