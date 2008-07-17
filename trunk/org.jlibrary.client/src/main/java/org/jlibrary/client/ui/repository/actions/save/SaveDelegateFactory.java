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
package org.jlibrary.client.ui.repository.actions.save;

import java.util.HashMap;

import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.User;

public class SaveDelegateFactory {

	private static HashMap delegates;
	
	public static SavingDelegate getSavingDelegate(Object object) {
		
		if (delegates == null) {
			initDelegates();
		}
		return (SavingDelegate)delegates.get(object.getClass());
	}

	private static void initDelegates() {
		
		delegates = new HashMap();
		delegates.put(Directory.class, new SaveDirectoryDelegate());
		delegates.put(Document.class, new SaveDocumentDelegate());
		delegates.put(Repository.class, new SaveRepositoryDelegate());
		delegates.put(ResourceNode.class, new SaveResourceDelegate());
		delegates.put(User.class, new SaveUserDelegate());
		delegates.put(Rol.class, new SaveRolDelegate());
		delegates.put(Group.class, new SaveGroupDelegate());
		delegates.put(Category.class, new SaveCategoryDelegate());
		delegates.put(Author.class, new SaveAuthorDelegate());
	}
}
