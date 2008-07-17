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
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jlibrary.core.search.extraction.xml.OOoContentHandler;
import org.jlibrary.core.search.extraction.xml.OOoMetaHandler;
import org.jlibrary.core.util.zip.ZipEntry;
import org.jlibrary.core.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author nicolasjouanin
 * @author mpermar
 *
 * OpenOffice text extraction class
 */
public class OOoExtractor implements Extractor
{
	static Logger logger = LoggerFactory.getLogger(OOoExtractor.class);
	
	private static String CONTENT_FILE = "content.xml";
	private static String META_FILE = "meta.xml";
	private static String DATE_FORMAT = "yyyy-MM-DD'T'HH:mm:ss";

	private XMLReader xmlReader;
	
	public OOoExtractor() throws ExtractionException 
	{
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			xmlReader = saxParser.getXMLReader();
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
			xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		} catch (Exception e) {
			throw new ExtractionException(e);
		}		

	}
	
	/**
	 * @see org.jlibrary.core.indexer.extractors.Extractor#extractText(java.io.File)
	 */	
	public String extractText(File f) throws ExtractionException
	{
		InputStream contentStream = null;
		try
		{
			ZipFile zf = new ZipFile(f);
			ZipEntry ze = zf.getEntry(CONTENT_FILE);
			contentStream = zf.getInputStream(ze);

			return extractText(contentStream);
		}
		catch(Exception e)
		{
			throw new ExtractionException(e);
		} finally {
			if (contentStream != null) {
				try {
					contentStream.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(),ioe);
				}
			}			
		}
	}

	/**
	 * @see org.jlibrary.core.indexer.extractors.Extractor#extractText(java.io.InputStream)
	 */	
	public String extractText(InputStream contentStream) throws ExtractionException
	{
		try
		{
			OOoContentHandler contentHandler = new OOoContentHandler();
			xmlReader.setContentHandler(contentHandler);
			xmlReader.parse(new InputSource(contentStream));
			contentStream.close();
			return contentHandler.getContent();
		}
		catch(Exception e)
		{
			throw new ExtractionException(e);
		}
	}	
	
	/**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.File)
	 */	
	public HeaderMetaData extractHeader(File f) throws ExtractionException
	{
		InputStream metaStream = null;
		try
		{
			ZipFile zf = new ZipFile(f);

			ZipEntry ze = zf.getEntry(META_FILE);
			metaStream = zf.getInputStream(ze);
			return extractHeader(metaStream);
		}
		catch(Exception e)
		{
			throw new ExtractionException(e);
		} finally {
			if (metaStream != null) {
				try {
					metaStream.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(),ioe);
				}
			}
		}
	}

	/**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.InputStream)
	 */	
	public HeaderMetaData extractHeader(InputStream metaStream) throws ExtractionException
	{
		HeaderMetaData metadata = new HeaderMetaData();
		try
		{
			OOoMetaHandler metaHandler = new OOoMetaHandler();
			DateFormat df = new SimpleDateFormat(DATE_FORMAT); 

			xmlReader.setContentHandler(metaHandler);
			xmlReader.parse(new InputSource(metaStream));
			
			metadata.setAuthor(metaHandler.getCreator());
			metadata.setDate(df.parse(metaHandler.getCreationDate()));
			metadata.setDescription(metaHandler.getDescription());
			metadata.setKeywords(metaHandler.getKeywords());
			String lang = metaHandler.getLanguage();
			if(lang != null && !lang.equals(""))
				metadata.setLanguage(lang.substring(0, lang.indexOf("-")));
			metadata.setTitle(metaHandler.getTitle());
			metaStream.close();
		}
		catch(Exception e)
		{
			throw new ExtractionException(e);
		}
		return metadata;
	}	
}
