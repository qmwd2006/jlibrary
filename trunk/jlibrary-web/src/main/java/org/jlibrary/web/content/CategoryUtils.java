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
package org.jlibrary.web.content;

import java.util.Collection;
import java.util.Iterator;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.web.RepositoryRegistry;


/**
 * Misc utils for dealing with categories and paths
 * 
 * @author martin
 *
 */
public class CategoryUtils {

	public static String pathOf(Category category) {
		
		StringBuilder path = new StringBuilder();
		while (category != null) {
			path.insert(0,relativePathOf(category));
			path.insert(0,"/");
			category = category.getParent();
		}
		return path.toString();
	}
	
	public static String relativePathOf(Category category) {
		
		Repository repository = 
			RepositoryRegistry.getInstance().getRepository(
					category.getRepository());
		Category parent = category.getParent();
		
		Collection categories;
		if (parent == null) {
			categories = repository.getCategories();
		} else {
			categories = parent.getCategories();
		}
		
		int value = -1;
		Iterator it = categories.iterator();
		while (it.hasNext()) {
			Category child = (Category) it.next();
			value++;
			if (child.equals(category)) {
				return String.valueOf(value);
			}
		}
		return "";
	}
}
