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
package org.jlibrary.client.part;

import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.jlibrary.core.entities.IResource;

/**
 * Files are leaf resources which contain data.
 * The contents of a file resource is stored as a file in the local
 * file system.
 * <p>
 * Files, like folders, may exist in the workspace but
 * not be local; non-local file resources serve as placeholders for
 * files whose content and properties have not yet been fetched from
 * a repository.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * Files implement the <code>IAdaptable</code> interface;
 * extensions are managed by the platform's adapter manager.
 * </p>
 *
 * @see Platform#getAdapterManager
 */
public interface IFile extends IAdaptable {
	/**
 * Returns the name of a charset to be used when decoding the contents of this 
 * file into characters. 
 * <p>
 * This refinement of the corresponding <code>IEncodingStorage</code> method
 * uses the following algorithm to determine the charset to be returned:
 * <ol>
 * <li>the charset defined by calling #setCharset, if any, and this file exists, 
 * or</li>
 * <li>the charset automatically discovered based on this file's contents,
 * if one can be determined, or</li>
 * <li>the default encoding for this file's parent (as defined by 
 * IContainer#getDefaultCharset).</li>
 * </ol>
 * </p> 
 * <p>
 * <b>Note</b>: This method is part of early access API that may well 
 * change in incompatible ways until it reaches its finished form. 
 * </p>
 *  
 * @return the name of a charset, or <code>null</code>
 * @see IEncodedStorage#getCharset
 * @see IContainer#getDefaultCharset
 * @since 3.0
 */
public String getCharset() throws CoreException;
/**
 * Returns an open input stream on the contents of this file.
 * This refinement of the corresponding <code>IStorage</code> method 
 * returns an open input stream on the contents of this file.
 * The client is responsible for closing the stream when finished.
 *
 * @return an input stream containing the contents of the file
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This resource does not exist.</li>
 * <li> This resource is not local.</li>
 * <li> The workspace is not in sync with the corresponding location
 *       in the local file system.</li>
 * </ul>
 */
public InputStream getContents() throws CoreException;
/**
 * This refinement of the corresponding <code>IStorage</code> method 
 * returns an open input stream on the contents of this file.
 * The client is responsible for closing the stream when finished.
 * If force is <code>true</code> the file is opened and an input
 * stream returned regardless of the sync state of the file. The file
 * is not synchronized with the workspace.
 * If force is <code>false</code> the method fails if not in sync.
 *
 * @param force a flag controlling how to deal with resources that
 *    are not in sync with the local file system
 * @return an input stream containing the contents of the file
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This resource does not exist.</li>
 * <li> This resource is not local.</li>
 * <li> The workspace is not in sync with the corresponding location
 *       in the local file system and force is <code>false</code>.</li>
 * </ul>
 */
public InputStream getContents(boolean force) throws CoreException;
/**
 * Returns a constant identifying the character encoding of this file, or 
 * ENCODING_UNKNOWN if it could not be determined.  The returned constant
 * will be one of the ENCODING_* constants defined on IFile.
 * 
 * This method attempts to guess the file's character encoding by analyzing
 * the first few bytes of the file.  If no identifying pattern is found at the 
 * beginning of the file, ENC_UNKNOWN will be returned.  This method will
 * not attempt any complex analysis of the file to make a guess at the 
 * encoding that is used.
 * 
 * @return The character encoding of this file
 * @exception CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This resource does not exist.</li>
 * <li> This resource could not be read.</li>
 * <li> This resource is not local.</li>
 * <li> The corresponding location in the local file system
 *       is occupied by a directory.</li>
 * </ul>
 * @deprecated use IFile#getCharset instead
 */
public int getEncoding() throws CoreException;
/**
 * Returns the full path of this file. 
 * This refinement of the corresponding <code>IStorage</code> and <code>IResource</code>
 * methods links the semantics of resource and storage object paths such that
 * <code>IFile</code>s always have a path and that path is relative to the
 * containing workspace.
 *
 * @see IResource#getFullPath
 * @see IStorage#getFullPath
 */
public IPath getFullPath();

/**
 * Returns the name of this file. 
 * This refinement of the corresponding <code>IStorage</code> and <code>IResource</code>
 * methods links the semantics of resource and storage object names such that
 * <code>IFile</code>s always have a name and that name equivalent to the
 * last segment of its full path.
 *
 * @see IResource#getName
 * @see IStorage#getName
 */
public String getName();
/**
 * Returns whether this file is read-only.
 * This refinement of the corresponding <code>IStorage</code> and <code>IResource</code>
 * methods links the semantics of read-only resources and read-only storage objects.
 *
 * @see IResource#isReadOnly
 * @see IStorage#isReadOnly
 */
public boolean isReadOnly();


/**
 * Sets the charset for this file.
 * <p>
 * <b>Note</b>: This method is part of early access API that may well 
 * change in incompatible ways until it reaches its finished form. 
 * </p> 
 * 
 * @param newCharset a charset name, or <code>null</code>
 * @throws CoreException if this method fails. Reasons include:
 * <ul>
 * <li> This resource does not exist.</li>
 * </ul>
 * @see #getCharset
 * @since 3.0
 */
public void setCharset(String newCharset) throws CoreException;

public boolean exists();

public IPath getLocation();
}
