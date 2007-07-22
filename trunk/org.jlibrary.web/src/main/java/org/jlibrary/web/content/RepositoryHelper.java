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
package org.jlibrary.web.content;

import java.util.Iterator;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This class will perform tasks that are useful in several actions, for example
 * creating a document structure, creating resources, etc.
 *
 * Having all this business logic centralized here prevents duplicate code and
 * by consequence promotes a less error-phrone basecode.
 */
public class RepositoryHelper {

	static Logger logger = LoggerFactory.getLogger(RepositoryHelper.class);

	public static Category findUnknownCategory(Repository repository) {

		if (repository.getCategories() != null) {
			Iterator it = repository.getCategories().iterator();
			while (it.hasNext()) {
				Category category = (Category) it.next();
				if (category.isUnknownCategory()) {
					return category;
				}
			}
		}
		return null;
	}
}
