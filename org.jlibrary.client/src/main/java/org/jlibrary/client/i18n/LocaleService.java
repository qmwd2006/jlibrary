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
package org.jlibrary.client.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.util.ResourceLoader;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Service used for deal with internationalization behaviour.
 */
public class LocaleService {

	static Logger logger = LoggerFactory.getLogger(LocaleService.class);
	
	private static LocaleService instance;
	
	private static final String[] supportedUILanguages = new String[]{"es","en"}; 
	
	private static final String[] supportedDocumentLanguages = 
		new String[]{DocumentMetaData.UNKNOWN_LANGUAGE, 
					 "es","en","ca","eu","gl","va","de","fr","du",
					 "fi","no","sw","ru","it","po"
					};
	
	public static final String LOCALE_PREFERENCE ="locale.preference";
	
	private static String[] languageDescriptions;
	private static String[] sortedLanguageDescriptions;
	private static String[] uiDescriptions;
	
	// Locale
	private Locale locale;
	
	// Resource bundle
	private Properties bundleProperties;
	
	/**
	 * Default constructor. It loads tries to load the locale, 
	 * resources bundle, supported languages, etc.
	 */
	public LocaleService() {

		locale = loadLocale();
	}
	
	private void initResourceBundle() {
		
		bundleProperties = new Properties();
		String messagesFile = "/resources/messages/messages.properties";		
		
		try {
			Bundle rcpBundle = Platform.getBundle("org.jlibrary.client");
			Path path = new Path("$nl$" + messagesFile);
			URL url = FileLocator.find(rcpBundle,path,null);

			bundleProperties.load(url.openStream());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			
			// Try to find an exit scape
			InputStream is = ResourceLoader.getResourceAsStream(messagesFile);
			try {
				bundleProperties.load(is);
				is.close();
			} catch (IOException ioe) {
				logger.error(e.getMessage(),e);
			}
		}
		
		languageDescriptions = new String[supportedDocumentLanguages.length];
		for (int i = 0; i < supportedDocumentLanguages.length; i++) {
			languageDescriptions[i] = 
				bundleProperties.getProperty("language_"+supportedDocumentLanguages[i]);
		}
		sortedLanguageDescriptions = new String[languageDescriptions.length];
		System.arraycopy(languageDescriptions,0,sortedLanguageDescriptions,0,languageDescriptions.length);
		Arrays.sort(sortedLanguageDescriptions);
		
		uiDescriptions = new String[supportedUILanguages.length];
		for (int i = 0; i < supportedUILanguages.length; i++) {
			uiDescriptions[i] = 
				bundleProperties.getProperty("language_"+supportedUILanguages[i]);
		}
	}
	
	/**
	 * Tries to load the locale from the user locale. This can be a locale 
	 * specified in config.ini, or using the System property -Dosgi.nl, 
	 * or using the -nl command line parameter
	 * 
	 * If the locale isn't supported, it will be used the properties
	 * file defined locale.
	 * 
	 * If there is not properties file, then it will be used the default 
	 * locale, English.
	 * 
	 * @return Locale locale used for jLibrary
	 */	
	private Locale loadLocale() {
		
		// 1st - Tries to load the current locale from the user computer.
		//
		// If the user has entered the -nl parameter, the default locale
		// will be the specified locale
		locale = Locale.getDefault();
		if (isSupportedLocale(locale)) {
			return locale;
		}
		
		// 3rd - Set the default locale, English 
		return Locale.ENGLISH;
	}
	
	private boolean isSupportedLocale(Locale locale) {
		
		String language = locale.getLanguage();
		for (int i = 0; i < supportedUILanguages.length; i++) {
			if (language.equals(supportedUILanguages[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a localized String
	 * 
	 * @param key Key of the String
	 * @return Localized String
	 */
	public String getValue(String key) {
		
		if (bundleProperties == null) {
			initResourceBundle();
		}
		return bundleProperties.containsKey(key)? bundleProperties.getProperty(key) :"!"+key+"!";
	}

	/**
	 * Returns a localized and parsed String
	 * 
	 * @param key Key of the String
	 * @param code Key code to parse
	 * @param newValue Value to be introduced
	 * @return Localized and parsed String
	 */
	public String getAndParseValue(String key, String code, String newValue) {
		
		if (bundleProperties == null) {
			initResourceBundle();
		}
		
		String value = bundleProperties.getProperty(key);
		if (value == null) {
			value = "!"+key+"!";
		} else {
			if (value.indexOf(code) != -1) {
				value = StringUtils.replace(value,code,newValue);
			}
		}
		return value;
	}
	
	/**
	 * Returns the prefered user locale
	 * 
	 * @return Locale user locale
	 */
	public Locale getUserLocale() {
		
		return locale;
	}
	
	/**
	 * Return the unique instance of the locale service
	 * 
	 * @return LocaleService Service for internationalization
	 */
	public static LocaleService getInstance() {
		
		if (instance == null) {
			instance = new LocaleService();
		}
		return instance;
	}
	
	/**
	 * Returns the supported languages for indexing documents
	 * 
	 * @return String[] An array with the language descriptions
	 */
	public String[] getSupportedDocumentLanguageDescriptions() {
		
		if (bundleProperties == null) {
			initResourceBundle();
		}
		
		return sortedLanguageDescriptions;
	}

	/**
	 * Returns the supported languages for the UI interface
	 * 
	 * @return String[] An array with the language descriptions
	 */
	public String[] getSupportedUILanguageDescriptions() {
		
		if (bundleProperties == null) {
			initResourceBundle();
		}
		
		return uiDescriptions;
	}
	
	/**
	 * Returns the language for a given description
	 * 
	 * @param description Description of the language
	 * 
	 * @return String key of the language
	 */
	public String getLanguageForDescription(String description) {
		
		if (bundleProperties == null) {
			initResourceBundle();
		}
		
		for (int i = 0; i < languageDescriptions.length; i++) {
			if (languageDescriptions[i].equals(description)) {
				return supportedDocumentLanguages[i];
			}
		}
		return null;
	}
	
	/**
	 * Returns the description for a given language
	 * 
	 * @param language Key of the language
	 * 
	 * @return String Description of the language
	 */
	public String getDescriptionForLanguage(String language) {
		
		if (bundleProperties == null) {
			initResourceBundle();
		}
		
		for (int i = 0; i < supportedDocumentLanguages.length; i++) {
			if (supportedDocumentLanguages[i].equals(language)) {
				return languageDescriptions[i];
			}
		}
		return languageDescriptions[0];
	}
	
	/**
	 * Tells us if a language is supported for indexing documents
	 * 
	 * @param language Language to query
	 * 
	 * @return <code>true</code> if the language is supported and
	 * <code>false</code> otherwise
	 */
	public boolean isSupportedDocumentLanguage(String language) {
		
		if (bundleProperties == null) {
			initResourceBundle();
		}
		
		for (int i = 0; i < supportedDocumentLanguages.length; i++) {
			if (language.equals(supportedDocumentLanguages[i])) {
				return true;
			}
		}
		return false;
	}
}
