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
package org.jlibrary.client;

import org.jlibrary.client.i18n.LocaleService;

/**
 * @author martin
 *
 * Class that will be used to get user interface messages
 */
public class Messages {

	/**
	 * Returns a specified message
	 * 
	 * @param key Key of the message
	 * @return Message
	 */
	public static String getMessage(String key) {
		
		return LocaleService.getInstance().getValue(key);
	}

	/**
	 * Returns a localized and parsed String
	 * 
	 * @param key Key of the String
	 * @param code Key code to parse
	 * @param newValue Value to be introduced
	 * @return Localized and parsed String
	 */
	public static String getAndParseValue(String arg0, String arg1, String arg2) {
		return LocaleService.getInstance().getAndParseValue(arg0, arg1, arg2);
	}
}
