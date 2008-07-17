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
package org.jlibrary.client.security;

import java.io.IOException;
import java.net.URL;

import javax.naming.NamingException;

import org.eclipse.core.runtime.FileLocator;
import org.jlibrary.core.jcr.JCRLocalSecurityService;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EclipseJCRSecurityService extends JCRLocalSecurityService {

	static Logger logger = LoggerFactory.getLogger(EclipseJCRSecurityService.class);

	@Override
	protected void registerRepository(String repositoriesHome) throws NamingException {

		URL resourceURL = JCRSecurityService.class.getClassLoader().getResource("repository.xml");
		if (logger.isDebugEnabled()) {
			logger.debug("Configuration Resource URL: " + resourceURL);
		}
		//Thread current = Thread.currentThread();
		//ClassLoader original = current.getContextClassLoader();
		try {
			URL realURL = FileLocator.toFileURL(resourceURL);
			if (logger.isDebugEnabled()) {
				logger.debug("Configuration URL: " + realURL);
			}
			// Swap classloaders to avoid lucene conflicts
			//current.setContextClassLoader(JCRLocalSecurityService.class.getClassLoader().getParent());
			super.registerRepository(repositoriesHome, realURL.getFile());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
		} finally {
			//current.setContextClassLoader(original);
		}
	}
	
}
