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
package org.jlibrary.client.export.freemarker.methods;

import java.util.Iterator;
import java.util.List;

import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Node;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * Tells us if a directory only contains resources
 * 
 * @author martin
 *
 */
public class IsResourcesDirectoryMethod implements TemplateMethodModel {

	public Object exec(List args) throws TemplateModelException {

		if (args.size() != 1) {
            throw new TemplateModelException("Wrong arguments");
        }
		
		String id = (String)args.get(0);
		Directory directory = (Directory)
			EntityRegistry.getInstance().getAlreadyLoadedNode(id);
		
		if (checkDirectory(directory)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private boolean checkDirectory(Directory directory) {
		
		Iterator it = directory.getNodes().iterator();
		
		while (it.hasNext()) {
			Node node = (Node) it.next();
			if (node.isDirectory()) {
				if (!checkDirectory((Directory)node)) {
					return false;
				}
			}
			if (node.isDocument()) {
				return false;
			}
		}
		return true;
	}
}
