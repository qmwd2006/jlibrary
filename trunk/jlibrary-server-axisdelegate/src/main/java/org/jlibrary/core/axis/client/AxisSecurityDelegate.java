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
package org.jlibrary.core.axis.client;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jlibrary.core.axis.util.AxisConstants;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.CategoryNode;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.DocumentVersion;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.RepositoryConfig;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Restriction;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.profiles.AxisServerProfile;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.GenericProperties;
import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.properties.RolProperties;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.GroupNotFoundException;
import org.jlibrary.core.security.exception.RoleNotFoundException;
import org.jlibrary.core.security.exception.UserAlreadyExistsException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This service will handle users and groups configuration. It will also 
 * on charge of roles gestion
 */
public class AxisSecurityDelegate implements SecurityService {

	static Logger logger = LoggerFactory.getLogger(AxisSecurityDelegate.class);

	private String endpoint;
	private Service  axisService = new Service(
			new XMLStringProvider(AxisConstants.CLIENT_CONFIG_WSDD));

	private Call call;	
	
	/**
	 * Constructor
	 */
	public AxisSecurityDelegate(AxisServerProfile profile) {
		
		AxisProperties.setProperty(AxisEngine.PROP_DOMULTIREFS, "FALSE");
		try {
			String location = profile.getLocation();
			location = StringUtils.replace(location,"jlibrary://","http://");
			if (!location.endsWith("/")) {
				location+="/";
			}
			endpoint = location + "services/AxisSecurityService";
			
			call = (Call) axisService.createCall();
			call.setTargetEndpointAddress(endpoint);
			//call.setProperty(HTTPConstants.MC_ACCEPT_GZIP, Boolean.TRUE);
			//call.setProperty(HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
			call.setMaintainSession(true);
		} catch (ServiceException e) {
			
			logger.error(e.getMessage(),e);
		}

		registerSerializers(call); 
	}


