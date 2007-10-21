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
package org.jlibrary.client.extraction;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.preference.JFacePreferences;
import org.jlibrary.client.i18n.LocaleService;
import org.jlibrary.client.preferences.JLibraryExtractionPreferences;
import org.jlibrary.client.search.WordCounter;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.search.extraction.Extractor;
import org.jlibrary.core.search.extraction.HTMLExtractor;
import org.jlibrary.core.search.extraction.HeaderMetaData;
import org.jlibrary.core.search.extraction.TextExtractionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaDataExtractor {
	
	static Logger logger = LoggerFactory.getLogger(MetaDataExtractor.class);
	
	/**
	 * Extracts the metadata information of a file
	 * 
	 * @param file File to be parsed
	 * 
	 * @return HeaderMetaData Metadata information for the file
	 */
	public static HeaderMetaData extractMetaData(String file) {
		
		HeaderMetaData metadata = null;
		Extractor extractor = null;
		try {
			extractor = TextExtractionService.getInstance().getExtractor(file);
			if (extractor != null) {
				metadata = extractor.extractHeader(new File(file));
				if ((metadata.getKeywords() == null) || 
					(metadata.getKeywords().equals(""))) {
					
					// manual metadata find
					boolean find = JFacePreferences.getPreferenceStore().getBoolean(JLibraryExtractionPreferences.P_FIND_KEYWORDS);
					if (find) {
						int numKeywords = JFacePreferences.getPreferenceStore().getInt(JLibraryExtractionPreferences.P_NUMBER_OF_KEYWORDS);
						
						LocaleService ls = LocaleService.getInstance();
						Locale locale = ls.getUserLocale();
						if (metadata.getLanguage() != null) {
							locale = new Locale(metadata.getLanguage()); 
						}
						
						String text = extractor.extractText(new File(file));
						StringBuilder buffer = new StringBuilder();
			            List palabras = WordCounter.busca(locale,text);
			            //logger.debug("Listado de palabras y sus frecuencias:");
			            if (palabras.size() > 0) {
			            	int i = 0;
				            while ((i < palabras.size()) && (i < numKeywords)) {
				            	buffer.append(palabras.get(i) + " ");
				                //info = new StringBuffer(p.toString()).append(": ").append(p.getOcurrencias()).append(" ocurrencias. Frecuencia: ").append(p.getFrecuencia());			            	
				            	i++;
				            }
			            }
			            metadata.setKeywords(buffer.toString());
					}					
				}
			}
			
			
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
		}
		return metadata;
	}
	
	/**
	 * Extracts the resources needed by a file if possible
	 * 
	 * @param file File to parse
	 * 
	 * @return String[] resources needed by the file
	 */
	public static String[] extractResources(String file) {
		
		if (Types.getTypeForFile(file) == Types.HTML_DOCUMENT) {
			try {
				HTMLExtractor extractor = new HTMLExtractor();
				return extractor.extractResources(new File(file));
			} catch (Exception e) {
		        logger.error(e.getMessage(),e);
			}
		}
		return new String[]{};
	}
	
	public static void main(String[] args) {
		
		String[] resources = MetaDataExtractor.extractResources("/tmp/release_beta2.html");
		for (int i = 0; i < resources.length; i++) {
			System.out.println(resources[i]);
		}
	}
}
