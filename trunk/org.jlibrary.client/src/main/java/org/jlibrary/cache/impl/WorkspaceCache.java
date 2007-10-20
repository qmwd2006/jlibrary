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
package org.jlibrary.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.NodeContentHandler;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author martin
 *
 * <p>This class implements the jLibrary LocalCache interface. That interface 
 * defines several callback methods used to store contents locally to allow 
 * fast access to document contents.</p>
 * 
 * <p>This implementation uses a workspace-based file system to store the 
 * contents of the documents. Each repository will be linked with an Eclipse 
 * workspace that will be stored on the jLibrary workspace data location. 
 * Each jLibrary repository will correspond with a workspace folder and each 
 * jLibrary document and resource will correspond with a workspace file.</p>
 * 
 * <p>Obviously, this implementation depends on the Eclipse Resources API.</p> 
 */
public class WorkspaceCache implements LocalCache {
	
	static Logger logger = LoggerFactory.getLogger(WorkspaceCache.class);
	
	public WorkspaceCache() {
		
		super();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();	
		try {
			root.refreshLocal(IResource.BACKGROUND_REFRESH, null);
		} catch (CoreException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#clearCache()
	 */
	public void clearCache() throws LocalCacheException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		

		try {
			IProject[] projects = root.getProjects();
			for (int i = 0; i < projects.length; i++) {
				projects[i].delete(true,true,null);				
			}
		} catch (CoreException e) {			
			logger.error(e.getMessage(),e);
			throw new LocalCacheException(e);
		}		
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#addNodeToCache(org.jlibrary.core.entities.Node, org.jlibrary.cache.NodeContentHandler)
	 */
	public void addNodeToCache(Node node, 
							   NodeContentHandler handler) throws LocalCacheException {

		String repository = node.getRepository();
		IWorkspace workspace = checkWorkspace(repository);
		IProject project = workspace.getRoot().getProject(node.getRepository());
		IFolder folder = checkFolder(project, node.getPath());
		createFile(project,folder,node,handler);		
	}

	/**
	 * @see org.jlibrary.client.cache.LocalCache#removeNodeFromCache(org.jlibrary.core.entities.Node)
	 */
	public void removeNodeFromCache(Node node) throws LocalCacheException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		

		String repository = node.getRepository();
		IProject project  = root.getProject(repository);
		if (!project.exists()) {
			return;
		}
		deleteFile(project,node);
	}
	


	/**
	 * @see org.jlibrary.client.cache.LocalCache#getNodeContent(org.jlibrary.core.entities.Node)
	 */
	public InputStream getNodeContent(Node node) throws LocalCacheException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		

		String repository = node.getRepository();
		IProject project  = root.getProject(repository);
		if (!project.exists()) {
			throw new LocalCacheException("Local project does not exist");
		}
		IFile file = getFile(project,node);		

		if (file == null) {
			throw new LocalCacheException("The file cannot be found on local cache");
		}
		
		try {
			// Refresh file contents
			file.refreshLocal(IResource.DEPTH_ZERO, null);
			
			InputStream stream = file.getContents();
			return stream;
		} catch (CoreException e) {
			logger.error(e.getMessage(),e);
			throw new LocalCacheException(e);
		}
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#getNodePath(org.jlibrary.core.entities.Node)
	 */
	public String getNodePath(Node node) throws LocalCacheException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		

		String repository = node.getRepository();
		IProject project  = root.getProject(repository);
		if (!project.exists()) {
			throw new LocalCacheException("Local project does not exist");
		}
		IFile file = getFile(project,node);
		if (file == null) {
			throw new LocalCacheException("The file cannot be found on local cache");
		}
		
		return file.getLocation().toFile().getAbsolutePath();
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#isNodeCached(org.jlibrary.core.entities.Node)
	 */
	public boolean isNodeCached(Node node) throws LocalCacheException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		

		String repository = node.getRepository();
		IProject project  = root.getProject(repository);
		if (!project.exists()) {
			return false;
		}
		IFile file = getFile(project,node);
		if ((file == null) || (!file.exists())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @see org.jlibrary.client.cache.LocalCache#clearCache(org.jlibrary.core.entities.Repository)
	 */
	public void clearCache(Repository repository) throws LocalCacheException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		

		IProject project  = root.getProject(repository.getId());
		if (!project.exists()) {
			throw new LocalCacheException("Local project does not exist");
		}		
		try {
			project.delete(true,true,null);
		} catch (CoreException e) {			
			logger.error(e.getMessage(),e);
			throw new LocalCacheException(e);
		}
	}
	
	private IWorkspace checkWorkspace(String repository) throws LocalCacheException {
		
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();		
			IProject project  = root.getProject(repository);
			if (!project.exists()) {
				project.create(new NullProgressMonitor());
			}
			if (!project.isOpen()) {
				project.open(null);
			}			
		
			return workspace;
		} catch (CoreException e) {			
			logger.error(e.getMessage(),e);
			throw new LocalCacheException(e);
		}
	}
	
