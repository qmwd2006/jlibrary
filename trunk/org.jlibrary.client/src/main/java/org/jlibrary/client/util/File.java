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
package org.jlibrary.client.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.jlibrary.core.entities.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class File implements org.jlibrary.client.part.IFile {

	static Logger logger = LoggerFactory.getLogger(File.class);
	
	private Object model;
	private IPath path;
	
	public File(IPath path, Node node) {
		
		this.path = path;
		this.model = node;
	}


	public File(IPath path, java.io.File file) {
		
		this.path = path;
		this.model = file;
	}	
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#exists()
	 */
	public boolean exists() {

		if (model instanceof java.io.File) {
			return ((java.io.File)model).exists();
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#getContents()
	 */
	public InputStream getContents() throws CoreException {

		try {
		    
			String strPath = StringUtils.replace(path.toString(),"file://","");
			strPath = StringUtils.replace(strPath,"file:/","");
			if (!strPath.startsWith("/")) {
			    strPath = "/" + strPath;
			}
			return new FileInputStream(strPath);
		} catch (FileNotFoundException e) {
			
            logger.error(e.getMessage(),e);
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#getName()
	 */
	public String getName() {
		
		if (model instanceof java.io.File) {
			return ((java.io.File)model).getName();
		} else {
			Node node = (Node)this.model;
			return node.getName();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#getFullPath()
	 */
	public IPath getFullPath() {

		return path;
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#getLocation()
	 */
	public IPath getLocation() {
		
		return path;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		
		return path;
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#isReadOnly()
	 */
	public boolean isReadOnly() {

		if (model instanceof java.io.File) {
			return !((java.io.File)model).canRead();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#setCharset(java.lang.String)
	 */
	public void setCharset(String newCharset) throws CoreException {
		
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#getEncoding()
	 */
	public int getEncoding() throws CoreException {
		
		// Not yet implemented
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#getCharset()
	 */
	public String getCharset() throws CoreException {
		
		// Not yet implemented
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.IFile#getContents(boolean)
	 */
	public InputStream getContents(boolean force) throws CoreException {
		
		// Not yet implemented
		return null;
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResource#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		
		File file = (File)other;
		if (file == null) {
			return false;
		}
		if (model == null) {
			return false;
		}
		return (model.equals(file.model));
	}

	/**
	 * @return Returns the document.
	 */
	public Object getFileContent() {
		return model;
	}	
}
