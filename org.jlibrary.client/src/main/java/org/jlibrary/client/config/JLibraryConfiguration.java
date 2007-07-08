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

import org.jlibrary.core.config.JLibraryProperties;

/**
 * @author Martín Pérez 
 *
 * Holder class for configuration properties
 */
public class JLibraryConfiguration {

	private String jLibraryHome = "/jlibrary";
	private String jLibraryRepositoriesHome  = "/jlibrary/repositories";	    

	/**
	 * Loads a JLibrary configuration file and returns and instance of 
	 * JLibraryConfiguration with that configuration loaded
	 *
	 * @return JLibraryConfiguration instance
	 */
	public static JLibraryConfiguration loadConfig() {

		String home = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_HOME); 
		File configHome = new File(home+"/conf");
		if (!(configHome.exists())) {
			configHome.mkdir();
		}
		
		JLibraryConfiguration instance = new JLibraryConfiguration();
                
        String value = JLibraryProperties.getProperty(
		JLibraryProperties.JLIBRARY_REPOSITORIES_HOME);
        if (value != null){
            instance.jLibraryRepositoriesHome = value;
        }
        
        value = JLibraryProperties.getProperty(
		JLibraryProperties.JLIBRARY_HOME);
        if (value != null){
            instance.jLibraryHome = value;
        }
                
		return instance;
	}

	public String getJLibraryHome() {
		return jLibraryHome;
	}


	public String getJLibraryRepositoriesHome() {
		return jLibraryRepositoriesHome;
	}

	public void setInternalServerRepositoriesHome(String value) {
		
		String newHome = value;
		String newRepositoriesHome = value + "/" + "repositories";

		File f = new File(newHome);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				return;
			}
		}
		
		f = new File(newRepositoriesHome);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				return;
			}
		}		
		/*
		JLibraryProperties.setProperty(JLibraryProperties.JLIBRARY_HOME,value);
		JLibraryProperties.setProperty(JLibraryProperties.JLIBRARY_REPOSITORIES_HOME,
									   newRepositoriesHome);
		*/
		jLibraryHome = newHome;
		jLibraryRepositoriesHome = newRepositoriesHome;
	}
}
