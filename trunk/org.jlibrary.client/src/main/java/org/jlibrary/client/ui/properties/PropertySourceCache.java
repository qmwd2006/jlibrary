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
package org.jlibrary.client.ui.properties;

import java.util.HashMap;

import org.eclipse.ui.views.properties.IPropertySource;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;

/**
 * Cache for reusing property source objects
 * 
 * @author martin
 *
 */
public class PropertySourceCache {

	private static HashMap cache = new HashMap();

	/**
	 * Gets a cached property source. 
	 * 
	 * If there is no cached property source, then this method creates an 
	 * appropiate one
	 * 
	 * @param object Object from we want to get a property source
	 * 
	 * @return IPropertySource Property source object
	 */
	public static IPropertySource getPropertySource(Object object) {
		
		if (object == null)
			return null;
		
		IPropertySource source = (IPropertySource)cache.get(object);
		if (source == null) {
			if (object.getClass() == Repository.class) {
				source = new RepositoryPropertySource((Repository)object);
				cache.put(object,source);
			} else if (object.getClass() == ResourceNode.class) {
				source = new ResourcePropertySource((ResourceNode)object);
			} else if (object.getClass() == Directory.class) {
				source = new DirectoryPropertySource((Directory)object);
			} else if (object.getClass() == Document.class) {
				source = new DocumentPropertySource((Document)object);
			}
		}
		return source;
	}
}
