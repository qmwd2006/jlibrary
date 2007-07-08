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
package org.jlibrary.client.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.core.config.ConfigException;
import org.jlibrary.core.config.JLibraryProperties;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author martin
 *
 * Configuration of opened repositories
 */
public class ItemsOpenedRegistry {

	static Logger logger = LoggerFactory.getLogger(ItemsOpenedRegistry.class);
	
	private List itemsRegistry = new ArrayList();
	private List repositoriesConfig = new ArrayList();
	
	private static ItemsOpenedRegistry instance;
	
	private ItemsOpenedRegistry() {}
	
	public void addRepository(Repository repository, 
							  String repositoryName) {
		
		Item item = new Item();
		item.setRepositoryId(repository.getId());
		item.setRepositoryLocation(repository.getServerProfile().getLocation());
		item.setUser(repository.getTicket().getUser().getName());
		item.setPassword(repository.getTicket().getUser().getPassword());
		item.setDisconnected(!repository.isConnected());
		item.setName(repositoryName);
		
		repositoriesConfig.add(item);
	}
	
	public void removeRepository(Repository repository) {
		
		Item item = new Item();
		item.setRepositoryId(repository.getId());
		item.setRepositoryLocation(repository.getServerProfile().getLocation());
		
		repositoriesConfig.remove(item);
	}
	
	public void closeRepository(Repository repository) {
		
		Item item = new Item();
		item.setRepositoryId(repository.getId());
		item.setRepositoryLocation(repository.getServerProfile().getLocation());
		
		Item realItem = (Item)repositoriesConfig.get(repositoriesConfig.indexOf(item));
		realItem.setDisconnected(true);
	}
	
	public void reconnectToRepository(Repository repository) {
		
		Item item = new Item();
		item.setRepositoryId(repository.getId());
		item.setRepositoryLocation(repository.getServerProfile().getLocation());
		
		Item realItem = (Item)repositoriesConfig.get(repositoriesConfig.indexOf(item));
		realItem.setDisconnected(false);
	}
	
	public void saveConfig() throws ConfigException {
                
		String home = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_HOME); 
		//String home = System.getProperty("user.home");
		String separator = System.getProperty("file.separator");
		
		File f = new File(home + separator + ".jlibrary");
                f.mkdirs();
		if (!f.exists()) {
			throw new ConfigException(".jlibrary directory don't found");
		}

		StringBuffer path = new StringBuffer(f.getAbsolutePath());
		path.append(separator);
		path.append(".registry.xml");

		XStream xstream = new XStream();
		ClassLoader clientClassLoader = 
			JLibraryPlugin.getDefault().getClass().getClassLoader();
		xstream.setClassLoader(clientClassLoader);
		