	private IFolder checkFolder(IProject project, 
								String path) throws LocalCacheException {

		try {
			IFolder folder = null;			
			
			String[] splittedPath = StringUtils.split(path,"/");
			if (splittedPath.length > 1) {
				for (int i = 0; i < splittedPath.length-1; i++) {
					String folderName = splittedPath[i];
					if (folder == null) {
						folder = project.getFolder(folderName);
					} else {
						folder = folder.getFolder(folderName);
					}
					if (!folder.exists()) {
						folder.create(IResource.NONE,true,null);
					}
				}
			}

			return folder;
		} catch (CoreException e) {			
			logger.error(e.getMessage(),e);
			throw new LocalCacheException(e);
		}
	}

	private IFile createFile(IProject project,
							 IFolder folder, 
							 Node node, 
							 NodeContentHandler handler) throws LocalCacheException {

		try {
			String filename = FileUtils.getFileName(node.getPath());
			IFile file = null;
			if (folder == null) {
				file = project.getFile(filename);
			} else {
				file = folder.getFile(filename);
			}
			
			if (!file.exists()) {
				file.create(new ByteArrayInputStream(new byte[]{}), 
							IResource.NONE, null);
				FileOutputStream fos = null;
				fos = new FileOutputStream(getNodePath(node));
				try {
					handler.copyTo(fos); } 
				finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							logger.error(e.getMessage(),e);
						}
					}
				}
				file.refreshLocal(IResource.DEPTH_ZERO, null);
			}
			
			return file;
		} catch (Exception e) {			
			logger.error(e.getMessage(),e);
			throw new LocalCacheException(e);
		} 
		
	}
	
	private IFile getFile(IProject project,Node node) {

		IFolder folder = null;			
		
		String[] splittedPath = StringUtils.split(node.getPath(),"/");
		if (splittedPath.length > 1) {
			for (int i = 0; i < splittedPath.length-1; i++) {
				String folderName = splittedPath[i];
				if (folder == null) {
					folder = project.getFolder(folderName);
				} else {
					folder = folder.getFolder(folderName);
				}
				if (!folder.exists()) {
					return null;
				}
			}
		}
		
		String filename = FileUtils.getFileName(node.getPath());
		IFile file = null;
		if (folder == null) {
		    //fix for the refreshing repository node
			if (node.getPath().equals("/")) {
				file = project.getFile(project.getFullPath());
			} else {
				file = project.getFile(filename);
			}
		} else {
			file = folder.getFile(filename);
		}
		
		if (!file.exists()) {
			return null;
		}
		
		return file;
	}	
	
	private void deleteFile(IProject project,
							Node node) throws LocalCacheException {

		IFile file = getFile(project,node);
		if ((file == null) || !file.exists()) {
			return;
		}
		try {
			file.delete(true,null);
		} catch (CoreException e) {			
			logger.error(e.getMessage(),e);
			throw new LocalCacheException(e);
		}			
	}	
	
	/**
	 * Returns the file associated with the given node
	 * 
	 * @param node Node to find
	 * 
	 * @return IFile File associated with that node
	 */
	public IFile getFile(Node node) throws LocalCacheException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		

		String repository = node.getRepository();
		IProject project  = root.getProject(repository);
		if (!project.exists()) {
			throw new LocalCacheException("Local project does not exist");
		}
		IFile file = getFile(project,node);
		if (file == null) {
			throw new LocalCacheException("The file cannot be found on local cache");
		}
		
		return file;
	}	
}
