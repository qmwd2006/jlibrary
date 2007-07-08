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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.repository.views.WorkingSetView;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Repository;
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
 * Class that will store opened repositories
 */
public class LockRegistry {

	static Logger logger = LoggerFactory.getLogger(LockRegistry.class);
	
	private static LockRegistry instance;

	// Map that stores the documents locked in each repository
	private HashMap locks = new HashMap();
	
	private LockRegistry() {
		
		super();
	}

	public void addRepository(Repository repository) {
		
		ServerProfile serverProfile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		
		try {
			List repositoryLocks = repositoryService.findAllLocks(ticket);
			if (repositoryLocks.size() == 0) {
				return;
			}
			locks.put(repository, repositoryLocks);
		} catch (RepositoryException e) {
			
            logger.error(e.getMessage(),e);
		} catch (SecurityException se) {
			
            logger.error(se.getMessage(),se);
		}
		refreshViews();
	}
	
	public void removeRepository(Repository repository) {
		
		locks.remove(repository);
		refreshViews();
	}
	
	public void lockDocument(Lock lock) {
		
		Repository repository = 
			RepositoryRegistry.getInstance().getRepository(lock.getRepository());		
		List list = (List)locks.get(repository);
		if (list == null) {
			list = new ArrayList();
			locks.put(repository,list);
		}

		list.add(lock);
		refreshViews();
	}
	
	private void refreshViews() {

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				WorkingSetView.refresh();
				if (RepositoryView.getRepositoryViewer() != null) {
					RepositoryView.getRepositoryViewer().refresh();
				}
			}
		});			
	}

	public void unlockDocument(Lock lock) {
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(lock.getRepository());
		List list = (List)locks.get(repository);
		if (list == null) {
			return;
		}
		list.remove(lock);
		if (list.size() == 0) {
			removeRepository(repository);
		} else {
			refreshViews();
		}
	}
	
	public List getLocks(Repository repository) {
		
		String userId = repository.getTicket().getUser().getId();
		List list = (List)locks.get(repository);
		if (list == null) {
			return Collections.EMPTY_LIST;
		}
		ArrayList returnList = new ArrayList();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Lock lock = (Lock) it.next();
			if (lock.getUserId().equals(userId)) {
				returnList.add(lock);
			}
		}
		return returnList;
	}
	
	/**
	 * Singleton 
	 * 
	 * @return Unique instance of this LockRegistry
	 */
	public static LockRegistry getInstance() {
		
		if (instance == null) {
			instance = new LockRegistry();
		}
		return instance;	}


	public Collection getRepositoriesWithLockedDocuments() {
		
		if (locks.size() > 0) {
			return locks.keySet();
		}
		return Collections.EMPTY_SET;
	}
}
