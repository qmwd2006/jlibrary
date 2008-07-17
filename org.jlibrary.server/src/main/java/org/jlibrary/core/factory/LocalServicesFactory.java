/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
package org.jlibrary.core.factory;

import java.io.InputStream;
import java.util.Properties;

import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalServicesFactory implements ServicesFactory {

	static Logger logger = LoggerFactory.getLogger(LocalServicesFactory.class);	
	private static final String FILE_PROPERTIES_LOCAL = "factory-local.properties";
	
	private RepositoryService repositoryService;
	private SecurityService securityService;
	private SearchService searchService;		
	
	public LocalServicesFactory(ServerProfile profile) {
		
		loadFactory(profile);
	}
	
	private void loadFactory(ServerProfile profile) {

		try {
			String propertiesFile = FILE_PROPERTIES_LOCAL;
			if (propertiesFile == null) {
				logger.error("Could not find properties for the given server profile");
				return;
			}
			Properties factoryProperties = new Properties();
			InputStream is = null;
			try {
				is = JLibraryServiceFactory.class.getClassLoader()
						.getResourceAsStream(propertiesFile);
				factoryProperties.load(is);
			} finally {
				if (is != null) {
					is.close();
				}
			}
			String repositoryService = factoryProperties.getProperty("RepositoryService");
			Class repositoryServiceClass = Class.forName(repositoryService);
			this.repositoryService = (RepositoryService)repositoryServiceClass.newInstance();
		
			String securityService = factoryProperties.getProperty("SecurityService");
			Class securityServiceClass = Class.forName(securityService);
			this.securityService = (SecurityService)securityServiceClass.newInstance();
		
			String searchService = factoryProperties.getProperty("SearchService");
			Class searchServiceClass = Class.forName(searchService);
			this.searchService = (SearchService)searchServiceClass.newInstance();		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return;
	}

	public RepositoryService getRepositoryService() {
		
		return repositoryService;
	}

	public SearchService getSearchService() {

		return searchService;
	}

	public SecurityService getSecurityService() {

		return securityService;
	}	
}
