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


import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.RolProperties;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.GroupAlreadyExistsException;
import org.jlibrary.core.security.exception.GroupNotFoundException;
import org.jlibrary.core.security.exception.RoleAlreadyExistsException;
import org.jlibrary.core.security.exception.RoleNotFoundException;
import org.jlibrary.core.security.exception.UserAlreadyExistsException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.core.ws.xfire.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author martin
 * 
 * This service will handle users and groups configuration. It will also on
 * charge of roles gestion
 */
public class WSSecurityServiceImpl implements org.jlibrary.core.security.SecurityService {


  static Logger logger = LoggerFactory.getLogger(WSSecurityServiceImpl.class);

  // Singleton
  private static HashMap services;

  private String endpoint;

  private XStream xstream = new XStream();

  private SecurityService service = null;


  /**
   * Constructor
   */
  private WSSecurityServiceImpl(ServerProfile profile) {

    logger.info("[WSSecurityServiceImpl.entrada]:: Creando servicio XFIRE: "  + Arrays.asList(new Object[] { profile }));
    
    try {

      String location = profile.getLocation();
      location = StringUtils.replace(location, "jlibrary://", "http://");

      if (!location.endsWith("/")) {
        location += "/";
      }
      endpoint = location + "services/XfSecurityService";

      Service serviceModel = new ObjectServiceFactory().create(SecurityService.class);
      service = (SecurityService) new XFireProxyFactory().create(serviceModel, endpoint);

    } catch (MalformedURLException e) {
      logger.error(e.getMessage(),e);
    }

  }


  public Ticket login(Credentials credentials, String name) throws UserNotFoundException, AuthenticationException, SecurityException, ConnectException,
                                                           RepositoryNotFoundException {

    logger.info("[login.entrada]:: " + Arrays.asList(new Object[] { credentials, name }));

    String xml = service.login(xstream.toXML(credentials), name);
    Ticket ticket = (Ticket) xstream.fromXML(xml);
    
    logger.info("[login.retorna]:: " + ticket);
    return ticket;
    
  }


  public void disconnect(Ticket ticket) throws SecurityException {

    logger.info("[disconnect.entrada]:: " + Arrays.asList(new Object[] { ticket }));
    
    service.disconnect(xstream.toXML(ticket));
    
    logger.info("[disconnect.retorna]:: ");
    
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
  public static WSSecurityServiceImpl getInstance(ServerProfile profile) {

    if (services == null) {
      services = new HashMap();
    }

    WSSecurityServiceImpl service = (WSSecurityServiceImpl) services.get(profile);
    if (service == null) {
      service = new WSSecurityServiceImpl(profile);
      services.put(profile, service);
    }

    return service;
  }


  public Group createGroup(Ticket ticket, GroupProperties groupProperties) throws GroupAlreadyExistsException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Rol createRol(Ticket ticket, RolProperties rolProperties) throws RoleAlreadyExistsException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public User createUser(Ticket ticket, UserProperties userProperties) throws UserAlreadyExistsException, SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Collection findAllGroups(Ticket ticket) throws SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Collection findAllRestrictions(Ticket ticket, String nodeId) throws SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Collection findAllRoles(Ticket ticket) throws SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Collection findAllUsers(Ticket ticket) throws SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Group findGroupById(Ticket ticket, String id) throws SecurityException, GroupNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }


  public Rol findRol(Ticket ticket, String rolId) throws SecurityException, RoleNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }


  public User findUserById(Ticket ticket, String id) throws SecurityException, UserNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }


  public User findUserByName(Ticket ticket, String name) throws SecurityException, UserNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }


  public void removeGroup(Ticket ticket, String groupId) throws SecurityException, GroupNotFoundException {
    // TODO Auto-generated method stub

  }


  public void removeRol(Ticket ticket, String rolId) throws SecurityException, RoleNotFoundException {
    // TODO Auto-generated method stub

  }


  public void removeUser(Ticket ticket, String userId) throws SecurityException, UserNotFoundException {
    // TODO Auto-generated method stub

  }


  public Group updateGroup(Ticket ticket, GroupProperties groupProperties) throws SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public Rol updateRol(Ticket ticket, RolProperties rolProperties) throws SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


  public User updateUser(Ticket ticket, UserProperties userProperties) throws SecurityException {
    // TODO Auto-generated method stub
    return null;
  }


}
