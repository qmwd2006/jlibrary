/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, Blandware (represented by
* Andrey Grebnev), and individual contributors as indicated by the
* @authors tag. See copyright.txt in the distribution for a full listing of
* individual contributors. All rights reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.jlibrary.core.profiles.HTTPServerProfile;
import org.jlibrary.core.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common methods for HTTP delegates
 * 
 * @author mpermar
 *
 */
public class HTTPDelegate {

	static Logger logger = LoggerFactory.getLogger(HTTPRepositoryDelegate.class);
	private URL servletURL;
	
	/**
	 * Constructor
	 * 
	 * @param profile HTTP profile
	 * @param String service name
	 */
	public HTTPDelegate(HTTPServerProfile profile, String serviceName) {
		
		String location = profile.getLocation();
		String servletURL = location;
		if (!servletURL.endsWith("/")) {
			servletURL+="/";
		}
		servletURL+=serviceName;
		try {
			this.servletURL = new URL(servletURL);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * Excecutes a void request
	 * 
	 * @param methodName Name of the method to execute
	 * @param params Method params
	 * 
	 * @throws Exception If there is any problem running the request
	 */
    public void doVoidRequest(
            String methodName,
            Object[] params) throws Exception {
    	
    	doRequest(methodName,params,null,null);
    }
	
	/**
	 * Excecutes a void streamed request
	 * 
	 * @param methodName Name of the method to execute
	 * @param params Method params
	 * @param InputStream contents stream 
	 * 
	 * @throws Exception If there is any problem running the request
	 */
    public void doVoidRequest(
            String methodName,
            Object[] params,
            InputStream stream) throws Exception {
    	
    	doRequest(methodName,params,null,stream);
    }      
    
	/**
	 * Excecutes a void request
	 * 
	 * @param methodName Name of the method to execute
	 * @param params Method params
	 * @param returnClass Class for the return object
	 * 
	 * @throws Exception If there is any problem running the request
	 */    
    public Object doRequest(
            String methodName,
            Object[] params,
            Class returnClass) throws Exception {
    	
    	return doRequest(methodName,params,returnClass,null);
    }
    
	/**
	 * Excecutes a void request
	 * 
	 * @param methodName Name of the method to execute
	 * @param params Method params
	 * @param returnClass Class for the return object. If the class is InputStream this method will return 
	 * the HTTP request input stream
	 * @param inputStream Stream for reading contents that will be sent
	 * 
	 * @throws Exception If there is any problem running the request
	 */    
    public Object doRequest(
            String methodName,
            Object[] params,
            Class returnClass,
            InputStream inputStream) throws Exception {
       
        try {
            logger.debug(servletURL.toString());
            logger.debug("opening connection");
            URLConnection conn = servletURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput (true);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setRequestProperty("Content-type", "text/plain");
            
            //write request and params:
            OutputStream stream = conn.getOutputStream();
            // write streaming header if necessary
            if (inputStream != null) {
                stream.write(ByteUtils.intToByteArray("stream-input".getBytes().length));
                stream.write("stream-input".getBytes());
                stream.flush();
            }
            if (returnClass == InputStream.class) {
                stream.write(ByteUtils.intToByteArray("stream-output".getBytes().length));
                stream.write("stream-output".getBytes());
                stream.flush();            	
            }
            
            // Write method
            stream.write(ByteUtils.intToByteArray(methodName.getBytes().length));
            stream.write(methodName.getBytes());
            //stream.flush();
            // Write parameters
            stream.write(ByteUtils.intToByteArray(params.length));
            for (int i=0;i<params.length;i++) {
            	byte[] content = SerializationUtils.serialize((Serializable)params[i]);        	
            	stream.write(ByteUtils.intToByteArray(content.length));
            	stream.write(content);
            	stream.flush();
            }
            
            if (inputStream != null) {
            	IOUtils.copy(inputStream, stream);
            }
            
            //stream.flush();
            //stream.close();
			
            //read response:
            InputStream input = conn.getInputStream();
            if (returnClass == InputStream.class) {
            	// Contents will be read from outside
            	return input;
            }
            ObjectInputStream objInput = new ObjectInputStream(input);
            Object o = objInput.readObject();            
            if (o == null) {
            	return null;
            }
            stream.close();
            if (o instanceof Exception) {
                throw (Exception)o;
            } else {
            	if (returnClass == null) {
            		return null;
            	}
                // try to cast
                try {
                    returnClass.cast (o);
                } catch (ClassCastException cce) {
                    String msg = "Unexpected response from execution servlet: " + o;
                    logger.error(msg);
                    throw new Exception(msg);                   
                }
            }
            return o;
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (ClassCastException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        }       
    }	
}
