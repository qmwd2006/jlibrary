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
package org.jlibrary.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.servlet.service.HTTPStreamingServlet;
import org.jlibrary.web.servlet.io.AccessStats;
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
public class LimitedHTTPRepositoryServlet extends HTTPStreamingServlet {

	private static final int BYTES_INPUT_LIMIT = 5242880; // 5Mbs maximum per InputStream
	private static final int BYTES_SESSION_INPUT_LIMIT = 20971520; // 20Mbs maximum per HTTP Session
	
	ServerProfile localProfile = new LocalServerProfile();
	private RepositoryService repositoryService;
	
	private ConcurrentHashMap<String, AccessStats> stats = new ConcurrentHashMap<String, AccessStats>();
	
	static Logger logger = LoggerFactory.getLogger(LimitedHTTPRepositoryServlet.class);
	
	public LimitedHTTPRepositoryServlet() {
		
		repositoryService = JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
	}
	
	@Override
	public void init() throws ServletException {

		super.init();
		
		// Now schedule a cleaning task. This task will clean stats for ips. 
		// An IP will have cleared its stats after one day. 
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.scheduleAtFixedRate(new Runnable() {
			public void run() {

				long currentTime = System.currentTimeMillis();
				Iterator<Map.Entry<String, AccessStats>> it = stats.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, AccessStats> entry = it.next();
					if (currentTime - entry.getValue().getCreationTime() > 24*60*60*1000L) {
						// Entry is older than one day. Remove it.
						it.remove();
					}
				}
			}
		},60,60,TimeUnit.SECONDS);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		super.doPost(req, resp);
	}
	
	protected Object getDelegate() throws Exception{
		
        return repositoryService;
	}
	
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
			String ip = request.getRemoteAddr();
			AccessStats accessStats = stats.get(ip);
			if (accessStats == null) {
				accessStats = new AccessStats();
				accessStats.setCreationTime(System.currentTimeMillis());
				accessStats.setInputBandwidthUsed(0L);				
				stats.put(ip,accessStats);
			}
			if (accessStats.getInputBandwidthUsed() > BYTES_SESSION_INPUT_LIMIT) {
				logger.error("Bandwidth exceeded for IP " + ip + ". Total bytes transfered: " + accessStats.getInputBandwidthUsed());
				throw new IOException("You have exceeded the maximum allowed bandwidth for your IP.");
			}
			accessStats.setInputBandwidthUsed(accessStats.getInputBandwidthUsed()+lis.getByteCount());
			if (logger.isDebugEnabled()) {
				logger.debug("IP " + ip + " has read " + accessStats.getInputBandwidthUsed() + " bytes");
			}
		}
		
		return returnValue;
	}	
	

}
