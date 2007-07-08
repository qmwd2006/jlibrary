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
package org.jlibrary.client.ui.repository.actions;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.ITextSelection;
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
import org.jlibrary.client.ui.dialogs.FileCheckSelectionDialog;
import org.jlibrary.client.ui.repository.RepositoryHelper;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.tree.FileSystemNode;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to add fastly resources
 */
public class AddResourcesAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(AddResourcesAction.class);
	
	private Directory parent;
	private Repository repository;
	
	/**
	 * Constructor
	 */
	public AddResourcesAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_add_resources"));
		setToolTipText(Messages.getMessage("tooltip_add_resource"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_ADD_RESOURCES));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_ADD_RESOURCES_DISABLED));
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
		
		if (selection.isEmpty())
			return false;
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Object[] elements = selection.toArray();
		if (elements.length > 1) {
			return false;
		}
					
		if (elements[0] instanceof Repository) {					
			if (!((Repository)elements[0]).isConnected()) {
				return false;
			}
			parent = ((Repository)elements[0]).getRoot();
		} else {
			Node node = (Node)elements[0];
			if (node.isDirectory()) {
				parent = (Directory)elements[0];
			} else {
				return false;
			}
		}
		
		if (!securityManager.canPerformAction(
				parent.getRepository(),
				parent,
				SecurityManager.ADD_RESOURCES_DIRECTORY)) {
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
	
	public void run(Directory directory) {
	
		parent = directory;
		run(new Object[]{});
	}
	
	public void run(Object[] elements) {	

		if (parent == null) {
			return;
		}
		repository = RepositoryRegistry.getInstance().getRepository(parent.getRepository());

		FileCheckSelectionDialog fcsd = 
			new FileCheckSelectionDialog(getSite().getWorkbenchWindow(),false);
		fcsd.open();
		
		if (fcsd.getReturnCode() == IDialogConstants.OK_ID) {
			final Object objects[] = fcsd.getCheckedElements();
			
			JobTask jobTask = new JobTask(
								Messages.getMessage("add_resources_job_name")) {

				public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

					int length = loadProcessLength(objects);
					monitor.beginTask(
							Messages.getMessage("add_resources_job_name"),
							length);
					IStatus status = createStructure(repository,
													 parent,
													 objects,
													 monitor);
					return status;					
				}
				
				public void postJobTasks() throws JobTaskException {

					if (repository.getRoot().equals(parent)) {
						RepositoryView.getRepositoryViewer().refresh(repository);
						RepositoryView.getRepositoryViewer().expandToLevel(repository,1);
					} else {
						RepositoryView.getRepositoryViewer().refresh(parent);
						RepositoryView.getRepositoryViewer().expandToLevel(parent,1);
					}
				}
			};
			
			jobTask.setPriority(Job.LONG);
			new JobRunner().run(jobTask);
		}
	}
	
	private int loadProcessLength(Object[] objects) {
		
		int length = 0;
		for (int i = 0; i < objects.length; i++) {
			length++;
			FileSystemNode node = (FileSystemNode)objects[i];
			File file = node.getFile();
			if (file.isDirectory()) {
				length+=loadProcessLength(node.getChildren().toArray());
			}
		}
		return length;
	}
	
	/**
	 * @param objects
	 */
	private IStatus createStructure(Repository repository,
								    Directory parent, 
								    Object[] objects,
								    IProgressMonitor monitor) {
		
		for (int i = 0; i < objects.length; i++) {
			FileSystemNode node = (FileSystemNode)objects[i];
			File file = node.getFile();
			
			if (file.isDirectory()) {
				try {
					monitor.subTask(Messages.getAndParseValue(
							"create_directory_job","%1",file.getName()));
					Directory directory = RepositoryHelper.createDirectory(repository,parent,file);
					createStructure(repository,directory, node.getChildren().toArray(),monitor);
				} catch (RepositoryException e) {
					
	                logger.error(e.getMessage(),e);
				} catch (SecurityException se) {}
			} else {
				monitor.subTask(Messages.getAndParseValue(
						"create_resource_job","%1",file.getName()));
				try {
					RepositoryHelper.createResource(repository,parent,file);
				} catch (RepositoryException e) {
					return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,e.getMessage(),e);
				} catch (SecurityException se) {
					return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,Messages.getMessage("not_enough_permissions"),se);
				}
			}
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			monitor.internalWorked(1);
		}
		return Status.OK_STATUS;
	}

	public void dispose() {}
}
