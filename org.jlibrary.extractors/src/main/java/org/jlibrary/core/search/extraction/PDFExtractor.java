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
import java.io.StringWriter;

import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author martin
 *
 * PDF text extraction class
 */
public class PDFExtractor implements Extractor {

	static Logger logger = LoggerFactory.getLogger(PDFExtractor.class);
	
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
			PDDocument pdDocument = PDDocument.load(is);
			PDFTextStripper stripper = new PDFTextStripper();
			StringWriter writer = new StringWriter();
			
			stripper.writeText(pdDocument,writer);
			
			pdDocument.close();
			
			return writer.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}		
	}	
	
	/**
	 * @see org.jlibrary.core.indexer.extractors.Extractor#getReader(java.io.File)
	 */
	public Reader getReader(File f) throws ExtractionException {
		
		throw new ExtractionException("PDFExtractor can't use getReader() method");
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
		
		HeaderMetaData metadata = new HeaderMetaData();
		
		PDFParser parser = null;
		try {
			parser = new PDFParser(is);
			parser.parse();
			
			if (parser.getPDDocument().getDocumentInformation().getAuthor() != null) {
				metadata.setAuthor(parser.getPDDocument().getDocumentInformation().getAuthor());
			}

			if (parser.getPDDocument().getDocumentInformation().getCreationDate() != null) {
				metadata.setDate(parser.getPDDocument().getDocumentInformation().getCreationDate().getTime());
			}
			
			if (parser.getPDDocument().getDocumentInformation().getKeywords() != null) {
				metadata.setKeywords(parser.getPDDocument().getDocumentInformation().getKeywords());
			}
			
			if (parser.getPDDocument().getDocumentInformation().getTitle() != null) {
				metadata.setTitle(parser.getPDDocument().getDocumentInformation().getTitle());
			}
			parser.getPDDocument().close();
		} catch (Exception e) {
			throw new ExtractionException(e);
		}		
		return metadata;
	}	
}
