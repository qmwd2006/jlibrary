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
package org.jlibrary.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jlibrary.core.config.ConfigException;
import org.jlibrary.core.config.JLibraryProperties;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author martin
 *
 * Simple local cache implementation.
 * 
 * The local cache contents will be stored on the jlibrary home directory under 
 * a .local-cache directory. Each repository will have its own directory. Under 
 * each repository directory will be stored the contents of the different documents 
 * with a file structure identical to the original repository structure.
 * 
 * The cache will be stored in the .local-cache/local-cache.xml file. 
 */
public class SimpleLocalCache implements LocalCache {

	static Logger logger = LoggerFactory.getLogger(SimpleLocalCache.class);
	
	private final static String NO_VERSION = "";
	
	// Clear the cache each week = 
	//		   7 days * 24 hours * 60 minutes * 60 seconds* 1000 milliseconds
	private final static int CACHE_CLEAN_INTERVAL = 7*24*60*60*1000;
	
	private HashMap repositories = new HashMap();
	
	private String localCacheDirectoryPath;
	private String localCacheConfigPath;
	
	private static SimpleLocalCache instance;
	
	private long time = 0;
	
	private SimpleLocalCache() {
		
		super();
	}

	/**
	 * Checks and clears the cache contents if needed
	 *
	 */
	private void checkToClear() throws LocalCacheException {

		long justNow = System.currentTimeMillis();
		long elapsed = justNow - time;
		if (elapsed > CACHE_CLEAN_INTERVAL) {
			time = System.currentTimeMillis();
			clearCache();
			saveLocalCache();
		}
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#clearCache()
	 */
	public void clearCache() throws LocalCacheException {
		
		try {
			File localCacheDirectory = new File(localCacheDirectoryPath);
			if (localCacheDirectory.exists()) {
				File[] child = localCacheDirectory.listFiles();
				for (int i = 0; i < child.length; i++) {
					if (child[i].isDirectory()) {
						FileUtils.deleteDirectory(child[i]);
					}
				}
			}
			repositories.clear();
		} catch (IOException ioe) {
			throw new LocalCacheException(ioe);
		}
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#addNodeToCache(org.jlibrary.core.entities.Node, org.jlibrary.cache.NodeContentHandler)
	 */
	public void addNodeToCache(Node node, 
							   NodeContentHandler handler) throws LocalCacheException {

		File repositoryDirectory = new File(localCacheDirectoryPath +
											System.getProperty("file.separator") +
											node.getRepository());

		if (!repositoryDirectory.exists()) {
			if (!repositoryDirectory.mkdirs()) {
				throw new LocalCacheException("Repository local cache directory can't be created");				
			}
		}
		
		if (repositoryDirectory.exists()) {
			createPath(repositoryDirectory,node.getPath());
			File file = new File(repositoryDirectory,
					 			 node.getPath());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				handler.copyTo(fos);
			} catch (Exception e) {
				throw new LocalCacheException(e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
			
			// Update the cache logic structure
			HashMap nodes = (HashMap)repositories.get(node.getRepository());
			if (nodes == null) {
				nodes = new HashMap();
				repositories.put(node.getRepository(),nodes);
			}
			if (node.isDocument()) {
				String lastVersion = ((Document)node).getLastVersionId();
				nodes.put(node.getId(),lastVersion);
			} else {
				nodes.put(node.getId(),NO_VERSION);
			}
			saveLocalCache();
		}
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#removeNodeFromCache(org.jlibrary.core.entities.Node)
	 */
	public void removeNodeFromCache(Node node) throws LocalCacheException {

		if (!isNodeCached(node)) {
			return;
		}
		String path = getNodePath(node);
		File file = new File(path);
		
		HashMap nodes = (HashMap)repositories.get(node.getRepository());
		if (nodes != null) {
			nodes.remove(node.getId());
		}

		try {
			FileUtils.forceDelete(file);
		} catch (IOException ioe) {
			throw new LocalCacheException("The node can't be removed from local cache");
		}
	}

	/**
	 * Creates a path in the local cache
	 * 
	 * @param repositoryDirectory Path of the repository directory
	 * @param path Path to be created
	 */
	private void createPath(File repositoryDirectory, String path) {
		
		String[] directories = StringUtils.split(path,"/");
                
            String cachePath = repositoryDirectory.getAbsolutePath() ;
//    		logger.debug("repositoryDirectory: " + repositoryDirectory.getAbsolutePath());                
		for (int i = 0; i < directories.length-1; i++) {
            cachePath += System.getProperty("file.separator") + directories[i];
//            logger.debug("otro dir: " + directories[i]);
		}
                logger.debug("cachePath: " + cachePath);
                new File(cachePath).mkdirs();
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#getNodeContent(org.jlibrary.core.entities.Node)
	 */
	public InputStream getNodeContent(Node node) throws LocalCacheException {
	
		String path = getNodePath(node);
		File file = new File(path);
		
		try {
			FileInputStream fis = new FileInputStream(file);
			
			return fis;
		} catch (Exception e) {
			throw new LocalCacheException(e);
		}
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#getNodePath(org.jlibrary.core.entities.Node)
	 */
	public String getNodePath(Node node) throws LocalCacheException {

		File repositoryDirectory = new File(localCacheDirectoryPath +
											System.getProperty("file.separator") +
											node.getRepository());

		if (repositoryDirectory.exists()) {
			File file = new File(repositoryDirectory,
								 node.getPath());
			if (file.exists()) {
				return file.getAbsolutePath();
			} else {
				throw new LocalCacheException("The node doesn't exists within local cache");
			}
		} else {
			throw new LocalCacheException("Repository local cache directory can't be found");
		}
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#isNodeCached(org.jlibrary.core.entities.Node)
	 */
	public boolean isNodeCached(Node node) throws LocalCacheException {
		
		// This method throws exceptions if the file can't be found
		try {
			getNodePath(node);
		} catch (LocalCacheException lce) {
			return false;
		}

		// Ok, file exists, now check the version
		HashMap nodes = (HashMap)repositories.get(node.getRepository());
		if (nodes != null) {
			if (nodes.get(node.getId()) == null) {
				return false;
			}
			//TODO: Check why sometimes the get method doesn't returns an string
			Object nodeVersion = (Object)nodes.get(node.getId());
			if (!(nodeVersion instanceof String)) {
				return false;
			}
			
			String cachedDocumentVersion = (String)nodeVersion;
			if (cachedDocumentVersion == NO_VERSION) {
				return true;
			}
			if (node.isDocument()) {
				String documentVersion = ((Document)node).getLastVersionId();
				if (cachedDocumentVersion != null) {
					if (cachedDocumentVersion.equals(documentVersion)) {
						return true;
					}
				}
			} else {
				// The resources have NO_VERSION. So if we're here, the 
				// resource couldn't be cached
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#clearCache(org.jlibrary.core.entities.Repository)
	 */
	public void clearCache(Repository repository) throws LocalCacheException {

		try {
			File repositoryDirectory = new File(localCacheDirectoryPath +
												System.getProperty("file.separator") +
												repository.getId());
			if (repositoryDirectory.exists()) {
				FileUtils.deleteDirectory(repositoryDirectory);
			}
			repositories.remove(repository.getId());
			saveLocalCache();
		} catch (IOException ioe) {
			throw new LocalCacheException(ioe);
		}
	}
	
	private void saveLocalCache() throws LocalCacheException {

		XStream xstream = new XStream();		
		try {
			xstream.toXML(this,new FileWriter(new File(localCacheConfigPath)));
		} catch (IOException e) {
			throw new LocalCacheException("Error saving the local cache");
		}
	}
	
	private static SimpleLocalCache initCache() throws ConfigException {
		
        String home = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_HOME); 
		//String home = System.getProperty("user.home");

		
		File f = new File(home,".jlibrary");
                f.mkdirs();

		f = new File(f.getAbsolutePath(),".local-cache");
		if (!f.exists()) {
			if (!f.mkdirs()) {
				throw new ConfigException("Can't create local-cache subdirectory");				
			}
		}
		XStream xstream = new XStream();
		
		try {
			File file = new File(f,"local-cache.xml");
			if (!file.exists()) {
				instance = new SimpleLocalCache();
				
				instance.localCacheConfigPath = f.getAbsolutePath();
				instance.localCacheDirectoryPath = 
					file.getParentFile().getAbsolutePath();
				instance.time = System.currentTimeMillis();
				instance.saveLocalCache();
				
			} else {
				instance = (SimpleLocalCache)xstream.fromXML(new FileReader(file));
			}
			instance.checkToClear();

			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConfigException("Error loading the config");
		}
	}
	
	public static SimpleLocalCache getInstance() {
		
		if (instance == null) {
			try {
				instance = initCache();
			} catch (ConfigException e) {
                logger.error(e.getMessage(),e);
			}
		}
		return instance;
	}		
}
