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
package org.jlibrary.client.ui.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jlibrary.client.Messages;
import org.jlibrary.client.i18n.LocaleService;
import org.jlibrary.client.util.NodeUtils;
import org.jlibrary.core.entities.Document;

/**
 * Property source for a document object
 * 
 * @author martin
 *
 */
public class DocumentPropertySource extends NodePropertySource {

	protected static final String DOCUMENT = 
		Messages.getMessage("properties_category_document");	
	
	protected static final String PROPERTY_TITLE = "title"; 	
	protected static final String PROPERTY_AUTHOR = "author"; 	
	protected static final String PROPERTY_CREATION_DATE = "creation_date"; 	
	protected static final String PROPERTY_URL = "url"; 	
	protected static final String PROPERTY_KEYWORDS = "keywords"; 	
	protected static final String PROPERTY_LANGUAGE = "language"; 	
		
	public DocumentPropertySource(Document document) {
		
		super(document);
		
		initCustomProperties();
	}
	
	private void initCustomProperties() {

		CustomPropertiesTable = new Object[][]
		{ { PROPERTY_TITLE, 
			new PropertyDescriptor(PROPERTY_TITLE,Messages.getMessage("properties_title")),
			DOCUMENT,
			Messages.getMessage("properties_title_desc")
			},
		  { PROPERTY_AUTHOR, 
			new PropertyDescriptor(PROPERTY_AUTHOR,Messages.getMessage("properties_author")),
			DOCUMENT,
			Messages.getMessage("properties_author_desc")
		  },
		  { PROPERTY_CREATION_DATE, 
			new PropertyDescriptor(PROPERTY_CREATION_DATE,Messages.getMessage("properties_creation_date")),
			DOCUMENT,
			Messages.getMessage("properties_creation_date_desc")
		  },
		  { PROPERTY_KEYWORDS, 
			new PropertyDescriptor(PROPERTY_KEYWORDS,Messages.getMessage("properties_keywords")),
			DOCUMENT,
			Messages.getMessage("properties_keywords_desc")
		  },
		  { PROPERTY_LANGUAGE, 
			new PropertyDescriptor(PROPERTY_LANGUAGE,Messages.getMessage("properties_language")),
			DOCUMENT,
			Messages.getMessage("properties_language_desc")
		  },		  
		  { PROPERTY_URL, 
			new PropertyDescriptor(PROPERTY_URL,Messages.getMessage("properties_url")),
			DOCUMENT,
			Messages.getMessage("properties_url_desc")
		  }		  
		};
	}

	public Object getPropertyValue(Object name) {
		
		Document node = (Document)getNode();
		if (name.equals(PROPERTY_TITLE))
			return node.getMetaData().getTitle();
		else if (name.equals(PROPERTY_CREATION_DATE))
			return node.getMetaData().getDate();
		else if (name.equals(PROPERTY_KEYWORDS))
			return node.getMetaData().getKeywords();
		else if (name.equals(PROPERTY_LANGUAGE))
			return LocaleService.getInstance().getDescriptionForLanguage(
					node.getMetaData().getLanguage());
		else if (name.equals(PROPERTY_URL))
			return node.getMetaData().getUrl();
		else if (name.equals(PROPERTY_AUTHOR))
			return NodeUtils.getAuthorName(node);

		
		return super.getPropertyValue(name);
	}
}
