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
package org.jlibrary.core.search.lucene;

import java.util.HashMap;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.jlibrary.core.i18n.LocaleService;

/**
 * @author Martín Pérez
 *
 * Abstract indexer. It's used to hold common attributes like analyzer and 
 * the set of SPANISH_STOP_WORDS
 */
public class JLibraryIndexer {

	private HashMap analyzers;

	Analyzer defaultAnalyzer = new StopAnalyzer();

	/**
	 * Returns an analyzer customized for the given locale
	 * 
	 * @param locale Locale for indexation
	 * @return Analyzer for the given locale
	 */
	public Analyzer getAnalyzer(Locale locale) {
		
		if (analyzers == null) {
			initStopWords();
		}
		Analyzer analyzer = (Analyzer)analyzers.get(locale.getLanguage());
		if (analyzer == null) {
			return defaultAnalyzer;
		} else {
			return analyzer;
		}
	}

	/**
	 * Inits common stop words
	 */
	private void initStopWords() {

		analyzers = new HashMap();
		
		analyzers.put(LocaleService.SPANISH_LOCALE.getLanguage(), 
					  new SpanishAnalyzer());
		
		analyzers.put(LocaleService.ENGLISH_LOCALE.getLanguage(), 
					  new EnglishAnalyzer());
		
		//TODO: Pass custom stop words lists for this languages for correct stemming
		analyzers.put(LocaleService.FRENCH_LOCALE.getLanguage(), 
					  new StandardAnalyzer());
		analyzers.put(LocaleService.ITALIAN_LOCALE.getLanguage(), 
				  new StandardAnalyzer());
		analyzers.put(LocaleService.DUTCH_LOCALE.getLanguage(), 
				  new StandardAnalyzer());
		analyzers.put(LocaleService.FINNISH_LOCALE.getLanguage(), 
				  new StandardAnalyzer());
		analyzers.put(LocaleService.NORWEGIAN_LOCALE.getLanguage(), 
				  new StandardAnalyzer());
		analyzers.put(LocaleService.PORTUGUESE_LOCALE.getLanguage(), 
				  new StandardAnalyzer());		
		analyzers.put(LocaleService.RUSSIAN_LOCALE.getLanguage(), 
				  new StandardAnalyzer());
		analyzers.put(LocaleService.SWEDISH_LOCALE.getLanguage(), 
				  new StandardAnalyzer());		
		
		
	}

}
