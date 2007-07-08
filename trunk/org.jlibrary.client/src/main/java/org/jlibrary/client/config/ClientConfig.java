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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.core.config.ConfigException;
import org.jlibrary.core.config.JLibraryProperties;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author martin
 *
 * This class stores configuration information about the preferences of the JLibrary user 
 */
public class ClientConfig {
	
	static Logger logger = LoggerFactory.getLogger(ClientConfig.class);
	
	public static final String DEFAULT_TOOL = "default_tool";
	public static final String SYSTEM_TOOL = "system_tool";
	
	public static final String NEW_RESOURCE_DIRECTORY = "new.resource.directory";
	public static final String NEW_REPOSITORY_DIRECTORY = "new.repository.directory";
	public static final String NEW_DOCUMENT_DIRECTORY = "new.document.directory";
	public static final String EXPORT_REPOSITORY = "export.repository";
	public static final String IMPORT_REPOSITORY = "import.repository";
	public static final String EXPORT_INDEX = "export.index";
	public static final String NEW_TOOL_DIRECTORY = "new.tool.directory";
	public static final String SAVE_DOCUMENT = "save.document";
	public static final String EXPORT_WEB = "export.web";
	
	public static final String FIRST_TIME_EXECUTION = "first.time.execution";
	
	public static final String USER_LANGUAGE = "user.language";
	
	public static final String JLIBRARY_HOME = "jlibrary.home";
	
	public final static String PROFILE_LOCAL_KEY = LocalServerProfile.PROFILE_LOCAL_KEY;
	public final static String REMOTE_PROFILE_LOCATION = 
									"http://localhost:8080/jlibrary/";
	
	// Configuration files
	private static File f;
	private static File fProfiles;
	
	// Properties file
	private static Properties properties;
	
	// Profiles list
	private static List profileProperties;

