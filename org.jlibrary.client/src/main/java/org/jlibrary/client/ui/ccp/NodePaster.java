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
package org.jlibrary.client.ui.ccp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.cache.NodeContentHandler;
import org.jlibrary.client.Messages;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryHelper;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.actions.RefreshRepositoryAction;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Paster for node paste operations
 */
public class NodePaster implements Paster {

	static Logger logger = LoggerFactory.getLogger(NodePaster.class);
	
	private Comparator resourceComparator = new Comparator() {
		public int compare(Object arg0, Object arg1) {
			
			File file0 = (File)arg0;
			File file1 = (File)arg1;

			if (Types.isResourceSensible(Types.getTypeForFile(file0.getName()))) {
				return -1;
			} else if (Types.isResourceSensible(Types.getTypeForFile(file1.getName()))) {
				return 1;
			} else {
				if (file0.isDirectory() && file1.isFile()) {
					return 1;
				}
				if (file0.isFile() && file1.isDirectory()) {
					return -1;
				}
			}			
			
			return 0;
		}
	};
	
	/**
	 * @see org.jlibrary.client.ui.ccp.Paster#paste(java.lang.Object, java.lang.Object)
	 */
	public void paste(Object source, 
					  Object destination,
					  boolean move) throws PasteException {

		paste(source,destination,move,new NullProgressMonitor());
	}

