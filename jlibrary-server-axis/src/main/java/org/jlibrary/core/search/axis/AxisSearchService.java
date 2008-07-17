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
package org.jlibrary.core.search.axis;

import java.util.Collection;

import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.search.SearchException;
import org.jlibrary.core.search.SearchService;

/**
 * @author martin
 *
 * <p>Axis web service for search and index operations.</p> 
 */
public class AxisSearchService implements SearchService {

	private ServerProfile localProfile = new LocalServerProfile();
	
	/**
	 * Constructor
	 */
	public AxisSearchService() {}

	public Collection search(Ticket ticket,
			 				 String phrase,
							 String searchType) 
							 throws SearchException {
	
		SearchService service = JLibraryServiceFactory.getInstance(localProfile).getSearchService();
	
		return service.search(ticket,phrase,searchType);
	}

	public Collection search(Ticket ticket,
							 String xpathQuery) throws SearchException {
			
		SearchService service = JLibraryServiceFactory.getInstance(localProfile).getSearchService();
	
		return service.search(ticket,xpathQuery);
	}
}
