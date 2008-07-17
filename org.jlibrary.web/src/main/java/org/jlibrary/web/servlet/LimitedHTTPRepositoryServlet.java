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
package org.jlibrary.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.servlet.service.HTTPStreamingServlet;
import org.jlibrary.web.services.ConfigurationService;
import org.jlibrary.web.servlet.io.AccessStats;
import org.jlibrary.web.servlet.io.LimitedInputStream;
import org.jlibrary.web.servlet.io.LimitedOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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

	// Default values can be overwritten by Spring configuration
	private static long BYTES_INPUT_LIMIT = 5242880L; // 5Mbs maximum per InputStream
	private static long BYTES_OUTPUT_LIMIT = 5242880L; // 5Mbs maximum per OutputStream
	private static long BYTES_SESSION_INPUT_LIMIT = 20971520L; // 20Mbs maximum per HTTP Session
	private static long BYTES_SESSION_OUTPUT_LIMIT = 20971520L; // 20Mbs maximum per HTTP Session
	
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
		
		ApplicationContext context = 
			WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		ConfigurationService configService = (ConfigurationService)context.getBean("template");
		if (configService.getOperationInputBandwidth() != null) {
			BYTES_INPUT_LIMIT = configService.getOperationInputBandwidth();
		}
		if (configService.getOperationOutputBandwidth() != null) {
			BYTES_OUTPUT_LIMIT = configService.getOperationOutputBandwidth();
		}
		if (configService.getTotalInputBandwidth() != null) {
			BYTES_SESSION_INPUT_LIMIT = configService.getTotalInputBandwidth();
		}
		if (configService.getTotalOutputBandwidth() != null) {
			BYTES_SESSION_OUTPUT_LIMIT = configService.getTotalOutputBandwidth();
		}
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
		LimitedOutputStream los = null;
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
			if (streamClass == InputStream.class) {
				// Tweak the streaming class with a restricted one
				InputStream is = (InputStream)params[params.length-1];
				lis = new LimitedInputStream(is,BYTES_INPUT_LIMIT);
				params[params.length-1] = lis;
			} else {
				// Tweak the streaming class with a restricted one
				OutputStream os = (OutputStream)params[params.length-1];
				los = new LimitedOutputStream(os,BYTES_OUTPUT_LIMIT);
				params[params.length-1] = los;
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
		
        // Check session bandwidths first
		if (lis != null || los != null) {
			String ip = request.getRemoteAddr();
			AccessStats accessStats = stats.get(ip);
			if (accessStats != null) {
				if (lis !=  null) {
					if (accessStats.getInputBandwidthUsed() > BYTES_SESSION_INPUT_LIMIT) {
						logger.error("Input bandwidth exceeded for IP " + ip + ". Total bytes transfered: " + accessStats.getInputBandwidthUsed());
						return new RepositoryException("You have exceeded the maximum allowed intput bandwidth for your IP.");
					}
				}
				if (los != null) {
					if (accessStats.getOutputBandwidthUsed() > BYTES_SESSION_OUTPUT_LIMIT) {
						logger.error("Output bandwidth exceeded for IP " + ip + ". Total bytes transfered: " + accessStats.getOutputBandwidthUsed());
						return new RepositoryException("You have exceeded the maximum allowed output bandwidth for your IP.");
					}
				}
			}
		}
		
		Method method = clazz.getMethod(callMethodName, paramTypes);
		Object returnValue = method.invoke(getDelegate(), params);
		
		if (lis != null || los != null) {
			String ip = request.getRemoteAddr();
			AccessStats accessStats = stats.get(ip);
			if (accessStats == null) {
				accessStats = new AccessStats();
				accessStats.setCreationTime(System.currentTimeMillis());
				accessStats.setInputBandwidthUsed(0L);
				accessStats.setOutputBandwidthUsed(0L);	
				stats.put(ip,accessStats);
			}
			if (lis != null) {
				accessStats.setInputBandwidthUsed(accessStats.getInputBandwidthUsed()+lis.getByteCount());
				if (logger.isDebugEnabled()) {
					logger.debug("IP " + ip + " has read " + accessStats.getInputBandwidthUsed() + " bytes");
				}
			}
			if (los != null) {
				accessStats.setOutputBandwidthUsed(accessStats.getOutputBandwidthUsed()+los.getByteCount());
				if (logger.isDebugEnabled()) {
					logger.debug("IP " + ip + " has written " + accessStats.getOutputBandwidthUsed() + " bytes");
				}
			}
		}
		
		return returnValue;
	}	
	

}
