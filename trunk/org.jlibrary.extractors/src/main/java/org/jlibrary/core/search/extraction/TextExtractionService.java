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
package org.jlibrary.core.search.extraction;

import java.util.HashMap;

import org.jlibrary.core.entities.Types;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * <p>This class will be used to obtain text extraction classes, and also to 
 * hold more text extraction utility methods.</p>
 */
public class TextExtractionService {

	static Logger logger = LoggerFactory.getLogger(TextExtractionService.class);
	
	private static TextExtractionService service;

	private HashMap extractors = new HashMap();
	
	/**
	 * Constructor
	 */
	private TextExtractionService() throws ExtractionException {}

	/**
	 * Returns an extractor class for the given extension
	 * 
	 * @param path File path
	 * 
	 * @return Extractor Extractor implementation class
	 */
	public Extractor getExtractor(String path) throws ExtractionException {
		
		String extension = FileUtils.getExtension(path);
		String extractorClass = Types.getExtractor(extension);
		
		if ((extractorClass == null) || (extractorClass.trim().equals(""))) {
			return null;
		}		

		try {
			Extractor extractor = (Extractor)extractors.get(extractorClass);
			if (extractor == null) {
				
				Class clazz = Class.forName(extractorClass);
				extractor = (Extractor)clazz.newInstance();
				extractors.put(extractorClass,extractor);
			}
			return extractor;
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(),e);
			return null;
		} catch (InstantiationException e) {
			logger.error(e.getMessage(),e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}

	/**
	 * Returns a TextExtractionService instance
	 * 
	 * @return TextExtractionService instance
	 */
	public static TextExtractionService getInstance() throws ExtractionException {
		
		if (service == null) {
			service = new TextExtractionService();
		}
		return service;
	}
}
