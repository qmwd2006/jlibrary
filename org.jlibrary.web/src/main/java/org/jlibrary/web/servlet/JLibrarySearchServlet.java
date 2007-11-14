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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.SearchResult;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRSearchService;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.jlibrary.web.services.TicketService;

@SuppressWarnings("serial")
public class JLibrarySearchServlet extends JLibraryServlet {

	private static Logger logger = Logger.getLogger(JLibrarySearchServlet.class);

	private ServerProfile profile = new LocalServerProfile();
	
	@Override
	public void init() throws ServletException {

		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processContent(req,resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processContent(req,resp);
	}

	private void processContent(HttpServletRequest req, HttpServletResponse resp) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Received search request");
		}
		String repositoryName = req.getParameter("repository");
		String text = req.getParameter("text");
		if ((repositoryName == null) || (text == null)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid request: repositoryName=" + repositoryName + ", text=" + text);
			}
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		String type = req.getParameter("type");
		if (type == null) {
			type = SearchService.SEARCH_CONTENT;
		}
		int init = JCRSearchService.NO_PAGING;
		String initParameter = req.getParameter("init");
		if (initParameter != null) {
			try {
				init = Integer.parseInt(initParameter);
			} catch (NumberFormatException nfe) {
				if (logger.isDebugEnabled()) {
					logger.debug("Wrong value for init parameter=" + initParameter);
				}
			}
		}
		int end = JCRSearchService.NO_PAGING;
		String endParameter = req.getParameter("end");
		if (endParameter != null) {
			try {
				end = Integer.parseInt(endParameter);
			} catch (NumberFormatException nfe) {
				if (logger.isDebugEnabled()) {
					logger.debug("Wrong value for end parameter=" + endParameter);
				}
			}
		}		
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		//TODO: Refactor init/end methods to interface and remove this explicit cast
		JCRSearchService searchService = 
			(JCRSearchService)JLibraryServiceFactory.getInstance(profile).getSearchService();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			
			Repository repository = 
				repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(new LocalServerProfile());
			repository.setTicket(ticket);
			
			SearchResult result = searchService.search(ticket, text, type, init, end);
			String output = exportResults(req,ticket,repository,result);
			resp.getOutputStream().write(output.getBytes());
			resp.flushBuffer();
		} catch (Exception e) {
			logErrorAndForward(req, resp, repositoryName, e, "There was an error in the server.");
		}
		
		return;
	}
	private String exportResults(HttpServletRequest request, 
								  Ticket ticket, 
								  Repository repository, 
								  SearchResult result) {
		
		try {
			RepositoryContext context = 
				new RepositoryContext(repository,getServletContext(),getContext());
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			
			exporter.setRootURL(getRootURL(request));
			exporter.setRepositoryURL(getRepositoryURL(request, repository.getName()));
			exporter.setError((String)request.getAttribute("error"));
			
			exporter.initExportProcess(context);
			return exporter.exportSearchResults(result, context);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
	}
}
