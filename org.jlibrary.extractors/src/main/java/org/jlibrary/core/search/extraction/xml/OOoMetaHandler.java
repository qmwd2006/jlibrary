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
 * SAX ContentHandler for meta OpenOffice file
 */
public class OOoMetaHandler extends DefaultHandler
{
	private String title;
	private String description;
	private String subject;
	private String keywords;
	private String initialCreator;
	private String creator;
	private String printedBy;
	private String creationDate;
	private String modifiedDate;
	private String printDate;
	private String language;
	
	private StringBuffer buffer;
	
	public OOoMetaHandler()
	{
		title = new String();
		description = new String();
		subject = new String();
		keywords = new String();
		initialCreator = new String();
		creator = new String();
		printedBy = new String();
		creationDate = new String();
		modifiedDate = new String();
		printDate = new String();
		language = new String();
		buffer = new StringBuffer();
	}
	public void startElement(String namespaceURI, String localName,
            String rawName, Attributes atts)
			 throws SAXException
	{
		buffer = new StringBuffer();
	}

	public void characters(char[] ch, int start, int length) throws SAXException
	{
		buffer.append(ch,start,length);
	}

	public void endElement(java.lang.String namespaceURI,
      java.lang.String localName,
      java.lang.String qName)
	   throws SAXException
	{
		if(qName.equals("dc:title"))
			title = new String(buffer);
		else if(qName.equals("dc:description"))
			description = new String(buffer);
		else if(qName.equals("dc:subject"))
			subject = new String(buffer);
		else if(qName.equals("meta:keyword"))
			keywords += buffer.append(" ");
		else if(qName.equals("meta:initial-creator"))
			initialCreator = new String(buffer);
		else if(qName.equals("dc:creator"))
			creator = new String(buffer);
		else if(qName.equals("meta:printed-by"))
			printedBy = new String(buffer);
		else if(qName.equals("meta:creation-date"))
			creationDate = new String(buffer);
		else if(qName.equals("dc:date"))
			modifiedDate = new String(buffer);
		else if(qName.equals("meta:print-date"))
			printDate = new String(buffer);
		else if(qName.equals("dc:language"))
			language = new String(buffer);
	}

	public String getCreationDate() {
		return creationDate;
	}

	public String getCreator() {
		return creator;
	}

	public String getDescription() {
		return description;
	}

	public String getInitialCreator() {
		return initialCreator;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getLanguage() {
		return language;
	}

	public String getPrintedBy() {
		return printedBy;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public String getPrintDate() {
		return printDate;
	}

	public String getSubject() {
		return subject;
	}

	public String getTitle() {
		return title;
	}

}
