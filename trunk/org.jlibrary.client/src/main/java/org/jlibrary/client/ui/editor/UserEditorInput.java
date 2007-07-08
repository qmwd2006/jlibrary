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
package org.jlibrary.client.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityService;

/**
 * @author martin
 *
 * Editor input for user objects
 */
public class UserEditorInput extends JLibraryEditorInput
{

	private User user;

	public UserEditorInput(User user) {
		
		this.user = user;
	}
	
	public String getName() {
		
		return user.getName();
	}
	
	public String getToolTipText() {

		return user.toString();
	}
	
	public User getUser() {
		
		return user;
	}
	
	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		memento.putString("REPOSITORY", user.getRepository());
		memento.putString("USER_ID", user.getId());
	}
	
	public boolean exists()
	{
		Repository repository = RepositoryRegistry.getInstance().
		getRepository(user.getRepository());
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		SecurityService service = JLibraryServiceFactory.getInstance(profile).getSecurityService();
		try
		{
			user = service.findUserById(ticket,user.getId());
			if(user == null)
				return false;
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	public ImageDescriptor getImageDescriptor()
	{
		return SharedImages.getImageDescriptor(SharedImages.IMAGE_USER);
	}

	public IPersistableElement getPersistable()
	{
		Repository repository = RepositoryRegistry.getInstance().
			getRepository(user.getRepository());
		if (repository == null) {
			return null;
		}
		if(repository.getTicket().isAutoConnect())
			return this;
		return null;
	}
}
