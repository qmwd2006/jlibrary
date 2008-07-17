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
package org.jlibrary.client.ui.dnd;

import java.util.ArrayList;

/**
 * @author Martin
 *
 * DND storage
 */
public class DNDItems {

	public final static String REPOSITORY_VIEW = "Repository";
	public final static String CATEGORY_VIEW = "Category";
	public final static String BOOKMARKS_VIEW = "Bookmarks";
	public final static String DOCUMENTS_VIEW = "Documents";
	public final static String RESTRICTIONS_VIEW = "Restrictions";
	public final static String USERS_VIEW = "Users";
	public final static String GROUPS_VIEW = "Groups";
	public final static String ROLES_VIEW = "Roles";
	public final static String SEARCH_VIEW = "Roles";
	
	
	public static final ArrayList items = new ArrayList();
	
	public static void setItems(Object[] elements) {
		
		items.clear();
		for (int i = 0; i < elements.length; i++) {
			items.add(elements[i]);
		}
	}
	
	public static Object[] getItems() {
		
		return items.toArray();
	}
	
	public static void clear() {
		
		items.clear();
	}
}
