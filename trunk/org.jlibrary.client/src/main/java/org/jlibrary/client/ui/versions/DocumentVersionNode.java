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
package org.jlibrary.client.ui.versions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jlibrary.core.entities.DocumentVersion;

/**
 * @author martin
 *
 * Node for the versions tree
 */
public class DocumentVersionNode implements Comparable {

	private static final SimpleDateFormat format1 = new SimpleDateFormat("dd-MMMM-yyyy");
	private static final SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
	
	private DocumentVersion version;
	private ArrayList children = new ArrayList();
	private Date date;
	private DocumentVersionNode parent;
	
	public void addVersion(DocumentVersionNode version) {
		
		children.add(version);
	}
	
	public void removeVersion(DocumentVersionNode version) {
		
		children.remove(version);
	}
	
	public int size() {
		
		return children.size();
	}
	
	public DocumentVersion getVersion() {
		return version;
	}
	
	public void setVersion(DocumentVersion version) {
		this.version = version;
	}
	
	public List getChildren() {
		return children;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		if (version == null) {
			return format1.format(date);
		} else {
			return format2.format(date);
		}
	}
	
	public DocumentVersionNode getParent() {
		return parent;
	}
	
	public void setParent(DocumentVersionNode parent) {
		this.parent = parent;
	}
	
	public int compareTo(Object o) {

		DocumentVersionNode dvn = (DocumentVersionNode)o;
		return getDate().compareTo(dvn.getDate());
	}
}
