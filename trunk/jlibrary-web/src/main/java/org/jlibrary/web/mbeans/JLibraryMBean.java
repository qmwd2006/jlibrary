/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
package org.jlibrary.web.mbeans;

import org.jlibrary.web.services.StatsService;
/**
 * MBean para monitorizar la aplicaci�n.
 * @author dlatorre
 *
 */
public class JLibraryMBean {
	private StatsService statsService;
	public JLibraryMBean(){
		statsService=StatsService.newInstance();
	}
	
	public Integer getLoggedUsers() {
		return statsService.getLoggedUsers();
	}
	
	public Integer getAnonymousUsers() {
		return statsService.getAnonymousUsers();
	}
	
	public Integer getTotalUsers() {
		return statsService.getTotalUsers();
	}
	
	public Integer getRegisteredUsers() {
		return statsService.getRegisteredUsers();
	}

	public Integer getCreatedDocuments() {
		return statsService.getCreatedDocuments();
	}

	public Integer getUpdatedDocuments() {
		return statsService.getUpdatedDocuments();
	}

	public Integer getDeletedDocuments() {
		return statsService.getDeletedDocuments();
	}

	public Integer getServedDocuments() {
		return statsService.getServedDocuments();
	}

	public Integer getCreatedDirectories() {
		return statsService.getCreatedDirectories();
	}

	public Integer getUpdatedDirectories() {
		return statsService.getUpdatedDirectories();
	}

	public Integer getDeletedDirectories() {
		return statsService.getDeletedDirectories();
	}

	public Integer getServedDirectories() {
		return statsService.getServedDirectories();
	}

	public Integer getCreatedCategories() {
		return statsService.getCreatedCategories();
	}

	public Integer getUpdatedCategories() {
		return statsService.getUpdatedCategories();
	}

	public Integer getDeletedCategories() {
		return statsService.getDeletedCategories();
	}

	public Integer getServedCategories() {
		return statsService.getServedCategories();
	}

	public Integer getAttachments() {
		return statsService.getAttachments();
	}

	public Integer getErrors() {
		return statsService.getErrors();
	}
	
	public Integer getComments() {
		
		return statsService.getComments();
	}
}
