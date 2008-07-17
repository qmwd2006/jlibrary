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
package org.jlibrary.client.ui.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.ToolTipHandler;
import org.jlibrary.client.ui.repository.dnd.NodeDragListener;
import org.jlibrary.client.ui.repository.dnd.NodeDropListener;
import org.jlibrary.client.ui.repository.filter.ViewerSorter;
import org.jlibrary.client.ui.repository.providers.RepositoryDecoratingLabelProvider;
import org.jlibrary.client.ui.repository.providers.RepositoryLabelProvider;
import org.jlibrary.client.ui.repository.providers.RepositoryTreeContentProvider;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.User;

/**
 * 
 * @author martin
 *
 * Viewer for the repository tree
 */
public class RepositoryViewer extends TreeViewer {

	public static final int ONLY_DIRECTORIES = 0;
	public static final int DIRECTORIES_AND_DOCUMENTS = 1;
	public static final int ONLY_RESOURCES = 2;
	
	private ArrayList repositories = new ArrayList();

	private ViewerSorter currentSortFilter;
	private IViewSite site;
	
	public RepositoryViewer(Composite parent, 
							IWorkbenchWindow window, 
							int type) {
		
		super(parent);
		
		if (type == ONLY_DIRECTORIES) {
			setContentProvider(new RepositoryTreeContentProvider(ONLY_DIRECTORIES));
			setLabelProvider(new RepositoryLabelProvider());			
		} else if (type == ONLY_RESOURCES) {
				setContentProvider(new RepositoryTreeContentProvider(ONLY_RESOURCES));
				setLabelProvider(new RepositoryLabelProvider());			
		} else {
			setContentProvider(new RepositoryTreeContentProvider(DIRECTORIES_AND_DOCUMENTS));
			setLabelProvider(new RepositoryDecoratingLabelProvider());			
		}
		
		
	}
	
	public Node getParentDirectory(Node node) {
		
		TreeItem item = (TreeItem)findItem(node);
		if (item == null) {
			return null;
		}
		TreeItem parent = item.getParentItem();
		if (parent != null) {
			Object data = parent.getData();
			if (data instanceof Repository) {
				return ((Repository)data).getRoot();
			}
			return (Node)data;
		}
		return null;
	}
	
	public Repository getRepositoryForNode(Node node) {
		
		Iterator it = repositories.iterator();
		while (it.hasNext()) {
			Repository repository = (Repository)it.next();
			if (node.getRepository().equals(repository.getId())) {
				return repository;
			}
		}
		return null;
	}
	
	/**
	 * @param arg0
	 */
	public RepositoryViewer(Composite parent, IViewSite site) {
		
		super(parent);

		this.site = site;
		
		initProviders();
		initListeners();
	}

	public void setRepositories(Collection repositories) {
		
		this.repositories.clear();
		this.repositories.addAll(repositories);
		setInput(repositories);
	}

	/**
	 * Initializes the tree
	 */
	private void initProviders() {
		
		setContentProvider(new RepositoryTreeContentProvider(DIRECTORIES_AND_DOCUMENTS));
		setLabelProvider(new RepositoryDecoratingLabelProvider());
		//getTree().setMenu(new RepositoryContextMenu().createContextMenu(getTree()));
		
	}

	private void initListeners() {
	
		ToolTipHandler handler = new ToolTipHandler(getControl().getShell());
		handler.activateHoverHelp(getTree());
		
		Transfer[] types = new Transfer[] {TextTransfer.getInstance(), FileTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		addDragSupport(operations,types,new NodeDragListener(this));
		addDropSupport(operations,types,new NodeDropListener(this));

		addSelectionChangedListener(new ISelectionChangedListener() {
						
			public void selectionChanged(SelectionChangedEvent event) {

				if (site != null) {
					updateStatusBar(event);
				}				
			}
		});
	}

	private void updateStatusBar(SelectionChangedEvent event) {

		StringBuilder buffer = new StringBuilder();		
		buffer.delete(0,buffer.length());
		
		Object selectedObject = ((StructuredSelection)event.getSelection()).getFirstElement();
		if (selectedObject == null) {
			site.getActionBars().getStatusLineManager().setMessage("");
			return;
		}
		Repository repository = null;
		
		if (selectedObject instanceof Repository) {
			repository = (Repository)selectedObject;

			
			ServerProfile profile = repository.getServerProfile();
			String profileName = profile.getLocation();
			if (profile.isLocal()) {
				profileName = Messages.getMessage(profileName);
			}
			
			buffer.append(repository.getName());
			buffer.append(" (");
			buffer.append(profileName);
		} else {
			Node node = (Node)selectedObject;
			repository = RepositoryRegistry.getInstance().
									getRepository(node.getRepository());
			buffer.append(node.getName());
			buffer.append(" - ");
			buffer.append(repository.getName());
			buffer.append(" (");
			buffer.append(repository.getServerProfile());
			buffer.append(")");					
		}
		
		String userName = repository.getTicket().getUser().getName();
		if (repository.getTicket().getUser().equals(User.ADMIN_USER)) {
			userName = Messages.getMessage(userName);
		}
		buffer.append(" - ");
        buffer.append(Messages.getMessage("connected_as"));                                
        buffer.append(" ");
		buffer.append(userName);
		
		site.getActionBars().getStatusLineManager().setMessage(buffer.toString());
	}	

	/**
	 * Adds a repository to this view
	 * 
	 * @param repository Repository to be added
	 */
	protected void addRepository(Repository repository) {
		
		repositories.add(repository);
		setInput(repositories);
	}

	/**
	 * Replaces a repository that already exists in the view with a new instance
	 * 
	 * @param repository Repository to be replaced
	 */
	protected void replaceRepository(Repository repository) {
		
		// Equals allows us to do this
		int index = repositories.indexOf(repository);
		repositories.remove(repository);
		// Add the new instance
		repositories.add(index,repository);
	}
	
	/**
	 * Removes a repository from the repositories list
	 * 
	 * @param repository Repository to be removed
	 */
	protected void removeRepository(Repository repository) {
		
		repositories.remove(repository);
	}
	
	/**
	 * Returns the selected item. If multiple selection is allowed then this method returns
	 * the first selected item.
	 * 
	 * @return Selected item
	 */
	public Object getSelectedItem() {
		
		IStructuredSelection iss = (IStructuredSelection)getSelection();
		return iss.getFirstElement();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#remove(java.lang.Object)
	 */
	public void remove(Object element) {
		
		super.remove(element);
		
		if (element instanceof Repository) {
			repositories.remove(element);
		}
	}
	
	/**
	 * Adds a sort filter
	 * 
	 * @param filter New sort filter
	 */
	public void addSortFilter(ViewerSorter filter) {
		
		if (currentSortFilter == filter) {
			currentSortFilter.reverseFilter();
		}
		if (currentSortFilter != null) {
			removeFilter(currentSortFilter);
		}
		addFilter(filter);
	}
}
