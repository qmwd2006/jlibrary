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
package org.jlibrary.core.util;

import javax.jcr.Session;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Useful methods to deal with save (in terms of JSR-170).
 *
 * @author Roman Puchkovskiy
 */
public class SaveUtils {

    private static final Logger logger = LoggerFactory.getLogger(SaveUtils.class);

    /**
     * Tries to save a session. If exception occurs when saving, tries to remove
     * all pending changes from session and then rethrows the exception.
     *
     * @param session   JSR-170 session
     * @throws RepositoryException if error occurred when when trying to save
     */
    public static void safeSaveSession(Session session) throws RepositoryException {
        try {
            session.save();
        } catch (RepositoryException e) {
            try {
                session.refresh(false);
            } catch (RepositoryException e1) {
                // ignore refreshing problem, just log
                logger.error("Error while discarding session changes", e1);
            }
            throw e;
        }
    }
}