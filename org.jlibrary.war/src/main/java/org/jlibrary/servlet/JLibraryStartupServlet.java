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
package org.jlibrary.servlet;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet is started at jlibrary server startup time
 * 
 * @author martin
 *
 */ 
public class JLibraryStartupServlet extends HttpServlet {
	 
	private static final long serialVersionUID = 2958184797904116139L;
	static Logger logger = LoggerFactory.getLogger(JLibraryStartupServlet.class);
	
	private static String JLIBRARY_HOME = null;
		
	public static final String getJLibraryHome() {
		
		return JLIBRARY_HOME;
	}
	
	/** 
     * Called by the servlet container to indicate to a servlet that 
     * the servlet is being placed into service. 
     *       * @param javax.servlet.ServletConfig config 
     * @throws javax.servlet.ServletException ServletException 
     */ 
    public void init(ServletConfig config) throws ServletException { 

    	super.init(config);
    	
    	JLIBRARY_HOME = config.getInitParameter("jlibrary.home");
    	
    	Properties env = new Properties();
    	Enumeration names = getServletConfig().getInitParameterNames();
    	while (names.hasMoreElements()) {
    	    String name = (String) names.nextElement();
    	    if (name.startsWith("java.naming.")) {
    	    	env.put(name, getServletConfig().getInitParameter(name));
    	    }
    	}
    	/*
        try {
			JCRSecurityService.initRepository(new InitialContext(env));
		} catch (NamingException e) {

			logger.error(e.getMessage(),e);
		}
		*/
    } 
    
    
    /** 
     * Returns information about the servlet, such as author, version, and copyright. By 
     * default, this method returns an empty string. Override this method to have it return 
     * a meaningful value. 
     *  
     * @return  
     */ 
    public java.lang.String getServletInfo() { 
        
    	return "jLibrary Startup servlet"; 
    } 
        
    /**
     * destroy the servlet
     */
    public void destroy() {

    	super.destroy();
    }           
}
