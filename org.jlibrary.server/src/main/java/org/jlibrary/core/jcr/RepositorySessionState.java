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

import javax.jcr.Session;

public class RepositorySessionState {

	private javax.jcr.Session defaultSession;
	private javax.jcr.Repository repository;
	private javax.jcr.Session systemSession;
	
	private HashMap sessions = new HashMap();
	
	public RepositorySessionState(javax.jcr.Session defaultSession,
								  javax.jcr.Repository repository) {
		
		this.repository = repository;
		this.defaultSession = defaultSession;
	}

	public javax.jcr.Session getDefaultSession() {
		return defaultSession;
	}

	public void setDefaultSession(javax.jcr.Session defaultSession) {
		this.defaultSession = defaultSession;
	}

	public javax.jcr.Repository getRepository() {
		return repository;
	}

	public void setRepository(javax.jcr.Repository repository) {
		this.repository = repository;
	}


	
	/**
	 * Attachs a JSR-170 session to an jLibrary repository 
	 * 
	 * @param repositoryId jLibrary repository id
	 * @param session JSR-170 session instance
	 */
	public void attach(String repositoryId, Session session) {
		
		sessions.put(repositoryId,session);
	}
	
	/**
	 * Removes a JSR-170 session for an user
	 * 
	 * @param repositoryId jLibrary repository id
	 */
	public void dettach(String repositoryId) {
		
		sessions.remove(repositoryId);
	}
	
	/**
	 * Returns a JSR-170 session for a given ticket
	 * 
	 * @param repositoryId jLibrary repository id
	 * 
	 * @return Session JSR-170 compatible session
	 */
	public Session getSession(String repositoryId) {
		
		return (Session)sessions.get(repositoryId);
	}

}
