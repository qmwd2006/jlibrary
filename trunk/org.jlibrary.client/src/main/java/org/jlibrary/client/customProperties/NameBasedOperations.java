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
package org.jlibrary.client.customProperties;

import org.jlibrary.core.entities.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Restricts operations on custom property name.
 * List of names of custom properties which are not allowed to be removed
 * are in comma-separated list in property named 'NamedBasedOperations.forbiddenForRemoval'.
 *
 * @author Roman Puchkovskiy
 */
public class NameBasedOperations implements CustomPropertiesOperations {

    private static Set forbiddenForRemoval;
    private static Set allowedPrefixes;
    static {
        init();
    }

    public boolean isRemovable(Document document, String name) {
        return !forbiddenForRemoval.contains(name);
    }

    public boolean isValidPropertyName(Document document, String name) {
        if (!name.contains(":")) {
            return true;
        }
        String prefix = name.substring(0, name.indexOf(':'));
        return allowedPrefixes.contains(prefix);
    }

    private static void init() {
        forbiddenForRemoval = new HashSet();
        String str = CustomPropertiesUtils.getProperty("NamedBasedOperations.forbiddenForRemoval");
        if (str != null) {
            forbiddenForRemoval.addAll(stringToList(str));
        }
        allowedPrefixes = new HashSet();
        str = CustomPropertiesUtils.getProperty("NamedBasedOperations.allowedPrefixes");
        if (str != null) {
            allowedPrefixes.addAll(stringToList(str));
        }
    }

    private static List<String> stringToList(String str) {
        List<String> result = new ArrayList<String>();
        if (str != null) {
            while (true) {
                int k = str.indexOf(',');
                if (k == -1) {
                    break;
                }
                result.add(str.substring(0, k).trim());
                str = str.substring(k + 1);
            }
            result.add(str.trim());
        }
        return result;
    }
}