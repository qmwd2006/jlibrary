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
package org.jlibrary.client.ui.notes;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.User;

/**
 * @author martin
 *
 * Search view label provider
 */
public class NotesLabelProvider extends LabelProvider  {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		
		Note note = (Note)element;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		
		User user = (User)
				MembersRegistry.getInstance().getMember(note.getCreator());
		if (user.getId().equals(User.ADMIN_CODE)) {
			buffer.append(Messages.getMessage(User.ADMIN_NAME));
		} else {
			buffer.append(user.getName());
		}
		buffer.append("] ");
		buffer.append(sdf.format(note.getDate()));
		buffer.append(" : ");
		
		if (note.getNote().length() > 50) {
			buffer.append(note.getNote().substring(0,50));
			buffer.append("...");
		} else {		
			buffer.append(note.getNote());
		}
		
		return buffer.toString();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		
		return SharedImages.getImage(SharedImages.IMAGE_NOTE);
	}
}
