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
package org.jlibrary.core.search.extraction.html;

import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Utility class to test parsing
 */
public class TestHTMLParser {

	static Logger logger = LoggerFactory.getLogger(TestHTMLParser.class);
	
    public static void main(String[] args) {
		
    	try {
    		if (args.length != 1) {
    			System.out.println("Usage: TestHTMLParser file");
    			System.exit(0);
    		}
    		
			File file = new File(args[0]);
			if (!file.exists()) {
				System.out.println("The specified file does not exist");
				System.exit(0);
			}
			
	    	System.out.println("----------- Header ---------");
	    	System.out.println(HTMLParser.extractHeader(new FileInputStream(file),"utf-8"));
	    	System.out.println("----------- Text ---------");
	    	System.out.println(HTMLParser.extractText(new FileInputStream(file),"utf-8"));
	    	System.out.println("----------- Image resources ---------");
	    	System.out.println(HTMLParser.extractResourcePaths(new FileInputStream(file),"utf-8"));
	    	
    	} catch (Exception e) {
    		logger.error(e.getMessage(),e);
    	}
	}

}
