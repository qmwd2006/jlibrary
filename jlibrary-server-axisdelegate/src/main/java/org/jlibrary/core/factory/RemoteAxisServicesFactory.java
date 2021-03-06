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
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.jlibrary.core.axis.client.AxisRepositoryDelegate;
import org.jlibrary.core.axis.client.AxisSearchDelegate;
import org.jlibrary.core.axis.client.AxisSecurityDelegate;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.profiles.AxisServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteAxisServicesFactory implements ServicesFactory {

	static Logger logger = LoggerFactory.getLogger(RemoteAxisServicesFactory.class);	
	private static final String FILE_PROPERTIES_REMOTE = "factory-remote.properties";
	
	private AxisRepositoryDelegate repositoryService;
	private AxisSecurityDelegate securityService;
	private AxisSearchDelegate searchService;		
	
	public RemoteAxisServicesFactory(ServerProfile profile) {
		
		loadFactory(profile);
	}
	
	private void loadFactory(ServerProfile profile) {

		try {
			String propertiesFile = FILE_PROPERTIES_REMOTE;
			if (propertiesFile == null) {
				logger.error("Could not find properties for the given server profile");
				return;
			}
			InputStream is = 
				JLibraryServiceFactory.class.getClassLoader().getResourceAsStream(propertiesFile);
			Properties factoryProperties = new Properties();
			factoryProperties.load(is);
		
			String repositoryService = factoryProperties.getProperty("RepositoryService");
			Class repositoryServiceClass = Class.forName(repositoryService);
			Constructor repositoryServiceConstructor = 
				repositoryServiceClass.getConstructor(new Class[]{AxisServerProfile.class});
			this.repositoryService = (AxisRepositoryDelegate)
				repositoryServiceConstructor.newInstance(new Object[]{profile});
		
			String securityService = factoryProperties.getProperty("SecurityService");
			Class securityServiceClass = Class.forName(securityService);
			Constructor securityServiceConstructor = 
				securityServiceClass.getConstructor(new Class[]{AxisServerProfile.class});
			this.securityService = (AxisSecurityDelegate)
				securityServiceConstructor.newInstance(new Object[]{profile});
		
			String searchService = factoryProperties.getProperty("SearchService");
			Class searchServiceClass = Class.forName(searchService);
			Constructor searchServiceConstructor = 
				searchServiceClass.getConstructor(new Class[]{AxisServerProfile.class});
			this.searchService = (AxisSearchDelegate)
				searchServiceConstructor.newInstance(new Object[]{profile});
			
			is.close();
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
