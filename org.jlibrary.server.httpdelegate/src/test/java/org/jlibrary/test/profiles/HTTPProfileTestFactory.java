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
package org.jlibrary.test.profiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.profiles.HTTPServerProfile;

/**
 * Returns a profile for using the HTTP delegate
 * 
 * @author mpermar
 *
 */
public class HTTPProfileTestFactory implements ProfileTestFactory {

	/**
	 * Returns the server profile used for the tests
	 * 
	 * @return ServerProfile Server profile
	 */
	public ServerProfile getServerProfile() {
		
		HTTPServerProfile profile = new HTTPServerProfile();
		
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("test.properties");
			Properties properties = new Properties();
			properties.load(is);
			
			String location = (String)properties.get("test.server.profile.location");
			profile.setLocation(location);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return profile;
	}
	
}
