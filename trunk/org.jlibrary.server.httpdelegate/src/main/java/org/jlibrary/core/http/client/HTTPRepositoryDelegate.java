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
package org.jlibrary.core.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
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
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.profiles.HTTPServerProfile;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
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
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will delegate all the calls through the HTTP service
 * 
 * @author martin
 * 
 */
public class HTTPRepositoryDelegate extends HTTPDelegate implements RepositoryService {

	static Logger logger = LoggerFactory.getLogger(HTTPRepositoryDelegate.class);
		
	/**
	 * Constructor
	 */
	public HTTPRepositoryDelegate(HTTPServerProfile profile) {
		
		super(profile,"HTTPRepositoryService");
	}


	public Repository createRepository(Ticket ticket,
	        						   String name,
									   String description,
									   User creator) 
										throws RepositoryAlreadyExistsException,
											   RepositoryException,
									   		   SecurityException {
		
		try {
			Repository r = (Repository)doRepositoryRequest(
					"createRepository",
					new Object [] {ticket,name,description,creator}, 
					Repository.class);
			return r;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}


	public Directory createDirectory(Ticket ticket,
	        						 String name,
									 String description,
									 String parentId) throws RepositoryException,
									 					     SecurityException {
     
		try {
			Directory dir = (Directory)doRepositoryRequest(
					"createDirectory",
					new Object [] { ticket,name,description,parentId},
					Directory.class);
			return dir;	
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}

	public Directory createDirectory(Ticket ticket,
	        						 DirectoryProperties properties) throws RepositoryException,
									 					     				SecurityException {

		try {
			Directory dir = (Directory)doRepositoryRequest(
					"createDirectory",
					new Object [] { ticket,properties},
					Directory.class);
			return dir;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}
	
	public void removeDirectory(Ticket ticket, String directoryId) throws RepositoryException,
																		  SecurityException {
		try {
			doVoidRepositoryRequest(
					"removeDirectory",
					new Object [] { ticket,directoryId});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}
	}


	public List findAllRepositoriesInfo(Ticket ticket) throws RepositoryException  {
	
		List list = (List)doRepositoryRequest(
				"findAllRepositoriesInfo",
				new Object [] { ticket},
				List.class);
		return list;
	}
	

	public Repository findRepository(String id, 
									 Ticket ticket) 
											throws RepositoryNotFoundException,
												   RepositoryException, 
												   SecurityException  {
	
		try {
			Repository r = (Repository)doRepositoryRequest(
					"findRepository",
					new Object [] {id, ticket}, 
					Repository.class);
			return r;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}	

	public Document findDocument(Ticket ticket, 
								 String id) throws RepositoryException, 
								 				   NodeNotFoundException,
								 				   SecurityException {

		try {
			Document d = (Document)doRepositoryRequest(
					"findDocument",
					new Object [] {ticket,id}, 
					Document.class);
			return d;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
    }

	public Node findNode(Ticket ticket, 
						 String id)
    							throws RepositoryException, 
    								   NodeNotFoundException,
    								   SecurityException {

		try {
			Node n = (Node)doRepositoryRequest(
					"findNode",
					new Object [] {ticket,id}, 
					Node.class);
			return n;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}			
	}
	
	public Directory findDirectory(Ticket ticket, 
								   String id) throws RepositoryException, 
								   					 NodeNotFoundException,
													 SecurityException {
		
		try {
			Directory d = (Directory)doRepositoryRequest(
					"findDirectory",
					new Object [] {ticket,id}, 
					Directory.class);
			return d;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}			
	}
        
	public void deleteRepository(Ticket ticket) throws RepositoryException,
													   SecurityException {

		try {
			doVoidRepositoryRequest(
					"deleteRepository",
					new Object [] {ticket});			
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}	

	public Directory copyDirectory( Ticket ticket,
							   	    String sourceId, 
									String destinationId,
									String destinationRepository) throws RepositoryException,
																 SecurityException {
		
		try {
			Directory d = (Directory)doRepositoryRequest(
					"copyDirectory",
					new Object [] {ticket,sourceId,destinationId,destinationRepository}, 
					Directory.class);
			return d;		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}

	public Document copyDocument( Ticket ticket,
							  	  String sourceId, 
								  String destinationId,
								  String destinationRepository) throws RepositoryException,
								  							   		   SecurityException {
		
		try {
			Document d = (Document)doRepositoryRequest(
					"copyDocument",
					new Object [] {ticket,sourceId,destinationId,destinationRepository}, 
					Document.class);
			return d;		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}


	public Directory moveDirectory( Ticket ticket,
							   		String sourceId, 
									String destinationId,
									String destinationRepository) throws RepositoryException,
																 SecurityException {
		
		try {
			Directory d = (Directory)doRepositoryRequest(
					"moveDirectory",
					new Object [] {ticket,sourceId,destinationId,destinationRepository}, 
					Directory.class);
			return d;
			
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}
	
	public Document moveDocument( Ticket ticket,
								  String documentId, 
								  String directoryId,
								  String destinationRepository) throws RepositoryException,
								  							 SecurityException {

		try {
			Document d = (Document)doRepositoryRequest(
					"moveDocument",
					new Object [] {ticket,documentId,directoryId,destinationRepository}, 
					Document.class);
			return d;		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}
	
	public byte[] loadDocumentContent(String docId, Ticket ticket) throws RepositoryException,
																		  SecurityException {
		
		try {
			InputStream is = (InputStream)doRepositoryRequest(
					"loadDocumentContent",
					new Object [] {docId,ticket}, 
					InputStream.class);
			byte[] content = IOUtils.toByteArray(is);
			is.close();
			return content;		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
		
	}

	public Document createDocument( Ticket ticket,
									DocumentProperties docProperties) throws RepositoryException,
											 									 SecurityException {
		
		try {
			Document d = (Document)doRepositoryRequest(
					"createDocument",
					new Object [] {ticket,docProperties}, 
					Document.class);
			return d;
		
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}				
	}

	public List createDocuments( Ticket ticket,
								 List properties) throws RepositoryException,
					 									 SecurityException {

		try {
			List l = (List)doRepositoryRequest(
					"createDocuments",
					new Object [] {ticket,properties}, 
					List.class);
			return l;		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}				
	}	
	
	public void removeDocument(Ticket ticket,
	        				   String docId) throws RepositoryException,
											 		SecurityException,
											 		ResourceLockedException {
		
		try {
			doVoidRepositoryRequest(
					"removeDocument",
					new Object [] {ticket,docId});		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
				if (e.getCause().getClass() == ResourceLockedException.class) {
					throw (ResourceLockedException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
		
	}

	public Document updateDocument(Ticket ticket,
							   	   DocumentProperties docProperties) throws RepositoryException, 
							   						 	 					SecurityException,
							   						 	 					ResourceLockedException {

		try {
			Document d = (Document)doRepositoryRequest(
					"updateDocument",
					new Object [] {ticket,docProperties}, 
					Document.class);
			return d;	
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
				if (e.getCause().getClass() == ResourceLockedException.class) {
					throw (ResourceLockedException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}			
	}
	
	public Directory updateDirectory(Ticket ticket,
									 DirectoryProperties directoryProperties) throws RepositoryException,
													  								 SecurityException {

		try {
			Directory d = (Directory)doRepositoryRequest(
					"updateDirectory",
					new Object [] {ticket,directoryProperties}, 
					Directory.class);
			return d;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}			
	}
	
	public Repository updateRepository(Ticket ticket,
									   RepositoryProperties repositoryProperties) throws RepositoryException, 
									   											 	     SecurityException {

		try {
			Repository r = (Repository)doRepositoryRequest(
					"updateRepository",
					new Object [] {ticket,repositoryProperties}, 
					Repository.class);
			return r;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}			
	}	
	
	public void renameNode(Ticket ticket,
						   String nodeId, 
						   String name) throws RepositoryException, 
						   					   SecurityException {

		try {
			doVoidRepositoryRequest(
					"renameNode",
					new Object [] {ticket,nodeId,name});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}			
	}
	
	public List findAllAuthors(Ticket ticket) throws RepositoryException {

		List l = (List)doRepositoryRequest(
				"findAllAuthors",
				new Object [] {ticket}, 
				List.class);
		return l;
	}
	
	public Author findAuthorByName(Ticket ticket,
								   String name) throws AuthorNotFoundException,
								   					   RepositoryException {

		try {
			Author a = (Author)doRepositoryRequest(
					"findAuthorByName",
					new Object [] {ticket,name}, 
					Author.class);
			return a;
		} catch (Exception e) {
			throw (RepositoryException)e;
		}			
	}
	
	public Author findAuthorById(Ticket ticket,
								 String id) throws AuthorNotFoundException,
								 				   RepositoryException {

		try {
			Author a = (Author)doRepositoryRequest(
					"findAuthorById",
					new Object [] {ticket,id}, 
					Author.class);
			return a;
		} catch (Exception e) {
			throw (RepositoryException)e;
		}		
	}
	
	public List findAllCategories(Ticket ticket) throws RepositoryException {

		List l = (List)doRepositoryRequest(
				"findAllCategories",
				new Object [] {ticket}, 
				List.class);
		return l;
	}
	
	public Author createAuthor(Ticket ticket,
							   AuthorProperties properties) 
										throws RepositoryException,
			   						   		   SecurityException,
			   						   		   AuthorAlreadyExistsException {

		try {
			Author a = (Author)doRepositoryRequest(
					"createAuthor",
					new Object [] {ticket,properties}, 
					Author.class);
			return a;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}		
			throw (RepositoryException)e;
		}		
	}
	
	public void updateAuthor(Ticket ticket,
			   				 String authorId,
							 AuthorProperties properties) 
											throws RepositoryException,
					   			  				   SecurityException,
					   			  				   AuthorNotFoundException {

		try {
			doVoidRepositoryRequest(
					"updateAuthor",
					new Object [] {ticket,authorId,properties});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}		
	}

	public void deleteAuthor(Ticket ticket,
							 String authorId) throws RepositoryException,
	   			  				   					 SecurityException,
	   			  				   					 AuthorNotFoundException {

		try {
			doVoidRepositoryRequest(
					"deleteAuthor",
					new Object [] {ticket,authorId});

		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}		
	}	
	
	public Category createCategory(Ticket ticket,
	        					   CategoryProperties categoryProperties) 
										throws CategoryAlreadyExistsException,
											   RepositoryException,
	        					   			   SecurityException {
		
		try {
			Category a = (Category)doRepositoryRequest(
					"createCategory",
					new Object [] {ticket,categoryProperties}, 
					Category.class);
			return a;
	
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}		
		
	}
	

	public Category findCategoryById(Ticket ticket,
									 String id) 
											throws CategoryNotFoundException,
												   RepositoryException {

		try {
			Category a = (Category)doRepositoryRequest(
					"findCategoryById",
					new Object [] {ticket,id}, 
					Category.class);
			return a;
	
		} catch (Exception e) {			
			throw (RepositoryException)e;
		}		
		
	}
	

	public Category findCategoryByName(Ticket ticket, 
									   String name) 
											throws CategoryNotFoundException,
												   RepositoryException {

		try {
			Category a = (Category)doRepositoryRequest(
					"findCategoryByName",
					new Object [] {ticket,name}, 
					Category.class);
			return a;
	
		} catch (Exception e) {			
			throw (RepositoryException)e;
		}		
		
	}
	
	public List findCategoriesForNode(Ticket ticket, String nodeId) throws RepositoryException,
																		   SecurityException {
			
		try {
			List l = (List)doRepositoryRequest(
					"findCategoriesForNode",
					new Object [] {ticket,nodeId}, 
					List.class);
			return l;
	
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}		
		
	}

	public List findNodesForCategory(Ticket ticket, 
									 String categoryId) 
											throws CategoryNotFoundException,
												   RepositoryException {
			
		try {
			List l = (List)doRepositoryRequest(
					"findNodesForCategory",
					new Object [] {ticket,categoryId}, 
					List.class);
			return l;
	
		} catch (Exception e) {			
			throw (RepositoryException)e;
		}		
		
	}

	public void deleteCategory(Ticket ticket, String categoryId) throws RepositoryException,
																		SecurityException {
		
		try {
			doVoidRepositoryRequest(
					"deleteCategory",
					new Object [] {ticket,categoryId});
	
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}		
			throw (RepositoryException)e;
		}		
		
	}
	

	public Category updateCategory(Ticket ticket,
	        				   String categoryId,
							   CategoryProperties categoryProperties) 
											throws CategoryNotFoundException,
												   RepositoryException,
												   SecurityException {
		
		try {
			Category category = (Category)doRepositoryRequest(
					"updateCategory",
					new Object [] {ticket,categoryId,categoryProperties},
					Category.class);
			return category;	
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}		
		
	}
	
	public Favorite createFavorite(Ticket ticket, 
								   Favorite favorite) throws RepositoryException,
															 SecurityException {

		try {
			Favorite f = (Favorite)doRepositoryRequest(
					"createFavorite",
					new Object [] {ticket,favorite}, 
					Favorite.class);
			return f;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}		
	}
	
	public void deleteFavorite(Ticket ticket, 
							   String favoriteId) throws RepositoryException,
														 SecurityException {
		
		try {
			doVoidRepositoryRequest(
					"deleteFavorite",
					new Object [] {ticket,favoriteId});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}		
	}
	
	public Bookmark createBookmark(Ticket ticket,Bookmark bookmark) throws RepositoryException {
		
		Bookmark b = (Bookmark)doRepositoryRequest(
				"createBookmark",
				new Object [] {ticket,bookmark}, 
				Bookmark.class);
		return b;
	}
	
	public void removeBookmark(Ticket ticket,
							   String bookmarkId) throws RepositoryException {
		
		doVoidRepositoryRequest(
				"removeBookmark",
				new Object [] {ticket,bookmarkId});	
	}
	
	public Bookmark updateBookmark(Ticket ticket,Bookmark bookmark) throws RepositoryException {
		
		Bookmark b = (Bookmark)doRepositoryRequest(
				"updateBookmark",
				new Object [] {ticket,bookmark},
				Bookmark.class);
		return b;
	}
	
	public byte[] exportRepository(Ticket ticket) 
											throws RepositoryNotFoundException, 
												   RepositoryException,
												   SecurityException {

		byte[] content = (byte[])doRepositoryRequest(
				"exportRepository",
				new Object [] {ticket}, 
				byte[].class);
		return content;
	}
	
	public void exportRepository(Ticket ticket, OutputStream outputStream) 
											throws RepositoryNotFoundException, 
												   RepositoryException,
												   SecurityException {

		try {
			InputStream inputStream = (InputStream)doRepositoryRequest(
					"exportRepository",
					new Object [] {ticket}, 
					InputStream.class);
			
			IOUtils.copy(inputStream, outputStream);
			
			inputStream.close();
		} catch (IOException e) {
			throw new RepositoryException(e);
		}

	}
	
	
	public void importRepository(Ticket ticket, 	
								 byte[] content,
								 String name) 
										throws RepositoryException,
											   SecurityException {

		try {
			doVoidRepositoryRequest(
					"importRepository",
					new Object [] {ticket,content,name});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}
	}
	

	public byte[] loadVersionContent(Ticket ticket, String versionId) throws RepositoryException,
	 																		 SecurityException {
		
		try {
			InputStream is = (InputStream)doRepositoryRequest(
					"loadVersionContent",
					new Object [] {ticket,versionId}, 
					byte[].class);
			byte[] content = (byte[])IOUtils.toByteArray(is);
			is.close();
			return content;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}		
	}
	
	public Lock lockDocument(Ticket ticket, 
							 String docId) throws RepositoryException,
							   				 	  SecurityException,
												  ResourceLockedException {

		try {
			Lock lock = (Lock)doRepositoryRequest(
					"lockDocument",
					new Object [] {ticket,docId}, 
					Lock.class);
			return lock;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
				if (e.getCause().getClass() == ResourceLockedException.class) {
					throw (ResourceLockedException)e.getCause();
				}	
			}
			throw (RepositoryException)e;
		}		
	}
	
	public void unlockDocument(Ticket ticket, 
							   String docId) throws RepositoryException, 
							 					    SecurityException,
							 					    ResourceLockedException{

		try {
			doVoidRepositoryRequest(
					"unlockDocument",
					new Object [] {ticket,docId});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
				if (e.getCause().getClass() == ResourceLockedException.class) {
					throw (ResourceLockedException)e.getCause();
				}	
			}			
			throw (RepositoryException)e;
		}		
	}

	public List findAllLocks(Ticket ticket) throws RepositoryException,
												   SecurityException {

		try {
			List l = (List)doRepositoryRequest(
					"findAllLocks",
					new Object [] {ticket}, 
					List.class);
			return l;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}

	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#createResource(org.jlibrary.core.entities.Ticket, org.jlibrary.core.properties.ResourceNodeProperties)
	 */
	public ResourceNode createResource(Ticket ticket, 
							   		   ResourceNodeProperties properties) throws RepositoryException,
							   		   											 SecurityException {

		try {
			ResourceNode rn = (ResourceNode)doRepositoryRequest(
					"createResource",
					new Object [] {ticket,properties}, 
					ResourceNode.class);
			return rn;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}	
	
	public void addResourceToDocument(Ticket ticket,
			  						  String resourceId,
									  String documentId) throws RepositoryException,
									  							SecurityException {
		try {
			doVoidRepositoryRequest(
						"addResourceToDocument",
						new Object [] {ticket,resourceId,documentId});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}
	
	public List findNodesForResource(Ticket ticket, 
			 						 String resourceId) throws RepositoryException {

		List l = (List)doRepositoryRequest(
				"findNodesForResource",
				new Object [] {ticket,resourceId}, 
				List.class);
		return l;
	}

	public byte[] loadResourceNodeContent(Ticket ticket, 
										  String resourceId) throws RepositoryException,
										  							SecurityException {

		try {
			InputStream is = (InputStream)doRepositoryRequest(
					"loadResourceNodeContent",
					new Object [] {ticket,resourceId}, 
					InputStream.class);
			byte[] content = (byte[])IOUtils.toByteArray(is);
			is.close();
			return content;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}

	public ResourceNode updateResourceNode(Ticket ticket,
								   	   	   ResourceNodeProperties properties) throws RepositoryException, 
								   													 SecurityException {

		try {
			ResourceNode rn = (ResourceNode)doRepositoryRequest(
					"updateResourceNode",
					new Object [] {ticket,properties}, 
					ResourceNode.class);
			return rn;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}

	public void removeResourceNode(Ticket ticket, 
								   String resourceId) throws RepositoryException, 
								   							 SecurityException {

		try {
			doVoidRepositoryRequest(
					"removeResourceNode",
					new Object [] {ticket,resourceId});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}
	
	public void removeResourceNode(Ticket ticket, 
								   String resourceId,
								   String docId) throws RepositoryException, 
								   						SecurityException {

		try {
			doVoidRepositoryRequest(
					"removeResourceNode",
					new Object [] {ticket,resourceId,docId});
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
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
			ResourceNode rn = (ResourceNode)doRepositoryRequest(
					"moveResource",
					new Object [] {ticket,resourceId,directoryId,destinationRepository}, 
					ResourceNode.class);
			return rn;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
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
			ResourceNode rn = (ResourceNode)doRepositoryRequest(
					"moveResource",
					new Object [] {ticket,resourceId,directoryId,destinationRepository}, 
					ResourceNode.class);
			return rn;	
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
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
			Node node = (Node)doRepositoryRequest(
					"copyNode",
					new Object [] {ticket,sourceId,directoryId,destinationRepository}, 
					Node.class);
			return node;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}		
	
	public List getVersions(Ticket ticket, 
							String documentId) throws RepositoryException, 
									  				  SecurityException {

		try {
			List l = (List)doRepositoryRequest(
					"getVersions",
					new Object [] {ticket,documentId}, 
					List.class);
			return l;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}	
	
	public Collection findDocumentsByName(Ticket ticket, 
										  String name) throws RepositoryException {

		Collection rn = (Collection)doRepositoryRequest(
				"findDocumentsByName",
				new Object [] {ticket,name}, 
				Collection.class);
		return rn;		
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
			Node node = (Node)doRepositoryRequest(
					"moveNode",
					new Object [] {ticket,sourceId,directoryId,destinationRepository}, 
					Node.class);
			return node;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}	

	
	/**
	 * @see RepositoryService#saveSession(Ticket)
	 */
	public void saveSession(Ticket ticket) throws RepositoryException {
		
		doVoidRepositoryRequest(
				"saveSession",
				new Object [] {ticket});
	}		
	
	public Collection findNodeChildren(Ticket ticket, String id) throws RepositoryException,
			NodeNotFoundException, SecurityException {

		try {
			Collection rn = (Collection)doRepositoryRequest(
					"findNodeChildren",
					new Object [] {ticket,id}, 
					Collection.class);
			return rn;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}											
			throw new RepositoryException(e);
		}		
	}	
	
	public void doVoidRepositoryRequest(String methodName, Object[] params) throws RepositoryException {
	
		try {
			doVoidRequest(methodName,params);
		} catch (Exception e) {
			if (e instanceof RepositoryException) {
				throw (RepositoryException)e;
			}
			throw new RepositoryException(e);			
		}
	}
	
	public Object doRepositoryRequest(
			String methodName, Object[] params, Class returnClass) throws RepositoryException {
	
		try {
			return doRequest(methodName,params,returnClass,null);
		} catch (Exception e) {
			if (e instanceof RepositoryException) {
				throw (RepositoryException)e;
			}		
			throw new RepositoryException(e);			
		}
	}
	
	public void doVoidRepositoryStreamedRequest(
			String methodName, 
			Object[] params,
			InputStream stream) throws RepositoryException {
	
		try {
			doVoidRequest(methodName,params,stream);
		} catch (Exception e) {
			if (e instanceof RepositoryException) {
				throw (RepositoryException)e;
			}
			throw new RepositoryException(e);			
		}
	}
	
	public Object doRepositoryStreamedRequest(
			String methodName, 
			Object[] params, 
			Class returnClass,
			InputStream stream) throws RepositoryException {
	
		try {
			return doRequest(methodName,params,returnClass,stream);
		} catch (Exception e) {
			if (e instanceof RepositoryException) {
				throw (RepositoryException)e;
			}		
			throw (RepositoryException)e;			
		}
	}

	public boolean isPropertyRegistered(Ticket ticket, String propertyName) throws RepositoryException {

		Boolean b = (Boolean)doRepositoryRequest(
				"isPropertyRegistered",
				new Object [] {ticket,propertyName}, 
				Boolean.class);
		return b;
	}

	public boolean isPropertyRegistered(Ticket ticket, 
										String uri, 
										String propertyName) throws RepositoryException {

		Boolean b = (Boolean)doRepositoryRequest(
				"isPropertyRegistered",
				new Object [] {ticket,uri,propertyName}, 
				Boolean.class);
		return b;
	}
	
	public void registerCustomProperty(Ticket ticket, CustomPropertyDefinition property) throws RepositoryException {

		doVoidRepositoryRequest(
				"registerCustomProperty",
				new Object [] {ticket,property});
	}


	public void unregisterCustomProperty(Ticket ticket, CustomPropertyDefinition property) throws RepositoryException {

		doVoidRepositoryRequest(
				"unregisterCustomProperty",
				new Object [] {ticket,property});		
	}


	public void importRepository(Ticket ticket, 
								 String name, 
								 InputStream stream) throws RepositoryAlreadyExistsException, 
								 							RepositoryException, 
								 							SecurityException {

		try {
			doVoidRepositoryStreamedRequest(
					"importRepository",
					new Object [] {ticket,name},
					stream);
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}		
	}


	public Node updateContent(Ticket ticket, 
							  String docId, 
							  InputStream stream) throws SecurityException, 
								  							 RepositoryException {

		try {
			return (Node)doRepositoryStreamedRequest(
					"updateContent",
					new Object [] {ticket,docId},
					Node.class,
					stream);
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}				
			}			
			throw (RepositoryException)e;
		}
	}


	public void loadDocumentContent(String docId, 
									Ticket ticket, 
									OutputStream stream) throws RepositoryException, 
																SecurityException {

		try {
			InputStream is = (InputStream)doRepositoryRequest(
					"loadDocumentContent",
					new Object [] {docId,ticket}, 
					InputStream.class);
			IOUtils.copy(is,stream);
			is.close();		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}


	public void loadResourceNodeContent(Ticket ticket, 
										String resourceId, 
										OutputStream stream) throws RepositoryException, 
																	SecurityException {

		try {
			InputStream is = (InputStream)doRepositoryRequest(
					"loadResourceNodeContent",
					new Object [] {ticket,resourceId}, 
					InputStream.class);
			IOUtils.copy(is,stream);
			is.close();		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}


	public void loadVersionContent(Ticket ticket, 
								   String versionId, 
								   OutputStream stream) throws RepositoryException, 
								   							   SecurityException {

		try {
			InputStream is = (InputStream)doRepositoryRequest(
					"loadVersionContent",
					new Object [] {ticket,versionId}, 
					InputStream.class);
			IOUtils.copy(is,stream);
			is.close();		
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}
			throw (RepositoryException)e;
		}		
	}


	public Node updateContent(Ticket ticket, 
							  String docId, 
							  byte[] content) throws SecurityException, 
							  						 RepositoryException {

		try {
			Node node = (Node)doRepositoryRequest(
					"updateContent",
					new Object [] {ticket,docId,content},
					Node.class);
			return node;
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause().getClass() == SecurityException.class) {
					throw (SecurityException)e.getCause();
				}
			}			
			throw (RepositoryException)e;
		}	}	
}
