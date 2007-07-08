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
package org.jlibrary.core.security.axis;

import java.net.ConnectException;
import java.util.Collection;

import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRLocalSecurityService;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.RolProperties;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.GroupNotFoundException;
import org.jlibrary.core.security.exception.RoleNotFoundException;
import org.jlibrary.core.security.exception.UserNotFoundException;


/**
 * @author martin
 *
 * Axis web service for security management
 */
public class AxisSecurityService implements SecurityService {

	private ServerProfile localProfile = new LocalServerProfile();
	
	/**
	 * Constructor
	 */
	public AxisSecurityService() {}

	public User createUser(Ticket ticket,
						   UserProperties userProperties) 
													throws SecurityException {
				
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.createUser(ticket,userProperties);
	}
	
	public User updateUser(Ticket ticket, 
	        			   UserProperties userProperties) 
													throws SecurityException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.updateUser(ticket, userProperties);
	}

	public User findUserByName(Ticket ticket,
							   String name) throws SecurityException, 
							   					   UserNotFoundException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.findUserByName(ticket,name);
	}

	public User findUserById(Ticket ticket,
							 String id) throws SecurityException, 
							 				   UserNotFoundException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.findUserById(ticket,id);
	}
	
	
	public Collection findAllUsers(Ticket ticket) throws SecurityException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.findAllUsers(ticket);
	}
	
	public Collection findAllRoles(Ticket ticket) throws SecurityException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.findAllRoles(ticket);
	}
	
	public Collection findAllGroups(Ticket ticket) throws SecurityException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.findAllGroups(ticket);
	}
	
	public Group createGroup(Ticket ticket, 
							 GroupProperties groupProperties) 
													throws SecurityException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.createGroup(ticket, groupProperties);
	}
	
	public Group updateGroup(Ticket ticket, 
	        				 GroupProperties groupProperties) 
													throws SecurityException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.updateGroup(ticket, groupProperties);
	}

	public Group findGroupById(Ticket ticket,
							   String id) throws SecurityException, 
							   					 GroupNotFoundException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.findGroupById(ticket,id);
	}
	
	public Rol createRol(Ticket ticket, 
						 RolProperties rolProperties) 
													throws SecurityException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.createRol(ticket,rolProperties);
	}
	
	public Rol updateRol(Ticket ticket, 
	        			 RolProperties rolProperties) throws SecurityException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.updateRol(ticket, rolProperties);
	}

	public Rol findRol(Ticket ticket,
					   String rolId) throws SecurityException, 
					   						RoleNotFoundException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.findRol(ticket,rolId);
	}
	
	public void removeUser(Ticket ticket, 
						   String userId) throws SecurityException, 
						   						 UserNotFoundException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		service.removeUser(ticket,userId);
	}
	
	public void removeGroup(Ticket ticket, 
							String groupId) throws SecurityException, 
												   GroupNotFoundException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		service.removeGroup(ticket, groupId);
	}
	
	public void removeRol(Ticket ticket, 
						  String rolId) throws SecurityException, 
						  					   RoleNotFoundException {
		
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		service.removeRol(ticket,rolId);
	}
	
	public Ticket login(Credentials credentials,
						String name) throws UserNotFoundException,
											AuthenticationException, 
											SecurityException,
											ConnectException,
											RepositoryNotFoundException {

		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.login(credentials,name);
	}
	
	public void disconnect(Ticket ticket) throws SecurityException {
	
		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		service.disconnect(ticket);
	}
	
	public Collection findAllRestrictions(Ticket ticket, 
										  String nodeId) 
												throws SecurityException {

		SecurityService service = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
		return service.findAllRestrictions(ticket,nodeId);		
	}
}
