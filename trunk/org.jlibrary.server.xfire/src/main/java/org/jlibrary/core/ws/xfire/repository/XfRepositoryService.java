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
package org.jlibrary.core.ws.xfire.repository;


import java.io.IOException;
import java.util.Arrays;

import org.codehaus.xfire.MessageContext;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.ws.xfire.AbstractXfireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author martin
 * 
 * Axis web service for repository management. It doesn't implements
 * RepositoryService interface because the method signatures don't match. This
 * is no problem because RepositoryService interface is implemented by
 * WSRepositoryServiceImpl.
 */
public class XfRepositoryService extends AbstractXfireService implements RepositoryService {


  /**
   * Loggger de clase
   */
  private static Logger logger = LoggerFactory.getLogger(XfRepositoryService.class);


  private org.jlibrary.core.repository.RepositoryService service = null;



  /**
   * Constructor
   */
  public XfRepositoryService() {
    service = JLibraryServiceFactory.getInstance(new LocalServerProfile()).getRepositoryService();
  }




  public String createDocument(String xmlTicket, String xmlDocumentProperties, MessageContext context) throws RepositoryException, SecurityException {

    logger.info("[createDocument.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>", "<xmlDocumentProperties>" }));

    String xmlret = null;
    try {

      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);
      DocumentProperties docProperties = (DocumentProperties) xstream.fromXML(xmlDocumentProperties);

      // -- Recogemos el contenido del documento
      try {

        byte[] abDocument = getDocumentContentInContext(context);

        if (abDocument != null) {
          docProperties.setProperty(DocumentProperties.DOCUMENT_CONTENT, abDocument);
        }

      } catch (IOException e) {
        // TODO Auto-generated catch block
        logger.error(e.getMessage(),e);
      } catch (PropertyNotFoundException e) {
        // TODO Auto-generated catch block
        logger.error(e.getMessage(),e);
      } catch (InvalidPropertyTypeException e) {
        // TODO Auto-generated catch block
        logger.error(e.getMessage(),e);
      }


      logger.debug("[createDocument]:: Obteniendo instancia del servicio JLibrary local");
      logger.debug("[createDocument]:: service: " + service);

      logger.debug("[createDocument]:: Invocando a la creacion del documento.");
      Object oRes = service.createDocument(ticket, docProperties);

      logger.debug("[createDocument]:: oRes: " + oRes);

      logger.debug("[createDocument]:: Convirtiendo a XML...");
      xmlret = xstream.toXML(oRes);


    } catch (Throwable e) {

      logger.warn("[createDocument]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof RepositoryException) {
        throw (RepositoryException) e;
      }
      else if (e instanceof SecurityException) {
        throw (SecurityException) e;
      }

    }

//    logger.info("[createDocument.retorna]:: xmlret:\n " + xmlret);
    logger.info("[createDocument.retorna]:: ");
    return xmlret;

  }


  public String findRepository(String id, String xmlTicket) throws RepositoryNotFoundException, RepositoryException, SecurityException {

    logger.info("[findRepository.entrada]:: " + Arrays.asList(new Object[] { id, "<xmlTicket>" }));

    String xmlret = null;

    try {
      
      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);
      Object oRes = service.findRepository(id, ticket);

      xmlret = xstream.toXML(oRes);

    } catch (Throwable e) {

      logger.warn("[findRepository]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof RepositoryException) {
        throw (RepositoryException) e;
        
      } else if (e instanceof RepositoryNotFoundException) {
        throw (RepositoryNotFoundException) e;
        
      } else if (e instanceof SecurityException) {
        throw (SecurityException) e;
      }

    }
    
//    logger.info("[findRepository.retorna]:: xmlret:\n " + xmlret);
    logger.info("[findRepository.retorna]:: ");
    return xmlret;

  }


  public String createAuthor(String xmlTicket, String xmlAuthorProperties) throws RepositoryException, SecurityException, AuthorAlreadyExistsException {

    logger.info("[createAuthor.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>", "<xmlAuthorProperties>" }));

    String xmlret = null;
    try {
      
      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);
      AuthorProperties properties = (AuthorProperties) xstream.fromXML(xmlAuthorProperties);

      Object oRes = service.createAuthor(ticket, properties);
      xmlret = xstream.toXML(oRes);

    } catch (Throwable e) {

      logger.warn("[createAuthor]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof RepositoryException) {
        throw (RepositoryException) e;

      }
      else if (e instanceof SecurityException) {
        throw (SecurityException) e;

      }
      else if (e instanceof AuthorAlreadyExistsException) {
        throw (AuthorAlreadyExistsException) e;
      }

    }

