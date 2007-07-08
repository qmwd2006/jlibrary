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
package org.jlibrary.cache;

import java.io.InputStream;

import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Interface for local cache implementations
 */
public interface LocalCache {

	/**
	 * Returns <code>true</code> if the node is in the local cache, otherwise
	 * returns <code>false</code>
	 * 
	 * @param node Node
	 * 
	 * @return boolean <code>true</code> if the node is in the local cache, otherwise
	 * returns <code>false</code>
	 */
	public boolean isNodeCached(Node node) throws LocalCacheException;
	
	
	/**
	 * Returns the cached node content
	 * 
	 * @param node Node
	 * 
	 * @return InputStream Stream to read contents from
	 * 
	 * @throws LocalCacheException if some error happens
	 */
	public InputStream getNodeContent(Node node) throws LocalCacheException;
		
	/**
	 * Adds a node with its content to the local cache with its resources
	 * 
	 * @param node Node to add
	 * @param handler Handler to perform the copy operation
	 * @param resources An array with the resource contents
	 * 
	 * @throws LocalCacheException if some error happens	 
	 */
	public void addNodeToCache(Node node, NodeContentHandler handler) throws LocalCacheException;
	
	/**
	 * Removes a node from the local cache
	 * 
	 * @param node Node to be removed
	 * 
	 * @throws LocalCacheException If the node can't be removed
	 */
	public void removeNodeFromCache(Node node) throws LocalCacheException;
	
	/**
	 * Clears the content of the local cache
	 * 
	 * @throws LocalCacheException if some error happens	  
	 */
	public void clearCache() throws LocalCacheException;
	
	/**
	 * Clears the content of the local cache for a specific repository
	 * @param repository
	 * 
	 * @throws LocalCacheException if some error happens	  
	 */
	public void clearCache(Repository repository) throws LocalCacheException;
	
	/**
	 * Returns the node path in the local cache
	 * 
	 * @param node Node
	 * 
	 * @return String Node's path in the local cache
	 */
	public String getNodePath(Node node) throws LocalCacheException;
		
}
