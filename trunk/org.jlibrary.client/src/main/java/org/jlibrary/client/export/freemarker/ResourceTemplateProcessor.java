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
package org.jlibrary.client.export.freemarker;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.cache.NodeContentHandler;
import org.jlibrary.client.export.ExportException;
import org.jlibrary.client.export.RepositoryContext;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Template processor for document entities
 */
public class ResourceTemplateProcessor implements FreemarkerTemplateProcessor {

	static Logger logger = LoggerFactory.getLogger(ResourceTemplateProcessor.class);
	
	private ResourceNode resource;
	private RepositoryContext context;
	
	/**
	 * Document template processor
	 * 
	 * @param exporter Main freemarker template exporter
	 * @param document Document to process
	 * @param context Context repository for this processor
	 */
	public ResourceTemplateProcessor(ResourceNode resource, 
									 RepositoryContext context) {

		this.resource = resource;
		this.context = context;
	}
	
	/**
	 * @see org.jlibrary.client.export.freemarker.FreemarkerTemplateProcessor#processTemplate(org.jlibrary.client.export.freemarker.FreemarkerFactory)
	 */
	public void processTemplate(FreemarkerFactory factory) 
										throws ExportException {

		loadContent();
	}
	
	public String loadContent() throws ExportException {
		
		Repository repository = context.getRepository();
		ServerProfile profile = repository.getServerProfile();
		final Ticket ticket = repository.getTicket();
		final RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		// First check if the document is cached
		LocalCache cache = LocalCacheService.getInstance().getLocalCache();
		try {
			if (!cache.isNodeCached(resource)) {
				cache.addNodeToCache(resource,new NodeContentHandler() {
					public void copyTo(OutputStream os) throws LocalCacheException {
						
						try {
							repositoryService.loadResourceNodeContent(ticket, resource.getId(), os);
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							throw new LocalCacheException(e);
						}
					}
				});
			}		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExportException(e);
		}
		
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			// Save the document to the disk and return a link
			fos = new FileOutputStream(getFilePath(resource.getPath()));
			is = cache.getNodeContent(resource);
			IOUtils.copy(is, fos);	
			return "";
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExportException(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
		
	private String getFilePath(String path) {
		
		return context.getOutputDirectory() + path;
	}
}
