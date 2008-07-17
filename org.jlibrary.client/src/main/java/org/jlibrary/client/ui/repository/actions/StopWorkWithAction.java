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
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.LockRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityException;

/**
 * @author martin
 *
 * This action will be called to export repository contents
 */
public class StopWorkWithAction extends SelectionDispatchAction {
 
	/**
	 * Constructor
	 */
	public StopWorkWithAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_stop_work_with"));
		setToolTipText(Messages.getMessage("tooltip_stop_work_with"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_STOP_WORK_WITH));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_STOP_WORK_WITH_DISABLED));
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

		Object[] elements = selection.toArray();
		if (elements.length > 1) {
			return false;
		}
		
		Repository repository = null;
		if (elements[0] instanceof Lock) {
			// Selection on the working set view
			Lock lock = (Lock)elements[0];
			repository = RepositoryRegistry.getInstance().
				getRepository(lock.getRepository());
		} else if (elements[0] instanceof Document) {
			// Selection on the repository view
			Document document = (Document)elements[0];
			repository = RepositoryRegistry.getInstance().
				getRepository(document.getRepository());			
		} else {
			// Invalid selection
			return false;
		}				
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				repository,SecurityManager.UNLOCK_DOCUMENT)) {
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

		final Lock lock = (elements[0] instanceof Node) ? ((Node)elements[0]).getLock() : (Lock)elements[0];
		final Repository repository = RepositoryRegistry.getInstance().getRepository(lock.getRepository());
		final String resourceName = EntityRegistry.getInstance().getNode(
				lock.getId(),lock.getRepository()).getName();	

		JobTask jobTask = new JobTask(
			Messages.getAndParseValue("job_unlocking","%1",resourceName)) {
			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {
				return unlockDocument(lock,repository);

			}
		};
		
		new JobRunner().run(jobTask);
	}

	protected IStatus unlockDocument(Lock lock, 
									 Repository repository) 
													throws JobTaskException {
            
		Ticket ticket = repository.getTicket();
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		
		try {
			repositoryService.unlockDocument(ticket,lock.getId());
			
			Node node = EntityRegistry.getInstance().getNode(
					lock.getId(),lock.getRepository());
			if (node == null) {				
				return null;
			}
			node.setLock(null);
			
			// Update locks registry
			LockRegistry.getInstance().unlockDocument(lock);
			return Status.OK_STATUS;
		} catch (SecurityException se) {
			throw new JobTaskException(se);
		} catch (ResourceLockedException rle) {
			throw new JobTaskException(rle);
		} catch (Exception e) {
			throw new JobTaskException(e);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}
}
