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
package org.jlibrary.client.ui.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jlibrary.cache.CacheUtils;
import org.jlibrary.client.Messages;
import org.jlibrary.client.extraction.MetaDataExtractor;
import org.jlibrary.client.i18n.LocaleService;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.search.extraction.ExtractionException;
import org.jlibrary.core.search.extraction.HTMLExtractor;
import org.jlibrary.core.search.extraction.HeaderMetaData;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This class will perform tasks that are useful in several actions, for example
 * creating a document structure, creating resources, etc.
 *
 * Having all this business logic centralized here prevents duplicate code and
 * by consequence promotes a less error-phrone basecode.
 */
public class RepositoryHelper {

	static Logger logger = LoggerFactory.getLogger(RepositoryHelper.class);
	
	private static NullProgressMonitor nullMonitor = new NullProgressMonitor();
	private static HTMLExtractor htmlExtractor = new HTMLExtractor();

	private static ArrayList addedResources = new ArrayList();
	
	/**
	 * Creates a document into a repository
	 *
	 * @param repository Repository
	 * @param parent Document's parent directory
	 * @param file File that contains the document contents
	 * @param crawlResources If <code>true</code> then jLibrary will try to extract
	 * and create resources for this document.
	 *
	 * @throws RepositoryException If the document can't be created
	 * @throws SecurityException If the user haven't enought rights to perform this operation
	 */
	public static void createDocument(Repository repository,
							   		  Directory parent,
							   		  File file,
							   		  boolean crawlResources) 
													throws RepositoryException,
												 		   SecurityException {
		
		createDocument(repository,parent,null,file,crawlResources);
	}
	
