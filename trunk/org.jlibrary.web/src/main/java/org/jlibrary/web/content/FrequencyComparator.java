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
package org.jlibrary.web.content;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: kohonen
 * Date: 28-Mar-2005
 * Time: 21:44:30
 * To change this template use File | Settings | File Templates.
 */
public class FrequencyComparator implements Comparator {
    public int compare(Object o1, Object o2) {

        Word word1 = (Word)o1;
        Word word2 = (Word)o2;        
        if(word1.getFrecuencia() < word2.getFrecuencia()) {
          return 1;
        } else if(word1.getFrecuencia() > word2.getFrecuencia()) {
          return -1;
        } else {
          return 0;
        }
    }

}
