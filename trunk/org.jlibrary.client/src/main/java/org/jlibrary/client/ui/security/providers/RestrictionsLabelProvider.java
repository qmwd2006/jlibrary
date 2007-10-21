/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
package org.jlibrary.client.ui.security.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.dialogs.ListDialog;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Restriction;
import org.jlibrary.core.entities.User;
	
/**
 * Label provider for our custom list implementation
 */
public class RestrictionsLabelProvider implements ITableLabelProvider {

	private Image image;

	/**
	 * Constructor
	 * 
	 * @param image Image for the list items
	 */
	public RestrictionsLabelProvider(Image image) {
		
		this.image = image;
		
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int arg1) {
		
		Restriction restriction = (Restriction)element;
		Member member = MembersRegistry.getInstance().getMember(restriction.getMember());
		if (member == null) {
			return restriction.getMember();
		}
		
		String name = member.getName();
		if ((name.equals(Group.ADMINS_GROUP_NAME)) ||
			(name.equals(Group.READERS_GROUP_NAME)) ||
			(name.equals(Group.PUBLISHERS_GROUP_NAME)) ||
			(name.equals(User.ADMIN_NAME))) {
		
			return Messages.getMessage(name);
		}
		return name;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int arg1) {
		
		if (element != ListDialog.EMPTY_LIST) {
			return image;
		} else {
			return null;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener arg0) {}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener arg0) {}


	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {}


}