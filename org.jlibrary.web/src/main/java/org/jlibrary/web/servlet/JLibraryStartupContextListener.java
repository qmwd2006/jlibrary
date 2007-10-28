/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
package org.jlibrary.web.servlet;

import javax.servlet.ServletContextEvent;

import org.jlibrary.core.entities.Ticket;
import org.jlibrary.web.services.StatsService;
import org.jlibrary.web.services.TicketService;

/**
 * Extends the jLibrary startup context listener adding initialization and shutdown 
 * hooks required for this webapp
 * 
 * @author mpermar
 *
 */
public class JLibraryStartupContextListener extends org.jlibrary.servlet.JLibraryStartupContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		super.contextInitialized(sce);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		super.contextDestroyed(sce);
		
		TicketService.getTicketService().clean();
	}
}
