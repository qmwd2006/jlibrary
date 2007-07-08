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

import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.factory.LocalServicesFactory;
import org.jlibrary.core.factory.RemoteHTTPServicesFactory;
import org.jlibrary.core.profiles.HTTPServerProfile;
import org.jlibrary.core.profiles.LocalServerProfile;

/**
 * Utility class for returning a candidate profile implementation given a location
 * 
 * @author mpermar
 *
 */
public class ProfileUtils {

	public static ServerProfile getProfile(String location) {
		
		if (location.startsWith("http://")) {
			ServerProfile profile = new HTTPServerProfile() {
				@Override
				public String getServicesFactory() {
					return RemoteHTTPServicesFactory.class.getName();
				}
			};
			profile.setLocation(location);
			profile.setName(location);
			return profile;
		} else if (location.equals(ClientConfig.PROFILE_LOCAL_KEY)) {
			ServerProfile profile = new LocalServerProfile() {
				@Override
				public String getServicesFactory() {
					return LocalServicesFactory.class.getName();
				}
			};

			return profile;
		}
		return null;
	}
}
