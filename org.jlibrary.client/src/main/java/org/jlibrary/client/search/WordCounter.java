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
package org.jlibrary.client.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.jlibrary.core.search.lucene.JLibraryIndexer;


/**
 * Created by IntelliJ IDEA.
 * User: kohonen
 * Date: 21-Mar-2005
 * Time: 22:30:25
 * To change this template use File | Settings | File Templates.
 */
public class WordCounter {

	private static JLibraryIndexer indexer = new JLibraryIndexer();
	
	private static int MIN_WORD_SIZE=4;
	
    public static List busca(String texto) {
    	
    	return busca(Locale.getDefault(),texto);
    }    

    public static String buildKeywords(String text,
			   						   int numKeywords) {
    	
    	return buildKeywords(Locale.getDefault(),text,numKeywords);
    }    
    
    public static String buildKeywords(Locale locale, 
    								   String texto,
    								   int numKeywords) {
    	
    	List keywords = busca(locale,texto);
    	StringBuffer buffer = new StringBuffer();
        if (keywords.size() > 0) {
        	int i = 0;
            while ((i < keywords.size()) && (i < numKeywords)) {
            	buffer.append(keywords.get(i) + " ");
            	i++;
            }
        }
        return buffer.toString();
    }
    
    public static List busca(Locale locale, String texto) {
       
    	if (texto == null) {
    		return Collections.EMPTY_LIST;
    	}
    	HashMap hash = new HashMap();
       
       // Use server lucene indexer to clean the text
       Analyzer analyzer = indexer.getAnalyzer(locale);

       TokenStream stream = analyzer.tokenStream(null,new StringReader(texto));

       int numberOfCorrectTokens = 0;
       do {
    	   try {
	    	   Token token = stream.next();

	    	   if (token == null) {
	    		   break;
	    	   }
	           String wordString = token.termText();
	           if (wordString.length() < MIN_WORD_SIZE) {
	        	   continue;
	           }
	            numberOfCorrectTokens++;
	            Word word = new Word(wordString);
	            if (hash.containsKey(wordString)) {
	               word.setOcurrencias(((Word)hash.get(wordString)).getOcurrencias()+1);
	             } else {
	               word.setOcurrencias(1);                
	             }
	           hash.put(wordString,word);
           } catch (IOException ioe) {
        	   ioe.printStackTrace();
        	   break;
           }
       } while (true);
       
       ArrayList list = new ArrayList(hash.values());
       for (int i = 0; i < list.size(); i++) {
           Word word =  (Word)list.get(i);
           word.setFrecuencia((double)word.getOcurrencias() / (double)numberOfCorrectTokens);
       }
       List wordList = new ArrayList(hash.values());
       Collections.sort(wordList,new FrequencyComparator());       
       return wordList;
    }
}
