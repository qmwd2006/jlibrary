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
package org.jlibrary.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.jlibrary.core.entities.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some static utility methods for cache content handling
 * 
 * @author mpermar
 *
 */
public class CacheUtils {

	static Logger logger = LoggerFactory.getLogger(CacheUtils.class);
	
	/**
	 * Adds a given node with the enclosed file contents to the cache
	 * 
	 * @param file File contents
	 * @param node Node to add
	 * 
	 * @throws LocalCacheException If the node cannot be added
	 */
	public static void addFileToCache(final File file, final Node node) throws LocalCacheException {
		
		
		// Update cache
		LocalCache cache = LocalCacheService.getInstance().getLocalCache();
		cache.addNodeToCache(node, new NodeContentHandler() {
			public void copyTo(OutputStream os) throws LocalCacheException {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					IOUtils.copy(fis, os);
					fis.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							logger.error(e.getMessage(),e);
						}
					}
				}
			}
		});
	}
}
