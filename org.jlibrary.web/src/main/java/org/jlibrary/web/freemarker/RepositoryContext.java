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
package org.jlibrary.web.freemarker;

import javax.servlet.ServletContext;

import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.web.services.ConfigurationService;
import org.jlibrary.web.services.config.ConfigNotFoundException;
import org.jlibrary.web.services.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author martin
 *
 * Holder class for export operation needed data
 */
public class RepositoryContext {

	static Logger logger = LoggerFactory.getLogger(RepositoryContext.class);
	
	// Fallbacks in case of exceptions
	private static final String FALLBACK_TEMPLATE = "templates/terrafirma";
	private static final boolean FALLBACK_REGISTRATION = true;
	private static final boolean FALLBACK_LOGIN = true;
	
	private Repository repository;
	private String templatesDirectory;
	private Ticket ticket;
	private boolean registrationEnabled;
	private boolean loginEnabled;
	
	public RepositoryContext(Repository repository, 
							 ServletContext servletContext, 
							 ApplicationContext context) {

		this.repository = repository;
		String rootPath = servletContext.getRealPath("/");
		ConfigurationService service=(ConfigurationService) context.getBean("template");
		
		RepositoryConfig config;
		try {
			config = service.getRepositoryConfig(repository.getName());
			registrationEnabled = config.isRegistrationEnabled();
			loginEnabled = config.isLoginEnabled();
			templatesDirectory = rootPath+"/"+config.getTemplateDirectory();
		} catch (ConfigNotFoundException e) {
			logger.warn(e.getMessage(),e);
			registrationEnabled = FALLBACK_REGISTRATION;
			loginEnabled = FALLBACK_LOGIN;
			templatesDirectory = rootPath+"/"+FALLBACK_TEMPLATE;

		}
	}

	public Repository getRepository() {
		return repository;
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public String getTemplatesDirectory() {
		return templatesDirectory;
	}

	public void setTemplatesDirectory(String templatesDirectory) {
		this.templatesDirectory = templatesDirectory;
	}
	
	/**
	 * Applies a filter. The new repository in this context will be the 
	 * filtered repository
	 * 
	 * @param filter Filter to apply
	 */
	public void applyFilter(ExportFilter filter) {
		
		setRepository(filter.filter(getRepository()));
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public boolean isRegistrationEnabled() {
		return registrationEnabled;
	}	
	
	public boolean isLoginEnabled() {
		return loginEnabled;
	}
}
