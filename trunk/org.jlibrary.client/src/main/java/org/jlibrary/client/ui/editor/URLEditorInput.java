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
package org.jlibrary.client.ui.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.util.URL;

/**
 * @author martin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class URLEditorInput extends JLibraryEditorInput
	implements IURLEditorInput
{
	private URL url;

	/**
	 * @param file
	 */
	public URLEditorInput(URL url) {

		super();
		this.url = url;
	}

	public URLEditorInput() {

		super();
	}
	
	public URL getURL() {
		return url;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		
		return SharedImages.getImageDescriptor(SharedImages.IMAGE_HTML);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		
		if (url != null) {
			return url.toString();
		} else {
			return Messages.getMessage("blank_page");
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		
		if (url != null) {
			return url.toString();
		} else {
			return Messages.getMessage("blank_page_description");
		}
	}
	/**
	 * @param url The url to set.
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.FileEditorInput#hashCode()
	 */
	public int hashCode() {

		if (url != null) {
			return url.hashCode();
		}
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.part.FileEditorInput#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		
		if (!(obj instanceof URLEditorInput)) {
			return false;
		}

		if (url == null) {
			return false;
		}
		
		return url.equals(((URLEditorInput)obj).url);
	}
	
	public IPath getPath() {

		if (url == null) {
			return new Path("");
		}
		return url.getFullPath();
	}
	
	public Object getAdapter(Class adapter)
	{
		return url;
	}

	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		if ((url != null) && (url.getFullPath() != null)) {
			memento.putString("URL", url.getFullPath().toString());
		} else {
			memento.putString("URL", "");
		}
	}
	
	public boolean exists()
	{
		return true;
	}

	public IPersistableElement getPersistable()
	{
		return this;
	}

}
