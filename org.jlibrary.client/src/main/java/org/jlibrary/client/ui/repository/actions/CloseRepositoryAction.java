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
package org.jlibrary.client.ui.repository.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.config.ItemsOpenedRegistry;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.config.ConfigException;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to remove a repository from the repositories view 
 */
public class CloseRepositoryAction 	extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(CloseRepositoryAction.class);
	
	private IWorkbenchSite site;

	/**
	 * Constructor
	 */
	public CloseRepositoryAction(IWorkbenchSite site) {

		super(site);
		this.site = site;

		setText(Messages.getMessage("item_close_rep"));
		setToolTipText(Messages.getMessage("tooltip_close_rep"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_CLOSE_REPOSITORY));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_CLOSE_REPOSITORY_DISABLED));
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	protected void selectionChanged(ITextSelection selection) {}

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

		Object item = selection.getFirstElement();
		if (!(item instanceof Repository)) {
			return false;
		}
		Repository repository = (Repository)item;
		if (!repository.isConnected()) {
			return true;
		}
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				repository.getId(),
				repository.getRoot(),
				SecurityManager.CLOSE_REPOSITORY)) {
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

		
		logger.info("Cerrando repositorio");

		final Repository repository = (Repository)elements[0];
		
		JLibraryPlugin.closeEditors(site,repository);
		RepositoryRegistry.getInstance().removeRepository(repository);
		
		JobTask jobTask = new JobTask(
									Messages.getMessage("close_job_name")) {

			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

				try {
					if (repository.isConnected()) {
						ServerProfile serverProfile = 
												repository.getServerProfile();
						Ticket ticket = repository.getTicket();
						SecurityService securityService = 
							JLibraryServiceFactory.getInstance(serverProfile).getSecurityService();

						securityService.disconnect(ticket);
						
						repository.setConnected(false);
					}				

				} catch (SecurityException se) {
					throw new JobTaskException(se);
				} catch (Exception e) {
					throw new JobTaskException(e);
				}
				return Status.OK_STATUS;
				
			}
			
			public void postJobTasks() throws JobTaskException {

			 	// Update views
				RepositoryView.getRepositoryViewer().refresh();
				
			 	RelationsView relationsView = JLibraryPlugin.findRelationsView();
			 	if (relationsView != null) {
			 		relationsView.refresh();
			 	}

			 	CategoriesView categoriesView = JLibraryPlugin.findCategoriesView();
			 	if (categoriesView != null) {
			 		categoriesView.refresh();
			 	}
			 	
			 	BookmarksView.removeRepository(repository);
			 	
			 	ItemsOpenedRegistry.getInstance().removeRepository(repository);
			 	try {
					ItemsOpenedRegistry.getInstance().saveConfig();
				} catch (ConfigException e) {
					e.printStackTrace();
				}
			}
		};
		
		new JobRunner().run(jobTask);
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}
}
