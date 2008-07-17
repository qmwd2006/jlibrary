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
package org.jlibrary.core.ws.xfire.security;

import java.net.ConnectException;
import java.util.Arrays;

import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.axis.AxisSecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.core.ws.xfire.AbstractXfireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author martin
 *
 * Axis web service for security management
 */
public class XfSecurityService extends AbstractXfireService implements SecurityService {

  
  /**
   * Loggger de clase
   */
  private static Logger logger = LoggerFactory.getLogger(XfSecurityService.class);
  
  
  private AxisSecurityService service = null;
  
  
  
  
  
  
	/**
	 * Constructor
	 */
	public XfSecurityService() {
    service = new AxisSecurityService();
  }


	

  public void disconnect(String xmlTicket) throws SecurityException {
    
    logger.info("[disconnect.entrada]:: " + Arrays.asList(new Object[] { "<xmlTicket>" }));
    
    try {

      Ticket ticket = (Ticket) xstream.fromXML(xmlTicket);
      service.disconnect(ticket);

    } catch (Throwable e) {

      logger.warn("[disconnect]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof SecurityException) {
        throw (SecurityException) e;
      }

    }
    
    logger.info("[disconnect.retorna]:: ");
  }



  public String login(String xmlCredentials, String name) throws UserNotFoundException, AuthenticationException, SecurityException, ConnectException, RepositoryNotFoundException {
    
    logger.info("[login.entrada]:: " + Arrays.asList(new Object[] { "<xmlCredentials>", name }));
    
    String xmlret = null;
    try {

      Credentials credentials = (Credentials) xstream.fromXML(xmlCredentials); 
      
      Object oRes = service.login(credentials, name);
      xmlret = xstream.toXML(oRes);

    } catch (Throwable e) {

      logger.warn("[login]:: ", e);
      logger.error(e.getMessage(),e);

      if (e instanceof UserNotFoundException) {
        throw (UserNotFoundException) e;
      }else if (e instanceof AuthenticationException) {
        throw (AuthenticationException) e;
      }else if (e instanceof SecurityException) {
        throw (SecurityException) e;
      }else if (e instanceof ConnectException) {
        throw (ConnectException) e;
      }else if (e instanceof RepositoryNotFoundException) {
        throw (RepositoryNotFoundException) e;
      }

    }
    
//    logger.info("[login.retorna]:: xmlret:\n " + xmlret);
    logger.info("[login.retorna]:: ");
    return xmlret;
  }

}
