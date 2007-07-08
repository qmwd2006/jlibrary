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
package org.jlibrary.core.axis.client;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
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
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.profiles.AxisServerProfile;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.GenericProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RecentlyRemovedRepositoryException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 * 
 * This will be the class in charge of repository management
 */
public class AxisRepositoryDelegate implements RepositoryService {

	static Logger logger = LoggerFactory.getLogger(AxisSecurityDelegate.class);

	private String endpoint;
	private Service  axisService = new Service(
			new XMLStringProvider(AxisConstants.CLIENT_CONFIG_WSDD));

	private Call call;
	
	/**
	 * Constructor
	 */
	public AxisRepositoryDelegate(AxisServerProfile profile) {
		
		try {
			
			String location = profile.getLocation();
			location = StringUtils.replace(location,"jlibrary://","http://");
			if (!location.endsWith("/")) {
				location+="/";
			}
			endpoint = location + "services/AxisRepositoryService";
			
			call = (Call) axisService.createCall();
			call.setTransport(new HTTPTransport());
			//call.setProperty(HTTPConstants.MC_ACCEPT_GZIP, Boolean.TRUE);
			//call.setProperty(HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
			call.setMaintainSession(true);
		} catch (ServiceException e) {
			logger.error(e.getMessage(),e);
		}

		registerSerializers(call); 
	}


	public Repository createRepository(Ticket ticket,
	        						   String name,
									   String description,
									   User creator) 
										throws RepositoryAlreadyExistsException,
											   RepositoryException,
									   		   SecurityException {
		
		try {       
			call.removeAllParameters();
			
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createRepository");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN);
		    call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "description", XMLType.XSD_STRING, ParameterMode.IN);
		    call.addParameter( "creator", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType( XMLType.XSD_ANY );

			Repository r = (Repository)call.invoke( 
					new Object [] {ticket,name,description,creator});
			return r;
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("RepositoryAlreadyExistsException") != -1) {
				throw new RepositoryAlreadyExistsException();
			} else if (fault.getFaultString().indexOf("RecentlyRemovedRepositoryException") != -1) {
				throw new RecentlyRemovedRepositoryException();
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}	
	}


