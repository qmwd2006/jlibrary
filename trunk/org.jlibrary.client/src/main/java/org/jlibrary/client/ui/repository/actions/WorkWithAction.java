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
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.repository.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to export repository contents
 */
public class WorkWithAction extends SelectionDispatchAction {
 
	static Logger logger = LoggerFactory.getLogger(WorkWithAction.class);
	
	/**
	 * Constructor
	 */
	public WorkWithAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_work_with"));
		setToolTipText(Messages.getMessage("tooltip_work_with"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_WORK_WITH));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_WORK_WITH_DISABLED));
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
		
		Object[] elements = selection.toArray();
		if (elements.length > 1) {
			return false;
		}
		
		if (!(elements[0] instanceof Document)) {
			return false;
		}
		
		Document document = ((Document)elements[0]);
		if (!securityManager.canPerformAction(
			document.getRepository(),
			document,
			SecurityManager.LOCK_DOCUMENT)) {
			
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
	

	public void run(Document document) {
		
		run(new Object[]{document});
	}
	
	public void run(Object[] elements) {

		
		logger.info("Starting to work with document");
		
		final Document doc = (Document)elements[0];
		
		final Repository repository = RepositoryRegistry.getInstance().getRepository(doc.getRepository());
		final Document document = (Document)
							EntityRegistry.getInstance().getNode(doc.getId(),doc.getRepository()); 
		
		JobTask jobTask = new JobTask(
				Messages.getAndParseValue("job_locking","%1",doc.getName())) {

			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

				// Always get the entity registry document
				if (document == null) {
					return new Status(Status.ERROR,
									  "org.jlibrary.client",
									  Status.OK,
									  "No reference to document in Entity Registry",
									  null);
				}
				IStatus status = lockDocument(document,repository);				
				return status;
				
			}
			
			public void postJobTasks() throws JobTaskException {

				RepositoryView.getRepositoryViewer().refresh(document);
			}
		};
		
		new JobRunner().run(jobTask);
	}
	
	protected IStatus lockDocument(Document document, 
								   Repository repository) 
													throws JobTaskException {
		
		Ticket ticket = repository.getTicket();
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		
		try {
			Lock lock = repositoryService.lockDocument(ticket,document.getId());
			document.setLock(lock);
			
			// Update locks registry
			LockRegistry.getInstance().lockDocument(lock);
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
