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

import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: kohonen
 * Date: 02-Apr-2005
 * Time: 12:33:44
 * To change this template use File | Settings | File Templates.
 */
public class WordTokenizer {

    private StringTokenizer tokenizer;

    static char punctuationsMarks[] = {',','.',';',':',')','('};

    final static String[] SPANISH_STOP_WORDS = new String[]{
		"de", "como", "para", "a", "e", "y", "o", "u", "más", "mas",
		"pero", "cual", "que", "qué", "cuál", "quién", "quien", "cómo",
		"cuando", "cuándo", "sino", "por", "en", "para", "desde", "hacia",
		"otro", "otra", "mi", "tu", "su", "suyo", "nuestro", "vuestro",
		"el", "la", "lo", "la", "los", "las", "un", "uno", "una", "unos",
		"unas", "tú", "él", "con"
	};


    public WordTokenizer (String text) {
      tokenizer = new StringTokenizer(deletePunctuationMarks(text)," ");
    }

    public boolean hasMoreTokens() {
      return tokenizer.hasMoreTokens();
    }

    public String nextToken() {
      String result = tokenizer.nextToken();
      if (!isStopWord(result)) {
        return result;
      } else {
        if (hasMoreTokens()) {
          return nextToken();
        } else {
          return "";
        }
      }
    }

    public boolean isStopWord (String word) {
        for (int i = 0; i < SPANISH_STOP_WORDS.length; i++) {
            if (word.equalsIgnoreCase(SPANISH_STOP_WORDS[i])){
                return true;
            }
        }
        return false;
    }

    public static String deletePunctuationMarks (String text) {

         String result = text;

         for (int i = 0; i < punctuationsMarks.length; i++) {
             char punctuationsMark = punctuationsMarks[i];
             result = result.replace(punctuationsMark, ' ');
         }
         return result;
        }
    

}
