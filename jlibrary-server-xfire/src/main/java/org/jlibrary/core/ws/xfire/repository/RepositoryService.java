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
package org.jlibrary.core.ws.xfire.repository;

import org.codehaus.xfire.MessageContext;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;

/**
 * @author martin
 *
 * Common interface for a repository implementation
 */
public interface RepositoryService {


  /**
   * Loads a repository given a id
   * 
   * @param id Identificator of the repository
   * @param ticket Ticket with user information
   * 
   * @return Repository repository or null if doesn't exists
   * 
   * @throws RepositoryNotFoundException If the repository id can't be found
   * @throws RepositoryException If repository can't be loaded
   * @throws SecurityException If the user don't have enough permissions 
   * to open the repository 
   */
  public String findRepository(String id, 
                   String xmlTicket) 
                      throws RepositoryNotFoundException, 
                               RepositoryException, 
                               SecurityException;

  

  /**
   * Adds a document to a repository in a given directory
   * 
   * @param ticket Ticket with user information
   * @param docProperties Properties of the document
   * 
   * @return Document New created document
   * 
   * @throws RepositoryException If the document can't be added to the repository
   * @throws SecurityException If the user don't have enough permisssions to add a document to the repository
   */
  public String createDocument( String xmlTicket,
                  String xmlDocProperties, MessageContext context) throws RepositoryException,
                                            SecurityException;
  
  /**
   * Returns an Author given a name
   * 
   * @param Ticket ticket with user information
   * @param name Name of the author
   * 
   * @return Author with the given name
   * 
   * @throws AuthorNotFoundException If an author with that name cannot be 
   * found
   * @throws RepositoryException If the author can't be loaded 
   */
  public String findAuthorByName(String xmlTicket, 
                   String name) throws AuthorNotFoundException,
                               RepositoryException;
  
  
  /**
   * Creates a new author
   * 
   * @param ticket Ticket with user information
   * @param properties Author's properties
   * 
   * @return New created author
   * 
   * @throws RepositoryException If the author can't be created
   * @throws SecurityException If the user doesn't have enough rights to
   * perform this operation
   * @throws AuthorAlreadyExistsException if an author with this name 
   * already exists 
   */
  public String createAuthor(String xmlTicket,
                 String xmlAuthorProperties) 
                      throws RepositoryException,
                             SecurityException,
                             AuthorAlreadyExistsException;
  
  
  /**
   * Loads a document given a id
   * 
   * @param id Identificator of the document
   * 
   * @return Document document or null if doesn't exists
   * 
   * @throws RepositoryException If document can't be loaded
   * @throws NodeNotFoundException if the document' can't be found
   * @throws SecurityException if the user don't have enough permissions to find the document info
   */
  public String findDocument(String xmlTicket, String id) throws RepositoryException, 
                                    NodeNotFoundException,
                                    SecurityException;  
  
  
  /**
   * Forces a session save operation. Ideally this method would be used when 
   * working with autocommit mode set to false
   * 
   * @param ticket Ticket with user information
   * 
   * @throws RepositoryException If the save operation cannot be performed
   */
  public void saveSession(String ticket) throws RepositoryException;



  /**
   * Loads a document contents
   * 
   * @param ticket Ticket with user information
   * @param docId Document to be loaded
   * 
   * @return byte[] Document's contents
   * 
   * @throws RepositoryException If the document's contents can't be loaded
   * @throws SecurityException If the user don't have enough permissions to download the document
   */
  public String loadDocumentContent(String xmlTicket, String docId) throws RepositoryException, 
                                      SecurityException;
  
  
  /**
   * @see org.jlibrary.core.repository.def.ResourcesModule#loadResourceNodeContent(org.jlibrary.core.entities.Ticket, java.lang.String)
   */ 
  public String loadResourceNodeContent(String xmlTicket,
                        String resourceId) throws RepositoryException, 
                                      SecurityException;
  

  /**
   * @see org.jlibrary.core.repository.def.ResourcesModule#createResource(org.jlibrary.core.entities.Ticket, org.jlibrary.core.properties.ResourceNodeProperties)
   */
  public String createResource(String xmlTicket, 
                       String xmlResourceNodeProperties) throws RepositoryException,
                                             SecurityException;
  
  
  /**
   * @see org.jlibrary.core.repository.def.ResourcesModule#addResourceToDocument(org.jlibrary.core.entities.Ticket, org.jlibrary.core.entities.ResourceNode, org.jlibrary.core.entities.Document)
   */
  public void addResourceToDocument(String xmlTicket,
                      String resourceId,
                      String documentId) throws RepositoryException,
                                  SecurityException;
  
}

