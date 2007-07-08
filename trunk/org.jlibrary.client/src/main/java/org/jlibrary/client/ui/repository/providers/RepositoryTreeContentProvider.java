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
package org.jlibrary.client.ui.repository.providers;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.RepositoryViewer;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.RepositoryConfig;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Content provider for repository tree
 */
public class RepositoryTreeContentProvider implements ITreeContentProvider {

	static Logger logger = LoggerFactory.getLogger(RepositoryTreeContentProvider.class);
	
	private int type;
	
	
	public RepositoryTreeContentProvider(int type) {
		
		this.type = type;
	}
	
	private static Comparator comparator = new Comparator() {
		
		Collator collator = Collator.getInstance();
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object arg0, Object arg1) {

			Node node1 = (Node)arg0;
			Node node2 = (Node)arg1;
			
			if (node1.isDirectory()) {
				if (node2.isDirectory()) {
					return collator.compare(node1.getName(), node2.getName());
				} else {
					return -1;
				}
			} else {
				if (node2.isDirectory()) {
					return 1;
				} else {
					return collator.compare(node1.getName(), node2.getName());
				}
			}
		}
	};
	
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element) {

		Object[] nodes = null;
		Collection list = null;
		if (element instanceof Repository) {
			if (!((Repository)element).isConnected()) {
				return new Object[]{};
			}
			list = loadNode(((Repository)element).getRoot()).getNodes();
		} else {	
			Node node = loadNode((Node)element);
			if ((node != null) && (node.getNodes() != null)) {
				list = new ArrayList(node.getNodes());
			} else {
				return new Object[]{};
			}
		}
		
		if (type == RepositoryViewer.ONLY_DIRECTORIES) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (node.isDocument() || node.isResource()) {
					it.remove();
				}
			}
			nodes = list.toArray();
		} else if (type == RepositoryViewer.ONLY_RESOURCES) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (node.isDocument()) {
					it.remove();
				}
			}
			nodes = list.toArray();			
		} else if (type == RepositoryViewer.DIRECTORIES_AND_DOCUMENTS) {
			if (list != null) {
				nodes = list.toArray();
			} else {
				return new Object[]{};
			}
		}
		
		Arrays.sort(nodes,comparator);
		return nodes;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		
		/*
		if (element instanceof Repository) {
			return null;
		}
		Node node = (Node)element;
		return node.getParent();
		*/
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		
		if (element instanceof Repository) {
			if (!((Repository)element).isConnected()) {
				return false;
			}
			return true;
		}
		Node node = (Node)element;
		return node.hasChildren();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object element) {
		
		Collection repositories = (Collection)element;
		return repositories.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}

	public static Comparator getComparator() {
		
		return comparator;
	}
	
	private Node loadNode(Node node)
	{
		Node aNode;
		try
		{
			Repository repository = RepositoryRegistry.getInstance().
										getRepository(node.getRepository());
			ServerProfile profile = repository.getServerProfile();
			Ticket ticket = repository.getTicket();
			RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			RepositoryConfig config = repository.getRepositoryConfig();

			if(config.isEnabledLazyLoading())
			{
				aNode = EntityRegistry.getInstance().
					getAlreadyLoadedNode(node.getId());
				if(aNode == null)
				{
					logger.debug("Loading lazy node: " + node.getId());
					//The node is not completelly loaded, we load it.
					node.getNodes().addAll(service.findNodeChildren(ticket,node.getId()));
					EntityRegistry.getInstance().nodeLoaded(node);
				}
				else
					node.setNodes(aNode.getNodes());
			}
		}
		catch (RepositoryException e)
		{
			return null;
		}
		catch (final SecurityException e)
		{
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					ErrorDialog.openError(new Shell(), "ERROR", 
							Messages.getMessage("security_exception"), new Status(IStatus.ERROR, "JLibrary", 101, e
							.getMessage(), e));
					StatusLine.setErrorMessage(e.getMessage());
				}
			});
			return null;
		}
		return node;
	}
}
