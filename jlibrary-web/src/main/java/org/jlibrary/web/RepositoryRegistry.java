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
package org.jlibrary.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Class that will store opened repositories
 */
public class RepositoryRegistry {

	static Logger logger = LoggerFactory.getLogger(RepositoryRegistry.class);
	
	private static final boolean DEBUG = false;
	
	private static RepositoryRegistry instance;

	private HashMap repositories = new HashMap();
	
	private RepositoryRegistry() {
		
		super();
	}
	
	/**
	 * Records the addition of a repository
	 * 
	 * @param repository Repository to be added
	 */
	public void addRepository(final Repository repository,
							  String repositoryName) {
		
		
		repositories.put(repository.getId(), repository);		
	}

	/**
	 * Records the removal of a repository
	 * 
	 * @param repository Repository to be removed
	 */
	public void removeRepository(Repository repository) {

		repositories.remove(repository.getId());		
	}

	/**
	 * Returns a repository given an id
	 * 
	 * @param repositoryId Id of the repository to be retrieved
	 * 
	 * @return Repository
	 */
	public Repository getRepository(String repositoryId) {
		
		Repository repository = (Repository)repositories.get(repositoryId);
		return repository;
	}

	/**
	 * Returns a repository given a Name
	 * 
	 * @param repositoryName Name of the repository to be retrieved
	 * 
	 * @return Repository
	 */
	public Repository getRepositoryByName(String repositoryName)
	{
		Iterator it = repositories.values().iterator();
		while (it.hasNext())
		{
			Repository r = (Repository) it.next();
			if(r.getName().equals(repositoryName))
				return r;
		}
		return null;
	}
	
	/**
	 * Returns a server profile given a repository id
	 * 
	 * @param repositoryId Id of the repository
	 * 
	 * @return ServerProfile profile for that repository
	 */
	public ServerProfile getServerProfile(String repositoryId) {
		
		Repository repository = (Repository)repositories.get(repositoryId);
		return repository.getServerProfile();
	}
	
	/**
	 * Returns the number of repositories opened
	 * 
	 * @return int Number of repositories opened
	 */
	public int getRepositoryCount() {
		
		return repositories.size();
	}
	
	/**
	 * Returns the number of connected repositories
	 * 
	 * @return int Number of connected repositories
	 */
	public int getConnectedRepositoriesCount() {
		
		int size = 0;
		Iterator it = getOpenedRepositories().iterator();
		while (it.hasNext()) {
			Repository repository = (Repository) it.next();
			if (repository.isConnected()) {
				size++;
			}
		}
		return size;
	}
	
	/**
	 * Tells if the repository registry is empty
	 * 
	 * @return <code>true</code> if the repository registry is empty and false otherwise
	 */
	public boolean isEmpty() {
		
		return repositories.isEmpty();
	}
	
	/**
	 * Tells if a repository is currently opened
	 * 
	 * @param repository Repository
	 * 
	 * @return <code>true</code> if the repository is opened and false otherwise
	 */
	public boolean isOpened(Repository repository) {
		
		return repositories.values().contains(repository);
	}
	
	/**
	 * Returns a collection with all the current opened repositories
	 * 
	 * @return Collection of opened repositories
	 */
	public Collection getOpenedRepositories() {
		
		return repositories.values();
	}
	
	/**
	 * Singleton 
	 * 
	 * @return Unique instance of this RepositoryRegistry
	 */
	public static RepositoryRegistry getInstance() {
		
		if (instance == null) {
			instance = new RepositoryRegistry();
		}
		return instance;
	}
}
