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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.transport.http.HTTPTransport;
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
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Relation;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.RepositoryConfig;
import org.jlibrary.core.entities.RepositoryInfo;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Restriction;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.profiles.AxisServerProfile;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.GenericProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.search.SearchException;
import org.jlibrary.core.search.SearchHit;
import org.jlibrary.core.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This will be the class in charge of search and index management
 */
public class AxisSearchDelegate implements SearchService {

	static Logger logger = LoggerFactory.getLogger(AxisSecurityDelegate.class);

	private String endpoint;
	private Service  axisService = new Service(
			new XMLStringProvider(AxisConstants.CLIENT_CONFIG_WSDD));

	private Call call;
	
	/**
	 * Constructor
	 */
	public AxisSearchDelegate(AxisServerProfile profile) {
		
		try {
			String location = profile.getLocation();
			location = StringUtils.replace(location,"jlibrary://","http://");

			if (!location.endsWith("/")) {
				location+="/";
			}
			endpoint = location + "services/AxisSearchService";
			
			call = (Call) axisService.createCall();
			call.setTransport(new HTTPTransport());
			//call.setProperty(HTTPConstants.MC_ACCEPT_GZIP, Boolean.TRUE);
			//call.setProperty(HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
		} catch (ServiceException e) {
			logger.error(e.getMessage(),e);
		}

		registerSerializers(call); 
	}

	public Collection search(Ticket ticket,
							 String phrase, 
							 String searchType) 
							 throws SearchException {
		
		try {       
			call.removeAllParameters();

			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "search");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "phrase", XMLType.XSD_STRING, ParameterMode.IN);
		    call.addParameter( "searchType", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType( XMLType.XSD_ANY );
			
			Object[] results = (Object[])call.invoke(new Object [] {ticket,
																	phrase,
																	searchType});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,results);
			return list;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SearchException(e);
		}		
	}
	
	
	public Collection search(Ticket ticket,
							 String xpathQuery) throws SearchException {
		
		try {       
			call.removeAllParameters();

			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "search");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "xpathQuery", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType( XMLType.XSD_ANY );
			
			Object[] results = (Object[])call.invoke(new Object [] {ticket,
																	xpathQuery});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,results);
			return list;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SearchException(e);
		}		
	}
	
	
	private void registerSerializers(Call call) {

        QName qn = new QName( "urn:BeanService", "SearchHit" );
        call.registerTypeMapping(SearchHit.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(SearchHit.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(SearchHit.class, qn));  
        qn = new QName( "urn:BeanService", "Repository" );
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
        qn = new QName( "urn:BeanService", "RepositoryInfo" );
        call.registerTypeMapping(RepositoryInfo.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(RepositoryInfo.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(RepositoryInfo.class, qn));        
        qn = new QName( "urn:BeanService", "CategoryNode" );
        call.registerTypeMapping(CategoryNode.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(CategoryNode.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(CategoryNode.class, qn));        
        qn = new QName( "urn:BeanService", "DocumentVersion" );
        call.registerTypeMapping(DocumentVersion.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(DocumentVersion.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(DocumentVersion.class, qn));        
        qn = new QName( "urn:BeanService", "Relation" );
        call.registerTypeMapping(Relation.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Relation.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Relation.class, qn));        
        qn = new QName( "urn:BeanService", "CategoryProperties" );
        call.registerTypeMapping(CategoryProperties.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(CategoryProperties.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(CategoryProperties.class, qn));
        qn = new QName( "urn:BeanService", "Lock" );
        call.registerTypeMapping(Lock.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Lock.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Lock.class, qn));                
        qn = new QName( "urn:BeanService", "Restriction" );
        call.registerTypeMapping(Restriction.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Restriction.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Restriction.class, qn));                
        qn = new QName( "urn:BeanService", "ResourceNode" );
        call.registerTypeMapping(Restriction.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(ResourceNode.class, qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(ResourceNode.class, qn));                
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
}
