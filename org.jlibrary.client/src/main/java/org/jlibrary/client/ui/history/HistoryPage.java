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
package org.jlibrary.client.ui.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class HistoryPage implements Serializable {

	static final long serialVersionUID = -20050423L;

	private HistoryBook book;
	private String description;
	private ArrayList items = new ArrayList();
	
	public void addItem(History item) {
		
		if (items.contains(item)) return;
		items.add(item);
	}
	
	public void removeItem(History item) {
		
		items.remove(item);
	}
	
	public Collection getItems() {
		
		return items;
	}
	
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the book.
	 */
	public HistoryBook getBook() {
		return book;
	}

	/**
	 * @param book The book to set.
	 */
	public void setBook(HistoryBook book) {
		this.book = book;
	}

	public String toString() {
		
		return description;
	}
}
