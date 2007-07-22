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
package org.jlibrary.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.RepositoryConfig;
import org.jlibrary.core.entities.ResourceNode;
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
 * Registry of repository nodes. This will be the unique access to node instances
 * within client code. With this class, we solve the problem of having multiple 
 * node instances in the various views.
 */
public class EntityRegistry {
	
	static Logger logger = LoggerFactory.getLogger(EntityRegistry.class);
	
	private static EntityRegistry instance;

	private HashMap nodes = new HashMap();
	private HashMap lazyNodes = new HashMap();
	
	private EntityRegistry() {
		
		super();
	}
	
	/**
	 * Adds all the nodes and resources of a repository to the regsitry
	 * 
	 * @param repository Repository to be added
	 */
	public void addRepository(Repository repository)
	{
		addNodeWithChildren(repository.getRoot(), repository.getRepositoryConfig().isEnabledLazyLoading());
	}
	
	/**
	 * Removes all the nodes and resources of a repository from the registry
	 * 
	 * @param repository Repository to be removed
	 */
	public void removeRepository(Repository repository) {
	
		removeNodeWithChildren(repository.getRoot());
	}
	
	/**
	 * Removes all the nodes and resources of a repository from the registry
	 * 
	 * @param repositoryId Id of the repository to be removed
	 */
	public void removeRepository(String repositoryId) {
	
		Repository repository = RepositoryRegistry.getInstance().getRepository(repositoryId);
		removeRepository(repository);
	}
	
	/**
	 * Adds a node and all of its children to the registry. If the node doesn't 
	 * have any children, only the node will be added.
	 * 
	 * @param node Node to be added
	 */
	public void addNodeWithChildren(Node node) {
		addNodeWithChildren(node, false);
	}
	public void addNodeWithChildren(Node node, boolean lazy) {
		
		addNode(node, lazy);
		if (node.getNodes() != null) {
			Iterator it = node.getNodes().iterator();
			while (it.hasNext()) {
				Node child = (Node) it.next();
				addNodeWithChildren(child, lazy);
			}
		}
	}
	
	/**
	 * Removes a node and all of its children from the registry. If the node doesn't 
	 * have any children, only the node will be removed.
	 * 
	 * @param node Node to be added
	 */
	public void removeNodeWithChildren(Node node) {
		
		if (node == null) {
			return;
		}
		removeNode(node);
		if (node.getNodes() == null) {
			return;
		}
		Iterator it = node.getNodes().iterator();
		while (it.hasNext()) {
			Node child = (Node) it.next();
			removeNodeWithChildren(child);
		}
	}
	
	/**
	 * Removes a node and all of its children from the registry. If the node doesn't 
	 * have any children, only the node will be removed.
	 * 
	 * @param node Node to be added
	 */
	public void removeNodeWithChildren(String nodeId) {
		
		Node node = (Node)nodes.get(nodeId);
		if (node != null) {
			removeNodeWithChildren(node);
		}
	}
	
	public void addNode(Node node)
	{
		addNode(node,false);
	}
	/**
	 * Adds a node to the regsitry
	 * 
	 * @param node Node to be added
	 */
	public void addNode(Node node, boolean lazy)
	{
		// The hasChildren method avoids to put documents or resources on the 
		// lazy list, that is unnecessary
		if(lazy && node.hasChildren())
			lazyNodes.put(node.getId(), node);
		else
			nodes.put(node.getId(), node);
		if (node.isDocument()) {
			Set resources = ((Document)node).getResourceNodes();
			if (resources != null) {
				Iterator it = resources.iterator();
				while (it.hasNext()) {
					ResourceNode resource = (ResourceNode) it.next();
					addNode(resource, lazy);
				}
			}
		}
		if (node.isDirectory()) {
			if (node.getNodes() != null) {
				Iterator it = node.getNodes().iterator();
				while (it.hasNext()) {
					Node child = (Node)it.next();
					addNode(child, lazy);
				}
			}
		}
	}

