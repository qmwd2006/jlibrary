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

import java.util.Collection;

import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.profiles.HTTPServerProfile;
import org.jlibrary.core.search.SearchException;
import org.jlibrary.core.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will delegate all the calls through the HTTP service
 * 
 * @author martin
 * 
 */
public class HTTPSearchDelegate extends HTTPDelegate implements SearchService {

	static Logger logger = LoggerFactory.getLogger(HTTPSearchDelegate.class);
		
	/**
	 * Constructor
	 */
	public HTTPSearchDelegate(HTTPServerProfile profile) {
		
		super(profile,"HTTPSearchService");
	}
	
	public Collection search(Ticket ticket, String xpathQuery) throws SearchException {

		Collection collection = (Collection)doSearchRequest(
				"search",
				new Object [] {ticket,xpathQuery}, 
				Collection.class);
		return collection;	
	}
	
	public Collection search(Ticket ticket, String phrase, String searchType) throws SearchException {

		Collection collection = (Collection)doSearchRequest(
				"search",
				new Object [] {ticket,phrase,searchType}, 
				Collection.class);
		return collection;	
	}
	
	public void doVoidSearchRequest(String methodName, Object[] params) throws SearchException {

		try {
			doVoidRequest(methodName,params);
		} catch (Exception e) {
			if (e instanceof SearchException) {
				throw (SearchException)e;
			}
			throw new SearchException(e);			
		}
	}
	
	public Object doSearchRequest(
			String methodName, Object[] params, Class returnClass) throws SearchException {

		try {
			return doRequest(methodName,params,returnClass);
		} catch (Exception e) {
			if (e instanceof SearchException) {
				throw (SearchException)e;
			}		
			throw new SearchException(e);			
		}
	}	
}