		try {
			xstream.toXML(this,new FileWriter(new File(path.toString())));
		} catch (IOException e) {
			throw new ConfigException("Error saving the config");
		}
	}
	
	public static void loadConfig() throws ConfigException {

		if (instance != null) {
			return;
		}
		String home = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_HOME); 
		//String home = System.getProperty("user.home");
		String separator = System.getProperty("file.separator");
		
		File f = new File(home + separator + ".jlibrary");
                f.mkdirs();
		if (!f.exists()) {
			throw new ConfigException(".jlibrary directory don't found");
		}

		StringBuffer path = new StringBuffer(f.getAbsolutePath());
		path.append(separator);
		path.append(".registry.xml");

		XStream xstream = new XStream();
		ClassLoader clientClassLoader = 
			JLibraryPlugin.getDefault().getClass().getClassLoader();
		xstream.setClassLoader(clientClassLoader);
		
		try {
			File file = new File(path.toString());
			if (!file.exists()) {
				return;
			}
			instance = (ItemsOpenedRegistry)xstream.fromXML(new FileReader(file));
			
			if (instance.repositoriesConfig == null) {
				instance.repositoriesConfig = new ArrayList();
			}
			if (instance.itemsRegistry == null) {
				instance.itemsRegistry = new ArrayList();
			}
		} catch (IOException e) {
			throw new ConfigException("Error saving the config");
		}
	}
	
	public List getOpenedRepositories(IProgressMonitor monitor) {
		
		List repositories = new ArrayList();
		boolean save = false;
		
		monitor.beginTask(Messages.getMessage("job_loading_repositories"),
						  repositoriesConfig.size()+1);
		Iterator it = repositoriesConfig.iterator();
		while(it.hasNext()) {
			monitor.worked(1);
			Item item = (Item)it.next();
			
			String name = item.getName();
			monitor.subTask(Messages.getAndParseValue(
					"job_loading_repository","%1",name));
			
			boolean disconnected = item.getDisconnected();
			
			Credentials credentials = new Credentials();
			credentials.setUser(item.getUser());
			credentials.setPassword(item.getPassword());
			
			ServerProfile profile = ProfileUtils.getProfile(item.getRepositoryLocation());

			if (disconnected) {
				// Add a disconnected repository instance
				Repository repository = new Repository();
				// Generate a random id
				repository.setId(item.getRepositoryId());
				repository.setConnected(false);
				repository.setServerProfile(profile);
				repositories.add(repository);
				
				Ticket ticket = new Ticket();
				User user = new User();
				user.setName(item.getUser());
				user.setPassword(item.getPassword());
				ticket.setUser(user);
				repository.setTicket(ticket);
				ticket.setRepositoryId(repository.getId());
				ticket.setAutoConnect(true);

				continue;
			}

			RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			SecurityService securityService = JLibraryServiceFactory.getInstance(profile).getSecurityService();
			Ticket ticket = null;
			try {
				ticket = securityService.login(credentials,item.getName());
				ticket.setRepositoryId(item.getRepositoryId());
				ticket.setAutoConnect(true);
			} catch (final ConnectException ce) {
				ce.printStackTrace();
				save = true;
				
				// The server isn't available
				Repository repository = new Repository();
				repository.setId(item.getRepositoryId());
				repository.setConnected(false);
				repository.setServerProfile(profile);
				repository.setTicket(ticket);
				repository.setName(name);
				
				repositories.add(repository);

				ticket = new Ticket();
				User user = new User();
				user.setName(item.getUser());
				user.setPassword(item.getPassword());
				ticket.setUser(user);
				ticket.setAutoConnect(true);
				repository.setTicket(ticket);
				
				continue;
			} catch (Exception e) {
				save = true;
                logger.error(e.getMessage(),e);
				it.remove();
				continue;
			}
			
			try {
				Repository repository = service.findRepository(item.getName(), 
															   ticket);
				repository.setConnected(true);
				repository.setServerProfile(profile);
				repository.setTicket(ticket);
				repositories.add(repository);
			} catch (Exception e) {
				save = true;
				it.remove();
			}	
		}
		if (save) {
			try {
				saveConfig();
			} catch (ConfigException e) {
                logger.error(e.getMessage(),e);
			}
		}
		monitor.done();
		return repositories;
	}
	
	public static ItemsOpenedRegistry getInstance() {
		
		if (instance == null) {
			instance = new ItemsOpenedRegistry();
		}
		return instance;
	}
	
	private static class Item {
		
		private String type;
		private String id;
		private String name;
		private String repositoryId;
		private String repositoryLocation;
		private String user;
		private String password;
		private int profileType;
		private boolean disconnected;
		private boolean jLibraryRepository;
		
		public boolean isJLibraryRepository() {
			return jLibraryRepository;
		}
		public void setJLibraryRepository(boolean libraryRepository) {
			jLibraryRepository = libraryRepository;
		}
		/**
		 * @return Returns the id.
		 */
		public String getId() {
			return id;
		}
		/**
		 * @param id The id to set.
		 */
		public void setId(String id) {
			this.id = id;
		}
		/**
		 * @return Returns the repositoryId.
		 */
		public String getRepositoryId() {
			return repositoryId;
		}
		/**
		 * @param repositoryId The repositoryId to set.
		 */
		public void setRepositoryId(String repositoryId) {
			this.repositoryId = repositoryId;
		}
		/**
		 * @return Returns the repositoryLocation.
		 */
		public String getRepositoryLocation() {
			return repositoryLocation;
		}
		/**
		 * @param repositoryLocation The repositoryLocation to set.
		 */
		public void setRepositoryLocation(String repositoryLocation) {
			this.repositoryLocation = repositoryLocation;
		}
		/**
		 * @return Returns the type.
		 */
		public String getType() {
			return type;
		}
		/**
		 * @param type The type to set.
		 */
		public void setType(String type) {
			this.type = type;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {

			if (type == null) {
				// repository
				return repositoryId.equals(((Item)obj).getRepositoryId());
			} else {
				// another item
				return id.equals(((Item)obj).getId());
			}
		}
		/**
		 * @return Returns the password.
		 */
		public String getPassword() {
			return password;
		}
		/**
		 * @param password The password to set.
		 */
		public void setPassword(String password) {
			this.password = password;
		}
		/**
		 * @return Returns the user.
		 */
		public String getUser() {
			return user;
		}
		/**
		 * @param user The user to set.
		 */
		public void setUser(String user) {
			this.user = user;
		}
		public boolean getDisconnected() {
			return disconnected;
		}
		public void setDisconnected(boolean disconnected) {
			this.disconnected = disconnected;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getProfileType() {
			return profileType;
		}
		public void setProfileType(int profileType) {
			this.profileType = profileType;
		}
	}
}
