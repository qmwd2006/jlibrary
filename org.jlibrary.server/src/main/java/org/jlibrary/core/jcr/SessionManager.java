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

import org.jlibrary.core.entities.Ticket;

/**
 * 
 * This class stores mappings between client sessions and JSR-170 session
 * objects
 * 
 * @author martin
 *
 */
//TODO: It turns out that session objects in Jackrabbit are now quite lightweight. Remove this class
// and maintain a session object per http request.   
// See: http://www.nabble.com/Sharing-a-Session-or-a-Session-per-web-user-tf4851166.html#a13954712
public class SessionManager {

	private static SessionManager instance = new SessionManager();
	
	private HashMap sessions = new HashMap();
	
	/**
	 * Singleton
	 *
	 */
	private SessionManager() {}
	
	/**
	 * Attachs a JSR-170 session to an user
	 * 
	 * @param ticket Ticket with user information
	 * @param session JSR-170 session instance
	 */
	public void attach(Ticket ticket, Session session) {
		
		sessions.put(ticket,session);
	}
	
	/**
	 * Removes a JSR-170 session for an user
	 * 
	 * @param ticket Ticket with user information
	 */
	public void dettach(Ticket ticket) {
		
		sessions.remove(ticket);
	}
	
	/**
	 * Returns a JSR-170 session for a given ticket
	 * 
	 * @param ticket Ticket with user information
	 * 
	 * @return Session JSR-170 compatible session
	 */
	public Session getSession(Ticket ticket) {
		
		return (Session)sessions.get(ticket);
	}
	
	/**
	 * Returns the number of opened sessions on this server
	 * 
	 * @return int Opened session count
	 */
	public int getOpenedSessionCount() {
		
		return sessions.size();
	}
	
	public static SessionManager getInstance() {
		
		return instance;
	}
}
