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
package org.jlibrary.core.repository.axis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.repository.exception.UnknownMethodException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Axis web service for repository management. It doesn't implements RepositoryService
 * interface because the method signatures don't match. This is no problem because RepositoryService
 * interface is implemented by WSRepositoryServiceImpl.
 */
public class AxisRepositoryService implements RepositoryService {

	static Logger logger = LoggerFactory.getLogger(AxisRepositoryService.class);
	
	private ServerProfile localProfile = new LocalServerProfile();
	
	/**
	 * Constructor
	 */
	public AxisRepositoryService() {}


	public List findAllRepositoriesInfo(Ticket ticket) throws RepositoryException  {
	
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		List list = service.findAllRepositoriesInfo(ticket);
		return list;
	}


	public Repository findRepository(String id, 
									 Ticket ticket) 
											throws RepositoryNotFoundException,
												   RepositoryException, 
												   SecurityException  {
	
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		Repository repository = service.findRepository(id, ticket);		
		return repository;
	}

	
	public List findAllCategories(Ticket ticket) throws RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.findAllCategories(ticket);
	}
	
	public List findAllAuthors(Ticket ticket) throws RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.findAllAuthors(ticket);
	}
	

	public Repository createRepository(Ticket ticket,
	        						   String name,
									   String description,
									   User creator) throws RepositoryAlreadyExistsException,
									   						RepositoryException,
									   						SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
				
		Repository repository = service.createRepository(ticket,name,description,creator);
		
		return repository;
	}


	public void deleteRepository(Ticket ticket) throws RepositoryException,
													   SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.deleteRepository(ticket);
	}
	
	public Directory updateDirectory(Ticket ticket,
									 DirectoryProperties directoryProperties) throws RepositoryException, 
	    											  								 SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.updateDirectory(ticket, directoryProperties);
	}

	public Repository updateRepository(Ticket ticket,
									   RepositoryProperties repositoryProperties) throws RepositoryException, 
							  								 							 SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.updateRepository(ticket, repositoryProperties);
	}
	
	
	public void removeDirectory(Ticket ticket, String directoryId) throws RepositoryException,
																		  SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.removeDirectory(ticket, directoryId);
	}
	

	public Directory createDirectory(Ticket ticket,
	        						 String name,
									 String description,
									 String parentId) throws RepositoryException, 
									 					  SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.createDirectory(ticket, name,description,parentId);	
	}

	public Directory createDirectory(Ticket ticket,
	        						 DirectoryProperties properties) throws RepositoryException, 
									 					  					SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.createDirectory(ticket, properties);	
	}	

	public Category createCategory(Ticket ticket, 
	        					   CategoryProperties categoryProperties) 
										throws CategoryAlreadyExistsException,
											   RepositoryException,
	        					   			   SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.createCategory(ticket,categoryProperties);	
	}

	public void deleteCategory(Ticket ticket,
	        				   String categoryId) throws RepositoryException,
														 SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.deleteCategory(ticket,categoryId);	
	}
	
	public Category updateCategory(Ticket ticket,
	        				       String categoryId,
	        				       CategoryProperties categoryProperties) 
											throws CategoryNotFoundException,
												   RepositoryException,
												   SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.updateCategory(ticket,categoryId,categoryProperties);			
	}
	

	public Category findCategoryById(Ticket ticket,
									 String id) 
											throws CategoryNotFoundException,
												   RepositoryException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.findCategoryById(ticket,id);	
	}
	
	public Category findCategoryByName(Ticket ticket,
									   String name) 
											throws CategoryNotFoundException,
												   RepositoryException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.findCategoryByName(ticket,name);	
	}
	
	public Document createDocument( Ticket ticket,
		 	 						DocumentProperties docProperties) throws RepositoryException,	
		 	 								 									 SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		byte[] realContent;
		try {
			AttachmentPart[] attachments = getMessageAttachements();
			InputStream is = attachments[0].getDataHandler().getInputStream();
			realContent = IOUtils.toByteArray(is);
			is.close();
			docProperties.setProperty(
					DocumentProperties.DOCUMENT_CONTENT,realContent);
		} catch (AxisFault e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			throw new RepositoryException(ioe);
		} catch (SOAPException se) {
			logger.error(se.getMessage(),se);
			throw new RepositoryException(se);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}				
		
		return service.createDocument(ticket,docProperties);	
	}

    public Document createDocument( Ticket ticket,
                                    DocumentProperties docProperties,
                                    InputStream content) throws RepositoryException,	
		 	 								 									 SecurityException {
        throw new UnsupportedOperationException();
    }
			
	public List createDocuments( Ticket ticket,
								 List properties) throws RepositoryException,	
						 								 SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		byte[] realContent;
		try {
			for (int i =0; i<properties.size();i++) {
				DocumentProperties props = 
					(DocumentProperties) properties.get(i);				
				AttachmentPart[] attachments = getMessageAttachements();
				InputStream is = attachments[i].getDataHandler().getInputStream();
				realContent = IOUtils.toByteArray(is);
				is.close();
				props.setProperty(DocumentProperties.DOCUMENT_CONTENT,
								  realContent);
			}
		} catch (AxisFault e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			throw new RepositoryException(ioe);
		} catch (SOAPException se) {
			logger.error(se.getMessage(),se);
			throw new RepositoryException(se);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		return service.createDocuments(ticket,properties);
	}	
	
	public void removeDocument(Ticket ticket,
	        				   String docId) throws RepositoryException,
	        							     		SecurityException,
	        							     		ResourceLockedException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.removeDocument(ticket,docId);
	}
	
	public Favorite createFavorite(Ticket ticket, 
								   Favorite favorite) throws RepositoryException,
															 SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.createFavorite(ticket,favorite);
	}
	
	public void deleteFavorite(Ticket ticket, 
							   String favoriteId) throws RepositoryException,
														 SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.deleteFavorite(ticket,favoriteId);
	}
	
	public void renameNode(Ticket ticket,
			   			   String nodeId, 
			   			   String name) throws RepositoryException, 
			   			   					   SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.renameNode(ticket,nodeId,name);	
	}
	
	public Document moveDocument( Ticket ticket,
		        				  String documentId, 
		        				  String directoryId,
		        				  String destinationRepository) throws RepositoryException,
		        				  							 SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.moveDocument(ticket,documentId,directoryId,destinationRepository);			
	}
	
	public Document copyDocument( Ticket ticket,
			  					  String sourceId, 
								  String destinationId,
		        				  String destinationRepository) throws RepositoryException,
								  							   SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.copyDocument(ticket,sourceId,destinationId,destinationRepository);			
	}
	
	public Directory copyDirectory( Ticket ticket,
			  					   	String sourceId, 
									String destinationId,
			        				String destinationRepository) throws RepositoryException, 
																 SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.copyDirectory(ticket, sourceId,destinationId,destinationRepository);			
	}
	
	public Directory moveDirectory( Ticket ticket,
			   						String sourceId, 
									String destinationId,
			        				String destinationRepository) throws RepositoryException,
																 SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.moveDirectory(ticket,sourceId,destinationId,destinationRepository);			
	}

	/**
	 * Loads a document contents
	 * 
	 * @param docId Document to be loaded
	 * @return byte[] Document's contents
	 * 
	 * @throws RepositoryException If the document's contents can't be loaded
	 */
	public byte[] loadDocumentContent(String docId, 
									  Ticket ticket) throws RepositoryException, 
									  						SecurityException {
	
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		try {
			byte[] content = service.loadDocumentContent(docId, ticket);
			createAttachment(content);
			return new byte[]{};
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}
	
	public Document updateDocument(Ticket ticket,
								   DocumentProperties docProperties) throws RepositoryException, 
								   					     					SecurityException,
								   					     					ResourceLockedException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		if (docProperties.hasProperty(DocumentProperties.DOCUMENT_CONTENT)) {
			byte[] realContent;
			try {
				AttachmentPart[] attachments = getMessageAttachements();
				InputStream is = attachments[0].getDataHandler().getInputStream();
				realContent = IOUtils.toByteArray(is);
				is.close();
				docProperties.setProperty(
						DocumentProperties.DOCUMENT_CONTENT,realContent);
			} catch (AxisFault e) {
				logger.error(e.getMessage(),e);
				throw new RepositoryException(e);
			} catch (IOException ioe) {
				logger.error(ioe.getMessage(),ioe);
				throw new RepositoryException(ioe);
			} catch (SOAPException se) {
				logger.error(se.getMessage(),se);
				throw new RepositoryException(se);
			} catch (PropertyNotFoundException e) {
				logger.error(e.getMessage(),e);
				throw new RepositoryException(e);
			} catch (InvalidPropertyTypeException e) {
				logger.error(e.getMessage(),e);
				throw new RepositoryException(e);
			}		
		}		
		return service.updateDocument(ticket,docProperties);	
	}

    public Document updateDocument(Ticket ticket,
                                   DocumentProperties docProperties,
                                   InputStream content) throws RepositoryException, 
								   					     					SecurityException,
								   					     					ResourceLockedException {
        throw new UnsupportedOperationException();
    }

	public Author findAuthorByName(Ticket ticket,
								   String name) throws AuthorNotFoundException,
								   					   RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.findAuthorByName(ticket,name);	
	}
	
	public Author findAuthorById(Ticket ticket,
								 String id) throws AuthorNotFoundException,
								 				   RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.findAuthorById(ticket,id);	
	}

	public Node findNode(Ticket ticket, 
						 String id) throws RepositoryException,
						 				   NodeNotFoundException,
						 				   SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.findNode(ticket,id);	
	}
	
	public Document findDocument(Ticket ticket, String id) throws RepositoryException,
																  NodeNotFoundException,
																  SecurityException {

	    org.jlibrary.core.repository.RepositoryService service = 
	        JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

	    return service.findDocument(ticket,id);	
	}
	
	public Directory findDirectory(Ticket ticket, String id) throws RepositoryException, 
																	NodeNotFoundException,
																	SecurityException {
		
	    org.jlibrary.core.repository.RepositoryService service = 
	        JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

	    return service.findDirectory(ticket,id);		
	}   
	
	public Bookmark createBookmark(Ticket ticket,Bookmark bookmark) throws RepositoryException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.createBookmark(ticket,bookmark);
	}
	
	public void removeBookmark(Ticket ticket,
							   String bookmarkId) throws RepositoryException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		service.removeBookmark(ticket,bookmarkId);
	}
	
	public Bookmark updateBookmark(Ticket ticket,Bookmark bookmark) throws RepositoryException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.updateBookmark(ticket,bookmark);
	}
	
	public List findCategoriesForNode(Ticket ticket, String nodeId) throws RepositoryException,
																		   SecurityException {
		org.jlibrary.core.repository.RepositoryService service = 
								JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.findCategoriesForNode(ticket, nodeId);
	}			

	public List findNodesForCategory(Ticket ticket, 
									 String categoryId) 
											throws CategoryNotFoundException,
												   RepositoryException {
		
		org.jlibrary.core.repository.RepositoryService service = 
								JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.findNodesForCategory(ticket, categoryId);
	}
	
	public byte[] exportRepository(Ticket ticket) 
											throws RepositoryNotFoundException,
												   RepositoryException,
												   SecurityException {

		try {
			org.jlibrary.core.repository.RepositoryService service = 
									JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
			
			byte[] content = service.exportRepository(ticket);
			createAttachment(content);
			return new byte[]{};
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}


	private void createAttachment(byte[] content) throws IOException {
		// We will add the content as attachment instead			
		MessageContext context = MessageContext.getCurrentContext();
		Message message = context.getResponseMessage();
		DataHandler handler = new DataHandler(
				new ByteArrayDataSource(content,"application/octet-stream"));
		javax.xml.soap.AttachmentPart attachment = message.createAttachmentPart(handler);
		message.addAttachmentPart(attachment);
	}

	public void importRepository(Ticket ticket, 
								 byte[] content,
								 String name) throws RepositoryAlreadyExistsException,
								 					 RepositoryException,
													 SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
								JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		byte[] realContent;
		try {
			AttachmentPart[] attachments = getMessageAttachements();
			InputStream is = attachments[0].getDataHandler().getInputStream();
			realContent = IOUtils.toByteArray(is);
			is.close();
		} catch (AxisFault e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			throw new RepositoryException(ioe);
		} catch (SOAPException se) {
			logger.error(se.getMessage(),se);
			throw new RepositoryException(se);
		}
		
		service.importRepository(ticket, realContent,name);
	}
	
	public byte[] loadVersionContent(Ticket ticket, String versionId) throws RepositoryException,
	 																		 SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		try {
			byte[] content = service.loadVersionContent(ticket,versionId);
			createAttachment(content);
			return new byte[]{};
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}

	public Lock lockDocument(Ticket ticket,
			 				 String docId) throws RepositoryException,
			 					  				  SecurityException,
												  ResourceLockedException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		return service.lockDocument(ticket,docId);
	}

	public void unlockDocument(Ticket ticket,
				   			   String docId) throws RepositoryException,
				 					  				SecurityException,
				 					  				ResourceLockedException {
	
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		service.unlockDocument(ticket,docId);
	}
	
	public List findAllLocks(Ticket ticket) throws RepositoryException,
												   SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		return service.findAllLocks(ticket);
	}

	public Author createAuthor(Ticket ticket,
							   AuthorProperties properties
							   			 ) throws RepositoryException,
							   					  SecurityException,
							   					  AuthorAlreadyExistsException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.createAuthor(ticket,properties);	
	}
	
	public void updateAuthor(Ticket ticket,
			   				 String authorId,
							 AuthorProperties properties) 
											throws RepositoryException,
			   					  				   SecurityException,
			   					  				   AuthorNotFoundException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.updateAuthor(ticket,authorId,properties);	
	}
	
	public void deleteAuthor(Ticket ticket,
							 String authorId) throws RepositoryException,
					  				   				 SecurityException,
					  				   				 AuthorNotFoundException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.deleteAuthor(ticket,authorId);	
	}
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#createResource(org.jlibrary.core.entities.Ticket, org.jlibrary.core.properties.ResourceNodeProperties)
	 */
	public ResourceNode createResource(Ticket ticket, 
							   		   ResourceNodeProperties properties) throws RepositoryException,
							   		   											 SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		byte[] realContent;
		try {
			AttachmentPart[] attachments = getMessageAttachements();
			InputStream is = attachments[0].getDataHandler().getInputStream();
			realContent = IOUtils.toByteArray(is);
			is.close();
			properties.setProperty(
					ResourceNodeProperties.RESOURCE_CONTENT,realContent);
		} catch (AxisFault e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			throw new RepositoryException(ioe);
		} catch (SOAPException se) {
			logger.error(se.getMessage(),se);
			throw new RepositoryException(se);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
		
		return service.createResource(ticket,properties);	
	}
	
	public void addResourceToDocument(Ticket ticket,
					  				  String resourceId,
					  				  String documentId) 
											throws RepositoryException,
					  							   SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		service.addResourceToDocument(ticket,resourceId,documentId);
	
	}
	
	public List findNodesForResource(Ticket ticket, 
			 						 String resourceId) throws RepositoryException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();

		return service.findNodesForResource(ticket,resourceId);		
	}
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#loadResourceNodeContent(org.jlibrary.core.entities.Ticket, java.lang.String)
	 */	
	public byte[] loadResourceNodeContent(Ticket ticket,
			  							  String resourceId) throws RepositoryException, 
			  							  							SecurityException {		

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
	
		try {
			byte[] content = service.loadResourceNodeContent(ticket,resourceId);
			createAttachment(content);
			return new byte[]{};
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#updateResourceNode(org.jlibrary.core.entities.Ticket, org.jlibrary.core.properties.ResourceNodeProperties)
	 */	
	public ResourceNode updateResourceNode(Ticket ticket,
			   							   ResourceNodeProperties properties) throws RepositoryException, 
			   											 							 SecurityException {
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
	
		if (properties.hasProperty(ResourceNodeProperties.RESOURCE_CONTENT)) {
			byte[] realContent;
			try {
				AttachmentPart[] attachments = getMessageAttachements();
				InputStream is = attachments[0].getDataHandler().getInputStream();
				realContent = IOUtils.toByteArray(is);
				is.close();
				properties.setProperty(
						ResourceNodeProperties.RESOURCE_CONTENT,realContent);
			} catch (AxisFault e) {
				logger.error(e.getMessage(),e);
				throw new RepositoryException(e);
			} catch (IOException ioe) {
				logger.error(ioe.getMessage(),ioe);
				throw new RepositoryException(ioe);
			} catch (SOAPException se) {
				logger.error(se.getMessage(),se);
				throw new RepositoryException(se);
			} catch (PropertyNotFoundException e) {
				logger.error(e.getMessage(),e);
				throw new RepositoryException(e);
			} catch (InvalidPropertyTypeException e) {
				logger.error(e.getMessage(),e);
				throw new RepositoryException(e);
			}		
		}		
		
		return service.updateResourceNode(ticket,properties);
	}	
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#removeResourceNode(org.jlibrary.core.entities.Ticket, java.lang.String)
	 */	
	public void removeResourceNode(Ticket ticket, 
								   String resourceId) throws RepositoryException,
								   							 SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		service.removeResourceNode(ticket,resourceId);
	}
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#removeResourceNode(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public void removeResourceNode(Ticket ticket, 
								   String resourceId,
								   String docId) throws RepositoryException,
								   						SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		service.removeResourceNode(ticket,resourceId,docId);
	}
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#copyResource(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public ResourceNode copyResource(Ticket ticket, 
							 		 String resourceId, 
							 		 String directoryId,
			        				 String destinationRepository) throws RepositoryException, 
							 									SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.copyResource(ticket,resourceId,directoryId,destinationRepository);
	}		
	
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#moveResource(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public ResourceNode moveResource(Ticket ticket, 
									 String resourceId, 
									 String directoryId,
			        				 String destinationRepository) throws RepositoryException, 
									 							SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.moveResource(ticket,resourceId,directoryId,destinationRepository);
	}			
	
	/**
	 * @see org.jlibrary.core.repository.RepositoryService#copyNode(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public Node copyNode(Ticket ticket, 
						 String sourceId, 
						 String directoryId,
       				  	 String destinationRepository) throws RepositoryException, 
						 							SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.copyNode(ticket,sourceId,directoryId,destinationRepository);
	}		
	
	
	/**
	 * @see org.jlibrary.core.repository.RepositoryService#moveNode(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public Node moveNode(Ticket ticket, 
				 		 String sourceId, 
						 String directoryId,
       				  	 String destinationRepository) throws RepositoryException, 
						 							SecurityException {
		
		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.moveNode(ticket,sourceId,directoryId,destinationRepository);
	}	
	
	public List getVersions(Ticket ticket, 
							String documentId) throws RepositoryException, 
													  SecurityException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.getVersions(ticket,documentId);		
	}
	
	public Collection findDocumentsByName(Ticket ticket, 
										  String name) throws RepositoryException { 
										 					   

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		
		return service.findDocumentsByName(ticket,name);	
	}
	
	private AttachmentPart[] getMessageAttachements() throws AxisFault {
		
		MessageContext context = MessageContext.getCurrentContext();
		Message message = context.getRequestMessage();
		Attachments attachments = message.getAttachmentsImpl();
		if (attachments == null) {
			return new AttachmentPart[0];
		}
		int attachmentCount = attachments.getAttachmentCount();
		AttachmentPart[] parts = new AttachmentPart[attachmentCount];
		Iterator it = attachments.getAttachments().iterator();
		int i = 0;
		while (it.hasNext()) {
			AttachmentPart part = (AttachmentPart) it.next();
			parts[i++] = part;
		}
		return parts;
	}	

	public Collection findNodeChildren(Ticket ticket, String id) throws RepositoryException,
			NodeNotFoundException, SecurityException {

		org.jlibrary.core.repository.RepositoryService service = JLibraryServiceFactory
				.getInstance(localProfile).getRepositoryService();

		return service.findNodeChildren(ticket, id);
	}

	/**
	 * @see RepositoryService#saveSession(Ticket)
	 */
	public void saveSession(Ticket ticket) throws RepositoryException {
		
		RepositoryService service = JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		service.saveSession(ticket);
	}


	public boolean isPropertyRegistered(Ticket ticket, String propertyName) throws RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		return service.isPropertyRegistered(ticket, propertyName);
	}

	public boolean isPropertyRegistered(Ticket ticket, String uri, String propertyName) throws RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		return service.isPropertyRegistered(ticket, uri, propertyName);
	}


	public void registerCustomProperty(Ticket ticket, CustomPropertyDefinition property) throws RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		service.registerCustomProperty(ticket, property);
	}


	public void unregisterCustomProperty(Ticket ticket, CustomPropertyDefinition property) throws RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		service.unregisterCustomProperty(ticket, property);
	}


	public void exportRepository(Ticket ticket, 
								 OutputStream stream) 
				throws RepositoryNotFoundException, 
					   RepositoryException, 
					   SecurityException {

		throw new UnsupportedOperationException();
	}


	public void importRepository(Ticket ticket, 
								 String name, 
								 InputStream stream) throws RepositoryAlreadyExistsException, 
								 							RepositoryException, 
								 							SecurityException {
		
		throw new UnsupportedOperationException();
	}


	public void loadDocumentContent(String docId, 
									Ticket ticket, 
									OutputStream stream) throws RepositoryException, 
																SecurityException {

		throw new UnsupportedOperationException();
	}


	public void loadResourceNodeContent(Ticket ticket, 
										String resourceId, 
										OutputStream stream) throws RepositoryException, 
																	SecurityException {

		throw new UnsupportedOperationException();
	}


	public void loadVersionContent(Ticket ticket, 
								   String versionId, 
								   OutputStream stream) throws RepositoryException, 
								   							   SecurityException {

		throw new UnsupportedOperationException();
	}


	public Node updateContent(Ticket ticket, 
							  String docId, 
							  InputStream stream) throws SecurityException, 
							  							 RepositoryException {

		throw new UnsupportedOperationException();
	}


	public Node updateContent(Ticket ticket, 
							  String docId, 
							  byte[] content) throws SecurityException, RepositoryException {

		org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		return service.updateContent(ticket, docId,content);
	}

    public String getJLibraryAPIVersion() throws UnknownMethodException {
    	org.jlibrary.core.repository.RepositoryService service = 
			JLibraryServiceFactory.getInstance(localProfile).getRepositoryService();
		return service.getJLibraryAPIVersion();
    }
}
