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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
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
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;


/**
 * @author  molpe
 *
 * This action will be called when the client wants to reload a repository
 * looking for changes
 */
public class RefreshRepositoryAction extends SelectionDispatchAction {
    
	/**
	 * Empty constructor
	 *
	 */
    public RefreshRepositoryAction() {}
	
    /**
     * Constructor
     */
    public RefreshRepositoryAction(IWorkbenchSite site) {
        
        super(site);
        
        setText(Messages.getMessage("item_refresh"));
        setToolTipText(Messages.getMessage("tooltip_refresh"));
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_REFRESH_REPOSITORY));
        setDisabledImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_REFRESH_REPOSITORY_DISABLED));
        
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
        
        SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
        Object item = selection.getFirstElement();
        if (!(item instanceof Repository) && !(item instanceof Node)) {
        	return false;
        }
        
        if (item instanceof Repository) {
    		Repository repository = ((Repository)item);
			if (!securityManager.canPerformAction(
    				repository.getId(),
    				repository.getRoot(),
    				SecurityManager.REFRESH_CONTENTS)) {
    			return false;
    		}

			if (!repository.isConnected()) {
				return false;
			}
        } else {        
	        Node node = (Node)item;
			if (!securityManager.canPerformAction(
					node.getRepository(),
					node,
					SecurityManager.REFRESH_CONTENTS)) {
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
    
    public void run(final Object[] elements) {
        
    	JobTask jobTask = new JobTask(Messages.getMessage("refresh_job_name")) {

    		public IStatus run(IProgressMonitor monitor) 
    										throws OperationCanceledException, 
    											   JobTaskException {

    			return refreshNodes(elements, monitor);
    		}
    		
    		public void postJobTasks() throws JobTaskException {

		    	final Node node = (elements[0] instanceof Repository) ?
			  			  ((Repository)elements[0]).getRoot() :
			  			  (Node)elements[0];
                final Repository repository = RepositoryRegistry.
							getInstance().getRepository(node.getRepository());    	
    			
    			afterJobDone(node,repository);
    		}
    	};
		
    	new JobRunner().run(jobTask);
    }
    
    /**
     * Refresh some nodes
     * 
     * @param elements Nodes to be refreshed
     * @param monitor Progress monitor
     * 
     * @return IStatus Operation status
     * 
     * @throws JobTaskException If the task cannot be performed
     */
	public IStatus refreshNodes(final Object[] elements, 
								IProgressMonitor monitor) 
													throws JobTaskException {
		
		monitor.beginTask(Messages.getMessage("refresh_job_name"),
						  IProgressMonitor.UNKNOWN);
		
    	final Node node = (elements[0] instanceof Repository) ?
				  			  ((Repository)elements[0]).getRoot() :
				  			  (Node)elements[0];
        final Repository repository = RepositoryRegistry.
						getInstance().getRepository(node.getRepository());    	

        ServerProfile profile = repository.getServerProfile();
        Ticket ticket = repository.getTicket();
        RepositoryService repositoryService = 
        						JLibraryServiceFactory.getInstance(profile).getRepositoryService();

        // Lookup refreshed node
        Node refreshedNode = null;
        IStatus status = null;
        try {
        	if (node.isDocument()) {
        		refreshedNode = 
        			repositoryService.findDocument(ticket,node.getId());
        	} else {
        		refreshedNode = 
        			repositoryService.findDirectory(ticket,node.getId());
        	}
	
        	if ((monitor.isCanceled() || (refreshedNode == null))) {
        		status = Status.CANCEL_STATUS;
        	}

        	status = Status.OK_STATUS;
        	if (status != null) {
        		checkNodes(node,refreshedNode,false);
        	}
        	if (monitor.isCanceled()) {
        		status = Status.CANCEL_STATUS;
        	}

        	return status;
        } catch (RepositoryException re) {
        	throw new JobTaskException(re);
        } catch (SecurityException se) {
        	throw new JobTaskException(se);
        } catch (Exception e) {
        	throw new JobTaskException(e);
        }
	}
    
    private void afterJobDone(Node node, Repository repository) {
    	
    	
        
        Node parent = EntityRegistry.getInstance().
        	getNode(node.getParent(),node.getRepository());
		if (parent == null) {
			RepositoryView.getRepositoryViewer().refresh(repository);
			RepositoryView.getRepositoryViewer().expandToLevel(repository,1);		 		
		} else {
			RepositoryView.getRepositoryViewer().refresh(parent);
			RepositoryView.getRepositoryViewer().expandToLevel(parent,1);
		}
		RepositoryView.getRepositoryViewer().refresh(); 
                
        // Update views
        RelationsView relationsView = JLibraryPlugin.findRelationsView();
        if (relationsView != null) {
            relationsView.refresh();
        }
        
        CategoriesView categoriesView = JLibraryPlugin.findCategoriesView();
        if (categoriesView != null) {
            categoriesView.refresh();
        }
    }
    
    private void checkNodes(Node node, Node refreshedNode, boolean onlyUpdate) {
    	    	
    	// Update the entity registry
        EntityRegistry.getInstance().addNode(refreshedNode);

        // Remove the node from the cache
		LocalCache cache = LocalCacheService.getInstance().getLocalCache();
		try {
			cache.removeNodeFromCache(refreshedNode);
		} catch (LocalCacheException e) {
			e.printStackTrace();
		}
        
		// Update lock status and some info needed for the tree
		node.setLock(refreshedNode.getLock());
		node.setName(refreshedNode.getName());
		node.setDescription(refreshedNode.getDescription());
        
        if (onlyUpdate == false) {
        	if (node.hasChildren() && node.isEmpty()) {
        		// this was a lazy node so return
        		onlyUpdate = true;
        	}
        }
		
        if (node.isDirectory()) {
	        List refreshedList = new ArrayList(refreshedNode.getNodes());
	        List nodesList = new ArrayList(node.getNodes());
	        
	        //Now check for deleted children nodes
	    	Iterator it = node.getNodes().iterator();
	    	while (it.hasNext()) {
				Node child = (Node) it.next();				
				if (onlyUpdate || refreshedList.contains(child)) {
					// The items are equals. Check recursively
					Node refreshedChild = (Node)
							refreshedList.get(refreshedList.indexOf(child));
					checkNodes(child,refreshedChild,onlyUpdate);
				} else {
					// The child don't exist in the repository
					tagAsDeleted(child);
				}
			}
	    	
	    	// Check for new insertions
	    	it = refreshedNode.getNodes().iterator();
	    	while (it.hasNext()) {
				Node refreshedChild = (Node) it.next();
				if (!nodesList.contains(refreshedChild)) {
					
					node.getNodes().add(refreshedChild);
					if (!onlyUpdate) {
						tagAsNew(refreshedChild);
					}
					EntityRegistry.getInstance().addNode(refreshedChild);
				}
			}
        }
    }
    
    private void tagAsDeleted(Node node) {
    	
    	node.setDeletedDocument(true);
    	Iterator it = node.getNodes().iterator();
    	while (it.hasNext()) {
			Node child = (Node) it.next();
			tagAsDeleted(child);
		}
    }
    
    private void tagAsNew(Node node) {
    	
    	node.setNewDocument(true);
    	Iterator it = node.getNodes().iterator();
    	while (it.hasNext()) {
			Node child = (Node) it.next();
			tagAsNew(child);
		}
    }
    
        /* (non-Javadoc)
         * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
         */
    public void dispose() {}
    
}