	public Directory createDirectory(Ticket ticket,
	        						 String name,
									 String description,
									 String parentId) throws RepositoryException,
									 					     SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createDirectory");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN);
		    call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "description", XMLType.XSD_STRING, ParameterMode.IN);
		    call.addParameter( "parent", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType( XMLType.XSD_ANY );

			Directory dir = (Directory)call.invoke( 
								new Object [] { ticket,name,description,parentId});
			return dir;
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}			
	}

	public Directory createDirectory(Ticket ticket,
	        						 DirectoryProperties properties) throws RepositoryException,
									 					     				SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createDirectory");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN);
		    call.addParameter( "properties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			Directory dir = (Directory)call.invoke( 
								new Object [] { ticket,properties});
			return dir;
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}			
	}
	public void removeDirectory(Ticket ticket, String directoryId) throws RepositoryException,
																		  SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "removeDirectory");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "directoryId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );

			call.invoke( new Object [] {ticket,directoryId});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}	
	}


	public List findAllRepositoriesInfo(Ticket ticket) throws RepositoryException  {
	
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findAllRepositoriesInfo" );
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.setReturnType( XMLType.XSD_ANY );

			Object[] o = (Object[])call.invoke( new Object [] {ticket});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}
	

	public Repository findRepository(String id, 
									 Ticket ticket) 
											throws RepositoryNotFoundException,
												   RepositoryException, 
												   SecurityException  {
	
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findRepository" );
		    call.addParameter( "id", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY);
			Repository repository = (Repository)call.invoke( new Object [] {id,ticket});
			return repository;
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf(
					"RepositoryNotFoundException") != -1) {
				throw new RepositoryNotFoundException(fault.getFaultString());
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}	

	public Document findDocument(Ticket ticket, 
								 String id) throws RepositoryException, 
								 				   NodeNotFoundException,
								 				   SecurityException {

        try {
            call.removeAllParameters();

            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName("findDocument");
            call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
            call.addParameter("id", XMLType.XSD_STRING, ParameterMode.IN);

            call.setReturnType(XMLType.XSD_ANY);
            Document document = (Document) call.invoke(new Object[] { ticket,
                    id });
            return document;
        } catch (Exception e) {
            AxisFault fault = (AxisFault) e;
            // I don't know if there is a better way to do this
            if (fault.getFaultString().indexOf("NodeNotFoundException") != -1) {
                throw new NodeNotFoundException(fault.getFaultString());

            }
            if (fault.getFaultString().indexOf("SecurityException") != -1) {
                throw new SecurityException(fault.getFaultString());

            } else {
                throw new RepositoryException(fault.getFaultString());
            }
        }
    }

	public Node findNode(Ticket ticket, 
						 String id)
    							throws RepositoryException, 
    								   NodeNotFoundException,
    								   SecurityException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("findNode");
			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("id", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);
    		Node node= (Node) call.invoke(new Object[] { ticket,id });
    		return node;
		} catch (Exception e) {
			AxisFault fault = (AxisFault) e;
			// 	I don't know if there is a better way to do this
			if (fault.getFaultString().indexOf("NodeNotFoundException") != -1) {
				throw new NodeNotFoundException(fault.getFaultString());
			}
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public Directory findDirectory(Ticket ticket, 
								   String id) throws RepositoryException, 
								   					 NodeNotFoundException,
													 SecurityException {

		try {
		    call.removeAllParameters();
		
		    call.setTargetEndpointAddress(new java.net.URL(endpoint));
		    call.setOperationName("findDirectory");
		    call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
		    call.addParameter("id", XMLType.XSD_STRING, ParameterMode.IN);
		
		    call.setReturnType(XMLType.XSD_ANY);
		    Directory directory = (Directory)call.invoke(new Object[] { ticket,id });
		    return directory;
		} catch (Exception e) {
		    AxisFault fault = (AxisFault) e;
		    // I don't know if there is a better way to do this
		    if (fault.getFaultString().indexOf("NodeNotFoundException") != -1) {
		        throw new NodeNotFoundException(fault.getFaultString());
		
		    }
		    if (fault.getFaultString().indexOf("SecurityException") != -1) {
		        throw new SecurityException(fault.getFaultString());
		
		    } else {
		        throw new RepositoryException(fault.getFaultString());
		    }
		}
	}
        
	public void deleteRepository(Ticket ticket) throws RepositoryException,
													   SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "deleteRepository");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );

			call.invoke( new Object [] {ticket});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}	

	public Directory copyDirectory( Ticket ticket,
							   	    String sourceId, 
									String destinationId,
									String destinationRepository) throws RepositoryException,
																 SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "copyDirectory" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "sourceId", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "destinationId", XMLType.XSD_STRING, ParameterMode.IN );
			call.addParameter("destinationRepository", XMLType.XSD_STRING, ParameterMode.IN);
			
			call.setReturnType( XMLType.XSD_ANY );

			Directory directory = (Directory)call.invoke(new Object [] {ticket,
									   								    sourceId,
																	    destinationId,
																	    destinationRepository});
			return directory;
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
		
	}

	public Document copyDocument( Ticket ticket,
							  	  String sourceId, 
								  String destinationId,
								  String destinationRepository) throws RepositoryException,
								  							   		   SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "copyDocument" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "sourceId", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "destinationId", XMLType.XSD_STRING, ParameterMode.IN );
			
			call.setReturnType( XMLType.XSD_ANY );

			Document document = (Document)call.invoke(new Object [] {ticket,
									   								 sourceId,
																	 destinationId,
																	 destinationRepository});
			return document;
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
		
	}


	public Directory moveDirectory( Ticket ticket,
							   		String sourceId, 
									String destinationId,
									String destinationRepository) throws RepositoryException,
																 SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "moveDirectory" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "sourceId", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "destinationId", XMLType.XSD_STRING, ParameterMode.IN );
			call.addParameter("destinationRepository", XMLType.XSD_STRING, ParameterMode.IN);
			
			call.setReturnType( XMLType.XSD_ANY );

			Directory directory = (Directory)call.invoke(new Object [] {ticket,
									   								    sourceId,
																	    destinationId,
																	    destinationRepository});
			return directory;
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
		
	}
	
	public Document moveDocument( Ticket ticket,
								  String documentId, 
								  String directoryId,
								  String destinationRepository) throws RepositoryException,
								  							 SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "moveDocument" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "documentId", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "directoryId", XMLType.XSD_STRING, ParameterMode.IN );
			call.addParameter("destinationRepository", XMLType.XSD_STRING, ParameterMode.IN);
			
			call.setReturnType( XMLType.XSD_ANY );

			return (Document)call.invoke(new Object [] {ticket,documentId,directoryId,destinationRepository});
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public byte[] loadDocumentContent(String docId, Ticket ticket) throws RepositoryException,
																		  SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "loadDocumentContent" );
			
		    call.addParameter( "docId", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			
			call.setReturnType( XMLType.SOAP_BASE64BINARY);

			call.invoke(new Object [] {docId, ticket});
			
			return extractAttachment();
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	public Document createDocument( Ticket ticket,
									DocumentProperties docProperties) throws RepositoryException,
											 									 SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createDocument" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "docProperties", XMLType.XSD_ANY, ParameterMode.IN );
			
			call.setReturnType( XMLType.XSD_ANY );

			// Change the binary property for an attachment
			byte[] content = (byte[])docProperties.getProperty(
					DocumentProperties.DOCUMENT_CONTENT).getValue();
			if (content != null) {
				docProperties.setProperty(
						DocumentProperties.DOCUMENT_CONTENT,null);
			    DataHandler handler = new DataHandler(new ByteArrayDataSource(
			    		content,"application/octet-stream"));
			    call.addAttachmentPart(handler);
			}
			
			Document document = (Document)call.invoke(
					new Object [] {ticket,docProperties});
			return document;
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	public List createDocuments( Ticket ticket,
								 List properties) throws RepositoryException,
					 									 SecurityException {

		try {       
			call.removeAllParameters();

			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createDocuments" );

			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "properties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			
			// TODO: Add N attachments
			Iterator it = properties.iterator();
			while (it.hasNext()) {
				DocumentProperties props = (DocumentProperties) it.next();
				byte[] content = (byte[])props.getProperty(
							DocumentProperties.DOCUMENT_CONTENT).getValue();
				if (content != null) {
					props.setProperty(DocumentProperties.DOCUMENT_CONTENT,null);
					DataHandler handler = new DataHandler(
							new ByteArrayDataSource(
									content,"application/octet-stream"));
					call.addAttachmentPart(handler);
				}
			}
			
			Object[] o = (Object[])call.invoke( new Object [] {ticket,properties});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;
		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}	
	
	public void removeDocument(Ticket ticket,
	        				   String docId) throws RepositoryException,
											 		SecurityException,
											 		ResourceLockedException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "removeDocument" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "docId", XMLType.XSD_STRING, ParameterMode.IN );
			
			call.setReturnType( XMLType.AXIS_VOID );
			
			call.invoke(new Object [] {ticket,docId});
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("ResourceLockedException") != -1) {
				// Create a virtual lock for info
				Lock lock = lookForLock(ticket, docId);
				throw new ResourceLockedException(lock);
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
		
	}

	public Document updateDocument(Ticket ticket,
							   	   DocumentProperties docProperties) throws RepositoryException, 
							   						 	 					SecurityException,
							   						 	 					ResourceLockedException {

		//DefaultRepositoryServiceImpl.getInstance().updateDocument(properties,docId,docProperties);
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "updateDocument");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "docProperties", XMLType.XSD_ANY, ParameterMode.IN );
		    
			call.setReturnType( XMLType.XSD_ANY );

			// Change the binary property for an attachment
			byte[] content = (byte[])docProperties.getProperty(
					DocumentProperties.DOCUMENT_CONTENT).getValue();
			if (content != null) {
				docProperties.setProperty(
						DocumentProperties.DOCUMENT_CONTENT,null);
			    DataHandler handler = new DataHandler(new ByteArrayDataSource(
			    		content,"application/octet-stream"));
			    call.addAttachmentPart(handler);
			}			
			return (Document)call.invoke( new Object [] {ticket,docProperties});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("ResourceLockedException") != -1) {
				// Create a virtual lock for info
				String docId = docProperties.getProperty(
						DocumentProperties.DOCUMENT_ID).getValue().toString();
				Lock lock = lookForLock(ticket, docId);				
				throw new ResourceLockedException(lock);
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
			
	}
	
	public Directory updateDirectory(Ticket ticket,
									 DirectoryProperties directoryProperties) throws RepositoryException,
													  								 SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "updateDirectory");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "directoryProperties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			return (Directory)call.invoke( new Object [] {ticket,directoryProperties});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}	
	}
	
	public Repository updateRepository(Ticket ticket,
									   RepositoryProperties repositoryProperties) throws RepositoryException, 
									   											 	     SecurityException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("updateRepository");
			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("RepositoryProperties", XMLType.XSD_ANY,ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			return (Repository) call.invoke(new Object[] { ticket,repositoryProperties });

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());

			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}	
	
	public void renameNode(Ticket ticket,
						   String nodeId, 
						   String name) throws RepositoryException, 
						   					   SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "renameNode" );
			
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "nodeId", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN);
			
			call.setReturnType( XMLType.AXIS_VOID );

			call.invoke( new Object [] {ticket, nodeId, name});
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public List findAllAuthors(Ticket ticket) throws RepositoryException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findAllAuthors" );
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			
			call.setReturnType( XMLType.XSD_ANY );

			Object[] o = (Object[])call.invoke( new Object [] {ticket});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}
	
	public Author findAuthorByName(Ticket ticket,
								   String name) throws AuthorNotFoundException,
								   					   RepositoryException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findAuthorByName" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN );
			
			call.setReturnType( XMLType.XSD_ANY );

			Author author = (Author)call.invoke( new Object [] {ticket,name});
			return author;
		} catch (Exception e) {
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("AuthorNotFoundException") != -1) {
				throw new AuthorNotFoundException();
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public Author findAuthorById(Ticket ticket,
								 String id) throws AuthorNotFoundException,
								 				   RepositoryException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findAuthorById" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "id", XMLType.XSD_STRING, ParameterMode.IN );
			
			call.setReturnType( XMLType.XSD_ANY );

			Author author = (Author)call.invoke( new Object [] {ticket,id});
			return author;
		} catch (Exception e) {
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("AuthorNotFoundException") != -1) {
				throw new AuthorNotFoundException();
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public List findAllCategories(Ticket ticket) throws RepositoryException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findAllCategories");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			Object[] o = (Object[])call.invoke( new Object [] {ticket});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}
	
	public Author createAuthor(Ticket ticket,
							   AuthorProperties properties) 
										throws RepositoryException,
			   						   		   SecurityException,
			   						   		   AuthorAlreadyExistsException {

		try {       
			call.removeAllParameters();

			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createAuthor");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "properties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			Author author = (Author)call.invoke( 
					new Object [] {ticket,properties});

			return author;

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("AuthorAlreadyExistsException") != -1) {
				throw new AuthorAlreadyExistsException();
			}else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public void updateAuthor(Ticket ticket,
			   				 String authorId,
							 AuthorProperties properties) 
											throws RepositoryException,
					   			  				   SecurityException,
					   			  				   AuthorNotFoundException {

		try {       
			call.removeAllParameters();

			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "updateAuthor");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "authorId", XMLType.XSD_STRING, ParameterMode.IN );
			call.addParameter( "properties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );
			call.invoke( new Object [] {ticket,authorId,properties});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("AuthorNotFoundException") != -1) {
				throw new AuthorNotFoundException();
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	public void deleteAuthor(Ticket ticket,
							 String authorId) throws RepositoryException,
	   			  				   					 SecurityException,
	   			  				   					 AuthorNotFoundException {

		try {       
			call.removeAllParameters();

			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "deleteAuthor");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "authorId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );
			call.invoke( new Object [] {ticket,authorId});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("AuthorNotFoundException") != -1) {
				throw new AuthorNotFoundException();
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}	
	
	public Category createCategory(Ticket ticket,
	        					   CategoryProperties categoryProperties) 
										throws CategoryAlreadyExistsException,
											   RepositoryException,
	        					   			   SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createCategory");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "categoryProperties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			Category category = (Category)call.invoke( new Object [] {ticket,categoryProperties});
			
			return category;

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else if (fault.getFaultString().indexOf("CategoryAlreadyExistsException") != -1) {
				throw new CategoryAlreadyExistsException();
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	

	public Category findCategoryById(Ticket ticket,
									 String id) 
											throws CategoryNotFoundException,
												   RepositoryException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findCategoryById");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );			
		    call.addParameter( "categoryId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			return (Category)call.invoke( new Object [] {ticket,id});

		} catch (Exception e) {
//			 I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("CategoryNotFoundException") != -1) {
				throw new CategoryNotFoundException();
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	

	public Category findCategoryByName(Ticket ticket, 
									   String name) 
											throws CategoryNotFoundException,
												   RepositoryException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findCategoryByName");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );			
		    call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			return (Category)call.invoke( new Object [] {ticket,name});

		} catch (Exception e) {
//			 I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("CategoryNotFoundException") != -1) {
				throw new CategoryNotFoundException();
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public List findCategoriesForNode(Ticket ticket, String nodeId) throws RepositoryException,
																		   SecurityException {
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findCategoriesForNode");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "nodeId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			Object[] o = (Object[])call.invoke( new Object [] {ticket,nodeId});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	public List findNodesForCategory(Ticket ticket, 
									 String categoryId) 
											throws CategoryNotFoundException,
												   RepositoryException {
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findNodesForCategory");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "categoryId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			Object[] o = (Object[])call.invoke( new Object [] {ticket,categoryId});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("CategoryNotFoundException") != -1) {
				throw new CategoryNotFoundException();
			}
			throw new RepositoryException(e);
		}
	}

	public void deleteCategory(Ticket ticket, String categoryId) throws RepositoryException,
																		SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "deleteCategory");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "categoryId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );

			call.invoke( new Object [] {ticket,categoryId});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	

	public Category updateCategory(Ticket ticket,
	        				       String categoryId,
							       CategoryProperties categoryProperties) 
											throws CategoryNotFoundException,
												   RepositoryException,
												   SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "updateCategory");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "categoryId", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "categoryProperties", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			return (Category)call.invoke( new Object [] {ticket,categoryId,categoryProperties});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("CategoryNotFoundException") != -1) {
					throw new CategoryNotFoundException();
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public Favorite createFavorite(Ticket ticket, 
								   Favorite favorite) throws RepositoryException,
															 SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createFavorite");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "favorite", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			return (Favorite)call.invoke( new Object [] {ticket,favorite});
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	
	}
	
	public void deleteFavorite(Ticket ticket, 
							   String favoriteId) throws RepositoryException,
														 SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "deleteFavorite");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "favoriteId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );

			call.invoke( new Object [] {ticket,favoriteId});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}	
	}
	
	public Bookmark createBookmark(Ticket ticket,Bookmark bookmark) throws RepositoryException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "createBookmark");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "bookmark", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );
			return (Bookmark)call.invoke( new Object [] {ticket,bookmark});
			
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}
	
	public void removeBookmark(Ticket ticket,
							   String bookmarkId) throws RepositoryException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "removeBookmark");
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "bookmarkId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.AXIS_VOID );

			call.invoke( new Object [] {ticket,bookmarkId});

		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}
	
	public Bookmark updateBookmark(Ticket ticket,Bookmark bookmark) throws RepositoryException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "updateBookmark");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "bookmark", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.XSD_ANY );

			return (Bookmark)call.invoke( new Object [] {ticket,bookmark});

		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}
	
	public byte[] exportRepository(Ticket ticket) 
											throws RepositoryNotFoundException, 
												   RepositoryException,
												   SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "exportRepository");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );

			call.setReturnType( XMLType.SOAP_BASE64BINARY );
			call.invoke( new Object [] {ticket});
			
			return extractAttachment();
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf(
					"RepositoryNotFoundException") != -1) {
				throw new RepositoryNotFoundException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}

	}

	public void importRepository(Ticket ticket, 	
								 byte[] content,
								 String name) 
										throws RepositoryException,
											   SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "importRepository");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "content", XMLType.SOAP_BASE64BINARY, ParameterMode.IN );
		    call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN );

		    DataHandler handler = new DataHandler(
		    		new ByteArrayDataSource(content,"application/octet-stream"));
		    call.addAttachmentPart(handler);
		    
			call.setReturnType( XMLType.AXIS_VOID );
			call.invoke( new Object [] {ticket,new byte[]{},name});
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("RepositoryAlreadyExistsException") != -1) {
				throw new RepositoryAlreadyExistsException();				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	

	public byte[] loadVersionContent(Ticket ticket, String versionId) throws RepositoryException,
	 																		 SecurityException {
		
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "loadVersionContent" );
			
		    call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "versionId", XMLType.XSD_STRING, ParameterMode.IN );
			
			call.setReturnType( XMLType.SOAP_BASE64BINARY);

			call.invoke(new Object [] {ticket, versionId});

			return extractAttachment();
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	public Lock lockDocument(Ticket ticket, 
							 String docId) throws RepositoryException,
							   				 	  SecurityException,
												  ResourceLockedException {

		try {       
			call.removeAllParameters();
			
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "lockDocument" );
			
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "docId", XMLType.XSD_STRING, ParameterMode.IN );
			
			call.setReturnType( XMLType.XSD_ANY);
			
			return (Lock)call.invoke(new Object [] {ticket, docId});
		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("ResourceLockedException") != -1) {
				// Create a virtual lock for info
				Lock lock = lookForLock(ticket, docId);				
				throw new ResourceLockedException(lock);
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public void unlockDocument(Ticket ticket, 
							   String docId) throws RepositoryException, 
							 					    SecurityException,
							 					    ResourceLockedException{

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("unlockDocument");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("docId", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.AXIS_VOID);

			call.invoke(new Object[] { ticket, docId });
		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("ResourceLockedException") != -1) {
				// Create a virtual lock for info
				Lock lock = lookForLock(ticket, docId);				
				throw new ResourceLockedException(lock);
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	public List findAllLocks(Ticket ticket) throws RepositoryException,
												   SecurityException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("findAllLocks");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			Object[] o = (Object[])call.invoke(new Object[] { ticket });
			
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;
		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#createResource(org.jlibrary.core.entities.Ticket, org.jlibrary.core.properties.ResourceNodeProperties)
	 */
	public ResourceNode createResource(Ticket ticket, 
							   		   ResourceNodeProperties properties) throws RepositoryException,
							   		   											 SecurityException {
		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("createResource");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("properties", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			// Change the binary property for an attachment
			byte[] content = (byte[])properties.getProperty(
					ResourceNodeProperties.RESOURCE_CONTENT).getValue();
			if (content != null) {
				properties.setProperty(
						ResourceNodeProperties.RESOURCE_CONTENT,null);
			    DataHandler handler = new DataHandler(new ByteArrayDataSource(
			    		content,"application/octet-stream"));
			    call.addAttachmentPart(handler);
			}			
			return (ResourceNode)call.invoke(new Object[] { ticket,properties });
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}	
	
	public void addResourceToDocument(Ticket ticket,
			  						  String resourceId,
									  String documentId) throws RepositoryException,
									  							SecurityException {
		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("addResourceToDocument");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("resource", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("document", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.AXIS_VOID);

			call.invoke(new Object[] { ticket,resourceId,documentId });
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public List findNodesForResource(Ticket ticket, 
			 						 String resourceId) throws RepositoryException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("findNodesForResource");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("resourceId", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			Object[] o = (Object[])call.invoke( new Object [] {ticket,resourceId});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;
			
		} catch (Exception e) {
			AxisFault fault = (AxisFault) e;
			throw new RepositoryException(fault.getFaultString());
		}
	}

	public byte[] loadResourceNodeContent(Ticket ticket, 
										  String resourceId) throws RepositoryException,
										  							SecurityException {

		try {       
			call.removeAllParameters();

			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "loadResourceNodeContent" );

			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "resourceId", XMLType.XSD_STRING, ParameterMode.IN );

			call.setReturnType( XMLType.SOAP_BASE64BINARY);

			call.invoke(new Object [] {ticket,resourceId});

			return extractAttachment();
		} catch (Exception e) {
			// 	I don't know if there is a better way to do this
			AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());	
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	public ResourceNode updateResourceNode(Ticket ticket,
								   	   	   ResourceNodeProperties properties) throws RepositoryException, 
								   													 SecurityException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("updateResourceNode");
			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("properties", XMLType.XSD_ANY,ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			// Change the binary property for an attachment
			byte[] content = (byte[])properties.getProperty(
					ResourceNodeProperties.RESOURCE_CONTENT).getValue();
			if (content != null) {
				properties.setProperty(
						ResourceNodeProperties.RESOURCE_CONTENT,null);
			    DataHandler handler = new DataHandler(new ByteArrayDataSource(
			    		content,"application/octet-stream"));
			    call.addAttachmentPart(handler);			
			}			
			return (ResourceNode) call.invoke(new Object[] {ticket, properties});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}

	public void removeResourceNode(Ticket ticket, 
								   String resourceId) throws RepositoryException, 
								   							 SecurityException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("removeResourceNode");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("resourceId", XMLType.XSD_STRING,
					ParameterMode.IN);

			call.setReturnType(XMLType.AXIS_VOID);

			call.invoke(new Object[] { ticket, resourceId });

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());

			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}
	
	public void removeResourceNode(Ticket ticket, 
								   String resourceId,
								   String docId) throws RepositoryException, 
								   						SecurityException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("removeResourceNode");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("resourceId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("docId", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.AXIS_VOID);

			call.invoke(new Object[] { ticket, resourceId, docId });

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());

			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}	
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#copyResource(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public ResourceNode copyResource(Ticket ticket, 
									 String resourceId, 
									 String directoryId,
									 String destinationRepository) throws RepositoryException, 
									 							SecurityException {
		
		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("copyResource");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("resourceId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("directoryId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("destinationRepository", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			return (ResourceNode)call.invoke(new Object[] { ticket, resourceId, directoryId, destinationRepository });

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());

			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}		
	
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#moveResource(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public ResourceNode moveResource(Ticket ticket, 
									 String resourceId, 
									 String directoryId,
									 String destinationRepository) throws RepositoryException, 
									 							SecurityException {
		
		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("moveResource");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("resourceId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("directoryId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("destinationRepository", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			return (ResourceNode)call.invoke(new Object[] { ticket, resourceId, directoryId, destinationRepository });

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());

			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}	
	
	/**
	 * @see org.jlibrary.core.repository.RepositoryService#copyNode(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public Node copyNode(Ticket ticket, 
						 String sourceId, 
						 String directoryId,
						 String destinationRepository) throws RepositoryException, 
						 							SecurityException {
		
		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("copyNode");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("sourceId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("directoryId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("destinationRepository", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			return (Node)call.invoke(new Object[] { ticket, sourceId, directoryId, destinationRepository });

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());

			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}		
	
	public List getVersions(Ticket ticket, 
							String documentId) throws RepositoryException, 
									  				  SecurityException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "getVersions" );
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "documentId", XMLType.XSD_STRING, ParameterMode.IN );
			call.setReturnType( XMLType.XSD_ANY );

			Object[] o = (Object[])call.invoke( new Object [] {ticket,documentId});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}	
	}	
	
	public Collection findDocumentsByName(Ticket ticket, 
										  String name) throws RepositoryException {

		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "findDocumentsByName" );
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
			call.addParameter( "name", XMLType.XSD_STRING, ParameterMode.IN );
			call.setReturnType( XMLType.XSD_ANY );

			Object[] o = (Object[])call.invoke( new Object [] {ticket,name});
			ArrayList list = new ArrayList();
			CollectionUtils.addAll(list,o);
			return list;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}	
	}
	
	/**
	 * @see org.jlibrary.core.repository.RepositoryService#moveNode(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public Node moveNode(Ticket ticket, 
				 		 String sourceId, 
						 String directoryId,
						 String destinationRepository) throws RepositoryException, 
						 							SecurityException {
		
		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("moveNode");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("sourceId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("directoryId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("destinationRepository", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);

			return (Node)call.invoke(new Object[] { ticket, sourceId, directoryId, destinationRepository });

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());

			} else {
				throw new RepositoryException(fault.getFaultString());
			}
		}
	}	

	
	/**
	 * @see RepositoryService#saveSession(Ticket)
	 */
	public void saveSession(Ticket ticket) throws RepositoryException {
		
		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("saveSession");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType(XMLType.AXIS_VOID);

			call.invoke(new Object[] { ticket});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			throw new RepositoryException(fault.getFaultString());
		}
	}	
	
	private byte[] extractAttachment() throws IOException, SOAPException {
		
		Message response = call.getMessageContext().getResponseMessage();
		Iterator it = response.getAttachments();
		AttachmentPart attachment = (AttachmentPart)it.next();
		InputStream is = attachment.getDataHandler().getInputStream();
		byte[] content = IOUtils.toByteArray(is);
		is.close();		
		return content;
	}
	
	
	public Collection findNodeChildren(Ticket ticket, String id) throws RepositoryException,
			NodeNotFoundException, SecurityException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("findNodeChildren");
			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("id", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_ANY);
			Object[] o = (Object[])call.invoke( new Object [] {ticket,id});
			HashSet set = new HashSet();
			CollectionUtils.addAll(set,o);
			return set;
		} catch (Exception e) {
			AxisFault fault = (AxisFault) e;
			// I don't know if there is a better way to do this
			if (fault.getFaultString().indexOf("NodeNotFoundException") != -1) {
				throw new NodeNotFoundException(fault.getFaultString());
			}
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else {
				throw new RepositoryException(fault.getFaultString());
			}
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
        qn = new QName( "urn:BeanService", "DocumentProperties" );
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
        call.registerTypeMapping(ResourceNode.class, qn,
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
	
	/**
	 * <p>This method looks for a node lock. This is necessary as it seems that 
	 * Axis 1.x is not able to transfer information about the original 
	 * exception thrown.</p>
	 * 
	 * <p>Note that this won't be 100% accurate as thee node could have been 
	 * unlocked or locked again since the excecution of the original method 
	 * and this check.</p>
	 * 
	 * @param ticket Ticket
	 * @param nodeId node id
	 * 
	 * @return Lock Current lock
	 */
	private Lock lookForLock(Ticket ticket, String nodeId) {
		
		Node node = null;
		try {
			node = findNode(ticket,nodeId);
			return node.getLock();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	public boolean isPropertyRegistered(Ticket ticket, 
										String propertyName) throws RepositoryException {


		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("isPropertyRegistered");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("propertyName", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_BOOLEAN);

			Boolean b = (Boolean)call.invoke(new Object[] {ticket,propertyName});
			return b.booleanValue();
		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			throw new RepositoryException(fault.getFaultString());
		}	
	}

	public boolean isPropertyRegistered(Ticket ticket, 
										String uri, 
										String propertyName) throws RepositoryException {


		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("isPropertyRegistered");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("uri", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("propertyName", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_BOOLEAN);

			Boolean b = (Boolean)call.invoke(new Object[] {ticket,uri,propertyName});
			return b.booleanValue();
		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			throw new RepositoryException(fault.getFaultString());
		}	
	}


	public void registerCustomProperty(Ticket ticket, CustomPropertyDefinition property) throws RepositoryException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("registerCustomProperty");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("property", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType(XMLType.AXIS_VOID);

			call.invoke(new Object[] { ticket,property});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			throw new RepositoryException(fault.getFaultString());
		}			
	}


	public void unregisterCustomProperty(Ticket ticket, CustomPropertyDefinition property) throws RepositoryException {

		try {
			call.removeAllParameters();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName("unregisterCustomProperty");

			call.addParameter("ticket", XMLType.XSD_ANY, ParameterMode.IN);
			call.addParameter("property", XMLType.XSD_ANY, ParameterMode.IN);

			call.setReturnType(XMLType.AXIS_VOID);

			call.invoke(new Object[] { ticket,property});

		} catch (Exception e) {
			// I don't know if there is a better way to do this
			AxisFault fault = (AxisFault) e;
			throw new RepositoryException(fault.getFaultString());
		}		
	}


	public void exportRepository(Ticket ticket, 
								 OutputStream stream) throws RepositoryNotFoundException, 
								 							 RepositoryException, 
								 							 SecurityException {

		byte[] content = exportRepository(ticket);
		try {
			IOUtils.write(content, stream);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}


	public void importRepository(Ticket ticket, 
								 String name, 
								 InputStream stream) throws RepositoryAlreadyExistsException, 
								 							RepositoryException, 
								 							SecurityException {
		
		byte[] content;
		try {
			content = IOUtils.toByteArray(stream);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		importRepository(ticket, content, name);
	}


	public void loadDocumentContent(String docId, 
									Ticket ticket, 
									OutputStream stream) throws RepositoryException, 
																SecurityException {

		byte[] content = loadDocumentContent(docId, ticket);
		try {
			IOUtils.write(content, stream);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}


	public void loadResourceNodeContent(Ticket ticket, 
										String resourceId, 
										OutputStream stream) throws RepositoryException, 
																	SecurityException {

		byte[] content = loadResourceNodeContent(ticket, resourceId);
		try {
			IOUtils.write(content, stream);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}


	public void loadVersionContent(Ticket ticket, 
								   String versionId, 
								   OutputStream stream) throws RepositoryException, 
								   							   SecurityException {

		byte[] content = loadVersionContent(ticket, versionId);
		try {
			IOUtils.write(content, stream);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}


	public Node updateContent(Ticket ticket, 
							  String docId, 
							  InputStream stream) throws SecurityException, 
							  							 RepositoryException {

		byte[] content;
		try {
			content = IOUtils.toByteArray(stream);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		return updateContent(ticket, docId, content);
	}


	public Node updateContent(Ticket ticket, 
							  String docId, 
							  byte[] content) throws SecurityException, 
							  						 RepositoryException {
		try {       
			call.removeAllParameters();
			   
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName( "updateContent");
			call.addParameter( "ticket", XMLType.XSD_ANY, ParameterMode.IN );
		    call.addParameter( "docId", XMLType.XSD_STRING, ParameterMode.IN );
		    call.addParameter( "content", XMLType.SOAP_BASE64BINARY, ParameterMode.IN );

		    DataHandler handler = new DataHandler(
		    		new ByteArrayDataSource(content,"application/octet-stream"));
		    call.addAttachmentPart(handler);
		    
			call.setReturnType( XMLType.XSD_ANY );
			Node node = (Node)call.invoke( new Object [] {ticket,docId,content});
			return node;
			
		} catch (Exception e) {
			// I don't know if there is a better way to do this
		    AxisFault fault = (AxisFault)e;
			if (fault.getFaultString().indexOf("SecurityException") != -1) {
				throw new SecurityException(fault.getFaultString());
			} else if (fault.getFaultString().indexOf("RepositoryAlreadyExistsException") != -1) {
				throw new RepositoryAlreadyExistsException();				
			} else {
			    throw new RepositoryException(fault.getFaultString());
			}
		}
	}	
}
