/*
 * jLibrary, Open Source Document Management System
 * 
 * Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual contributors as
 * indicated by the @authors tag. See copyright.txt in the distribution for a
 * full listing of individual contributors. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the Modified BSD License as published by the Free Software
 * Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the Modified BSD License for more details.
 * 
 * You should have received a copy of the Modified BSD License along with this
 * software; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.jlibrary.core.xfire.client;


import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
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
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.ws.xfire.repository.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author martin
 * 
 * This will be the class in charge of repository management
 */
public class WSRepositoryImpl implements org.jlibrary.core.repository.RepositoryService {


  static Logger logger = LoggerFactory.getLogger(WSRepositoryImpl.class);

  // Services map
  private static HashMap services;

  private String endpoint;

  private XStream xstream = new XStream();

  private RepositoryService service = null;


  /**
   * Constructor
   */
  private WSRepositoryImpl(ServerProfile profile) {

    try {

      String location = profile.getLocation();
      location = StringUtils.replace(location, "jlibrary://", "http://");
      if (!location.endsWith("/")) {
        location += "/";
      }
      endpoint = location + "services/XfRepositoryService";

      logger.debug("[WSRepositoryImpl]:: Creando servicio para: " + endpoint);
      
      Service serviceModel = new ObjectServiceFactory().create(RepositoryService.class);
      service = (RepositoryService) new XFireProxyFactory().create(serviceModel, endpoint);

    } catch (MalformedURLException e) {
    	logger.error(e.getMessage(),e);
    }

  }


  /**
   * Returns an instance of the members service
   * 
   * @param profile
   *          Server profile for connecting to the web services server
   * 
   * @return Members service instance
   */
  @SuppressWarnings("unchecked")
  public static WSRepositoryImpl getInstance(ServerProfile profile) {

    if (services == null) {
      services = new HashMap();
    }

    WSRepositoryImpl service = (WSRepositoryImpl) services.get(profile);
    if (service == null) {
      service = new WSRepositoryImpl(profile);
      services.put(profile, service);
    }

    return service;
  }


  public void addResourceToDocument(Ticket ticket, String resourceId, String documentId) throws RepositoryException, SecurityException {
    
    logger.info("[addResourceToDocument.entrada]:: " + Arrays.asList(new Object[] { ticket, resourceId, documentId }));
    
    service.addResourceToDocument(xstream.toXML(ticket), resourceId, documentId);
    
    logger.info("[addResourceToDocument.retorna]:: ");
  }


  public Directory copyDirectory(Ticket ticket, String sourceId, String destinationId, String destinationRepository) throws RepositoryException,
                                                                                                                    SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Document copyDocument(Ticket ticket, String sourceId, String destinationId, String destinationRepository) throws RepositoryException,
                                                                                                                  SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Node copyNode(Ticket ticket, String sourceId, String destinationId, String destinationRepository) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public ResourceNode copyResource(Ticket ticket, String resourceId, String directoryId, String destinationRepository) throws RepositoryException,
                                                                                                                      SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Author createAuthor(Ticket ticket, AuthorProperties properties) throws RepositoryException, SecurityException, AuthorAlreadyExistsException {
    
    logger.info("[createAuthor.entrada]:: " + Arrays.asList(new Object[] { ticket, properties }));
    
    String xml = service.createAuthor(xstream.toXML(ticket), xstream.toXML(properties));
    Author author = (Author) xstream.fromXML(xml);
    
    logger.info("[createAuthor.retorna]:: " + author);
    return author;
  }


  public Bookmark createBookmark(Ticket ticket, Bookmark bookmark) throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public Directory createDirectory(Ticket ticket, String name, String description, String parentId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }

