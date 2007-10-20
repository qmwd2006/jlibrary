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
package org.jlibrary.client.ui.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.config.ItemsOpenedRegistry;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.config.ConfigException;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

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

	protected boolean reopenedRepositories;
	
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
		
		// Add the repository to the list of current opened repositories and save config
	 	if (repository.getTicket().isAutoConnect()) {
	 		ItemsOpenedRegistry.getInstance().addRepository(repository,
	 														repositoryName);
			try {
				ItemsOpenedRegistry.getInstance().saveConfig();
			} catch (ConfigException e) {
				
                logger.error(e.getMessage(),e);
			}
	 	}
		
	 	if (DEBUG) {debug(repository);};
		
		repositories.put(repository.getId(), repository);
		RepositoryViewer viewer = 
			RepositoryView.getRepositoryViewer();
		if (viewer != null) {
			viewer.addRepository(repository);
		}
		
		// Update locks registry
		LockRegistry.getInstance().addRepository(repository);
		
		// Adds all the nodes to the node registry
		EntityRegistry.getInstance().addRepository(repository);
	}

	/**
	 * Records the removal of a repository
	 * 
	 * @param repository Repository to be removed
	 */
	public void removeRepository(Repository repository) {
	
		// Remove the repository from the list of current opened repositories and save config
		if (repository.getTicket().isAutoConnect()) {
			ItemsOpenedRegistry.getInstance().removeRepository(repository);
			try {
				ItemsOpenedRegistry.getInstance().saveConfig();
			} catch (ConfigException e) {
				
	            logger.error(e.getMessage(),e);
			}
		}
		repositories.remove(repository.getId());
		
		// Update the repository viewer with the new repository
		RepositoryViewer viewer = RepositoryView.getRepositoryViewer();
		if (viewer != null) {
			viewer.removeRepository(repository);
		}
		
		// Update locks registry
		LockRegistry.getInstance().removeRepository(repository);
		
		// Removes all the nodes from the node registry
		if (repository.isConnected()) {
			EntityRegistry.getInstance().removeRepository(repository);
		}
	}
	
	/**
	 * Records the close of a repository
	 * 
	 * @param repository Repository to be removed
	 */
	public void closeRepository(Repository repository) {
	
		// Remove the repository from the list of current opened repositories and save config
		if (repository.getTicket().isAutoConnect()) {
			ItemsOpenedRegistry.getInstance().closeRepository(repository);
			try {
				ItemsOpenedRegistry.getInstance().saveConfig();
			} catch (ConfigException e) {
				
	            logger.error(e.getMessage(),e);
			}
		}		
		// Update locks registry
		LockRegistry.getInstance().removeRepository(repository);
		
		// Removes all the nodes from the node registry
		EntityRegistry.getInstance().removeRepository(repository);
	}
	
	/**
	 * Records the reconnection to a repository
	 * 
	 * @param repository Repository to connect
	 */
	public void reconnectToRepository(Repository repository) {
	
		if (repository.getTicket().isAutoConnect()) {
			ItemsOpenedRegistry.getInstance().reconnectToRepository(repository);
		}
		try {
			ItemsOpenedRegistry.getInstance().saveConfig();
		} catch (ConfigException e) {
			
            logger.error(e.getMessage(),e);
		}
		
		repositories.put(repository.getId(), repository);
		
		// Update the repository viewer with the new repository
		RepositoryViewer viewer = RepositoryView.getRepositoryViewer();
		if (viewer != null) {
			viewer.replaceRepository(repository);
		}
		
		// Update locks registry
		LockRegistry.getInstance().addRepository(repository);
		
		// Removes all the nodes from the node registry
		EntityRegistry.getInstance().addRepository(repository);
	}
	
	/**
	 * Records the removal of a repository
	 * 
	 * @param repositoryId Id of the repository to be removed
	 * @param removeConfig Flag that indicates if the repository has to be removed 
	 * from the list of repositories to be opened in the next session
	 */
	public void removeRepository(String repositoryId, boolean removeConfig) {
		//TODO : Use removeConfig flag
		Repository repository = (Repository)repositories.get(repositoryId);
		removeRepository(repository);
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

	public void reopenRepositories() {

		final Job refreshJob = new Job(Messages.getMessage("job_loading_repositories")) {
			public IStatus run(IProgressMonitor monitor) {
				RepositoryRegistry.getInstance().reopenRepositories(monitor);
				reopenedRepositories = true;
				return Status.OK_STATUS;
			}
			
			public boolean belongsTo(Object family) {
				return family.equals(JobTask.LOADING_REPOSITORIES);
			}
		};
		refreshJob.schedule();	
	}
	
	private List reopenRepositories(IProgressMonitor monitor) {
		
		// Load items opened registry
		Repository repository = null;
		try {
			ItemsOpenedRegistry.loadConfig();
			List repositories = 
				ItemsOpenedRegistry.getInstance().getOpenedRepositories(monitor);
			Iterator it = repositories.iterator();
			
			while (it.hasNext()) {
				repository = (Repository) it.next();
				
				this.repositories.put(repository.getId(), repository);
				
				if (!repository.isConnected()) {
					aynchronouslyAdd(repository);
					continue;
				}
				// Update locks registry
				LockRegistry.getInstance().addRepository(repository);
				
				// Adds all the nodes from the node registry
				EntityRegistry.getInstance().addRepository(repository);
				aynchronouslyAdd(repository);
			 	if (DEBUG) {debug(repository);};

			}
			return repositories;
		} catch (ConfigException e) {
			ItemsOpenedRegistry.getInstance().removeRepository(repository);
			try {
				ItemsOpenedRegistry.getInstance().saveConfig();
			} catch (ConfigException ce) {
				logger.error(ce.getMessage(),ce);
			}
			
            logger.error(e.getMessage(),e);
			return Collections.EMPTY_LIST;
		}	
	}
	
	private void aynchronouslyAdd(final Repository repository) {

		repositories.put(repository.getId(), repository);
		PlatformUI.getWorkbench().getDisplay().asyncExec(
			new Runnable() {
				public void run() {
					RepositoryViewer viewer = 
						RepositoryView.getRepositoryViewer();
					if (viewer != null) {
						viewer.addRepository(repository);
					}
					if (repository.isConnected()) {
						// Update locks registry
						LockRegistry.getInstance().addRepository(repository);
						
						// Adds all the nodes to the node registry
						EntityRegistry.getInstance().addRepository(repository);
					}
					
				 	CategoriesView categoriesView = 
				 		JLibraryPlugin.findCategoriesView();
				 	if (categoriesView != null) {
				 		categoriesView.refresh();
				 	}
				 	
				 	BookmarksView.addRepository(repository);
				}
		});		
	}

	/**
	 * This method is useful to know the data that comes from the server. It's used
	 * to optimize the bandwidth usage.
	 * 
	 * @param repository Repository to debug
	 */
	private void debug(Repository repository) {
		
		XStream xstream = new XStream();
		ClassLoader clientClassLoader = 
			JLibraryPlugin.getDefault().getClass().getClassLoader();
		xstream.setClassLoader(clientClassLoader);
		
		String str = xstream.toXML(repository);
		PrintWriter pw = null;
		FileOutputStream fos = null;
		try {
			new File("c:/tmp/" + repository.getName()).createNewFile();
			fos = new FileOutputStream("c:/tmp/" + repository.getName());
			pw = new PrintWriter(fos);
			pw.print(str);
			logger.debug("Repository info on /tmp/"+repository.getName());
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
		} finally {
			try {
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		
	}

	public boolean isReopenedRepositories() {
		return reopenedRepositories;
	}
}
