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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jlibrary.client.part.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 */
public class URL  implements IStorage  {

	static Logger logger = LoggerFactory.getLogger(URL.class);
	
	private java.net.URL url;
	private File file;
	
	public URL(String url) throws MalformedURLException {
		
		this.url = new java.net.URL(url);

		file = new File(this.url.getFile());
	}
	
	public URL(java.net.URL url) {
		
		this.url = url;
		file = new File(url.getFile());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		
		try {
			return url.openStream();
		} catch (IOException e) {
			
            logger.error(e.getMessage(),e);
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath() {
		
		return new Path(url.toString());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		
		return url.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#isReadOnly()
	 */
	public boolean isReadOnly() {
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		
		return null;
	}
	
	public String getFileName() {
		
		return file.getName();
	}
	
	/**
	 * @return Returns the url.
	 */
	public java.net.URL getURL() {
		return url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		
		if (!(arg0 instanceof URL)) {
			return false;
		}

		return url.getFile().equals(((URL)arg0).url.getFile());
	}
	
	public String toString() {

		String name = getName();
		
		return (name == null) ? "" : name;
	}
}
