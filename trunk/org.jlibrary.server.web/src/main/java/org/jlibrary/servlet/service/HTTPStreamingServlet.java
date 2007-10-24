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
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SerializationUtils;
import org.jlibrary.core.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Streaming bse servlet
 *  
 * @author martin
 *
 */
public abstract class HTTPStreamingServlet extends HttpServlet {

	private static final long serialVersionUID = 7520606671931567655L;
	static Logger logger = LoggerFactory.getLogger(HTTPStreamingServlet.class);
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (logger.isDebugEnabled()) {
            logger.error("The service doesn't support GET method");
        }            
        super.doGet(request,response);
    }
	
    public void doPost(
            HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {
        
        if (logger.isDebugEnabled()) {
            logger.debug("Received a POST request");
        }

        try {
	        InputStream stream = req.getInputStream();
	        byte[] count = read(stream,4);	        
	        int messageNameSize = ByteUtils.byteArrayToInt(count);
	        
	        byte[] messageNameBuffer = read(stream,messageNameSize); 
	        String callMethodName = new String(messageNameBuffer);
	        
	        Object returnValue = null;
	        boolean streamOutput = false;
	        boolean streamInput = false;
	        
	        if (callMethodName.equals("stream-input")) {
	        	// Streaming request. Re-read call method name
	        	streamInput = true;
	        		
	        	count = read(stream,4);
		        messageNameSize = ByteUtils.byteArrayToInt(count);
		        messageNameBuffer = read(stream,messageNameSize); 
		        callMethodName = new String(messageNameBuffer);       	
	        } else if (callMethodName.equals("stream-output")) {
	        	// Streaming request. Re-read call method name
	        	streamOutput = true;
	        		
	        	count = read(stream,4);
		        messageNameSize = ByteUtils.byteArrayToInt(count);
		        messageNameBuffer = read(stream,messageNameSize); 
		        callMethodName = new String(messageNameBuffer);       	
	        }
	        
	        if (logger.isDebugEnabled()) {
	            logger.debug("Calling method: " + callMethodName);
	        }
	        
        	// Handle request
	        count = read(stream,4);
            int parameters = ByteUtils.byteArrayToInt(count);
        	Object[] params = new Object[parameters];
        	if (!streamInput && !streamOutput) {
        		params = new Object[parameters];
        	} else {
        		params = new Object[parameters+1];
        		if (streamInput) {
        			params[parameters] = stream;
        		} else if (streamOutput) {
        			params[parameters] = resp.getOutputStream();
        		}
        	}
        	for (int i=0;i<parameters;i++) {
        		count = read(stream,4);
        		int parameterSize = ByteUtils.byteArrayToInt(count);
        		byte[] parameterBuffer = read(stream,parameterSize);        		
        		params[i] = SerializationUtils.deserialize(parameterBuffer);
        	}        	
        	
        	try {
        		if (!streamInput && !streamOutput) {
        			returnValue = handleRequest(req,callMethodName,params);
        		} else if (streamInput) {
        			returnValue = handleRequest(req,callMethodName,params,InputStream.class);
        		} else if (streamOutput) {
        			returnValue = handleRequest(req,callMethodName,params,OutputStream.class);
        		}
            } catch (InvocationTargetException ite) {            	
            	returnValue = ite.getCause();
            	if (returnValue == null) {
            		returnValue = ite.getTargetException();
            	}
            } catch (Exception e) {
            	returnValue = e;
            }
            
            if (! streamOutput) {
            	handleReturnValue(resp, returnValue);
            }
        } catch (IOException e) {
            logger.error("IOException in doPost", e);
        } catch (Exception e) {
            logger.error("Exception in doPost", e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("POST request handled successfully");
            }
        }
    }

	private void handleReturnValue(HttpServletResponse resp, Object returnValue) throws IOException {

		new ObjectOutputStream(resp.getOutputStream()).writeObject(returnValue);                    
	}    

	protected abstract Object getDelegate() throws Exception;
	
	protected Object handleRequest(HttpServletRequest request, String callMethodName, Object[] params) throws Exception{
		
        return handleRequest(request,callMethodName, params, null);
	}	
	
	protected Object handleRequest(HttpServletRequest request,
								   String callMethodName, 
								   Object[] params, 
								   Class streamClass) throws Exception{
		
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
		return returnValue;
	}	
	
	protected void debugMethodCall(String callMethodName, Class[] paramTypes) {
		
		StringBuffer logMessage = new StringBuffer("Calling method " + callMethodName + " with params [");
		for(Class c: paramTypes) {
			logMessage.append(c.getName());
			logMessage.append(",");
		}
		logMessage.replace(logMessage.length()-1, logMessage.length(), "]");
		logger.debug(logMessage.toString());
	}

	private byte[] read(InputStream stream, int bytes) throws IOException {
				
		byte[] content = new byte[bytes];
		int read = 0;
		while (read < bytes) {
			if (bytes - read > 1024) {
				read+=stream.read(content,read,1024);
			} else {
				read+=stream.read(content,read,bytes-read);
			}
		}
		return content;
	}
}
