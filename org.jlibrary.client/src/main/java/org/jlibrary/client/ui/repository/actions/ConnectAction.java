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

import java.net.ConnectException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.properties.NullPropertiesSourceProvider;
import org.jlibrary.client.ui.properties.PropertiesSourceProvider;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to close a repository
 */
public class ConnectAction 	extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(ConnectAction.class);
	
	private IWorkbenchSite site;
	private Repository repository;

	/**
	 * Constructor
	 */
	public ConnectAction(IWorkbenchSite site) {

		super(site);
		this.site = site;

		setText(Messages.getMessage("item_disconnect_rep"));
		setToolTipText(Messages.getMessage("tooltip_disconnect_rep"));
		setImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_DISCONNECT));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_DISCONNECT_DISABLED));
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
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				((Repository)item).getId(),
				((Repository)item).getRoot(),
				SecurityManager.CONNECT_REPOSITORY)) {
			return false;
		}			
		
		if (((Repository)item).isConnected()) {
			setText(Messages.getMessage("item_disconnect_rep"));
			setToolTipText(Messages.getMessage("tooltip_disconnect_rep"));
			setImageDescriptor(SharedImages.getImageDescriptor(
										SharedImages.IMAGE_DISCONNECT));
			setDisabledImageDescriptor(SharedImages.getImageDescriptor(
					SharedImages.IMAGE_DISCONNECT_DISABLED));
		} else {
			setText(Messages.getMessage("item_connect_rep"));
			setToolTipText(Messages.getMessage("tooltip_connect_rep"));
			setImageDescriptor(SharedImages.getImageDescriptor(
										SharedImages.IMAGE_CONNECT));			
			setDisabledImageDescriptor(SharedImages.getImageDescriptor(
					SharedImages.IMAGE_CONNECT_DISABLED));			
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

		

		repository = (Repository)elements[0];
		if (repository.isConnected()) {
			logger.info("Desconectando el repositorio");
			closeRepository();
		} else {
			logger.info("Reconectándose al repositorio");
			openRepository();
		}
	}

	private void closeRepository() {

		repository.setConnected(false);
		closeEditors(repository);
		RepositoryRegistry.getInstance().closeRepository(repository);	
		
		JobTask jobTask = new JobTask(
								Messages.getMessage("disconnect_job_name")) {
			
			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

				try {
					ServerProfile serverProfile = repository.getServerProfile();
					Ticket ticket = repository.getTicket();
					SecurityService securityService = 
									JLibraryServiceFactory.getInstance(serverProfile).getSecurityService();
					securityService.disconnect(ticket);
				} catch (SecurityException se) {
					throw new JobTaskException(se);
				} catch (Exception e) {
					throw new JobTaskException(e);
				}
				return Status.OK_STATUS;
			}
			
			public void postJobTasks() throws JobTaskException {

				afterJobDone();
			}
		};

		new JobRunner().run(jobTask);
	}

	private void openRepository() {

		repository.setConnected(false);
		closeEditors(repository);
		RepositoryRegistry.getInstance().closeRepository(repository);
		
		JobTask jobTask =  new JobTask(
									Messages.getMessage("connect_job_name")) {

			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

				try {
					monitor.beginTask(Messages.getMessage("connect_job_name"),
									  IProgressMonitor.UNKNOWN);
					// When reconnecting we have to obtain a new ticket instance
					ServerProfile serverProfile = repository.getServerProfile();
					Ticket ticket = repository.getTicket();
					boolean autoconnect = ticket.isAutoConnect();
					SecurityService securityService = 
									JLibraryServiceFactory.getInstance(serverProfile).getSecurityService();
					Credentials credentials = new Credentials();
					credentials.setUser(ticket.getUser().getName());
					credentials.setPassword(ticket.getUser().getPassword());
					ticket = securityService.login(credentials,
												   repository.getName());
					ticket.setAutoConnect(autoconnect);
					
					// Now that we have a new ticket. We'll try to connect to the repository
					RepositoryService repositoryService = 
						JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
					repository = repositoryService.findRepository(
							repository.getName(),ticket);
					if (repository != null) {
						repository.setConnected(true);
						repository.setTicket(ticket);
						repository.setServerProfile(serverProfile);
					}
					
					RepositoryRegistry.getInstance().	
											reconnectToRepository(repository);
					
				} catch (SecurityException se) {
					throw new JobTaskException(se);
				} catch (RepositoryException re) {
					throw new JobTaskException(re);
				} catch (ConnectException ce) {
					throw new JobTaskException(ce);
				} catch (Exception e) {
					throw new JobTaskException(e);
				}
				return Status.OK_STATUS;				
			}
			
			public void postJobTasks() throws JobTaskException {

				afterJobDone();
			}
		};
		
		new JobRunner().run(jobTask);		
	}
	
	private void afterJobDone() {
		
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
	 	
		// Update label state
		checkEnabled((IStructuredSelection)getSelection());
		
		// Update properties view
		PropertySheet sheet = JLibraryPlugin.findPropertiesView();
		if (sheet != null) {
			PropertySheetPage page = (PropertySheetPage) sheet.getCurrentPage();
			if (page != null) {
				if (!repository.isConnected()) {
					page.setPropertySourceProvider(
							new NullPropertiesSourceProvider());
				} else {
					page.setPropertySourceProvider(
							new PropertiesSourceProvider());
				}
				page.refresh();
			}
		}
	}
	
	
	private void closeEditors(Repository repository) {

		IEditorReference[] editorReferences = site.getPage().getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			IEditorPart editor = editorReferences[i].getEditor(false);
			if (editor instanceof JLibraryEditor) {
				Object model = ((JLibraryEditor)editor).getModel();
				if (model instanceof Node) {
					Node node = (Node)model;
					if (node.getRepository().equals(repository.getId())) {
						site.getPage().closeEditor(editor,((JLibraryEditor)editor).isDirty());
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}
}
