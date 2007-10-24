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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jlibrary.servlet.service.HTTPRepositoryService;
import org.jlibrary.web.servlet.io.LimitedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet extends the regular HTTP repository service available in the 
 * org.jlibrary.war project. The idea is to add some limitations to prevent the 
 * web application to be attacked by malicious users. Example of these limitations 
 * can be just to limit the amount of binary data that can be transferred in a 
 * single call or the total amount of binary data. 
 * 
 * @author mperez
 *
 */
@SuppressWarnings("serial")
public class LimitedHTTPRepositoryServlet extends HTTPRepositoryService {

	private static final String KEY_BYTES_SESSION_INPUT_LIMIT = "bytesSessionInputLimit";
	
	private static final int BYTES_INPUT_LIMIT = 5242880; // 5Mbs maximum per InputStream
	private static final int BYTES_SESSION_INPUT_LIMIT = 20971520; // 20Mbs maximum per HTTP Session
	
	static Logger logger = LoggerFactory.getLogger(LimitedHTTPRepositoryServlet.class);
	
	@Override
	protected Object handleRequest(HttpServletRequest request,
								   String callMethodName, 
								   Object[] params, 
								   Class streamClass) throws Exception{
		
		LimitedInputStream lis = null;
		Class clazz = getDelegate().getClass();
		Class[] paramTypes = new Class[params.length];
		if (streamClass == null) {
			for (int i=0;i<params.length;i++) {
				paramTypes[i] = params[i].getClass();
			}
		} else {
			for (int i=0;i<params.length-1;i++) {
				paramTypes[i] = params[i].getClass();
			}
			// Tweak the streaming class with a restricted one
			InputStream is = (InputStream)params[params.length-1];
			lis = new LimitedInputStream(is,BYTES_INPUT_LIMIT);
			params[params.length-1] = lis;
			paramTypes[params.length-1]=streamClass;
			
		}
		
		//TODO: Fix this hack. Probably the class type should also come in the request for each param
		for (int i=0;i<paramTypes.length;i++) {
			if (paramTypes[i].getName().equals("java.util.ArrayList")) {
				paramTypes[i] = List.class;
			}
		}
		
        if (logger.isDebugEnabled()) {
        	debugMethodCall(callMethodName,paramTypes);            
        }
		
		Method method = clazz.getMethod(callMethodName, paramTypes);
		Object returnValue = method.invoke(getDelegate(), params);
		
		if (lis != null) {
			HttpSession session = request.getSession(true);
			Long bytes = (Long)session.getAttribute(KEY_BYTES_SESSION_INPUT_LIMIT);
			if (bytes == null) {
				bytes = 0L;
			}
			bytes+=lis.getByteCount();
			if (bytes > BYTES_SESSION_INPUT_LIMIT) {
				logger.error("Bandwidth exceeded for session " + session.getId() + ". Total bytes transfered: " + bytes);
				throw new IOException("You have exceeded the maximum allowed bandwidth for your session.");
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Session " + session.getId() + " has read " + bytes + " bytes");
				}
				session.setAttribute(KEY_BYTES_SESSION_INPUT_LIMIT, bytes);
			}
		}
		
		return returnValue;
	}	
	

}