  public Directory createDirectory(Ticket ticket, DirectoryProperties properties) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }  
  
  public Document createDocument(Ticket ticket, DocumentProperties docProperties) throws RepositoryException, SecurityException {
    return createDocument(ticket, docProperties, null);
  }
  private Document createDocument(Ticket ticket, DocumentProperties docProperties, MessageContext context) throws RepositoryException, SecurityException {
    
    logger.info("[createDocument.entrada]:: " + Arrays.asList(new Object[] { ticket, docProperties }));

    //-- El contexto lo inyectará XFire
    String xml = service.createDocument(xstream.toXML(ticket), xstream.toXML(docProperties), null);
    Document document = (Document) xstream.fromXML(xml);

    logger.info("[createDocument.retorna]:: " + document);
    return document;

  }


  public List createDocuments(Ticket ticket, List properties) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Favorite createFavorite(Ticket ticket, Favorite favorite) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Repository createRepository(Ticket ticket, String name, String description, User creator) throws RepositoryAlreadyExistsException,
                                                                                                  RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public ResourceNode createResource(Ticket ticket, ResourceNodeProperties properties) throws RepositoryException, SecurityException {
    
    logger.info("[createResource.entrada]:: " + Arrays.asList(new Object[] { ticket, properties }));
    
    String xml = service.createResource(xstream.toXML(ticket), xstream.toXML(properties));
    ResourceNode res = (ResourceNode) xstream.fromXML(xml);
    
    logger.info("[createResource.retorna]:: " + res);
    return res;
    
  }


  public void deleteAuthor(Ticket ticket, String authorId) throws RepositoryException, SecurityException, AuthorNotFoundException {
    // TODO Auto-generated method stub

  }


  public void deleteCategory(Ticket ticket, String categoryId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub

  }


  public void deleteFavorite(Ticket ticket, String favoriteId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub

  }


  public void deleteRepository(Ticket ticket) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub

  }


  public byte[] exportRepository(Ticket ticket) throws RepositoryNotFoundException, RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public List findAllAuthors(Ticket ticket) throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public List findAllCategories(Ticket ticket) throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public List findAllLocks(Ticket ticket) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public List findAllRepositoriesInfo(Ticket ticket) throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public Author findAuthorById(Ticket ticket, String id) throws AuthorNotFoundException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public Author findAuthorByName(Ticket ticket, String name) throws AuthorNotFoundException, RepositoryException {
    logger.info("[findAuthorByName.entrada]:: " + Arrays.asList(new Object[] { ticket, name }));
    
    String xml = service.findAuthorByName(xstream.toXML(ticket), name);
    Author author = (Author) xstream.fromXML(xml);
    
    logger.info("[findAuthorByName.retorna]:: " + author);
    return author;
  }


  public List findCategoriesForNode(Ticket ticket, String nodeId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Category findCategoryById(Ticket ticket, String id) throws CategoryNotFoundException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public Category findCategoryByName(Ticket ticket, String name) throws CategoryNotFoundException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public Directory findDirectory(Ticket ticket, String id) throws RepositoryException, NodeNotFoundException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Document findDocument(Ticket ticket, String id) throws RepositoryException, NodeNotFoundException, SecurityException {
    
    logger.info("[findDocument.entrada]:: " + Arrays.asList(new Object[] { ticket, id }));
    
    String xml = service.findDocument(xstream.toXML(ticket), id);
    Document doc = (Document) xstream.fromXML(xml);
    
    logger.info("[findDocument.retorna]:: " + doc);
    return doc;
    
  }


  public Collection findDocumentsByName(Ticket ticket, String name) throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public Node findNode(Ticket ticket, String id) throws RepositoryException, NodeNotFoundException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Collection findNodeChildren(Ticket ticket, String id) throws RepositoryException, NodeNotFoundException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public List findNodesForCategory(Ticket ticket, String categoryId) throws CategoryNotFoundException, RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public List findNodesForResource(Ticket ticket, String resourceId) throws RepositoryException {
    // TODO Auto-generated method stub
    return null;
  }


  public Repository findRepository(String id, Ticket ticket) throws RepositoryNotFoundException, RepositoryException, SecurityException {

    logger.info("[findRepository.entrada]:: " + Arrays.asList(new Object[] { id, ticket }));

    String xml = service.findRepository(id, xstream.toXML(ticket));
    Repository repository = (Repository) xstream.fromXML(xml);

    logger.info("[findRepository.retorna]:: " + repository);
    return repository;
    
  }


  public List getVersions(Ticket ticket, String documentId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public void importRepository(Ticket ticket, byte[] content, String name) throws RepositoryAlreadyExistsException, RepositoryException, SecurityException {
    // TODO Auto-generated method stub

  }


  public byte[] loadDocumentContent(String docId, Ticket ticket) throws RepositoryException, SecurityException {
    logger.info("[loadDocumentContent.entrada]:: " + Arrays.asList(new Object[] { ticket, docId }));
    
    String xml = service.loadDocumentContent(xstream.toXML(ticket), docId);
    byte[] abDoc = (byte[]) xstream.fromXML(xml);
    
    logger.info("[loadDocumentContent.retorna]:: " + abDoc);
    return abDoc;
  }


  public byte[] loadResourceNodeContent(Ticket ticket, String resourceId) throws RepositoryException, SecurityException {
    logger.info("[loadResourceNodeContent.entrada]:: " + Arrays.asList(new Object[] { ticket, resourceId }));
    
    String xml = service.loadResourceNodeContent(xstream.toXML(ticket), resourceId);
    byte[] abRes = (byte[]) xstream.fromXML(xml);
    
    logger.info("[loadResourceNodeContent.retorna]:: " + abRes);
    return abRes;
  }


  public byte[] loadVersionContent(Ticket ticket, String versionId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Lock lockDocument(Ticket ticket, String docId) throws RepositoryException, SecurityException, ResourceLockedException {
    // TODO Auto-generated method stub
    return null;
  }


  public Directory moveDirectory(Ticket ticket, String sourceId, String destinationId, String destinationRepository) throws RepositoryException,
                                                                                                                    SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Document moveDocument(Ticket ticket, String documentId, String directoryId, String destinationRepository) throws RepositoryException,
                                                                                                                  SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Node moveNode(Ticket ticket, String sourceId, String destinationId, String destinationRepository) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public ResourceNode moveResource(Ticket ticket, String resourceId, String directoryId, String destinationRepository) throws RepositoryException,
                                                                                                                      SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public void removeBookmark(Ticket ticket, String bookmarkId) throws RepositoryException {
    // TODO Auto-generated method stub

  }


  public void removeDirectory(Ticket ticket, String directoryId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub

  }


  public void removeDocument(Ticket ticket, String docId) throws RepositoryException, SecurityException, ResourceLockedException {
    // TODO Auto-generated method stub

  }


  public void removeResourceNode(Ticket ticket, String resourceId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub

  }


  public void removeResourceNode(Ticket ticket, String resourceId, String docId) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub

  }


  public void renameNode(Ticket ticket, String nodeId, String name) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub

  }


  public void saveSession(Ticket ticket) throws RepositoryException {
    logger.info("[saveSession.entrada]:: " + Arrays.asList(new Object[] { ticket }));
    
    service.saveSession(xstream.toXML(ticket));
    
    logger.info("[saveSession.retorna]:: ");
  }


  public void unlockDocument(Ticket ticket, String docId) throws RepositoryException, SecurityException, ResourceLockedException {
    // TODO Auto-generated method stub

  }


  public void updateAuthor(Ticket ticket, String authorId, AuthorProperties properties) throws RepositoryException, SecurityException, AuthorNotFoundException {
    // TODO Auto-generated method stub

  }


  public void updateBookmark(Ticket ticket, Bookmark bookmark) throws RepositoryException {
    // TODO Auto-generated method stub

  }


  public Directory updateDirectory(Ticket ticket, DirectoryProperties directoryProperties) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Document updateDocument(Ticket ticket, DocumentProperties docProperties) throws RepositoryException, SecurityException, ResourceLockedException {
    // TODO Auto-generated method stub
    return null;
  }


  public Repository updateRepository(Ticket ticket, RepositoryProperties repositoryProperties) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public ResourceNode updateResourceNode(Ticket ticket, ResourceNodeProperties properties) throws RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Category createCategory(Ticket ticket, CategoryProperties categoryProperties) throws CategoryAlreadyExistsException,
                                                                                                                   RepositoryException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public void updateCategory(Ticket ticket, String categoryId, CategoryProperties categoryProperties) throws CategoryNotFoundException, RepositoryException,
                                                                                                     SecurityException {
    // TODO Auto-generated method stub
  }


	public boolean isPropertyRegistered(Ticket ticket, String propertyName) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public void registerCustomProperty(Ticket ticket, CustomPropertyDefinition property) throws RepositoryException {
		// TODO Auto-generated method stub
		
	}
	
	
	public void unregisterCustomProperty(Ticket ticket, CustomPropertyDefinition property) throws RepositoryException {
		// TODO Auto-generated method stub
		
	}




}
