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
package org.jlibrary.core.http.client;

import java.net.ConnectException;
import java.util.Collection;

import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.profiles.HTTPServerProfile;
import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.RolProperties;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.GroupNotFoundException;
import org.jlibrary.core.security.exception.RoleNotFoundException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will delegate all the calls through the HTTP service
 * 
 * @author martin
 * 
 */
public class HTTPSecurityDelegate extends HTTPDelegate implements SecurityService {

	static Logger logger = LoggerFactory.getLogger(HTTPSecurityDelegate.class);
		
	/**
	 * Constructor
	 */
	public HTTPSecurityDelegate(HTTPServerProfile profile) {
		
		super(profile,"HTTPSecurityService");
	}


	public User createUser(Ticket ticket,UserProperties userProperties) throws SecurityException {
		
		
		User user = (User)doSecurityRequest(
				"createUser",
				new Object [] {ticket,userProperties}, 
				User.class);
		return user;	
	}
	
	public User updateUser(Ticket ticket, 
	        			   UserProperties userProperties) throws SecurityException {
		
		
		User user = (User)doSecurityRequest(
				"updateUser",
				new Object [] {ticket,userProperties}, 
				User.class);
		return user;		
	}

	public User findUserByName(Ticket ticket,
							   String name) throws SecurityException, 
							   					   UserNotFoundException {
		
		User user = (User)doSecurityRequest(
				"findUserByName",
				new Object [] {ticket,name}, 
				User.class);
		return user;
	}

	public User findUserById(Ticket ticket,
							 String userId) throws SecurityException, 
							 					   UserNotFoundException {
		
		
		User user = (User)doSecurityRequest(
				"findUserById",
				new Object [] {ticket,userId}, 
				User.class);
		return user;		
	}
	
	
	public Collection findAllUsers(Ticket ticket) throws SecurityException {
		
		
		Collection collection = (Collection)doSecurityRequest(
				"findAllUsers",
				new Object [] {ticket}, 
				Collection.class);
		return collection;		
	}
	
	public Collection findAllRoles(Ticket ticket) throws SecurityException {
		
		
		Collection collection = (Collection)doSecurityRequest(
				"findAllRoles",
				new Object [] {ticket}, 
				Collection.class);
		return collection;	
	}
	
	public Collection findAllGroups(Ticket ticket) throws SecurityException {
		
		
		Collection collection = (Collection)doSecurityRequest(
				"findAllGroups",
				new Object [] {ticket}, 
				Collection.class);
		return collection;		
	}

	public Rol createRol(Ticket ticket, RolProperties rolProperties) throws SecurityException {
		
		
		Rol rol = (Rol)doSecurityRequest(
				"createRol",
				new Object [] {ticket,rolProperties}, 
				Rol.class);
		return rol;	
	}
	
	public Rol updateRol(Ticket ticket,
	        			 RolProperties rolProperties) throws SecurityException {
		
		
		Rol rol = (Rol)doSecurityRequest(
				"updateRol",
				new Object [] {ticket, rolProperties}, 
				Rol.class);
		return rol;	
	}

	public Rol findRol(Ticket ticket,
					   String rolId) throws SecurityException, 
					   					    RoleNotFoundException {
		
		
		Rol rol = (Rol)doSecurityRequest(
				"findRol",
				new Object [] {ticket,rolId}, 
				Rol.class);
		return rol;	
	}

	public Group createGroup(Ticket ticket, GroupProperties groupProperties) throws SecurityException {
		
		
		Group createGroup = (Group)doSecurityRequest(
				"createGroup",
				new Object [] {ticket,groupProperties}, 
				Group.class);
		return createGroup;	
	}

	public Group updateGroup(Ticket ticket, 
	        				 GroupProperties groupProperties) throws SecurityException {
		
		
		Group createGroup = (Group)doSecurityRequest(
				"updateGroup",
				new Object [] {ticket,groupProperties}, 
				Group.class);
		return createGroup;		
	}
	
	public Group findGroupById(Ticket ticket,
							   String groupId) throws SecurityException, 
							   						  GroupNotFoundException {
				
		Group createGroup = (Group)doSecurityRequest(
				"findGroupById",
				new Object [] {ticket,groupId}, 
				Group.class);
		return createGroup;		
	}

	public void removeUser(Ticket ticket, 
						   String userId) throws SecurityException {
		
		
		doVoidSecurityRequest(
				"removeUser",
				new Object [] {ticket,userId});
	}
	
	public void removeRol(Ticket ticket, 
						  String rolId) throws SecurityException {
		
		
		
		doVoidSecurityRequest(
				"removeRol",
				new Object [] {ticket,rolId});
	}
	
	public void removeGroup(Ticket ticket, 
							String groupId) throws SecurityException {
		
		
		doVoidSecurityRequest(
				"removeGroup",
				new Object [] {ticket,groupId});	
	}

	public Ticket login(Credentials credentials,
						String name) throws UserNotFoundException,
											AuthenticationException, 
											SecurityException,
											ConnectException,
											RepositoryNotFoundException {

		
		try {
			Ticket ticket = (Ticket)doSecurityRequest(
					"login",
					new Object [] {credentials,name},
					Ticket.class);
			return ticket;
		} catch (SecurityException e) {

			if (e.getClass() == UserNotFoundException.class) {
				throw (UserNotFoundException)e;
			}
			if (e.getClass() == AuthenticationException.class) {
				throw (AuthenticationException)e;
			}
			if (e.getCause().getClass() == RepositoryNotFoundException.class) {
				throw (RepositoryNotFoundException)e.getCause();
			}
			if (e.getCause() != null) {
				if (e.getCause().getClass() == ConnectException.class) {
					throw (ConnectException)e.getCause();
				}
			}
			throw (SecurityException)e;
		}	
	}
	
	
	public void disconnect(Ticket ticket) throws SecurityException {
		
		doVoidSecurityRequest(
				"disconnect",
				new Object [] {ticket});
	}	
	
	public Collection findAllRestrictions(Ticket ticket,
								    	  String nodeId) throws SecurityException {

		Collection collection = (Collection)doSecurityRequest(
				"findAllRestrictions",
				new Object [] {ticket,nodeId}, 
				Collection.class);
		return collection;		
	}	

	public void doVoidSecurityRequest(String methodName, Object[] params) throws SecurityException {

		try {
			doVoidRequest(methodName,params);
		} catch (Exception e) {
			if (e instanceof SecurityException) {
				throw (SecurityException)e;
			}
			throw new SecurityException(e);			
		}
	}
	
	public Object doSecurityRequest(
			String methodName, Object[] params, Class returnClass) throws SecurityException {

		try {
			return doRequest(methodName,params,returnClass);
		} catch (Exception e) {
			if (e instanceof SecurityException) {
				throw (SecurityException)e;
			}		
			throw new SecurityException(e);			
		}
	}
}