	/**
	 * @see org.jlibrary.client.ui.ccp.Paster#paste(java.lang.Object, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void paste(Object source, 
					  Object destination,
					  boolean move,
					  IProgressMonitor monitor) throws PasteException,
					  								   OperationCanceledException {


		if (destination instanceof Repository) {
			destination = ((Repository)destination).getRoot();
		}
		try {
			Object[] nodes = (Object[])source;
			monitor.beginTask(Messages.getMessage("copy_job_name"),calculateLength(nodes));
			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i] instanceof File) {
					copyNode((File)nodes[i],(Directory)destination,monitor);
				} else {
					if (!isIncluded((Node)nodes[i],(Object[])nodes)) {
						copyNode((Node)nodes[i],(Directory)destination,move,monitor);
					}
				}
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		} catch (RepositoryException e) {
			
            logger.error(e.getMessage(),e);
			throw new PasteException(e);
		} catch (SecurityException e) {
			
            logger.error(e.getMessage(),e);
			throw new PasteException(e);
		}
	}

	private int calculateLength(Object[] nodes) {
		
		int length = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof File) {
				length+= calculateFileLength((File)nodes[i]);
			} else {
				Node node = (Node)nodes[i];
				if (!isIncluded(node,(Object[])nodes)) {
					length+=calculateNodeLength(node);
				}
			}
		}
		return length;
	}	
	
	private int calculateFileLength(File file) {
		
		if (file.isFile()) {
			return 1;
		} else {
			int i = 1;
			File[] child = file.listFiles();
			for (int j = 0; j < child.length; j++) {
				i+=calculateFileLength(child[j]);
			}
			return i;
		}
	}	
	
	private int calculateNodeLength(Node node) {
		
		if (node.isDocument()) {
			return 1;
		} else {
			int i = 1;
			Iterator it = node.getNodes().iterator();
			while (it.hasNext()) {
				Node child = (Node) it.next();
				i+=calculateNodeLength(child);
			}
			return i;
		}
	}

	private void copyNode(File source, 
			  			  Directory destination, 
			  			  IProgressMonitor monitor) 
			throws RepositoryException, 
			  	   SecurityException,
			  	   OperationCanceledException {

		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		String repositoryId = destination.getRepository();
		Repository repository = RepositoryRegistry.getInstance().getRepository(repositoryId);

		if (source.isFile()) {
			monitor.subTask(Messages.getAndParseValue("copy_job_step","%1",source.getName()));
			// We will look if the file exists. If exist, and is a resource 
			// then it won't be added
			boolean found = false;
			Iterator it = destination.getNodes().iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (!node.isResource()) continue;
				if (node.getName().equals(source.getName())) {
					found = true;
				}
			}			
			if (!found) {
				RepositoryHelper.createDocument(repository,destination,source,true);
			}
			monitor.worked(1);
		} else {
			// First, look for the directory. It could have been already created
			Directory newDestination = null;
			Iterator it = destination.getNodes().iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (!node.isDirectory()) continue;
				if (node.getName().equals(source.getName())) {
					newDestination = (Directory)node;
				}
			}
			if (newDestination == null) {
				newDestination = RepositoryHelper.createDirectory(repository,destination,source);
			}
			File[] child = source.listFiles();
			Arrays.sort(child,resourceComparator);
			for (int j = 0; j < child.length; j++) {
				copyNode(child[j],newDestination,monitor);
			}		
		}
	}	
	
	private void copyNode(Node source, 
						  Directory destination, 
						  boolean move,
						  IProgressMonitor monitor) throws RepositoryException, 
						  								   SecurityException {

		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}		
		String destinationRepositoryId = destination.getRepository();
		Repository destinationRepository = RepositoryRegistry.getInstance()
				.getRepository(destinationRepositoryId);
		ServerProfile destinationProfile = destinationRepository
				.getServerProfile();
		Ticket destinationTicket = destinationRepository.getTicket();
		RepositoryService destinationRepositoryService = 
			JLibraryServiceFactory.getInstance(destinationProfile).getRepositoryService();

		String sourceRepositoryId = source.getRepository();
		Repository sourceRepository = RepositoryRegistry.getInstance()
				.getRepository(sourceRepositoryId);
		ServerProfile sourceProfile = sourceRepository.getServerProfile();
		Ticket sourceTicket = sourceRepository.getTicket();
		RepositoryService sourceRepositoryService = 
			JLibraryServiceFactory.getInstance(sourceProfile).getRepositoryService();

		if (!move) {
			
			monitor.subTask(Messages.getAndParseValue("copy_job_step","%1",source.getName()));
			monitor.worked(1);

			Node newNode = null;
			if (sourceProfile == destinationProfile) {
				// Copy within the same repository
				newNode = destinationRepositoryService.copyNode(destinationTicket,
																source.getId(),
																destination.getId(),
																destination.getRepository());
			} else {
				if (source.isDocument()) {
					newNode = addDocument(sourceRepositoryService,
										  sourceTicket, 
										  destinationRepositoryService,
										  destinationTicket, 
										  destination, 
										  (Document) source,
										  monitor);					
				} else if (source.isDirectory()) {
					newNode = addDirectory(sourceRepositoryService,
										   sourceTicket, 
										   destinationRepositoryService,
										   destinationTicket, 
										   destination, 
										   (Directory) source,
										   monitor);

				} else if (source.isResource()) {
					// Copy the resource to another repository
					newNode = addResource(sourceRepositoryService, 
										  sourceTicket,
										  destinationRepositoryService, 
										  destinationTicket,
										  destination, 
										  (ResourceNode) source, 
										  monitor);					
				} else {
					return;
				}
			}
			if (destination.getNodes().contains(newNode)) {
				destination.getNodes().remove(newNode);
			}
			destination.getNodes().add(newNode);
			newNode.setParent(destination.getId());
			newNode.setRepository(destinationRepository.getId());
			EntityRegistry.getInstance().addNode(newNode);
			
		} else {
			// Move
			Node newNode = null;
			if (sourceProfile == destinationProfile) {
				newNode = destinationRepositoryService.moveNode(destinationTicket,
																source.getId(),
																destination.getId(),
																destination.getRepository());
			} else {
				if (source.isDocument()) {
					//TODO: Keep un eye when the document is resource for other documents
					
					// Add the document to the destination repository
					newNode = addDocument(sourceRepositoryService,
										  sourceTicket, 
										  destinationRepositoryService,
										  destinationTicket, 
										  destination, 
										  (Document) source,
										  monitor);
					// Remove the document from the source repository
					try {
						sourceRepositoryService.removeDocument(sourceTicket, 
														       source.getId());
					} catch (ResourceLockedException rle) {
						throw new RepositoryException(rle);
					}
					
				} else if (source.isDirectory()) {
					// Copy the directory to another repository
					newNode = addDirectory(sourceRepositoryService, 
										   sourceTicket,
										   destinationRepositoryService, 
										   destinationTicket,
										   destination, 
										   (Directory) source, 
										   monitor);
					// Remove the original directory
					sourceRepositoryService.removeDirectory(sourceTicket,
															source.getId());
					
				} else if (source.isResource()) {
					// Copy the resource to another repository
					newNode = addResource(sourceRepositoryService, 
										  sourceTicket,
										  destinationRepositoryService, 
										  destinationTicket,
										  destination, 
										  (ResourceNode) source, 
										  monitor);
					// Remove the original resource
					List nodesList = 
						sourceRepositoryService.findNodesForResource(sourceTicket,
																	 source.getId());
					sourceRepositoryService.removeResourceNode(sourceTicket,
															   source.getId());
					Iterator it = nodesList.iterator();
					while (it.hasNext()) {
						Node node = (Node) it.next();
						Node registryNode = 
							EntityRegistry.getInstance().getNode(
									node.getId(),node.getRepository());
						((Document)registryNode).getResourceNodes().remove(source);
					}
				} else {
					return;
				}				
			}	
			Node parentDirectory = 
				EntityRegistry.getInstance().getNode(
						source.getParent(),source.getRepository());
			if (parentDirectory != null) {
				parentDirectory.getNodes().remove(source);
			} else {
				sourceRepository.getRoot().getNodes().remove(source);
			}								
			
			
			// Remove the cutted node reference from the registry
			EntityRegistry.getInstance().removeNode(source);
			destination.getNodes().add(newNode);
			newNode.setParent(destination.getId());
			newNode.setRepository(destinationRepository.getId());
			EntityRegistry.getInstance().addNode(newNode);				
			
		}
	}

	private Directory addDirectory(RepositoryService sourceService,
								   Ticket sourceTicket, 
								   RepositoryService destinationService,
								   Ticket destinationTicket, 
								   Directory destinationDirectory,
								   Directory sourceDirectory, 
								   IProgressMonitor monitor) throws RepositoryException, 
								   									SecurityException {

		monitor.subTask(Messages.getAndParseValue("copy_job_step","%1",sourceDirectory.getName()));
		monitor.worked(1);
		Directory newDirectory = destinationService.createDirectory(destinationTicket, 
																	sourceDirectory.getName(), 
																	sourceDirectory.getDescription(), 
																	destinationDirectory.getId());

		destinationDirectory.getNodes().add(newDirectory);
		newDirectory.setParent(destinationDirectory.getId());
		newDirectory.setRepository(destinationDirectory.getRepository());

		// Add all the child
		if (sourceDirectory.hasChildren() && sourceDirectory.isEmpty()) {
			try {
				new RefreshRepositoryAction().
					refreshNodes(new Node[]{sourceDirectory},
								 new NullProgressMonitor());
			} catch (JobTaskException e) {
				logger.error(e.getMessage(),e);
			}
		}
		if (sourceDirectory.getNodes() != null) {
			Iterator it = sourceDirectory.getNodes().iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				Node newNode = null;
				if (node.isDirectory()) {
					newNode = addDirectory(sourceService, 
								 		   sourceTicket,
								 		   destinationService, 
								 		   destinationTicket,
								 		   newDirectory, 
								 		   (Directory) node, 
								 		   monitor);
				} else if (node.isDocument()) {
					newNode = addDocument(sourceService, 
										  sourceTicket,
										  destinationService, 
										  destinationTicket,
										  newDirectory, 
										  (Document) node, 
										  monitor);
				} else if (node.isResource()) {
					newNode = addResource(sourceService, 
										  sourceTicket,
										  destinationService, 
										  destinationTicket,
										  newDirectory, 
										  (ResourceNode) node, 
										  monitor);
				}
				EntityRegistry.getInstance().addNode(newNode);
			}
		}

		return newDirectory;
	}

	private Document addDocument(final RepositoryService sourceService,
								 final Ticket sourceTicket, 
								 final RepositoryService destinationService,
								 final Ticket destinationTicket, 
								 final Directory directory, 
								 final Document document,
								 IProgressMonitor monitor) throws RepositoryException,
								 								  SecurityException {

		Repository destinationRepository = RepositoryRegistry.getInstance().getRepository(directory.getRepository());

		monitor.subTask(Messages.getAndParseValue("copy_job_step","%1",document.getName()));
		monitor.worked(1);
		
		Document newDocument = createDocumentCopy(destinationTicket,document);
		DocumentProperties documentProperties = document.dumpProperties();
		
		try {
			// Add notes
			if (document.getNotes() != null) {
				Iterator it = document.getNotes().iterator();
				while (it.hasNext()) {
					Note note = (Note) it.next();
					PropertyDef noteProperty = new PropertyDef();
					noteProperty.setKey(DocumentProperties.DOCUMENT_ADD_NOTE);
					noteProperty.setValue(note);
					documentProperties.put(DocumentProperties.DOCUMENT_ADD_NOTE,noteProperty);				
				}
			}
			
			// Add the new document to the unclassified category
			documentProperties.addProperty(
					DocumentProperties.DOCUMENT_ADD_CATEGORY,
					Category.UNKNOWN.getId());
			
			
			// Create a document clone
			try {
				String filename = FileUtils.getFileName(document.getPath());
				documentProperties.setProperty(
						DocumentProperties.DOCUMENT_PARENT,
						directory.getId());
				String path = FileUtils.buildPath(directory,filename);
				documentProperties.setProperty(
						DocumentProperties.DOCUMENT_PATH,path);
				documentProperties.setProperty(
						DocumentProperties.DOCUMENT_CREATOR,
						destinationTicket.getUser().getId());
			} catch (PropertyNotFoundException e) {
				logger.error(e.getMessage(),e);
			} catch (InvalidPropertyTypeException e) {
				logger.error(e.getMessage(),e);
			}
			
			newDocument = destinationService.createDocument(destinationTicket,
															documentProperties);		
			
			updateContent(sourceService, sourceTicket, destinationService, destinationTicket, document, newDocument);
			
			if (document.getResourceNodes() != null) {
				newDocument.setResourceNodes(new TreeSet());
				Iterator it = document.getResourceNodes().iterator();
				int i = 0;
				boolean found = false;
				Directory destinationDirectory = null;
				while (it.hasNext()) {
					ResourceNode resource = (ResourceNode)it.next();
					destinationDirectory = destinationRepository.getRoot();
					
					String[] directories = StringUtils.split(resource.getPath(),"/");
					String processedPath = "";
					for (int j = 0; j < directories.length-1; j++) {
						String directoryName = directories[i];
						processedPath = processedPath + "/" + directoryName;
						Iterator it2 = destinationDirectory.getNodes().iterator();
						while (it2.hasNext()) {
							Node node = (Node) it2.next();
							if (node.isDirectory()) {
								if (processedPath.equals(node.getPath())) {
									found = true;
									destinationDirectory = (Directory)node;
									break;
								}
							}
						}
						if (!found) {
							Directory newDirectory = 
								destinationService.createDirectory(destinationTicket, 
																   directoryName, 
																   Messages.getMessage("autogenerated_description"), 
																   destinationDirectory.getId());

							destinationDirectory.getNodes().add(newDirectory);
							newDirectory.setParent(destinationDirectory.getId());
							newDirectory.setRepository(destinationDirectory.getRepository());
							
							destinationDirectory = newDirectory;
						}
					}
					
					ResourceNode newResource = addResource(sourceService,
											   sourceTicket,
											   destinationService,
											   destinationTicket,
											   destinationDirectory,
											   resource,
											   monitor);
					
					destinationService.addResourceToDocument(destinationTicket,
															 newResource.getId(),
															 newDocument.getId());
					newDocument.getResourceNodes().add(newResource);
				}
			}
		} catch (LocalCacheException lce) {
			throw new RepositoryException(lce);
		} catch (InvalidPropertyTypeException e) {
			throw new RepositoryException(e);
		} catch (PropertyNotFoundException e) {
			throw new RepositoryException(e);
		}
		
		directory.getNodes().add(newDocument);
		newDocument.setParent(directory.getId());
		newDocument.setRepository(directory.getRepository());
		EntityRegistry.getInstance().addNode(newDocument);

		return newDocument;
	}

	private void updateContent(final RepositoryService sourceService, 
							   final Ticket sourceTicket, 
							   final RepositoryService destinationService, 
							   final Ticket destinationTicket, 
							   final Node node, 
							   Node newNode) throws LocalCacheException, SecurityException, RepositoryException {
		
		InputStream is = null;
		try {
			LocalCache cache = LocalCacheService.getInstance().getLocalCache();
			if (cache.isNodeCached(node)) {
				is = cache.getNodeContent(node);
				destinationService.updateContent(
						destinationTicket, newNode.getId(), is);
			} else {
				// Download content to the cache
				cache.addNodeToCache(node, new NodeContentHandler() {
					public void copyTo(OutputStream os) throws LocalCacheException {
						
						try {
							if (node.isDocument()) {
								sourceService.loadDocumentContent(node.getId(), sourceTicket, os);
							} else if (node.isResource()) {
								sourceService.loadResourceNodeContent(sourceTicket, node.getId(), os);
							}
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							throw new LocalCacheException(e);
						}
					}
				});
				is = cache.getNodeContent(node);
				destinationService.updateContent(
						destinationTicket, newNode.getId(), is);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}

	private ResourceNode addResource(RepositoryService sourceService,
									 Ticket sourceTicket, 
									 RepositoryService destinationService,
									 Ticket destinationTicket, 
									 Directory directory, 
									 ResourceNode resource,
									 IProgressMonitor monitor) throws RepositoryException,
									 								  SecurityException {

		monitor.subTask(Messages.getAndParseValue("copy_job_step","%1",resource.getName()));
		monitor.worked(1);

		ResourceNodeProperties properties = resource.dumpProperties();

		try {
			String filename = FileUtils.getFileName(resource.getPath());

			properties.setProperty(ResourceNodeProperties.RESOURCE_PARENT_ID,
								   directory.getId());
			properties.setProperty(ResourceNodeProperties.RESOURCE_PATH,
								   FileUtils.buildPath(directory,filename));


			ResourceNode newResource = 
				destinationService.createResource(destinationTicket,
												  properties);
			
			updateContent(sourceService, sourceTicket, destinationService, destinationTicket, resource, newResource);			

			directory.getNodes().add(newResource);
			newResource.setParent(directory.getId());
			newResource.setRepository(directory.getRepository());
			EntityRegistry.getInstance().addNode(newResource);

			return newResource;
		} catch (LocalCacheException lce) {
			throw new RepositoryException(lce);
		} catch (InvalidPropertyTypeException e) {
			throw new RepositoryException(e);
		} catch (PropertyNotFoundException e) {
			throw new RepositoryException(e);
		}
			
	}
	
	
	private Document createDocumentCopy(Ticket ticket, Document document) {
		
		Document newDocument = (Document)SerializationUtils.clone(document);
				
		// clean
		newDocument.setId(null);
		newDocument.getMetaData().setId(null);
		newDocument.setResourceNodes(new TreeSet());
	
		return newDocument;
	}
	
	/**
	 * @see org.jlibrary.client.ui.ccp.Paster#afterPaste(java.lang.Object, java.lang.Object)
	 */
	public void afterPaste(Object source, Object destination) {

		// Update repository tree
		Node destinationNode;
		Repository destinationRepository;
		if (destination instanceof Repository) {
			destinationRepository = (Repository)destination;
			destinationNode = destinationRepository.getRoot(); 
		} else {
			destinationNode = (Node)destination;
			destinationRepository = RepositoryRegistry.getInstance().getRepository(destinationNode.getRepository());
		}

		Object[] sourceNodes = (Object[])source;
		for (int i = 0; i < sourceNodes.length; i++) {
			if (sourceNodes[i] instanceof Node) {
				Node sourceNode = (Node)sourceNodes[i];
				Node parentSource = EntityRegistry.getInstance().
					getNode(sourceNode.getParent(),sourceNode.getRepository());			
				Repository sourceRepository = RepositoryRegistry.getInstance().getRepository(sourceNode.getRepository());
			
				RepositoryView.getRepositoryViewer().refresh(sourceNode);
				RepositoryView.getRepositoryViewer().expandToLevel(parentSource,1);
		
				if (!(sourceRepository == destinationRepository)) {
					RepositoryView.getRepositoryViewer().refresh(sourceRepository);
					RepositoryView.getRepositoryViewer().expandToLevel(sourceRepository,1);
				}
			}
		}
		
		RepositoryView.getRepositoryViewer().refresh(destinationRepository);
		RepositoryView.getRepositoryViewer().expandToLevel(destinationRepository,1);
		RepositoryView.getRepositoryViewer().refresh(destinationNode);
		RepositoryView.getRepositoryViewer().expandToLevel(destinationNode,1);		
	}
	
	/**
	 * @see org.jlibrary.client.ui.ccp.Paster#beforePaste(java.lang.Object, java.lang.Object)
	 */
	public boolean beforePaste(Object source, Object destination) {

		//Check preconditions
		boolean isRepository = destination instanceof Repository;
		
		if (!((destination instanceof Node) || isRepository) ||
			 (destination == null) ||
			!(source instanceof Object[]) ||
			 (source == null)) {
			return false;
		}
			
		if (!isRepository) {
			if (!((Node)destination).isDirectory()) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isIncluded(Node node, Object[] nodes) {

		// Check if node ancestors are included in the selection
		String parentId = node.getParent();
		while (parentId != null) {
			Node parentNode = EntityRegistry.getInstance().
				getNode(parentId,node.getRepository());
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j] == parentNode) {
					return true;
				}
			}
			parentId = parentNode.getParent();
		}

		return false;
	}
	
}
