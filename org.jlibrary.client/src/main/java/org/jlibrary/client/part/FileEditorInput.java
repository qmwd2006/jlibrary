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
package org.jlibrary.client.part;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.cache.NodeContentHandler;
import org.jlibrary.client.Messages;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.ui.editor.EditorsRegistry;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.editor.JLibraryEditorInput;
import org.jlibrary.client.ui.editor.NodeEditorInput;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.util.File;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentVersion;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for making a file resource a suitable input for an editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class FileEditorInput 
	extends JLibraryEditorInput implements IPathEditorInput
{
	
	static Logger logger = LoggerFactory.getLogger(FileEditorInput.class);
	
	private IFile file;
	
	public FileEditorInput() {}
	
	/**
	 * Creates an editor input based of the given file resource.
	 *
	 * @param file the file resource
	 */
	public FileEditorInput(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		this.file = file;
	}
	
	/* (non-Javadoc)
	 * Method declared on Object.
	 */
	public int hashCode() {
		return file.hashCode();
	}
	
	/* (non-Javadoc)
	 * Method declared on Object.
	 *
	 * The <code>FileSystemEditorInput</code> implementation of this <code>Object</code>
	 * method bases the equality of two <code>FileSystemEditorInput</code> objects on the
	 * equality of their underlying <code>IFile</code> resources.
	 */
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (!(obj instanceof FileEditorInput))
			return false;
		FileEditorInput other = (FileEditorInput) obj;
		
		if (file == null) {
			return false;
		}
		
		if (other == null) {
			return false;
		}
		
		return file.equals(other.file);
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorInput.
	 */
	public boolean exists() {
		return file.exists();
	}
	
	/* (non-Javadoc)
	 * Method declared on IAdaptable.
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == IFile.class)
			return file;
		if (adapter.equals(IPathEditorInput.class)) {
			return this;
		}
		return file.getAdapter(adapter);
	}
	
	/* (non-Javadoc)
	 * Method declared on IFileSystemEditorInput.
	 */
	public IFile getFile() {
		return file;
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorInput.
	 */
	public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(file.getName());
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorInput.
	 */
	public String getName() {
		return file.getName();
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorInput.
	 */
	public String getToolTipText() {
		
		//return file.getFullPath().makeRelative().toString();
		return file.getName();
	}
	
	/* (non-Javadoc)
	 * Method declared on IPersistableElement.
	 */
	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		if(file != null)
			memento.putString("FILE_PATH", file.getFullPath().toString());
	}
	
	
	
	/* (non-Javadoc)
	 * Method declared on IPathEditorInput
	 * @since 3.0
	 * @issue consider using an internal adapter for IPathEditorInput rather than adding this as API
	 */
	public IPath getPath() {
		if (file == null) {
			return null;
		}
		return file.getLocation();
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IStorageEditorInput#getStorage()
	 */
	public IStorage getStorage() throws CoreException {

		return null;
	}
	
	/**
	 * This method creates a File Editor Input for a node with the contents
	 * of a given file. The contents will be stored on the local cache. If the 
	 * file already exists on the local cache, then its contents will be returned.
	 * 
	 * This is the more common method used to create a file editor input to 
	 * display the document contents in jlibrary.
	 * 
	 * @param node Node that will be opened 
	 * 
	 * @return File Editor Input for that node
	 * 
	 * @throws LocalCacheException If an error occurs acceding to the local cache
	 * @throws RepositoryException If the version contents can't be loaded
	 * @throws SecurityException If the user hasn't enough permissions to perform this operation
	 * @throws IOException If the file can't be created
	 */	
	public static FileEditorInput createFileEditorInput(
			final Node node) throws LocalCacheException,
									RepositoryException,
									SecurityException,
									IOException {

		Repository repository = RepositoryRegistry.getInstance().getRepository(node.getRepository());
		ServerProfile serverProfile = repository.getServerProfile();
		final RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		final Ticket ticket = repository.getTicket();
		
		// First check if the document is cached
		LocalCache cache = LocalCacheService.getInstance().getLocalCache();

		if (!cache.isNodeCached(node)) {
			cache.addNodeToCache(node,new NodeContentHandler() {
				public void copyTo(OutputStream os) throws LocalCacheException {
					
					try {
						if (node.isDocument()) {
							service.loadDocumentContent(node.getId(),ticket,os);
						} else if (node.isResource()) {
							service.loadResourceNodeContent(ticket, node.getId(),os);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						throw new LocalCacheException(e);
					}
				}
			});
		}
		
		/* Now, we must check if the document resources are cached 
		 * Now, we have to download all needed resources.
		 * 
		 * This will be a background job. A document can have attached many, many resources. 
		 * Doing this task synchronously will mean to wait several seconds. So, we will try to 
		 * load the resources asynchronously, refreshing each time the editor
		 */
		if (node.isDocument()) {
			loadResourcesAsynchronously(ticket,service,(Document)node,cache);
		}

		
		return createFEI(node);
	}

	/**
	 * 
	 */
	private static void loadResourcesAsynchronously(final Ticket ticket,
													final RepositoryService service,
													final Document document, 
													final LocalCache cache) {

		final UIJob refreshJob = new UIJob(Messages.getMessage("job_save")) {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IEditorPart editor = EditorsRegistry.getInstance().getEditor(document.getId());
				if (editor instanceof JLibraryEditor) {
					JLibraryEditor jeditor = (JLibraryEditor)editor;
					jeditor.refreshContents();
				}
				return Status.OK_STATUS;
			}
		};
		
		JobTask jobTask = new JobTask(Messages.getMessage("job_save")) {
			public IStatus run(IProgressMonitor monitor) 
										throws OperationCanceledException, 
											   JobTaskException {

				try {
					int i = 0;
					if (document.getResourceNodes() != null) {
						Iterator it = document.getResourceNodes().iterator();
						while (it.hasNext()) {
							final ResourceNode resource = (ResourceNode) it.next();
														
							if (!(cache.isNodeCached(resource))) {
								cache.addNodeToCache(resource,new NodeContentHandler() {
									public void copyTo(OutputStream os) throws LocalCacheException {
										
										try {
											service.loadResourceNodeContent(ticket, resource.getId(),os);
										} catch (Exception e) {
											logger.error(e.getMessage(),e);
											throw new LocalCacheException(e);
										}
									}
								});
								i++;
								if (i == 5) {
									i = 0;
									refreshJob.schedule();
								}
							}
						}
					}
					if (i != 0) {
						refreshJob.schedule();
					}
				} catch (final LocalCacheException lce) {
					throw new JobTaskException(lce);
				}
				return Status.OK_STATUS;
			}
		};
		
		
		jobTask.setSystemTask(true);
		new JobRunner().run(jobTask);		
	}

	/**
	 * This method creates a File Editor Input for a document with the contents
	 * of a given file. The contents will be stored on the local cache. If the 
	 * file already exists on the local cache, then its contents will be returned.
	 * 
	 * This method is commonly used to load the contents of a file into a document.
	 * 
	 * @param file File to be loaded
	 * @param document Document
	 * 
	 * @return File Editor Input for that file
	 * 
	 * @throws LocalCacheException If an error occurs acceding to the local cache
	 * @throws RepositoryException If the version contents can't be loaded
	 * @throws SecurityException If the user hasn't enough permissions to perform this operation
	 * @throws IOException If the file can't be created
	 */
	public static FileEditorInput createFileEditorInput(final java.io.File file,
                                                        Document document) throws LocalCacheException,
																				  RepositoryException,
																				  SecurityException, 
																				  IOException {
		// Replace the local cache contents with the contents of the file
		LocalCache cache = LocalCacheService.getInstance().getLocalCache();
		cache.addNodeToCache(document,new NodeContentHandler() {
			public void copyTo(OutputStream os) throws LocalCacheException {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					IOUtils.copy(fis, os);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					throw new LocalCacheException(e);
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

		return createFEI(document);
	}

	/**
	 * This method creates a File Editor Input for an specific version of a document.
	 * To do this, it will try to create a local document with the version contents and 
	 * store it on the local cache. If an equivalent document exists in the local cache, 
	 * then it will be returned.
	 * 
	 * This method is commonly used to display different version contents on the 
	 * version browsing dialog.
	 * 
	 * @param document Document representing the specific version
	 * @param version Version contents
	 * 
	 * @return File Editor Input for that version
	 * 
	 * @throws LocalCacheException If an error occurs acceding to the local cache
	 * @throws RepositoryException If the version contents can't be loaded
	 * @throws SecurityException If the user hasn't enough permissions to perform this operation
	 * @throws IOException If the file can't be created
	 */
	public static FileEditorInput createFileEditorInput(final Document document,	
														final DocumentVersion version) throws LocalCacheException,
																						RepositoryException, 
																						SecurityException, 
																						IOException {
		// This is method creates a editor for a document with the contents loaded
		// from the version specified. 
		// We must be aware that we can't overwrite the local cache contents with
		// the version contents.
		
		// We have to correct document's path for local caching of versions
		String[] partsPath = StringUtils.split(document.getPath(),"/");
		
		StringBuffer bufferPath = new StringBuffer("/");
		if (partsPath.length > 1) {
			for (int i = 0; i < partsPath.length-1; i++) {
				bufferPath.append(partsPath[i]);
			}
			bufferPath.append("/");
		}
		bufferPath.append(version.getId());
		bufferPath.append("/");
		bufferPath.append(partsPath[partsPath.length-1]);

		document.setPath(bufferPath.toString());
		
		// Replace the local cache contents with the contents of the file
		LocalCache cache = LocalCacheService.getInstance().getLocalCache();
		cache.addNodeToCache(document,new NodeContentHandler() {
			public void copyTo(OutputStream os) throws LocalCacheException {

				// Load version contents
				Repository repository = RepositoryRegistry.getInstance().getRepository(version.getRepository());
				ServerProfile serverProfile = repository.getServerProfile();
				RepositoryService service = 
					JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
				Ticket ticket = repository.getTicket();
				try {
					service.loadVersionContent(ticket, version.getId(), os);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					throw new LocalCacheException(e);
				}			
			}
		});

		return createFEI(document);
	}

	private static FileEditorInput createFEI(Node node) throws LocalCacheException,
											  						 RepositoryException {
		
		if (node.isDirectory()) {
			return new NodeEditorInput(node);
		} else {
			LocalCache cache = LocalCacheService.getInstance().getLocalCache();
			String documentPath = cache.getNodePath(node);
				                
			IPath path = new Path(documentPath);
			File cachedFile = new File(path,node);
			
			FileEditorInput fei = new NodeEditorInput(cachedFile);
			return fei;
		}
	}
}
