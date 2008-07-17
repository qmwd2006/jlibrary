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
package org.jlibrary.core.util;

import org.jlibrary.core.jcr.JLibraryConstants;

import java.util.List;
import java.util.ArrayList;

/**
 * Class which allows to work with the jLibrary API.
 *
 * @author Roman Puchkovskiy
 */
public class JLibraryAPIUtils {

    private static List knownVersions = new ArrayList();
    static {
        knownVersions.add(JLibraryConstants.VERSION_1_0_FINAL);
        knownVersions.add(JLibraryConstants.VERSION_1_0_1);
        knownVersions.add(JLibraryConstants.VERSION_1_0_2);
        knownVersions.add(JLibraryConstants.VERSION_1_0_3);
        knownVersions.add(JLibraryConstants.VERSION_1_1);
        knownVersions.add(JLibraryConstants.VERSION_1_2);
    }

    private JLibraryAPIUtils() {}

    /**
     * Returns true if the given version is the same or more recent then the
     * threshold version.
     * If any of the versions are not known, false is returned.
     *
     * @param version   the version to check
     * @param threshold the threshold with which the version will be compared
     * @return false if any of the two versions is unknown, and boolean which
     * indicates whether the version is at least at threshold otherwise
     */
    public static boolean equalsOrExceeds(String version, String threshold) {
        int versionIndex = knownVersions.indexOf(version);
        int thresholdIndex = knownVersions.indexOf(threshold);
        if (versionIndex == -1 || thresholdIndex == -1) {
            return false;
        }
        return versionIndex >= thresholdIndex;
    }
}