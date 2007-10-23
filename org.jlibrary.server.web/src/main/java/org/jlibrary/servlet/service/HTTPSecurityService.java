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
package org.jlibrary.servlet.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This HTTP Servlet will expect calls through the GET interface to Security Service methods. 
 * This is the basic implementation of a security service. It doesn´t has any parsing or 
 * marshalling costs like web services implementations so it´s very lightweight and it has a 
 * good performance. 
 * 
 * On the other hand, this implementation is very tight coupled to the server. So, any minor 
 * change in the API will change this class.  
 *  
 * @author martin
 *
 */
public class HTTPSecurityService extends HTTPStreamingServlet {

	private static final long serialVersionUID = 7520606671931567655L;
	static Logger logger = LoggerFactory.getLogger(HTTPSecurityService.class);
	
	ServerProfile localProfile = new LocalServerProfile();
	private SecurityService securityService;
	
	public HTTPSecurityService() {
		
		securityService = JLibraryServiceFactory.getInstance(localProfile).getSecurityService();
	}
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (logger.isDebugEnabled()) {
            logger.error("The service doesn't support GET method");
        }            
        super.doGet(request,response);
    }
	
    public void doPost(
            HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {
        
        super.doPost(req, resp);
    }    
    
	protected Object getDelegate() throws Exception{
		
        return securityService;
	}
}