//    logger.info("[createAuthor.retorna]:: xmlret:\n " + xmlret);
    logger.info("[createAuthor.retorna]:: ");
    return xmlret;

  }




  public String findAuthorByName(String xmlTicket, String name) throws AuthorNotFoundException, RepositoryException {

    logger.info("[findAuthorByName.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>", "<name>" }));

    Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);

    String xmlret = null;
    try {

      Object oRes = service.findAuthorByName(ticket, name);
      xmlret = xstream.toXML(oRes);

    } catch (Throwable e) {

      logger.warn("[findAuthorByName]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof AuthorNotFoundException) {
        throw (AuthorNotFoundException) e;
      }
      else if (e instanceof RepositoryException) {
        throw (RepositoryException) e;
      }

    }

//    logger.info("[createAuthor.retorna]:: xmlret:\n " + xmlret);
    logger.info("[findAuthorByName.retorna]:: ");
    return xmlret;
  }




  public void saveSession(String xmlTicket) throws RepositoryException {

    logger.info("[saveSession.entrada]:: " + Arrays.asList(new Object[] { xmlTicket }));

    try {

      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);
      service.saveSession(ticket);

    } catch (Throwable e) {

      logger.warn("[saveSession]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof RepositoryException) {
        throw (RepositoryException) e;
      }

    }

    logger.info("[saveSession.retorna]:: ");

  }




  public String findDocument(String xmlTicket, String id) throws RepositoryException, NodeNotFoundException, SecurityException {

    logger.info("[findDocument.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>", id }));

    String xmlret = null;

    try {

      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);

      Object oRes = service.findDocument(ticket, id);
      
      xmlret = xstream.toXML(oRes);

    } catch (Throwable e) {

      logger.warn("[findDocument]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof NodeNotFoundException) {
        throw (NodeNotFoundException) e;

      }
      else if (e instanceof RepositoryException) {
        throw (RepositoryException) e;

      }
      else if (e instanceof SecurityException) {
        throw (SecurityException) e;

      }

    }

    logger.info("[findDocument.retorna]:: ");
    return xmlret;

  }




  public String loadDocumentContent(String xmlTicket, String docId) throws RepositoryException, SecurityException {
    logger.info("[loadDocumentContent.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>", docId }));

    String xmlret = null;

    try {

      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);

      Object oRes = service.loadDocumentContent(docId, ticket);
      
      xmlret = xstream.toXML(oRes);

    } catch (Throwable e) {

      logger.warn("[loadDocumentContent]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof RepositoryException) {
        throw (RepositoryException) e;

      }
      else if (e instanceof SecurityException) {
        throw (SecurityException) e;

      }

    }

    logger.info("[loadDocumentContent.retorna]:: ");
    return xmlret;
  }




  public String loadResourceNodeContent(String xmlTicket, String resourceId) throws RepositoryException, SecurityException {
    logger.info("[loadResourceNodeContent.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>", resourceId }));

    String xmlret = null;

    try {

      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);

      Object oRes = service.loadResourceNodeContent(ticket, resourceId);
      
      xmlret = xstream.toXML(oRes);

    } catch (Throwable e) {

      logger.warn("[loadResourceNodeContent]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof RepositoryException) {
        throw (RepositoryException) e;

      }
      else if (e instanceof SecurityException) {
        throw (SecurityException) e;

      }

    }

    logger.info("[loadResourceNodeContent.retorna]:: ");
    return xmlret;
  }




  public void addResourceToDocument(String xmlTicket, String resourceId, String documentId) throws RepositoryException, SecurityException {
    
    logger.info("[addResourceToDocument.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>", resourceId, documentId }));

    try {

      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);

      service.addResourceToDocument(ticket, resourceId, documentId);


    } catch (Throwable e) {

      logger.warn("[addResourceToDocument]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof RepositoryException) {
        throw (RepositoryException) e;

      }
      else if (e instanceof SecurityException) {
        throw (SecurityException) e;

      }

    }

    logger.info("[addResourceToDocument.retorna]:: ");
    
  }




  public String createResource(String xmlTicket, String xmlResourceNodeProperties) throws RepositoryException, SecurityException {

    logger.info("[createResource.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>", "<xmlResourceNodeProperties>" }));
    
    String xmlret = null;

    try {

      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);
      ResourceNodeProperties rnp = (ResourceNodeProperties) xstream.fromXML(xmlResourceNodeProperties);  

      Object oRes = service.createResource(ticket, rnp);
      
      xmlret = xstream.toXML(oRes);

    } catch (Throwable e) {

      logger.warn("[createResource]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof RepositoryException) {
        throw (RepositoryException) e;

      }
      else if (e instanceof SecurityException) {
        throw (SecurityException) e;

      }

    }

    logger.info("[createResource.retorna]:: ");
    return xmlret;
    
  }




}
