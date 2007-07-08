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

import java.io.File;
import java.io.InputStream;

/**
 * @author martin
 *
 * This interface defines interfac methods common to all extraction classes
 */
public interface Extractor {

	/**
	 * Extracts the text of a given file
	 * 
	 * @param f File that contains the text
	 * @return String Extracted text
	 * @throws ExtractionException If the text can't be extracted
	 */
	public String extractText(File f) throws ExtractionException;

	/**
	 * Extracts the text of a given file
	 * 
	 * @param is Input Stream with the file content
	 * 
	 * @return String Extracted text
	 * 
	 * @throws ExtractionException If the text can't be extracted
	 */
	public String extractText(InputStream is) throws ExtractionException;	
	
	/**
	 * Extract header metadata information
	 * 
	 * @param f File to extract metadata
	 * 
	 * @return Metadata for the file
	 * 
	 * @throws ExtractionException If the metadata can't be parsed
	 */
	public HeaderMetaData extractHeader(File f) throws ExtractionException;
	
	/**
	 * Extract header metadata information
	 * 
	 * @param is Input Stream with the file content
	 * 
	 * @return Metadata for the file
	 * 
	 * @throws ExtractionException If the metadata can't be parsed
	 */
	public HeaderMetaData extractHeader(InputStream is) throws ExtractionException;	
}
