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
package org.jlibrary.client.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityService;

/**
 * @author martin
 *
 * Editor input for rol objects
 */
public class RolEditorInput extends JLibraryEditorInput
{

	private Rol rol;

	public RolEditorInput(Rol rol) {
		
		this.rol = rol;
	}
	
	public String getName() {
		
		return rol.getName();
	}
	
	public String getToolTipText() {

		return rol.getDescription();
	}
	
	public Rol getRol() {
		
		return rol;
	}
	
	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		memento.putString("REPOSITORY", rol.getRepository());
		memento.putString("ROLE_ID", rol.getId());
	}
	
	public boolean exists()
	{
		Repository repository = RepositoryRegistry.getInstance().
			getRepository(rol.getRepository());
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		SecurityService service = JLibraryServiceFactory.getInstance(profile).getSecurityService();
		try
		{
			rol = service.findRol(ticket,rol.getId());
			if(rol == null)
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
		return SharedImages.getImageDescriptor(SharedImages.IMAGE_ROL);
	}

	public IPersistableElement getPersistable()
	{
		Repository repository = RepositoryRegistry.getInstance().
			getRepository(rol.getRepository());
		if (repository == null) {
			return null;
		}
		if(repository.getTicket().isAutoConnect())
			return this;
		return null;
	}

}
