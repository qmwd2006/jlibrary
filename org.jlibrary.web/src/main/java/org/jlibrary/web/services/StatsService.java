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
	private static StatsService instance;
	
	private int loggedUsers;
	private int totalUsers;
	private int registeredUsers;
	
	private int createdDocuments;
	private int updatedDocuments;
	private int deletedDocuments;
	private int servedDocuments;
	
	private int createdDirectories;
	private int updatedDirectories;
	private int deletedDirectories;
	private int servedDirectories;
	
	private int createdCategories;
	private int updatedCategories;
	private int deletedCategories;
	private int servedCategories;
	
	private int attachments;
	
	private int errors;
	
	private int comments;
	
	public static final String SESSION_LOGGED_USER = "logged_user";
	
	private StatsService(){
		loggedUsers=0;
		totalUsers=0;
		registeredUsers=0;
		createdDocuments=0;
		updatedDocuments=0;
		deletedDocuments=0;
		servedDocuments=0;
		createdDirectories=0;
		updatedDirectories=0;
		deletedDirectories=0;
		servedDirectories=0;
		createdCategories=0;
		updatedCategories=0;
		deletedCategories=0;
		servedCategories=0;
		attachments=0;
		errors=0;
		comments=0;
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
	
	public void incRegisteredUsers() {
		this.registeredUsers++;
	}

	public int getRegisteredUsers() {
		return registeredUsers;
	}
	
	public void incCreatedDocuments() {
		this.createdDocuments++;
	}

	public int getCreatedDocuments() {
		return createdDocuments;
	}
	
	public void incUpdatedDocuments() {
		this.updatedDocuments++;
	}

	public int getUpdatedDocuments() {
		return updatedDocuments;
	}
	
	public void incDeletedDocuments() {
		this.deletedDocuments++;
	}

	public int getDeletedDocuments() {
		return deletedDocuments;
	}
	
	public void incServedDocuments() {
		this.servedDocuments++;
	}

	public int getServedDocuments() {
		return servedDocuments;
	}
	
	public void incCreatedDirectories() {
		this.createdDirectories++;
	}

	public int getCreatedDirectories() {
		return createdDirectories;
	}
	
	public void incUpdatedDirectories() {
		this.updatedDirectories++;
	}

	public int getUpdatedDirectories() {
		return updatedDirectories;
	}
	
	public void incDeletedDirectories() {
		this.deletedDirectories++;
	}

	public int getDeletedDirectories() {
		return deletedDirectories;
	}
	
	public void incServedDirectories() {
		this.servedDirectories++;
	}

	public int getServedDirectories() {
		return servedDirectories;
	}
	
	public void incCreatedCategories() {
		this.createdCategories++;
	}

	public int getCreatedCategories() {
		return createdCategories;
	}
	
	public void incUpdatedCategories() {
		this.updatedCategories++;
	}

	public int getUpdatedCategories() {
		return updatedCategories;
	}
	
	public void incDeletedCategories() {
		this.deletedCategories++;
	}

	public int getDeletedCategories() {
		return deletedCategories;
	}
	
	public void incServedCategories() {
		this.servedCategories++;
	}

	public int getServedCategories() {
		return servedCategories;
	}
	
	public void incAttachments() {
		this.attachments++;
	}

	public int getAttachments() {
		return attachments;
	}
	
	public void incErrors() {
		this.errors++;
	}

	public int getErrors() {
		return errors;
	}
	
	public void incComments() {
		comments++;
	}
	public int getComments() {
		return comments;
	}
}