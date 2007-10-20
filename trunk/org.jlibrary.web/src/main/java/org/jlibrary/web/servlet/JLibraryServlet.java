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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jlibrary.web.services.TemplateService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This is a base class for jLibrary servlets. It includes several utility methods 
 * that all the servlets will share. It is also used to share Spring configuration. 
 * 
 * @author mpermar
 *
 */
@SuppressWarnings("serial")
public class JLibraryServlet extends HttpServlet{
	
	protected ApplicationContext context;
	
	private static Logger logger = Logger.getLogger(JLibraryServlet.class);
	
	@Override
	public void init() throws ServletException {
		context=WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		super.init();
	}
	
	/**
	 * Returns the web template used for this webapp
	 * 
	 * @return String web template path
	 */
	public String getTemplate(){
		
		TemplateService templ=(TemplateService) context.getBean("template");
		return templ.getTemplateDirectory();		
	}
	
	/**
	 * Returns the referer URL. If the referer URL is not found then the main web page
	 * will be returned. 
	 * 
	 * @param request Request
	 * @param repositoryName Repository name
	 * 
	 * @return String referer URL
	 */
	protected String getRefererURL(HttpServletRequest request, String repositoryName) {
		
		// Get the referer. We will use it in case of errors
		String refererURL = request.getHeader("referer");
		if (refererURL == null) {
			refererURL = "/repositories/" + repositoryName;
		} else {
			int i = refererURL.indexOf("/repositories");
			refererURL = refererURL.substring(i,refererURL.length());
		}
		return refererURL;
	}
	
	/**
	 * Returns the root URL of this application. 
	 * 
	 * @param request Requets
	 * 
	 * @return String root url
	 */
	protected String getRootURL(HttpServletRequest request) {

		return request.getScheme( ) + "://"
				+ request.getLocalAddr( )  + ":"
				+ request.getLocalPort( )
				+ request.getContextPath( );
	}

	/**
	 * Returns the repository URL. The repository url is made by the root URL plus
	 * repositories plus the repository name
	 * 
	 * @param request Request
	 * @param repositoryName Repository name
	 * 
	 * @return String repository url
	 */
	protected String getRepositoryURL(HttpServletRequest request, String repositoryName) {
		
		return getRootURL(request) + "/repositories/" + repositoryName;
	}
	
	/**
	 * Logs an error and forwards the HTTP request to the referer page or to the main 
	 * web page if there is no referer available. 
	 * 
	 * @param req HTTP request
	 * @param resp HTTP response
	 * @param repositoryName Repository name
	 * @param e Exception that will be logged
	 * @param message Message error. The message error will be put into the request so 
	 * the client web pages can use it to show information to the user.
	 */
	protected void logErrorAndForward(HttpServletRequest req,
			   						  HttpServletResponse resp, 
			   						  String repositoryName, 
			   						  Exception e, 
			   						  String message) {

		if (logger.isDebugEnabled()) { logger.error(e.getMessage(),e);}
		String refererURL = "";
		if (req.getAttribute("error") == null) {
			refererURL = getRefererURL(req, repositoryName);
		} else {
			// This if branch is to prevent from recursive errors. If we come from another 
			// error page we will forward to the root.
			if (repositoryName.equals("search") || (repositoryName.equals("forward"))) {
				refererURL = "/";
			} else {
				refererURL = "/repositories/" + repositoryName;
			}
		}
		RequestDispatcher rd = getServletContext().getRequestDispatcher(refererURL);		
		req.setAttribute("error", message);
		try {
			rd.forward(req, resp);
		} catch (Exception fe) {
			logger.error(fe.getMessage(),fe);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Returns the repository name for a given request. The repository name can be 
	 * infered from the request URL
	 * 
	 * @param request HTTP Request
	 * 
	 * @return String repository name
	 */
	protected String getRepositoryName(HttpServletRequest request) {
		
		String appURL = request.getContextPath();
		String uri = request.getRequestURI();
		String path = StringUtils.difference(appURL+"/repositories",uri);
		
		String[] pathElements = StringUtils.split(path,"/");
		
		String repositoryName = pathElements[0];
		
		return repositoryName;
	}
	
	/**
	 * Returns the value of a field. Values must be present as request parameter. 
	 * If the value is not found then the error will be logged and the request will 
	 * be forwarded to the referer URL or to the main page if the referer is not available.
	 * 
	 * @param req HTTP request
	 * @param resp HTTP response
	 * @param fieldName Field that we want to search for
	 * 
	 * @return String value of that attribute
	 * 
	 * @throws FieldNotFoundException Will be thrown if the attribute is not available
	 */
	protected String getField(HttpServletRequest req, 
							  HttpServletResponse resp, 
							  String fieldName) throws FieldNotFoundException {
		
		String field = req.getParameter(fieldName);
		if (field == null) {
			String repositoryName;
			repositoryName = req.getParameter("repository");
			if (repositoryName == null) {
				repositoryName = getRepositoryName(req);
			}
			FieldNotFoundException fnfe = 
				new FieldNotFoundException("Invalid update request. Field '" + fieldName + "' not found.");
			logErrorAndForward(req, resp, repositoryName, fnfe, fnfe.getMessage());
		}
		return field;
	}
}
