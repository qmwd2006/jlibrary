/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;

public class CategoryEditorInput extends JLibraryEditorInput
{
	
	Category category;
	
	public CategoryEditorInput(Category category) {
		
		this.category = category;
	}
	
	public String getName()
	{
		return category.getName();
	}
	
	public String getToolTipText()
	{
		return category.getDescription();
	}
	
	public Category getCategory() {
		
		return category;
	}

	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		memento.putString("REPOSITORY", category.getRepository());
		memento.putString("CATEGORY_ID", category.getId());
	}
	
	public boolean exists()
	{
		Repository repository = RepositoryRegistry.getInstance().
			getRepository(category.getRepository());
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try
		{
			category = service.findCategoryById(ticket,category.getId());
			if(category == null)
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
		return SharedImages.getImageDescriptor(SharedImages.IMAGE_CATEGORY);
	}

	public IPersistableElement getPersistable()
	{
		Repository repository = RepositoryRegistry.getInstance().
			getRepository(category.getRepository());
		if (repository == null) {
			return null;
		}
		if(repository.getTicket().isAutoConnect())
			return this;
		return null;
	}
}
