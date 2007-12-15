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
package org.jlibrary.web.servlet.listener;

import java.util.Enumeration;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.web.WebConstants;
import org.jlibrary.web.services.StatsService;
import org.jlibrary.web.services.TicketService;
/**
 * 
 * @author dlatorre
 *
 */
public class JLibrarySessionListener implements HttpSessionListener {
	
	private static Logger logger = Logger.getLogger(JLibrarySessionListener.class);
	private StatsService statsService=StatsService.newInstance();
	
	public void sessionCreated(HttpSessionEvent hse) {
		
    	statsService.incTotalUsers();
	}
	
	public void sessionDestroyed(HttpSessionEvent hse) {
		
    	statsService.decTotalUsers();
    	
    	Enumeration<String> attributeNames = hse.getSession().getAttributeNames();
    	while (attributeNames.hasMoreElements()) {
    		String name = attributeNames.nextElement();
    		if (name.startsWith(TicketService.SESSION_TICKET_ID)) {
    			Object value = hse.getSession().getAttribute(name);
    			// Safety check
    			if ((value != null) && (value instanceof Ticket)) {
    				Ticket ticket = (Ticket)value;
    				if ((ticket == null) || (ticket.getUser() == null)) {
    					if (logger.isDebugEnabled()) {
    						logger.debug("Found an invalid ticket : " + ticket);
    					}
    					continue;
    				}
    				// Guest tickets won't be removed
    				if (ticket.getUser().getName().equals(WebConstants.ANONYMOUS_WEB_USERNAME)) {
    					continue;
    				}
    				
    				LocalServerProfile profile = new LocalServerProfile();
    				SecurityService service = 
    					JLibraryServiceFactory.getInstance(profile).getSecurityService();
    				try {     					
						service.disconnect((Ticket)ticket);
	    				if (logger.isDebugEnabled()) {
	    					logger.debug("User " + ticket.getUser() + " has been disconnected from jLibrary");
	    				}
					} catch (SecurityException e) {
						logger.error("It was impossible to disconnect session for user " + ticket.getUser());
					}
    			}
    		}
    	}
	}
}
