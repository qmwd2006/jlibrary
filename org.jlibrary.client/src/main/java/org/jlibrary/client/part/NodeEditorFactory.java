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
package org.jlibrary.client.part;

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.client.Messages;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.editor.AuthorEditorInput;
import org.jlibrary.client.ui.editor.CategoryEditorInput;
import org.jlibrary.client.ui.editor.DirectoryEditorInput;
import org.jlibrary.client.ui.editor.GroupEditorInput;
import org.jlibrary.client.ui.editor.NodeEditorInput;
import org.jlibrary.client.ui.editor.RepositoryEditorInput;
import org.jlibrary.client.ui.editor.RolEditorInput;
import org.jlibrary.client.ui.editor.URLEditorInput;
import org.jlibrary.client.ui.editor.UserEditorInput;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.util.URL;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the implementation of the ElementFactory extension point.
 * Its goals is to create EditorInput class depending on the data given.
 * It also provides static class or quickly create EditorInput.
 * @author nicolasjouanin
 *
 */public class NodeEditorFactory  implements IElementFactory 
{
	 static Logger logger = LoggerFactory.getLogger(NodeEditorFactory.class);
	 
	public static final String ID = "org.jlibrary.client.part.NodeEditorFactory";

	public IAdaptable createElement(IMemento memento)
	{
		synchronized (RepositoryRegistry.getInstance()) {
			if (!RepositoryRegistry.getInstance().isReopenedRepositories()) {
				RepositoryRegistry.getInstance().reopenRepositories();
			}
			// Wait for all the repositories to be loaded until open editors
			Job[] jobs = Job.getJobManager().find(JobTask.LOADING_REPOSITORIES);
			logger.debug("Found " + jobs.length + " jobs on the family '"
					+ JobTask.LOADING_REPOSITORIES + "'");
			for (int i = 0; i < jobs.length; i++) {
				// we're waiting for all jobs not only the running ones
				try {
					jobs[i].join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}

			}

		}
		
		try
		{
			String type = memento.getString("TYPE");
			if(type.equals(AuthorEditorInput.class.getCanonicalName()))
			{
				Repository rep = RepositoryRegistry.getInstance().
					getRepository(memento.getString("REPOSITORY"));
				ServerProfile profile = rep.getServerProfile();
				Ticket ticket = rep.getTicket();
				RepositoryService service = 
					JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				Author author = service.findAuthorById(ticket, memento.getString("AUTHOR_ID"));
				return createEditorInput(author);
			}
			if(type.equals((CategoryEditorInput.class.getCanonicalName())))
			{
				Repository rep = RepositoryRegistry.getInstance().
				getRepository(memento.getString("REPOSITORY"));
				ServerProfile profile = rep.getServerProfile();
				Ticket ticket = rep.getTicket();
				RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				Category category = service.findCategoryById(ticket, memento.getString("CATEGORY_ID"));
				return createEditorInput(category);
			}
			if(type.equals((DirectoryEditorInput.class.getCanonicalName())))
			{
				Repository repository = RepositoryRegistry.getInstance().
				getRepository(memento.getString("REPOSITORY"));
				ServerProfile profile = repository.getServerProfile();
				Ticket ticket = repository.getTicket();
				RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				Directory directory = service.findDirectory(ticket,memento.getString("DIRECTORY_ID"));
				return createEditorInput(directory);
			}			
			if(type.equals((URLEditorInput.class.getCanonicalName())))
			{
				String str = memento.getString("URL");
				if ((str == null) || (str.equals(""))) {
					return new URLEditorInput();
				} else {
					try {
						URL url = new URL(str);
						return createEditorInput(url);
					} catch (MalformedURLException e) {
						logger.error(e.getMessage());
						return new URLEditorInput();
					}
				}
			}
			if(type.equals((GroupEditorInput.class.getCanonicalName())))
			{
				Repository repository = RepositoryRegistry.getInstance().
								getRepository(memento.getString("REPOSITORY"));
				ServerProfile profile = repository.getServerProfile();
				Ticket ticket = repository.getTicket();
				SecurityService service = JLibraryServiceFactory.getInstance(profile).getSecurityService();
				Group group = service.findGroupById(ticket,memento.getString("GROUP_ID"));
				return createEditorInput(group);
			}
			if(type.equals((RolEditorInput.class.getCanonicalName())))
			{
				Repository repository = RepositoryRegistry.getInstance().
				getRepository(memento.getString("REPOSITORY"));
				ServerProfile profile = repository.getServerProfile();
				Ticket ticket = repository.getTicket();
				SecurityService service = JLibraryServiceFactory.getInstance(profile).getSecurityService();
				Rol rol = service.findRol(ticket,memento.getString("ROLE_ID"));
				return createEditorInput(rol);
			}
			if(type.equals((UserEditorInput.class.getCanonicalName())))
			{
				Repository repository = RepositoryRegistry.getInstance().
				getRepository(memento.getString("REPOSITORY"));
				ServerProfile profile = repository.getServerProfile();
				Ticket ticket = repository.getTicket();
				SecurityService service = JLibraryServiceFactory.getInstance(profile).getSecurityService();
				User user = service.findUserById(ticket,memento.getString("USER_ID"));
				return createEditorInput(user);
			}
			if(type.equals((RepositoryEditorInput.class.getCanonicalName())))
			{
				Repository repository = RepositoryRegistry.getInstance().
					getRepository(memento.getString("REPOSITORY_ID"));
				return createEditorInput(repository);
			}
			if(type.equals((NodeEditorInput.class.getCanonicalName())))
			{
				Repository repository = RepositoryRegistry.getInstance().
				getRepository(memento.getString("REPOSITORY"));
				ServerProfile profile = repository.getServerProfile();
				Ticket ticket = repository.getTicket();
				RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				Node node = service.findNode(ticket, memento.getString("NODE_ID"));
				return FileEditorInput.createFileEditorInput(node);
			}
		}
		catch (SecurityException e)
		{
			ErrorDialog.openError(new Shell(),
					"ERROR",
					Messages.getMessage("author_open_error"),
					new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
			StatusLine.setErrorMessage(Messages.getMessage("author_open_error"));
		}
		catch (Exception e)
		{
            logger.error(e.getMessage(),e);
			//Caught when repository is null for example
		}
		return null;
	}

	/**
	 * Create the AuthorEditorInput class for the given editor
	 * @param author author to create editor input for
	 * @return the new AuthorEditorInput object
	 * @throws LocalCacheException
	 * @throws RepositoryException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static AuthorEditorInput createEditorInput(Author author)
		throws LocalCacheException, RepositoryException, SecurityException, IOException
	{
		return new AuthorEditorInput(author);
	}

	/**
	 * Create the CategoryEditorInput class for the given editor
	 * @param category category to create editor input for
	 * @return the new CategoryEditorInput object
	 * @throws LocalCacheException
	 * @throws RepositoryException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static CategoryEditorInput createEditorInput(Category category)
		throws LocalCacheException, RepositoryException, SecurityException,	IOException
	{
		return new CategoryEditorInput(category);
	}

	/**
	 * Create the GroupEditorInput class for the given editor
	 * @param group group to create editor input for
	 * @return the new GroupEditorInput object
	 * @throws LocalCacheException
	 * @throws RepositoryException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static GroupEditorInput createEditorInput(Group group)
		throws LocalCacheException, RepositoryException, SecurityException,	IOException
	{
		return new GroupEditorInput(group);
	}	

	/**
	 * Create the RepositoryEditorInput class for the given editor
	 * @param repository repository to create editor input for
	 * @return the new RepositoryEditorInput object
	 * @throws LocalCacheException
	 * @throws RepositoryException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static RepositoryEditorInput createEditorInput(Repository repository)
		throws LocalCacheException, RepositoryException, SecurityException, IOException
	{
		return new RepositoryEditorInput(repository);
	}	

	/**
	 * Create the DirectoryEditorInput class for the given editor
	 * @param directory Directory to create editor input for
	 * @return the new RepositoryEditorInput object
	 * @throws LocalCacheException
	 * @throws RepositoryException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static DirectoryEditorInput createEditorInput(Directory directory)
		throws LocalCacheException, RepositoryException, SecurityException, IOException
	{
		return new DirectoryEditorInput(directory);
	}	
	
	/**
	 * Create the RolEditorInput class for the given editor
	 * @param rol rol to create editor input for
	 * @return the new RolEditorInput object
	 * @throws LocalCacheException
	 * @throws RepositoryException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static RolEditorInput createEditorInput(Rol rol)
		throws LocalCacheException, RepositoryException, SecurityException,	IOException
	{
		return new RolEditorInput(rol);
	}	

	/**
	 * Create the URLEditorInput class for the given editor
	 * @param url url to create editor input for
	 * @return the new URLEditorInput object
	 * @throws LocalCacheException
	 * @throws RepositoryException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static URLEditorInput createEditorInput(URL url)
	{
		return new URLEditorInput(url);
	}	

	/**
	 * Create the UserEditorInput class for the given editor
	 * @param user user to create editor input for
	 * @return the new UserEditorInput object
	 * @throws LocalCacheException
	 * @throws RepositoryException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static UserEditorInput createEditorInput(User user)
		throws LocalCacheException,  RepositoryException,  SecurityException, IOException
	{
		return new UserEditorInput(user);
	}

}
