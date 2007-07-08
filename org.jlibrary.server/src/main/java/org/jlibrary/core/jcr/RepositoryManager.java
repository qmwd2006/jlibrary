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
package org.jlibrary.core.jcr;

import java.util.HashMap;

import javax.jcr.Repository;

import org.jlibrary.core.entities.Ticket;

/**
 * 
 * This class stores mappings between client repositories and JSR-170 repository
 * objects
 * 
 * @author martin
 *
 */
public class RepositoryManager {

	private static RepositoryManager instance;
	
	private HashMap repositories = new HashMap();
	
	/**
	 * Singleton
	 *
	 */
	private RepositoryManager() {}
	
	/**
	 * Attachs a JSR-170 repository to an user
	 * 
	 * @param ticket Ticket with user information
	 * @param state Repository state
	 */
	public void attach(Ticket ticket, RepositorySessionState state) {
		
		repositories.put(ticket,state);
	}
	
	/**
	 * Removes a JSR-170 repository for an user
	 * 
	 * @param ticket Ticket with user information
	 */
	public void dettach(Ticket ticket) {
		
		repositories.remove(ticket);
	}
	
	/**
	 * Returns a JSR-170 repository for a given ticket
	 * 
	 * @param ticket Ticket with user information
	 * 
	 * @return repository JSR-170 compatible repository
	 */
	public Repository getRepository(Ticket ticket) {
		
		return ((RepositorySessionState)repositories.get(ticket)).getRepository();
	}
	
	/**
	 * Returns the state of the repository
	 * 
	 * @param ticket Ticket with user information
	 * 
	 * @return RepositorySessionState state
	 */
	public RepositorySessionState getRepositoryState(Ticket ticket) {
		
		return (RepositorySessionState)repositories.get(ticket);
	}	
	
	public static RepositoryManager getInstance() {
		
		if (instance == null) {
			instance = new RepositoryManager();
		}
		
		return instance;
	}
}