	/**
	 * Creates a document into a repository
	 *
	 * @param repository Repository
	 * @param parent Document's parent directory
	 * @param properties Document properties. It can be <code>null</code> if 
	 * you want jLibrary to perform the document data and metadata extraction 
	 * automatically. Note that the content property must not be filled on 
	 * the document's properties as is responsability of this method to grab 
	 * it from the file and replace URL references if necessary.
	 * @param file File that contains the document contents
	 * @param crawlResources If <code>true</code> then jLibrary will try to extract
	 * and create resources for this document.
	 *
	 * @return Document New created document
	 *
	 * @throws RepositoryException If the document can't be created
	 * @throws SecurityException If the user haven't enought rights to perform this operation
	 */
	public static Document createDocument(Repository repository,
							   		  	  Directory parent,
							   		  	  DocumentProperties properties,
							   		  	  File file,
							   		  	  boolean crawlResources) 
													throws RepositoryException,
												 		   SecurityException {

		addedResources.clear();

		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();

		if (properties == null) {
			properties = 
				createDocumentProperties(
						repository,
						file,
						parent,
						repository.getRepositoryConfig().isExtractMetadata());
		}
		
		Integer typecode = (Integer)properties.getProperty(
				DocumentProperties.DOCUMENT_TYPECODE).getValue();

		RepositoryService service = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		boolean modifiedFile = false;
		// Create resources structure, currently only if the document is an HTML file
		if (file.exists() && 
			crawlResources && 
			Types.isResourceSensible(typecode)) {
			createResourcesStructure(repository,parent,file);
			if (!addedResources.isEmpty()) {
				try {
					File tempFile = File.createTempFile("jlib","html");
					org.apache.commons.io.FileUtils.copyFile(file,tempFile);
					file = tempFile;
					modifiedFile = true;
					htmlExtractor.changePaths(file,
											  parent,
											  addedResources);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}		
				
		Document document = service.createDocument(ticket,properties);

		if (file.exists()) {
			// Stream content
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				service.updateContent(ticket, document.getId(), fis);
				if (modifiedFile) {
					file.delete();
				}
			} catch (Exception e) {
				throw new RepositoryException(e);
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
		
		EntityRegistry.getInstance().addNode(document);

		
		Iterator it = addedResources.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			service.addResourceToDocument(ticket,
										 node.getId(),
										 document.getId());		
			
			document.getResourceNodes().add(node);

			Directory resourceParent = (Directory)EntityRegistry.getInstance().
				getNode(node.getParent(),node.getRepository());
			if (!resourceParent.getNodes().contains(node)) {
				resourceParent.getNodes().add(node);
				EntityRegistry.getInstance().addNode(node);
			}
		}
		
		parent.getNodes().add(document);
		return document;
	}
	
	/**
	 * <p>Builds all the needed properties for a document. It will fill the 
	 * metadata properties if specified. If the file exists, it will fill the 
	 * contents. If the file does not exist, then it will create a stub 
	 * document.</p>
	 * 
	 * <p>Note that this method won't create any resources for the document.</p>
	 *
	 * @param repository Repository
	 * @param parent Document's parent directory
	 * @param file File that contains the document contents
	 * @param extractMetadata If <code>true</code> then jLibrary will try to 
	 * extract the metadata. If <code>false</code> jLibrary will ignore 
	 * document metadata
	 *
	 * @return DocumentProperties Properties of the created document
	 *
	 * @throws RepositoryException If the document properties cannot be 
	 * obtained
	 */
	public static DocumentProperties buildDocument(
			Repository repository,
			Directory parent,
			File file,
			boolean extractMetadata) throws RepositoryException {

		DocumentProperties properties = 
			createDocumentProperties(repository,file,parent,extractMetadata);			
		return properties;
	}	
	
	private static void createResourcesStructure(
										  Repository repository,
										  Directory parent,
										  File file) throws RepositoryException,
				   						   					SecurityException {

		String[] filenames = null;
		try {
			filenames = filterResourceNames(htmlExtractor.extractResources(file));
		} catch (ExtractionException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			return;
		}
		for (int i = 0; i < filenames.length; i++) {
			String resourcePath = filenames[i];
			File resourceFile = new File(resourcePath);
			if (!resourceFile.exists()) {
				// try to add the parent's path
				filenames[i] = file.getParentFile().getAbsolutePath() +
							   System.getProperty("file.separator") +
							   resourcePath;
				resourceFile = new File(filenames[i]);
				if (!resourceFile.exists()) {
					continue;
				}
			}
			ResourceNode resource = createResourceFromFile(
													filenames[i],
								   					file.getAbsolutePath(),
								   					nullMonitor,
								   					repository,
								   					parent);
			if (resource != null) {
				addedResources.add(resource);
			}
		}
	}

	private static String[] filterResourceNames(String[] filenames) {
		
		//TODO: Remove this Jackrabbit dependency
		for (int i = 0; i < filenames.length; i++) {
			filenames[i] = Text.unescape(filenames[i]);
		}
		return filenames;
	}

	public static Directory createDirectory(Repository repository,
									 Directory dir,
									 File file) throws RepositoryException,
									  				   SecurityException {

		Directory parent = (Directory)EntityRegistry.getInstance().
			getNode(dir.getId(),dir.getRepository());
		String directoryName = file.getName();
		int indexColon = directoryName.indexOf(":");
		if (indexColon != -1) {
			directoryName = directoryName.substring(0, indexColon);
		}
		String description = Messages.getMessage("autogenerated_description");

		Directory directory = createDirectory(repository,parent,directoryName,description);
		return directory;
	}

	public static Directory createDirectory(Repository repository,
									 Directory parent,
									 String directoryName,
									 String directoryDescription) throws RepositoryException,
									 									 SecurityException {

		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();

		RepositoryService service = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		Directory directory = service.createDirectory(ticket,
													  directoryName,
													  directoryDescription,
													  parent.getId());
		parent.getNodes().add(directory);
		EntityRegistry.getInstance().addNode(directory);
		return directory;
	}


	public static Category findUnknownCategory(Repository repository) {

		if (repository.getCategories() != null) {
			Iterator it = repository.getCategories().iterator();
			while (it.hasNext()) {
				Category category = (Category) it.next();
				if (category.isUnknownCategory()) {
					return category;
				}
			}
		}
		return null;
	}

	private static Author findAuthor(Repository repository, String authorName) {

		// Find author based in author text
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();

		RepositoryService service = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		Author author = null;
		try {
			author = service.findAuthorByName(ticket,authorName);
		} catch (AuthorNotFoundException anee) {
		} catch (RepositoryException e1) {}

		if (author == null) {
		    return Author.UNKNOWN;
		}
		return author;
	}

	/**
	 * Creates a resource node
	 *
	 * @param resourcePath Path to the resource file
	 * @param documentPath Path to the document file. It can be <code>null</code>
	 * if the document it has been already created
	 * @param monitor Progress monitor to track resource addition
	 * @param repository Repository
	 * @param parent Parent node for the resource
	 * 
	 * @return ResourceNode Created resource node
	 *
	 * @throws RepositoryException If the resources can't be created
	 * @throws SecurityException If the user doesn't have permissions to do this operation
	 */
	private static ResourceNode createResourceFromFile(
									   String resourcePath,
									   String documentPath,
								 	   IProgressMonitor monitor,
								 	   Repository repository,
								 	   Node parent) throws RepositoryException,
								   						   SecurityException {

		Ticket ticket = repository.getTicket();
		ServerProfile profile = repository.getServerProfile();
		RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		File resourceFile = new File(resourcePath);
		try {
			if ((documentPath != null) && !documentPath.equals("")) {
				String filteredResourcePath =
					StringUtils.replace(resourcePath,"\\","/");
				filteredResourcePath = filteredResourcePath.substring(
						0,filteredResourcePath.lastIndexOf("/"));
				String filteredDocumentPath =
					StringUtils.replace(documentPath,"\\","/");
				filteredDocumentPath = filteredDocumentPath.substring(
						0,filteredDocumentPath.lastIndexOf("/"));

				String subpath = StringUtils.difference(filteredDocumentPath,
														filteredResourcePath);

				// Remove the .. references

				if (subpath.length() > 0) {
					// subpath. We must create the directories
					String[] dirs = StringUtils.split(subpath,"/");
					for (int i = 0; i < dirs.length; i++) {
						String dirname = dirs[i];
						if (dirname.equals(".")) {
							continue;
						}
						if (dirname.equals("..")) {
							if (parent.getParent() != null) {
								parent = EntityRegistry.getInstance().
									getNode(parent.getParent(),
											parent.getRepository());
							}
							continue;
						}

						// Look if the directory already exists
						boolean found = false;
						Iterator it = parent.getNodes().iterator();
						while (it.hasNext()) {
							Node node = (Node) it.next();
							if (!node.isDirectory()) continue;
							if (node.getName().equalsIgnoreCase(dirname)) {
								//catched
								parent = node;
								found = true;
							}
						}
						if (!found) {
							Node newParent = service.createDirectory(
									ticket,
									dirname,
									Messages.getMessage("autogenerated_description"),
									parent.getId());
							EntityRegistry.getInstance().addNode(newParent);
							parent.getNodes().add(newParent);
							parent = newParent;
						}
					}
				}
			}
			String resourceName = resourceFile.getName();

			// Check if the resource already exists. We will look on the 
			// resources added for this document
			Iterator it = addedResources.iterator();
			while (it.hasNext()) {
				ResourceNode node = (ResourceNode) it.next();
				if (node.getName().equals(resourceName)) {
					if (node.getParent().equals(parent.getId())) {
						// Resource already exists
						return node;
					}
				}
			}
					
			// Check if the resource already exists. We will look now on the 
			// resources added to other documents
			it = parent.getNodes().iterator();
			while (it.hasNext()) {
				Node node = (Node)it.next();
				if (!node.isResource()) continue;
				ResourceNode resourceNode = (ResourceNode) node;
				if (resourceNode.getName().equals(resourceName)) {
					return resourceNode;
				}
			}			
			
			// It's a file. Create it
			monitor.subTask(Messages.getAndParseValue(
					"new_document_wizard_add_resource",
					"%1",resourceFile.getName()));
			monitor.internalWorked(1);

			ResourceNodeProperties resourceProperties =
				new ResourceNodeProperties();
			resourceProperties.addProperty(
					ResourceNodeProperties.RESOURCE_NAME,
										   resourceName);
			resourceProperties.addProperty(
					ResourceNodeProperties.RESOURCE_TYPECODE,
					Types.getTypeForFile(resourcePath));
			resourceProperties.addProperty(
					ResourceNodeProperties.RESOURCE_CONTENT,
					IOUtils.toByteArray(new FileInputStream(resourceFile)));
			resourceProperties.addProperty(
					ResourceNodeProperties.RESOURCE_PARENT_ID,
					parent.getId());
			resourceProperties.addProperty(
					ResourceNodeProperties.RESOURCE_PATH,
					FileUtils.buildPath((Directory)parent,resourceName));
			resourceProperties.addProperty(
					ResourceNodeProperties.RESOURCE_DESCRIPTION,
					Messages.getMessage("autogenerated_description"));
			
			ResourceNode resource = 
				service.createResource(ticket,resourceProperties);
			return resource;

		} catch(InvalidPropertyTypeException ipte) {
			logger.error(ipte.getMessage(),ipte);
			throw new RepositoryException(ipte);
		} catch(PropertyNotFoundException pnfe) {
			logger.error(pnfe.getMessage(),pnfe);
			throw new RepositoryException(pnfe);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			throw new RepositoryException(ioe);
		}
	}

	/**
	 * Creates a a resource from a file and adds it to a document
	 *
	 * @param repository Repository
	 * @param parent Resource's parent directory
	 * @param file File that contains the document contents
	 * @param document Document in which the resource will be added
	 * 
	 * @return ResourceNode Created resource node
	 *
	 * @throws RepositoryException If the resource can't be created
	 * @throws SecurityException If the user haven't enought rights to perform this operation
	 */
	public static ResourceNode createResource(Repository repository,
							   		  		  Directory parent,
							   		  		  Document document,
							   		  		  File file) 
													throws RepositoryException,
														   SecurityException {
		
		ResourceNode resource = createResource(repository,parent,file);
		
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		RepositoryService service = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		
		service.addResourceToDocument(ticket,
				 					  resource.getId(),
				 					  document.getId());
		
		return resource;
	}
	
	/**
	 * Creates a a resource from a file
	 *
	 * @param repository Repository
	 * @param parent Resource's parent directory
	 * @param file File that contains the document contents
	 * 
	 * @return ResourceNode Created resource node
	 *
	 * @throws RepositoryException If the resource can't be created
	 * @throws SecurityException If the user haven't enought rights to perform this operation
	 */
	public static ResourceNode createResource(Repository repository,
							   		  		  Directory parent,
							   		  		  File file) 
													throws RepositoryException,
												 	       SecurityException {

		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		
		ResourceNode resource = new ResourceNode();
		resource.setName(file.getName());
		resource.setTypecode(Types.getTypeForFile(file.getName()));
		resource.setDescription(Messages.getMessage("metadata_extracted"));
		resource.setNodes(new TreeSet());
		resource.setParent(parent.getId());
		resource.setPath(file.getAbsolutePath());
		resource.setRepository(parent.getRepository());
		resource.setImportance(Node.IMPORTANCE_MEDIUM);
		resource.setCreator(ticket.getUser().getId());
		resource.setSize(new BigDecimal((double)file.length()));
		resource.setDate(new Date());

		FileInputStream fis = null;
		try {
			RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			ResourceNodeProperties resProperties = resource.dumpProperties();
			resource = service.createResource(ticket,resProperties);

			try {
				fis = new FileInputStream(file);
				service.updateContent(ticket, resource.getId(), fis);
				CacheUtils.addFileToCache(file, resource);
			} catch (Exception e) {
				throw new RepositoryException(e);
			}

			
			EntityRegistry.getInstance().addNode(resource);

			parent.getNodes().add(resource);

		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(),ioe);
				}
			}
		}
		return resource;
	}
	
	private static DocumentProperties 
							createDocumentProperties(Repository repository, 
													 File file, 
													 Directory parent,
													 boolean extractMetadata) {
		
		Ticket ticket = repository.getTicket();
		
		Category unknownCategory = findUnknownCategory(repository);
		// Try to extract metadata
		HeaderMetaData header = null;
		if (extractMetadata) {
			MetaDataExtractor.extractMetaData(file.getAbsolutePath());
		}
		Document document = new Document();
		document.setTypecode(Types.getTypeForFile(file.getName()));
		DocumentMetaData metadata = new DocumentMetaData();

		if ((header != null) && (header.getAuthor() != null) && (!header.getAuthor().equals(""))) {
			metadata.setAuthor(findAuthor(repository,header.getAuthor()));
		} else {
			metadata.setAuthor(Author.UNKNOWN);
		}
		if ((header != null) && (header.getKeywords() != null) && (!header.getKeywords().equals(""))) {
			metadata.setKeywords(header.getKeywords());
		} else {
			metadata.setKeywords("");
		}
		if ((header != null) &&
			(header.getLanguage() != null) &&
			(!header.getLanguage().equals("")) &&
			LocaleService.getInstance().isSupportedDocumentLanguage(header.getLanguage())) {
			metadata.setLanguage(header.getLanguage());
		} else {
			metadata.setLanguage(DocumentMetaData.UNKNOWN_LANGUAGE);
		}
		if ((header != null) && (header.getTitle() != null) && (!header.getTitle().equals(""))) {
			metadata.setTitle(header.getTitle());
			if (document.getTypecode() == Types.HTML_DOCUMENT) {
				// It's very common that related web pages have the same title
				document.setName(file.getName());
			} else {
				document.setName(header.getTitle());
			}
		} else {
			metadata.setTitle(file.getName());
			document.setName(file.getName());
		}
		metadata.setUrl("file://" + file.getAbsolutePath());
		metadata.setDate(new Date(file.lastModified()));

		if ((header != null) && (header.getDescription() != null) && (!header.getDescription().equals(""))) {
			document.setDescription(header.getDescription());
		} else {
			document.setDescription(Messages.getMessage("metadata_extracted"));
		}
		document.setExternal(false);
		document.setMetaData(metadata);
		document.setNodes(new TreeSet());
		document.setNotes(new HashSet());
		document.setParent(parent.getId());
		document.setReference(false);
		document.setRepository(parent.getRepository());
		document.setImportance(Node.IMPORTANCE_MEDIUM);
		document.setCreator(ticket.getUser().getId());

		document.setSize(new BigDecimal((double)file.length()));
		document.setDate(new Date());

		document.setMetaData(metadata);
		
		String path = FileUtils.buildPath(parent,file.getName());
		document.setPath(path);	
		
		DocumentProperties docProperties = document.dumpProperties();
		if (!(document.getTypecode() == Types.FOLDER) &&
			!(document.getTypecode() == Types.IMAGE_DOCUMENT) &&
			!(document.getTypecode() == Types.OTHER)) {
			if (unknownCategory != null) {
				try {
					docProperties.addProperty(
							DocumentProperties.DOCUMENT_ADD_CATEGORY,
							unknownCategory.getId());
				} catch (PropertyNotFoundException e) {
					logger.error(e.getMessage(),e);
				} catch (InvalidPropertyTypeException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return docProperties;
	}
	
}
