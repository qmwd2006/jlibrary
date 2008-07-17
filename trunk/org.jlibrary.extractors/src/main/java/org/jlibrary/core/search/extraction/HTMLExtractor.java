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
package org.jlibrary.core.search.extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import org.htmlparser.util.ParserException;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.search.extraction.html.HTMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Word text extraction class
 */
public class HTMLExtractor implements Extractor {

	static Logger logger = LoggerFactory.getLogger(HTMLExtractor.class);
	
	/**
	 * @see org.jlibrary.core.indexer.extractors.Extractor#extractText(java.io.File)
	 */
	public String extractText(File f) throws ExtractionException {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			return extractText(fis);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);			
		} catch (ExtractionException ee) {
			throw ee;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new ExtractionException(e);
				}
			}
		}
	}
	
	/**
	 * @see org.jlibrary.core.indexer.extractors.Extractor#extractText(java.io.InputStream)
	 */
	public String extractText(InputStream is) throws ExtractionException {

		try {
			String text = HTMLParser.extractText(is,"iso-8859-1");
			return text;
		} catch (ParserException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}
	}	
	
	/**
	 * @see org.jlibrary.core.indexer.extractors.Extractor#getReader(java.io.File)
	 */
	public Reader getReader(File f) throws ExtractionException {
		
		throw new ExtractionException("HTMLExtractor can't use getReader() method");		
	}

	/**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.File)
	 */
	public HeaderMetaData extractHeader(File f) throws ExtractionException {
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			return extractHeader(fis);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);			
		} catch (ExtractionException ee) {
			throw ee;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new ExtractionException(e);
				}
			}
		}
	}

	/**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.InputStream)
	 */
	public HeaderMetaData extractHeader(InputStream is) throws ExtractionException {
		
		try {
			HeaderMetaData header = 
				HTMLParser.extractHeader(is,"iso-8859-1");
			return header;
		} catch (ParserException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}
	}	
	
	/**
	 * Extract needed resources from a file. Resources can be images, CSS 
	 * stylesheets, ... In summary, all the data needed to render the HTML
	 * document on the screen
	 * 
	 * @param f File to be parsed
	 * 
	 * @return String[] File references to the resources
	 * 
	 * @throws ExtractionException If the resources can't be extracted
	 */
	public String[] extractResources(File f) throws ExtractionException  {

		try {
			FileInputStream fis = new FileInputStream(f);
			String[] paths = 
				HTMLParser.extractResourcePaths(fis,"iso-8859-1");
			fis.close();
			return paths;
		} catch (ParserException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}		
	}
	
	/**
	 * Changes the path from all the references from a file if that references
	 * are pointing to some of the resources passed as parameters
	 * 
	 * @param f File that we want to parse
	 * @param directory Document's parent directory
	 * @param resources Set of resources from which we want to change their 
	 * references
	 * 
	 * @throws ExtractionException If there is some error during the parsing
	 * process
	 */
	public void changePaths(File f, 
							Directory directory, 
							List resources) throws ExtractionException  {

		try {
			FileInputStream fis = new FileInputStream(f);
			HTMLParser.setLocalPaths(f,"iso-8859-1",directory,resources);
			fis.close();
		} catch (ParserException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}		
	}	
	
	/**
	 * Extract needed resources from an URL. Resources can be images, CSS 
	 * stylesheets, ... In summary, all the data needed to render the HTML
	 * document on the screen
	 * 
	 * @param url URL to be parsed
	 * 
	 * @return String[] String references to the resources
	 * 
	 * @throws ExtractionException If the resources can't be extracted
	 */
	public String[] extractResources(URL url) throws ExtractionException  {

		try {
			InputStream is = url.openStream();
			String[] resources = 
				HTMLParser.extractResourcePaths(is,"iso-8859-1");
			is.close();
			return resources;
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			throw new ExtractionException(ioe);
		} catch (ParserException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}		
	}	
}
