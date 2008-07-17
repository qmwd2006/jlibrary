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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.LocalCacheService;
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
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.search.SearchView;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.IResource;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.repository.RepositoryService;

/**
 * @author martin
 *
 * This action will be called to close a repository
 */
public class DeleteAction extends SelectionDispatchAction {

	private IWorkbenchSite site;
	
	/**
	 * Constructor
	 */
	public DeleteAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;
		setText(Messages.getMessage("item_delete"));
		setToolTipText(Messages.getMessage("tooltip_delete"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_DISABLED));
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
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof Repository) {
				Repository repository = (Repository)elements[i];
				if (!securityManager.canPerformAction(
						repository.getId(),
						repository.getRoot(),
						SecurityManager.DELETE_REPOSITORY)) {
					return false;
				}				

				if (!((Repository)elements[i]).isConnected()) {
					return false;
				}

				return true;
			}
			Node node = (Node)elements[i];
			if (!node.isDirectory() && 
				!node.isDocument() && 
				!node.isResource()) {
				
				return false;
			}	
			
			if (node.isDirectory()) {
				if (!securityManager.canPerformAction(
						node.getRepository(),
						node,
						SecurityManager.DELETE_DIRECTORY)) {
					return false;
				}				
			} else if (node.isResource()) {
				if (!securityManager.canPerformAction(
						node.getRepository(),
						node,
						SecurityManager.DELETE_RESOURCE)) {
					return false;
				}				
			} else if (node.isDocument()) {
				if (!securityManager.canPerformAction(
						node.getRepository(),
						node,
						SecurityManager.DELETE_DOCUMENT)) {
					return false;
				}				
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(ITextSelection selection) {}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}
	
	public void run(final Object[] elements) {
				
		IWorkbenchWindow window = site.getWorkbenchWindow();

		MessageDialog dialog = new MessageDialog(
				window.getShell(), 
				Messages.getMessage("delete_confirmationAll_title"), 
				SharedImages.getImage(SharedImages.IMAGE_JLIBRARY),
				Messages.getMessage("delete_confirmationAll_text"),
				MessageDialog.QUESTION, 
				new String[]{IDialogConstants.OK_LABEL,
							 IDialogConstants.CANCEL_LABEL}, 
				0);
		
		boolean d = dialog.open() == 0;

		if (!d) return;
		
		final List list = createDeleteList(elements);
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			if (item instanceof Repository) {
		 		JLibraryPlugin.closeEditors(site,(Repository)item);
			} else if (item instanceof Directory) {
				Directory directory = (Directory)item;
				Repository repository = RepositoryRegistry.getInstance().getRepository(directory.getRepository());
			 	if (directory.equals(repository.getRoot())) {
			 		boolean delete = MessageDialog.openConfirm(window.getShell(),
			 						 Messages.getMessage("delete_root_confirmation_title"),
									 Messages.getAndParseValue("delete_root_confirmation_text","%1",item.toString()));
			 		if (!delete) {
			 			it.remove();
			 		}
			 	}
			 	JLibraryPlugin.closeEditors(site,directory.getId());
			} else if (item instanceof IResource) {
				JLibraryPlugin.closeEditors(site,((IResource)item).getId());
			}
		}
		
		JobTask jobTask = new JobTask(Messages.getMessage("delete_job_name")) {
			
			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

				IStatus status = null;
				monitor.beginTask(Messages.getMessage(
												"delete_job_name"),list.size());
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Object item = it.next();
					monitor.subTask(Messages.getAndParseValue(
									"delete_step","%1",getObjectName(item)));
					status = deleteItem(item);
					if (!(status == Status.OK_STATUS)) {
						monitor.done();
						break;
					} else {
						if (monitor.isCanceled()) {
							status = Status.CANCEL_STATUS;
							break;
						}
						monitor.worked(1);
					}
				}
				if (status == null) {
					status = Status.OK_STATUS;
				}				
				return status;				
			}
			
			public void postJobTasks() throws JobTaskException {
				
				afterJobDone(list);
			}
		};

		new JobRunner().run(jobTask);
	}
	

	private List createDeleteList(Object[] elements) {

		List toDelete = new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof Node) {
				Node node = (Node)elements[i];
				boolean found = false;
				while ((node.getParent() != null) || found) {
					Node parent = EntityRegistry.getInstance().getNode(
											node.getParent(),node.getRepository());
					if (toDelete.contains(parent)) {
						found = true;
						break;
					} else {
						node = parent;
					}				
				}
				if (found) {
					continue;
				}
			}
			toDelete.add(elements[i]);
		}
		return toDelete;
	}

	private void afterJobDone(final List list) {
		Iterator it;
		if (CategoriesView.getInstance() != null) {
			CategoriesView.getInstance().refresh();
		}
		if (DocumentsView.getInstance() != null) {
			DocumentsView.getInstance().refreshDocuments();
		}
		if (RelationsView.getInstance() != null) {
			RelationsView.getInstance().refresh();
		}

		// update search view
		it = list.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			if (item instanceof Repository) {
			 	RepositoryRegistry.getInstance().removeRepository((Repository)item);
			 	RepositoryView.getRepositoryViewer().remove((Repository)item);
				if (SearchView.getInstance() != null) {
					SearchView.getInstance().removeFromSearchView((Repository)item);
				}
			} else if (item instanceof Directory) {
				Directory directory = (Directory)item;
				Repository repository = RepositoryRegistry.getInstance().getRepository(directory.getRepository());
	 			Node parent = RepositoryView.getInstance().getParentNode(directory);
	 			parent.getNodes().remove(directory);
	 			if (parent.equals(repository.getRoot())) {
	 				RepositoryView.getRepositoryViewer().refresh(repository);
	 			} else {
	 				RepositoryView.getRepositoryViewer().refresh(parent);
	 			}
				if (SearchView.getInstance() != null) {
					SearchView.getInstance().removeFromSearchView((Directory)item);
				}
				// Remove the node from the registry
				EntityRegistry.getInstance().removeNode(directory);
			} else {
				Node node = (Node)item;
				Repository repository = RepositoryRegistry.getInstance().getRepository(node.getRepository());
				Node parent = RepositoryView.getInstance().getParentNode(node);
				if (parent == null) {
					parent = EntityRegistry.getInstance().
						getNode(node.getId(),node.getRepository());
					RepositoryView.getRepositoryViewer().refresh(repository);
				}
				parent.getNodes().remove(node);
				if (parent.equals(repository.getRoot())) {
					RepositoryView.getRepositoryViewer().refresh(repository);
				} else {
					RepositoryView.getRepositoryViewer().refresh(parent);
				}
				if (SearchView.getInstance() != null) {
					SearchView.getInstance().removeFromSearchView(node);
				}
				
				// Remove the node from the registry
				EntityRegistry.getInstance().removeNode(node);
			}
		}
	}

	private IStatus deleteRepository(Repository repository) 
													throws JobTaskException {
		
	    Ticket ticket = repository.getTicket();
	 	ServerProfile profile = repository.getServerProfile();
	 	RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
	 	try {
	 		service.deleteRepository(ticket);
	 	} catch (final SecurityException se) {
	 		throw new JobTaskException(se);
	 	} catch (Exception e) {
	 		throw new JobTaskException(e);
	 	}
	 	
	 	return Status.OK_STATUS;
	}
	
	private IStatus deleteDirectory(Directory directory) 
													throws JobTaskException {
		
	 	Repository repository = RepositoryRegistry.getInstance().
	 								getRepository(directory.getRepository());
	 	
	 	Ticket ticket = repository.getTicket();
	 	ServerProfile profile = repository.getServerProfile();
	 	RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
	 	
	 	try {
	 		if (directory.equals(repository.getRoot())) {
	 			service.deleteRepository(ticket);
	 		} else {
	 			service.removeDirectory(ticket,directory.getId());
	 		}
	 	} catch (final SecurityException se) {
	 		throw new JobTaskException(se);
	 	} catch (Exception e) {
	 		throw new JobTaskException(e);
	 	}
	 	
	 	return Status.OK_STATUS;
	}
	
	private IStatus deleteDocument(Document document) 
													throws JobTaskException {

	 	String repositoryId = document.getRepository();
	 	Repository repository = 
	 			RepositoryRegistry.getInstance().getRepository(repositoryId);
	 	Ticket ticket = repository.getTicket();
	 	ServerProfile profile = repository.getServerProfile();

	 	RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
	 	try {
	 		service.removeDocument(ticket,document.getId());
		} catch (final ResourceLockedException rle) {
			throw new JobTaskException(rle);
	 	} catch (final SecurityException se) {
	 		throw new JobTaskException(se);
	 	} catch (Exception e) {
	 		throw new JobTaskException(e);
	 	}

	 	return Status.OK_STATUS;
	}

	private IStatus deleteResource(ResourceNode resource) 
													throws JobTaskException {

	 	String repositoryId = resource.getRepository();
	 	Repository repository = 
	 			RepositoryRegistry.getInstance().getRepository(repositoryId);
	 	Ticket ticket = repository.getTicket();
	 	ServerProfile profile = repository.getServerProfile();

	 	RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
	 	try {
			List nodes = service.findNodesForResource(ticket, resource.getId());
			service.removeResourceNode(ticket,resource.getId());
			
			// Clean client references
			Iterator it = nodes.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				Node clientNode = EntityRegistry.getInstance().
					getNode(node.getId(),node.getRepository());
				if (clientNode !=  null) {
					((Document)clientNode).getResourceNodes().remove(resource);
				}
			}
			
			// Remove it from the parent node
			Node parent = EntityRegistry.getInstance().getNode(
					resource.getParent(),resource.getRepository());
			parent.getNodes().remove(resource);
			
	 	} catch (final SecurityException se) {
	 		throw new JobTaskException(se);
	 	} catch (Exception e) {
	 		throw new JobTaskException(e);
	 	}

	 	return Status.OK_STATUS;
	}
	
	
	private IStatus deleteItem(Object item) throws JobTaskException {
				 
		 if (item instanceof Repository) {
		 	deleteRepository((Repository)item);
		 } else {
			 Node node = (Node)item;
			 if (node.isDirectory()) {
				 deleteDirectory((Directory)node);				 
			 } else if (node.isDocument()) {
				 deleteDocument((Document)node);				 
			 } else if (node.isResource()) {
				 deleteResource((ResourceNode)node);				 				 
			 }
			 LocalCache cache = LocalCacheService.getInstance().getLocalCache();
			 try {
				cache.removeNodeFromCache(node);
			} catch (LocalCacheException e) {
				e.printStackTrace();
			}
		 }
			 		 		 
		return Status.OK_STATUS;
	}	
	
	private String getObjectName(Object item) {
		
		if (item instanceof Repository) {
			return ((Repository)item).getName();
		} else if (item instanceof Node) {
			return ((Node)item).getName();
		}
		return item.toString();
	}
}
