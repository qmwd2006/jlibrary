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
package org.jlibrary.core.search.extraction.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author nicolasjouanin
 *
 * SAX ContentHandler for content.xml OpenOffice file
 */
public class OOoContentHandler extends DefaultHandler
{

	private StringBuffer content;
	private boolean appendChar;
	
	public OOoContentHandler()
	{
		content = new StringBuffer();
		appendChar = false;
	}
	
	public String getContent()
	{
		return content.toString();
	}

	public void startElement(String namespaceURI, String localName,
            String rawName, Attributes atts)
			 throws SAXException
	{
		if(rawName.startsWith("text:"))
			appendChar = true;
	}

	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if(appendChar)
			content.append(ch,start,length).append(" ");
	}

	public void endElement(java.lang.String namespaceURI,
      java.lang.String localName,
      java.lang.String qName)
	   throws SAXException
	{
		appendChar = false;
	}
}
