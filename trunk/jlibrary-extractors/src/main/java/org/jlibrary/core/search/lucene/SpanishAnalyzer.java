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
package org.jlibrary.core.search.lucene;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.jlibrary.core.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Spanish lucene analyzer
 */
public class SpanishAnalyzer extends Analyzer {

	static Logger logger = LoggerFactory.getLogger(SpanishAnalyzer.class);
	
	private static StandardAnalyzer analyzer;


	private static String SPANISH_STOP_WORDS[];
	
	public SpanishAnalyzer() {

		initStopWords();
		analyzer = new StandardAnalyzer(SPANISH_STOP_WORDS);
	}

	public SpanishAnalyzer(String stopWords[]) {

		initStopWords();
		analyzer = new StandardAnalyzer(stopWords);
	}

	private static void initStopWords() {
		
		if (SPANISH_STOP_WORDS == null) {
			InputStream is = null;
			try {
				is = ResourceLoader.getResourceAsStream("org/jlibrary/core/search/lucene/indexers/spanish.stop");
				
				if (is != null) {
					StringBuilder words = new StringBuilder(IOUtils.toString(is,"iso_8859_1"));
					SPANISH_STOP_WORDS = words.toString().split("\r\n");
				} else {
					SPANISH_STOP_WORDS = new String[]{};
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
		}
	}
	
	public static final String[] getStopWords() {
		
		if (SPANISH_STOP_WORDS == null) {
			initStopWords();
		}
		return SPANISH_STOP_WORDS;
	}
	
	public TokenStream tokenStream(String fieldName, Reader reader) {

		return analyzer.tokenStream(fieldName, reader);
	}
}