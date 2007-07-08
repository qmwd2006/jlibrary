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
package org.jlibrary.client.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author martin
 *
 * Class that will perform clean operations
 */
public class JLibraryCleaner {

	/**
	 * Empty constructor
	 */
	public JLibraryCleaner() {
		
		super();
	}

	public void cleanTempFiles() {
		System.out.println(System.getProperties());
		File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
		File[] child = tempDirectory.listFiles(new FilenameFilter() {
			/**
			 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
			 */
			public boolean accept(File dir, String name) {
				if (name.startsWith("jlib")) {
					return true;
				}
				return false;
			}
		});
		for (int i = 0; i < child.length; i++) {
			child[i].delete();
		}
	}
}