	/**
	 * Removes a node from the registry
	 * 
	 * @param node Node to be removed
	 */
	public void removeNode(Node node) {
	
		if (node == null) {
			return;
		}
		nodes.remove(node.getId());
		
		if (node.getNodes() != null) {
			Iterator it = node.getNodes().iterator();
			while (it.hasNext()) {
				Node child = (Node) it.next();
				removeNode(child);
			}
		}
	}
	
	/**
	 * Removes a node from the registry
	 * 
	 * @param nodeId Id of the node to be removed
	 */
	public void removeNode(String nodeId) {
	
		Node node = (Node)nodes.get(nodeId);
		removeNode(node);
	}

	/**
	 * Returns a node given an id
	 * 
	 * @param nodeId Id of the node
	 * @param repositoryId Id of the repository
	 * 
	 * @return Node Unique instance of that node
	 */
	public Node getNode(String nodeId, String repositoryId) {
		
		Node node = (Node)nodes.get(nodeId);
		if (node == null) {
			// double check
			node = (Node)lazyNodes.get(nodeId);
			if (node == null) {
				// Not still loaded. Load it !
				return loadNode(nodeId,repositoryId);
			}
		}
		return node;
	}	
	
	/**
	 * Returns a node given an id. The caller explicitly calls this method 
	 * when he is sure that the node has been already loaded on the registry. 
	 * Node, that if the node does not have been loaded then this method will 
	 * return <code>null</code>.
	 * 
	 * @param nodeId Id of the node
	 * 
	 * @return Node Unique instance of that node
	 */
	public Node getAlreadyLoadedNode(String nodeId) {
		
		Node node = (Node)nodes.get(nodeId);
		return node;
	}	
	
	/**
	 * Returns a lzay node given an id
	 * 
	 * @param nodeId Id of the node
	 * 
	 * @return Node Unique instance of that node
	 */
	public Node getLazyNode(String nodeId) {
		
		Node node = (Node)lazyNodes.get(nodeId);
		return node;
	}
	
	/**
	 * Singleton 
	 * 
	 * @return Unique instance of this RepositoryRegistry
	 */
	public static EntityRegistry getInstance() {
		
		if (instance == null) {
			instance = new EntityRegistry();
		}
		return instance;
	}
	
	/**
	 * Depending on the Lazy config parameter stored in a repository, this method move loaded node
	 * to the Node list by assuming that:
	 *  - if the repository is not lazy, the node is recusively (the node and its children) moved to
	 *    the node list.
	 *  - else (the repository is lazy), ONLY the node is moved to the loaded list. The children are
	 *    loadedto the lazy list : it assumes that children are lazy.
	 * @param node The node to load
	 */
	public void nodeLoaded(Node node)
	{
		Repository repository = RepositoryRegistry.getInstance().
										getRepository(node.getRepository());
		RepositoryConfig config = repository.getRepositoryConfig();
		if(getLazyNode(node.getId())!= null)
		{
			if(config.isEnabledLazyLoading())
			{
				nodes.put(node.getId(), node);
				Iterator it = node.getNodes().iterator();
				while(it.hasNext())
					addNodeWithChildren((Node)it.next(), true);
			}
			else
				addNodeWithChildren(node, false);
			lazyNodes.remove(getLazyNode(node.getId()));
		}
	}
	
	public Node loadNode(String id, String repositoryId)
	{
		try
		{
			
			logger.debug("Loading node: " + id);
			
			Repository repository = RepositoryRegistry.getInstance().
										getRepository(repositoryId);
			ServerProfile profile = repository.getServerProfile();
			Ticket ticket = repository.getTicket();
			RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			Node node = service.findNode(ticket,id);
			// Store and return
			addNode(node,false);
			return node;
		}
		catch (RepositoryException e)
		{
			return null;
		}
		catch (final SecurityException e)
		{
			logger.error(e.getMessage());
			return null;
		}
	}	
}