	/**
	 * Constructor
	 */
	private ClientConfig() {
		
		try {
			initClientConfiguration();
		} catch (ConfigException e) {
			ErrorDialog.openError(new Shell(),
                            "ERROR",
                            Messages.getMessage("new_repository_config_can't_create"),
                            new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));

		}
	}

	/**
	 * Inits client Configuration
	 * 
	 * @throws ConfigException If couldn't init JLibrary configuration
	 */
	private void initClientConfiguration() throws ConfigException {

        String home = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_HOME); 
        logger.info("Using home : " + home);
        
		String separator = System.getProperty("file.separator");

		f = new File(home + separator + ".jlibrary");
		logger.info("Creating directory : " + 
		f.getAbsolutePath());
		
		f.mkdirs();
                
		if (!f.exists()) {
			throw new ConfigException(".jlibrary directory don't found");
		}
		
		String jLibraryHome = f.getAbsolutePath();
		logger.info("Using config home: " + jLibraryHome);
		
		StringBuffer path = new StringBuffer(f.getAbsolutePath());
		path.append(separator);
		path.append(".jlibrary-client.cfg");
		
		// create and load standard configuration
		f = new File(path.toString());
		try {
			if (!f.exists()) {
				createConfig();
			}
			loadConfig();
			properties.put(JLIBRARY_HOME, jLibraryHome);
		} catch (FileNotFoundException e) {
			throw new ConfigException(e);
		} catch (IOException e) {
			throw new ConfigException(e);
		}
		
		// create and load profiles configuration
		fProfiles = new File(home + separator + ".jlibrary");
		
		path = new StringBuffer(fProfiles.getAbsolutePath());
		path.append(separator);
		path.append(".jlibrary-profiles.cfg");		
		// create and load standard configuration
		fProfiles = new File(path.toString());
		if (!fProfiles.exists()) {
			try {
				createProfilesConfig();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ConfigException(e);
			}
		} else {
			try {
				loadProfilesConfig();
			} catch (Exception e) {
				try {
					createProfilesConfig();
				} catch (Exception e2) {
					e.printStackTrace();
					throw new ConfigException(e);
				}
			}
		}
		
		// Load items opened registry
		ItemsOpenedRegistry.loadConfig();
	}


	/**
	 * Loads the configuration file
	 */
	private void loadConfig() throws FileNotFoundException, IOException {
		
		properties = new Properties();
		FileInputStream fis = new FileInputStream(f);
		properties.load(fis);
		fis.close();
	}
	
	/**
	 * Loads the configuration file
	 */
	private void loadProfilesConfig() throws FileNotFoundException, IOException {
		
		XStream xstream = new XStream();
		ClassLoader clientClassLoader = 
			JLibraryPlugin.getDefault().getClass().getClassLoader();
		xstream.setClassLoader(clientClassLoader);
		
		FileReader reader = new FileReader(fProfiles);
		profileProperties = (ArrayList)xstream.fromXML(reader);
		reader.close();
	}

	/**
	 * Creates a default client configuration file
	 */
	private void createConfig() throws ConfigException,IOException {
		
		logger.info("Creating configuration files");
		properties = new Properties();
		
		// Load the jlibrary.properties file values
		JLibraryConfiguration config = JLibraryConfiguration.loadConfig();
		
		properties.put(NEW_REPOSITORY_DIRECTORY, config.getJLibraryRepositoriesHome());
		properties.put(NEW_DOCUMENT_DIRECTORY, config.getJLibraryRepositoriesHome());
		properties.put(EXPORT_REPOSITORY, config.getJLibraryRepositoriesHome());
		properties.put(IMPORT_REPOSITORY, config.getJLibraryRepositoriesHome());
		properties.put(EXPORT_INDEX, "true");
		properties.put(NEW_TOOL_DIRECTORY, config.getJLibraryRepositoriesHome());
		properties.put(SAVE_DOCUMENT, config.getJLibraryRepositoriesHome());
		properties.put(EXPORT_WEB, config.getJLibraryRepositoriesHome());
		
		logger.info("Writing config file : " + f.getAbsolutePath());
		
		FileOutputStream fos = new FileOutputStream(f);
		properties.store(fos,"JLibrary Preferences");
		fos.close();
	}

	/**
	 * Creates profiles client configuration file
	 */
	private void createProfilesConfig() throws IOException {
		
		profileProperties = new ArrayList();
		profileProperties.add(PROFILE_LOCAL_KEY);
		
		XStream xstream = new XStream();
		ClassLoader clientClassLoader = 
			JLibraryPlugin.getDefault().getClass().getClassLoader();
		xstream.setClassLoader(clientClassLoader);
		
		FileWriter writer = new FileWriter(fProfiles);
		xstream.toXML(profileProperties, writer);
		writer.close();
	}
	
	/**
	 * Saves configuration file
	 *
	 */
	private static void saveConfig() throws FileNotFoundException, IOException {
		
		if (properties == null) {
			new ClientConfig();
		}
		
		XStream xstream = new XStream();
		ClassLoader clientClassLoader = 
			JLibraryPlugin.getDefault().getClass().getClassLoader();
		xstream.setClassLoader(clientClassLoader);
		
		FileWriter writer = new FileWriter(fProfiles);
		xstream.toXML(profileProperties, writer);
		writer.close();
		
		try {
			FileOutputStream fos = new FileOutputStream(f);
			properties.store(fos, "JLibrary Preferences");
			fos.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * Returns the value of a preference. Preferences have to be some of the public constants
	 * exposed by this class
	 * 
	 * @param key Key of the value. Must have to be some of the public constants exposed 
	 * by this class
	 * @return Value for that key
	 */
	public static String getValue(String key) {
		
		if (properties == null) {
			new ClientConfig();
		}		
		return properties.getProperty(key);
	}

	/**
	 * Saves a preference value
	 * 
	 * @param key Key of the value. Must have to be some of the public constants exposed 
	 * by this class
	 * @param value Value for that key
	 */
	public static void setValue(String key, String value) {
		
		if (properties == null) {
			new ClientConfig();
		}
		
		properties.put(key,value);
		
		try {
			saveConfig();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}	

	public static List getServerProfiles() {
		
		if (profileProperties == null) {
			new ClientConfig();
		}
		
		List profiles = new ArrayList();
		Iterator it = profileProperties.iterator();
		while (it.hasNext()) {
			String strProfile = (String)it.next();
			ServerProfile profile = ProfileUtils.getProfile(strProfile);
			if (profile != null) {
				profiles.add(profile);
			}
		}
		return profiles;
	}
	
	public static void addProfile(ServerProfile profile) {
		
		if (profileProperties == null) {
			new ClientConfig();
		}
		profileProperties.add(0,profile.getLocation());
		
		try {
			saveConfig();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void moveToFirst(ServerProfile profile) {
		
		if (profileProperties == null) {
			new ClientConfig();
		}
		profileProperties.remove(profile.getLocation());
		profileProperties.add(0,profile.getLocation());
		
		try {
			saveConfig();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
	}

	/**
	 * @param extension
	 * @return
	 */
	public static String getDefaultToolForExtension(String extension) {
		
		String key = "EXTENSION" + extension;
		
		if (properties == null) {
			new ClientConfig();
		}	
		String property = properties.getProperty(key);
		if (property == null) {
			return "";
		}
		return property;
	}

	/**
	 * @param string
	 * @param extension
	 */
	public static void setDefaultToolForExtension(String toolId, String extension) {

		String key = "EXTENSION" + extension;
		
		if (properties == null) {
			new ClientConfig();
		}	
		properties.setProperty(key, toolId);
		
		try {
			saveConfig();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
	}


	public static String getLastOpenedCategory() {
		
		String key = "CATEGORY";
		
		if (properties == null) {
			new ClientConfig();
		}	
		String property = properties.getProperty(key);
		if (property == null) {
			return "";
		}
		return property;
	}

	public static void setLastOpenedCategory(String categoryId) {

		String key = "CATEGORY";
		
		if (properties == null) {
			new ClientConfig();
		}	
		properties.setProperty(key, categoryId);
		
		try {
			saveConfig();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}		
	}	
	
	public static void setUserLocale(Locale locale) {

		if (properties == null) {
			new ClientConfig();
		}
		
		properties.put(USER_LANGUAGE,locale.toString());
		
		try {
			saveConfig();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public static Locale getUserLocale() {
		
		if (properties == null) {
			new ClientConfig();
		}
		
		String locale = properties.getProperty(USER_LANGUAGE);
		if (locale == null) {
			return null;
		}
		return new Locale(locale);
	}
	
	public static String getInternalServerRepositoryHome() {
		
		JLibraryConfiguration config = JLibraryConfiguration.loadConfig();
		return config.getJLibraryHome();
	}
	
	public static void setInternalServerRepositoryHome(String value) {
		
		JLibraryConfiguration config = JLibraryConfiguration.loadConfig();
		config.setInternalServerRepositoriesHome(value);

	}	
}
