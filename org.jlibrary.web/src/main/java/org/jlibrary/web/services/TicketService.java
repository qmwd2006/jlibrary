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
package org.jlibrary.web.services;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.RepositoryManager;
import org.jlibrary.core.jcr.RepositorySessionState;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.web.WebConstants;

/**
 * <p>This class handles ticket creation and management. Every content request will use the ticket 
 * service to get an appropiate ticket for a given request and repository.</p>
 * 
 * <p>If the user already has a ticket in its session then that ticket will be used. If the user 
 * does not have any ticket then a 'guest' ticket will be created. If the 'guest' user does not 
 * exist then it will be created and 'read' permissions will be added for all content to that user.</p> 
 * 
 * @author mpermar
 */
public class TicketService {

	private static Logger logger = Logger.getLogger(TicketService.class);
	
	public static final String SESSION_TICKET_ID = "ticket";
	private volatile static TicketService instance;
	
	/**
	 * Admin ticket in the system repository. 
	 */
	private Ticket systemTicket;
	
	private ConcurrentHashMap<String, Ticket> guestTickets = new ConcurrentHashMap<String, Ticket>();

	private String rootPassword;
	
	public TicketService() {}
	
	/**
	 * Cleans up acquired resources
	 */
	public void clean() {
		
		if (systemTicket != null) {
			try {
				LocalServerProfile profile = new LocalServerProfile();
				SecurityService service = 
					JLibraryServiceFactory.getInstance(profile).getSecurityService();	
				service.disconnect(systemTicket);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	@Override
	protected void finalize() throws Throwable {

		// System should not rely in this method though
		clean();
	}
	
	/**
	 * Returns a ticket for the given request and repository
	 * 
	 * @param request Request
	 * @param repositoryName Repository name
	 * 
	 * @return Ticket Ticket for the user. If the user already has a ticket in its session then 
	 * that ticket will be returned, otherwise a new 'guest' ticket will be created
	 */
	public Ticket getTicket(HttpServletRequest request, String repositoryName) {
		
		Ticket ticket = null;
		
		HttpSession session = request.getSession(true);
		ticket = (Ticket)session.getAttribute((SESSION_TICKET_ID+repositoryName).toLowerCase());
		if (ticket == null) {
			// Obtain a guest ticket. Synchronization for obtaining guest tickets it is not very 
			// critical so we don't bother adding synchronized blocks for this collection
			ticket = guestTickets.get(repositoryName);
			if (ticket == null) {
				ticket = createGuestSession(session, repositoryName);
			}			
		} else {
			// Check that the session is still valid, e.g. between server restarts
			
			RepositoryManager manager = RepositoryManager.getInstance();
			RepositorySessionState state = manager.getRepositoryState(ticket);
			if (state == null) {
				// Session not active
				ticket = createGuestSession(session, repositoryName);
			}
		}
		
		return ticket;
	}

	/**
	 * Adds a new Ticket to the user session. 
	 * 
	 * @param request HTTP Request object
	 * @param repositoryName Repository to which we will add the user's ticket
	 * @param ticket Ticket with user information.
	 */
	public void putTicket(HttpServletRequest request, String repositoryName, Ticket ticket) {
		
		HttpSession session = request.getSession(true);
		session.setAttribute((SESSION_TICKET_ID+repositoryName).toLowerCase(),ticket);		
	}
	
	/**
	 * Removes a ticket from the user's session. This method will commonly be called when the 
	 * user logs out from a repository and so the ticket must be removed from the user's space.
	 * 
	 * @param request HTTP request
	 * @param repositoryName Repository name which the user is logging out
	 */
	public void removeTicket(HttpServletRequest request, String repositoryName) {
		
		HttpSession session = request.getSession(true);
		session.removeAttribute((SESSION_TICKET_ID+repositoryName).toLowerCase());
	}
	
	private Ticket createGuestSession(HttpSession session, String repositoryName) {
		Ticket ticket;
		ticket = createGuestTicket(repositoryName);
		if (ticket == null) {
			logger.error("Unable to obtain a guest ticket for repository '" + repositoryName + "'");
			return null;
		}	
		guestTickets.put(repositoryName, ticket);
		session.setAttribute((SESSION_TICKET_ID+repositoryName).toLowerCase(), ticket);
		return ticket;
	}
	
	private synchronized Ticket createGuestTicket(String repositoryName) {
		
		LocalServerProfile profile = new LocalServerProfile();
		SecurityService service = 
			JLibraryServiceFactory.getInstance(profile).getSecurityService();	
		Credentials credentials = new Credentials();
		credentials.setUser(User.ADMIN_NAME);
		credentials.setPassword(rootPassword);
		Ticket adminTicket = null;
		try {
			adminTicket = service.login(credentials, repositoryName);
			// Check that the guest user exists, otherwise create it 
			Credentials webCredentials = new Credentials();
			webCredentials.setUser(WebConstants.ANONYMOUS_WEB_USERNAME);
			webCredentials.setPassword(WebConstants.ANONYMOUS_WEB_PASSWORD);
			User user = null;
			try {
				user = service.findUserByName(adminTicket, WebConstants.ANONYMOUS_WEB_USERNAME);
				logger.debug("Found user '" + user.getName() + "'");
				if (!user.getPassword().equals(WebConstants.ANONYMOUS_WEB_PASSWORD)) {
					logger.error("User '" + WebConstants.ANONYMOUS_WEB_USERNAME + "' has an " +
								 "unexpected password. This probably means that the user already " +
								 "existed before in the repository. The web application expects an " +
								 "user with name '" + WebConstants.ANONYMOUS_WEB_USERNAME + "' and " +
								 "password '" + WebConstants.ANONYMOUS_WEB_PASSWORD + " to properly run. " +
								 "You can run your jLibrary admin tool and change the password to fix " +
								 "this issue.");
				}
			} catch (UserNotFoundException unfe) {

				UserProperties userProperties = new UserProperties();
				userProperties.addProperty(UserProperties.USER_NAME, WebConstants.ANONYMOUS_WEB_USERNAME);
				userProperties.addProperty(UserProperties.USER_PASSWORD, WebConstants.ANONYMOUS_WEB_PASSWORD);
				userProperties.addProperty(UserProperties.USER_FIRSTNAME, WebConstants.ANONYMOUS_WEB_USERNAME);
				userProperties.addProperty(UserProperties.USER_LASTNAME, WebConstants.ANONYMOUS_WEB_USERNAME);
				userProperties.addProperty(UserProperties.USER_REPOSITORY, adminTicket.getRepositoryId());
				logger.debug("Creating user '" + WebConstants.ANONYMOUS_WEB_USERNAME + "'");
				user = service.createUser(adminTicket, userProperties);
				logger.debug("User '" + WebConstants.ANONYMOUS_WEB_USERNAME + "' was created successfully");
				userProperties = user.dumpProperties();
				// The repository has the same id that the root directory. Adding a restriction over
				// a directory will propagate that restriction down to all the folders. So the guest 
				// user will be able to read all the repository folders.
				//
				// An admin can disable rights using the admin interface
				userProperties.addProperty(UserProperties.USER_ADD_RESTRICTION, adminTicket.getRepositoryId());
				logger.debug("Propagating permissions for user '" + WebConstants.ANONYMOUS_WEB_USERNAME + "'. This operation can take some time.");
				service.updateUser(adminTicket, userProperties);
				logger.debug("Permissions were successfully propagated.");
			}
			Ticket ticket = service.login(webCredentials, repositoryName);
			return ticket;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		} finally {
			if (adminTicket != null) {
				try {
					service.disconnect(adminTicket);
				} catch (SecurityException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
	
	/**
	 * Returns the system ticket
	 * 
	 * @return Ticket System ticket
	 */
	public Ticket getSystemTicket() {
		
		return systemTicket;
	}
	
	/**
	 * Returns the ticket service instance
	 * 
	 * @return TicketService Ticket service instance
	 */
	public static TicketService getTicketService() {
		
		if (instance == null) {
			synchronized(TicketService.class) {
				if (instance == null)
					instance= new TicketService();
				}
	     	}
	    return instance;
	}

	/**
	 * Initialises this ticket service
	 * 
	 * @param configService Configuration service
	 */
	public void init(ConfigurationService configService) {

		this.rootPassword = configService.getRootPassword();
		
		LocalServerProfile profile = new LocalServerProfile();
		SecurityService service = 
			JLibraryServiceFactory.getInstance(profile).getSecurityService();	
		Credentials credentials = new Credentials();
		credentials.setUser(User.ADMIN_NAME);
		credentials.setPassword(rootPassword);
		try {
			systemTicket = service.login(credentials, "system");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
}
