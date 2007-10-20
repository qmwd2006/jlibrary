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
package org.jlibrary.web.services;


/**
 * 
 * @author dlatorre
 *
 */
public class StatsService {
	private int loggedUsers;
	private int totalUsers;
	private static StatsService instance;
	public static final String SESSION_LOGGED_USER = "logged_user";
	
	private StatsService(){
		loggedUsers=0;
		totalUsers=0;
	}
	
	public static StatsService newInstance(){
		if (instance == null) {
			synchronized(StatsService.class) {
				if (instance == null)
					instance= new StatsService();
				}
	     	}
	    return instance;
	}

	public int getAnonymousUsers() {
		return totalUsers-loggedUsers;
	}

	public int getLoggedUsers() {
		return loggedUsers;
	}

	public void incLoggedUsers() {
		this.loggedUsers++;
	}
	
	public void decLoggedUsers() {
		if(this.loggedUsers>0)
			this.loggedUsers--;
	}

	public int getTotalUsers() {
		return totalUsers;
	}

	public void incTotalUsers() {
		this.totalUsers++;
	}
	
	public void decTotalUsers() {
		if(this.totalUsers>0)
			this.totalUsers--;
	}
}