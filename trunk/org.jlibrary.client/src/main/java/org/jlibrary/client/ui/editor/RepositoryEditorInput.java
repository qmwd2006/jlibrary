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

/**
 * @author martin
 *
 * Editor input for repository objects
 */
public class RepositoryEditorInput extends JLibraryEditorInput
{

	private Repository repository;

	public RepositoryEditorInput(Repository repository) {
		
		this.repository = repository;
	}
	
	public String getName() {
		
		return repository.getName();
	}
	
	public String getToolTipText() {

		return repository.getDescription();
	}
	
	public Repository getRepository() {
		
		return repository;
	}
	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		memento.putString("REPOSITORY_ID", repository.getId());
	}
	
	public boolean exists()
	{
		try
		{
			repository = RepositoryRegistry.getInstance().getRepository(repository.getId());
			if(repository == null)
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
		return SharedImages.getImageDescriptor(SharedImages.IMAGE_NODE_REPOSITORY);
	}

	public IPersistableElement getPersistable()
	{
		if ((repository == null) || (repository.getTicket() == null)) {
			return null;
		}
		if(repository.getTicket().isAutoConnect())
			return this;
		return null;
	}

}
