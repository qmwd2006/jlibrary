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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Martín
 *
 * Word text extraction class
 */
public class RTFExtractor implements Extractor {

	static Logger logger = LoggerFactory.getLogger(RTFExtractor.class);
	
	private RTFEditorKit rek = new RTFEditorKit();
	private DefaultStyledDocument doc = new DefaultStyledDocument();

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
			doc.remove(0,doc.getLength());
			rek.read(is,doc,0);
			return doc.getText(0,doc.getLength());
		} catch (Exception e) {
			throw new ExtractionException(e);
		}
	}	
	
	/**
	 * @see org.jlibrary.core.indexer.extractors.Extractor#getReader(java.io.File)
	 */
	public Reader getReader(File f) throws ExtractionException {
		
		throw new ExtractionException("RTFExtractor can't use getReader() method");
	}

	/**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.File)
	 */
	public HeaderMetaData extractHeader(File f) throws ExtractionException {
		
		HeaderMetaData metadata = new HeaderMetaData();
		
		return metadata;
	}
	
	/**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.InputStream)
	 */
	public HeaderMetaData extractHeader(InputStream is) throws ExtractionException {
		
		HeaderMetaData metadata = new HeaderMetaData();
		
		return metadata;
	}	
}