	public User createUser(Ticket ticket,UserProperties userProperties) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "createUser");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "userProperties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			
			return (User)call.invoke(new Object [] {ticket,userProperties});
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	public User updateUser(Ticket ticket, 
	        			   UserProperties userProperties) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "updateUser");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter("userProperties", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType( XMLType.XSD_ANY );
			
			return (User)call.invoke(new Object [] {ticket, userProperties});
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}

	public User findUserByName(Ticket ticket,
							   String name) throws SecurityException, 
							   					   UserNotFoundException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "findUserByName");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			
			User user = (User)call.invoke(new Object [] {ticket,name});
			return user;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}

	public User findUserById(Ticket ticket,
							 String userId) throws SecurityException, 
							 					   UserNotFoundException {
		
		
		try {       
			call.removeAllParameters();
			
			call.setOperationName( "findUserById");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "userId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			
			User user = (User)call.invoke(new Object [] {ticket,userId});
			return user;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	
	public Collection findAllUsers(Ticket ticket) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "findAllUsers");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.SOAP_ARRAY );
			
			Object[] users = (Object[])call.invoke(new Object[]{ticket});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,users);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	public Collection findAllRoles(Ticket ticket) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "findAllRoles");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.SOAP_ARRAY );
			
			Object[] roles = (Object[])call.invoke(new Object[]{ticket});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,roles);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	public Collection findAllGroups(Ticket ticket) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "findAllGroups");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.SOAP_ARRAY );
			
			Object[] groups = (Object[])call.invoke(new Object[]{ticket});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,groups);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}

	public Rol createRol(Ticket ticket, RolProperties rolProperties) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "createRol");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "rolProperties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			
			Rol rol = (Rol)call.invoke(new Object [] {ticket,rolProperties});
			return rol;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	public Rol updateRol(Ticket ticket,
	        			 RolProperties rolProperties) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "updateRol");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter("rolProperties", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType( XMLType.XSD_ANY );
			
			return (Rol)call.invoke(new Object [] {ticket, rolProperties});
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}

	public Rol findRol(Ticket ticket,
					   String rolId) throws SecurityException, 
					   					    RoleNotFoundException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "findRol");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "rolId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			
			Rol rol = (Rol)call.invoke(new Object [] {ticket,rolId});
			return rol;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}

	public Group createGroup(Ticket ticket, GroupProperties groupProperties) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "createGroup");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "groupProperties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			
			return (Group)call.invoke(new Object [] {ticket,groupProperties});
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}

	public Group updateGroup(Ticket ticket, 
	        				 GroupProperties groupProperties) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "updateGroup");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter("groupProperties", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType( XMLType.XSD_ANY );
			
			return (Group)call.invoke(new Object [] {ticket, groupProperties});
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	public Group findGroupById(Ticket ticket,
							   String groupId) throws SecurityException, 
							   						  GroupNotFoundException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "findGroupById");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "groupId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			
			Group group = (Group)call.invoke(new Object [] {ticket,
															groupId});
			return group;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}

	public void removeUser(Ticket ticket, 
						   String userId) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "removeUser");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "userId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );
			
			call.invoke(new Object [] {ticket,userId});
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	public void removeRol(Ticket ticket, 
						  String rolId) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "removeRol");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "rolId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );
			
			call.invoke(new Object [] {ticket,rolId});
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	public void removeGroup(Ticket ticket, 
							String groupId) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName( "removeGroup");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "groupId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );
			
			call.invoke(new Object [] {ticket,groupId});
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}	
	}

	public Ticket login(Credentials credentials,
						String name) throws UserNotFoundException,
											AuthenticationException, 
											SecurityException,
											ConnectException,
											RepositoryNotFoundException {

		
		try {       
			call.removeAllParameters();

			call.setOperationName( "login");
			call.addParameter( "credentials", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			Ticket ticket = (Ticket)call.invoke(new Object [] {credentials,name});
			return ticket;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("ConnectException") != -1) {
				throw new ConnectException(fault.getFaultString());
			}
			if (fault.getFaultString().indexOf("RepositoryNotFoundException") != -1) {
				throw new RepositoryNotFoundException(fault.getFaultString());				
			}
			throw createSecurityException((AxisFault)e);
		}	
	}
	
	
	public void disconnect(Ticket ticket) throws SecurityException {
		
		
		try {       
			call.removeAllParameters();

			call.setOperationName("disconnect");
		    call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );
			
			call.invoke(new Object [] {ticket});

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw createSecurityException((AxisFault)e);
		}
	}	
	
	public Collection findAllRestrictions(Ticket ticket,
								    	  String nodeId) throws SecurityException {

		try {       
			call.removeAllParameters();

			call.setOperationName( "findAllRestrictions");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "nodeId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			Object[] restrictions = (Object[])
				call.invoke(new Object [] {ticket,nodeId});

			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,restrictions);
			return list;
		} catch (Exception e) {
			throw createSecurityException((AxisFault)e);
		}	
	}	

	private void registerSerializers(Call call) {

        QName qn = new QName( "urn:BeanService", "Repository" );
        call.registerTypeMapping(Repository.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Repository.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Repository.class, qn));  
        qn = new QName( "urn:BeanService", "User" );
        call.registerTypeMapping(User.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(User.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(User.class, qn));  
        qn = new QName( "urn:BeanService", "Group" );
        call.registerTypeMapping(Group.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Group.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Group.class, qn));  
        qn = new QName( "urn:BeanService", "Member" );
        call.registerTypeMapping(Member.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Member.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Member.class, qn));  
        qn = new QName( "urn:BeanService", "Directory" );
        call.registerTypeMapping(Directory.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Directory.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Directory.class, qn));  
        qn = new QName( "urn:BeanService", "Document" );
        call.registerTypeMapping(Document.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Document.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Document.class, qn));  
        qn = new QName( "urn:BeanService", "Node" );
        call.registerTypeMapping(Node.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Node.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Node.class, qn));  
        qn = new QName( "urn:BeanService", "Favorite" );
        call.registerTypeMapping(Favorite.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Favorite.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Favorite.class, qn));  
        qn = new QName( "urn:BeanService", "DocumentMetaData" );
        call.registerTypeMapping(DocumentMetaData.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(DocumentMetaData.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(DocumentMetaData.class, qn));  
        qn = new QName( "urn:BeanService", "Category" );
        call.registerTypeMapping(Category.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Category.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Category.class, qn));  
        qn = new QName( "urn:BeanService", "Rol" );
        call.registerTypeMapping(Rol.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Rol.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Rol.class, qn));  
        qn = new QName( "urn:BeanService", "Category" );
        call.registerTypeMapping(Category.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Category.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Category.class, qn));  
        qn = new QName( "urn:BeanService", "Bookmark" );
        call.registerTypeMapping(Bookmark.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Bookmark.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Bookmark.class, qn));  
        qn = new QName( "urn:BeanService", "Author" );
        call.registerTypeMapping(Author.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Author.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Author.class, qn));  
        qn = new QName( "urn:BeanService", "Note" );
        call.registerTypeMapping(Note.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Note.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Note.class, qn));
        qn = new QName( "urn:BeanService", "ResourceProperties" );
        call.registerTypeMapping(DocumentProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(DocumentProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(DocumentProperties.class, qn));
        qn = new QName( "urn:BeanService", "PropertyDef" );
        call.registerTypeMapping(PropertyDef.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(PropertyDef.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(PropertyDef.class, qn));
        qn = new QName( "urn:BeanService", "Credentials" );
        call.registerTypeMapping(Credentials.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Credentials.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Credentials.class, qn));        
        qn = new QName( "urn:BeanService", "Ticket" );
        call.registerTypeMapping(Ticket.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Ticket.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Ticket.class, qn));        
        qn = new QName( "urn:BeanService", "UserProperties" );
        call.registerTypeMapping(UserProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(UserProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(UserProperties.class, qn));        
        qn = new QName( "urn:BeanService", "GroupProperties" );
        call.registerTypeMapping(GroupProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(GroupProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(GroupProperties.class, qn));        
        qn = new QName( "urn:BeanService", "RolProperties" );
        call.registerTypeMapping(RolProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(RolProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(RolProperties.class, qn));        
        qn = new QName( "urn:BeanService", "CategoryNode" );
        call.registerTypeMapping(CategoryNode.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(CategoryNode.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(CategoryNode.class, qn));        
        qn = new QName( "urn:BeanService", "Restriction" );
        call.registerTypeMapping(Restriction.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Restriction.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Restriction.class, qn));        
        qn = new QName( "urn:BeanService", "DocumentVersion" );
        call.registerTypeMapping(DocumentVersion.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(DocumentVersion.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(DocumentVersion.class, qn));        
        qn = new QName( "urn:BeanService", "ResourceNode" );
        call.registerTypeMapping(Restriction.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(ResourceNode.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(ResourceNode.class, qn));                
        qn = new QName( "urn:BeanService", "ResourceNodeProperties" );
        call.registerTypeMapping(ResourceNodeProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(ResourceNodeProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(ResourceNodeProperties.class, qn));                
        qn = new QName( "urn:BeanService", "DirectoryProperties" );
        call.registerTypeMapping(DirectoryProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(DirectoryProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(DirectoryProperties.class, qn));                
        qn = new QName( "urn:BeanService", "RepositoryProperties" );
        call.registerTypeMapping(RepositoryProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(RepositoryProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(RepositoryProperties.class, qn));                
        qn = new QName( "urn:BeanService", "RepositoryConfig" );
        call.registerTypeMapping(RepositoryConfig.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(RepositoryConfig.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(RepositoryConfig.class, qn));                
        qn = new QName( "urn:BeanService", "AuthorProperties" );
        call.registerTypeMapping(AuthorProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(AuthorProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(AuthorProperties.class, qn));
        qn = new QName( "urn:BeanService", "GenericProperties" );
        call.registerTypeMapping(GenericProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(GenericProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(GenericProperties.class, qn));
        qn = new QName( "urn:BeanService", "CustomPropertyDefinition" );
        call.registerTypeMapping(CustomPropertyDefinition.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(CustomPropertyDefinition.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(CustomPropertyDefinition.class, qn));
        
	}
	
	public SecurityException createSecurityException(AxisFault fault) {
		
		// 	I don't know if there is a better way to do this
		if (fault.getFaultString().indexOf("UserNotFoundException") != -1) {
			return new UserNotFoundException();
		}
		if (fault.getFaultString().indexOf("UserAlreadyExistsException") != -1) {
			return new UserAlreadyExistsException();
		}
		if (fault.getFaultString().indexOf("RoleNotFoundException") != -1) {
			return new RoleNotFoundException();
		}
		if (fault.getFaultString().indexOf("GroupNotFoundException") != -1) {
			return new GroupNotFoundException();
		}
		if (fault.getFaultString().indexOf("AuthenticationException") != -1) {
			return new AuthenticationException(fault.getFaultString());				
		}
		if (fault.getFaultString().contains(SecurityException.NOT_ENOUGH_PERMISSIONS)) {
			return new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
		}
		return new SecurityException(fault);
	}
}
