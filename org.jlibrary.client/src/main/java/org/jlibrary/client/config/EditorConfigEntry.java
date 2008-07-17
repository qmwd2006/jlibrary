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
package org.jlibrary.client.config;

import java.io.Serializable;

/**
 * Configuration holder class for opened editors. It will be useful to reopen 
 * editors on jLibrary startup 
 * 
 * @author martin
 *
 */
public class EditorConfigEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5494212711361882099L;
	
	private String repositoryId;
	private String entityClass;
	private String id;
	
	public String getEntityClass() {
		return entityClass;
	}
	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRepositoryId() {
		return repositoryId;
	}
	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public int hashCode() {

		return id.hashCode();
	}
	
	public boolean equals(Object item) {
		
		if (!(item instanceof EditorConfigEntry)) {
			return false;
		}
		
		return id.equals(((EditorConfigEntry)item).id);
	}
	
	public String toString() {
		return '['+repositoryId+','+id+','+entityClass+']';
	}
}
