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

/**
 * This interface decides which operations on custom properties are allowed.
 *
 * @author Roman Puchkovskiy
 */
public interface CustomPropertiesOperations {
    /**
     * Returns true if a given custom property may be removed from a given
     * document and false otherwise.
     *
     * @param document document to which property belongs
     * @param name  property name
     * @return true if removable
     */
    public boolean isRemovable(Document document, String name);

    /**
     * Determines whether given is a valid property name.
     *
     * @param document document to which property belongs
     * @param name  property name
     * @return true if valid name
     */
    public boolean isValidPropertyName(Document document, String name);
}