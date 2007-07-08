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
package org.jlibrary.core.ws.xfire.security;

import java.net.ConnectException;

import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.UserNotFoundException;


public interface SecurityService {

	public final String SYSTEM_REPOSITORY = "system";
	

  /**
   * Does a login against a jLibrary server and a repository
   * 
   * @param credentials Credentials for performing the login
   * @param name Repository name
   * 
   * @return Ticket A ticket that allows access to the jLibrary server
   * 
   * @throws UserNotFoundException If the users doesn't exists
   * @throws AuthenticationException If the user password is wrong
   * @throws SecurityException If some other internal error happens
   * @throws ConnectException If the server isn't available
   * @throws RepositoryNotFoundException If the repository cannot be found
   */
  public String login(String credentials,
            String name) throws UserNotFoundException,
                      AuthenticationException, 
                      SecurityException,
                      ConnectException,
                      RepositoryNotFoundException;
  
  
  
  
  /**
   * Disconnects the user from the server
   * 
   * @param ticket Ticket with the user information
   * 
   * @throws SecurityException If the user can't be disconnected
   */
  public void disconnect(String ticket) throws SecurityException; 
  
	
}