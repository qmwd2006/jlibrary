/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2008, Martín Pérez Mariñán, and individual 
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
package org.jlibrary.client.customProperties;

import org.jlibrary.core.entities.Document;

/**
 * Implementation which allows to edit custom properties on all documents
 * which parent has specified JLibrary path.
 * Path is specified in editAllower.properties under property named
 * ParentBasedAllower.parentPath. If this property is not specified, everything
 * is forbidden.
 *
 * @author Roman Puchkovskiy
 */
public class ParentBasedAllower implements CustomPropertiesEditAllower {

    private static String parentPath = CustomPropertiesUtils.getProperty("ParentBasedAllower.parentPath");

    public boolean isEditable(Document document) {
        if (parentPath == null) {
            return false;
        }
        return parentPath.equals(getParentPath(document.getPath()));
    }

    /**
     * Returns parent path.
     *
     * @param path
     * @return parent path
     */
    private String getParentPath(String path) {
        int index = path.lastIndexOf('/');
        if (index < 0) {
            // no slashes at all... let's return root path
            return "/";
        }
        if (index == 0) {
            // the only slash is leading one, so this path is child of root
            return "/";
        }
        return path.substring(0, index);
    }
}