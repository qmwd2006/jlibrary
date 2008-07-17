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
package org.jlibrary.core.search.extraction.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class XMLParser extends DefaultHandler implements ErrorHandler {

	private XMLReader xmlReader;
	private StringBuffer buffer;

	public XMLParser(XMLReader xmlReader) {

		try {

            this.xmlReader = xmlReader;
		    this.xmlReader.setContentHandler(this);
		    this.xmlReader.setErrorHandler(this);

        } catch (Exception ex) {

        }
	}

	public void startDocument() throws SAXException {
	
		buffer = new StringBuffer();
	}

    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes atts)
							 throws SAXException {}

    public void characters(char[] ch, 
    					   int start, 
    					   int length) throws SAXException {
		
		buffer.append(ch,start,length);
	}

	public void endElement(java.lang.String namespaceURI,
                       java.lang.String localName,
                       java.lang.String qName)
                	   throws SAXException {}


    public void warning(SAXParseException spe) throws SAXException {
    	

	}

    public void error(SAXParseException spe) throws SAXException {
    	
	}

    public void fatalError(SAXParseException spe) throws SAXException {
    	
    }
    
    public void parse(File f) throws FileNotFoundException, 
    								 IOException, 
    								 SAXException {
    	
		xmlReader.parse(new InputSource(new FileInputStream(f)));
    }
    
    public void parse(byte[] content) throws FileNotFoundException, 
	 										 IOException, 
											 SAXException {

    	xmlReader.parse(new InputSource(new ByteArrayInputStream(content)));
    }
    
	/**
	 * @return
	 */
	public String getContents() {
		
		String[] words = StringUtils.split(buffer.toString());
		return StringUtils.join(words," ");
	}

}

