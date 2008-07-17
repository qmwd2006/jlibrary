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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilities to work with custom properties.
 *
 * @author Roman Puchkovskiy
 */
public class CustomPropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(CustomPropertiesUtils.class);

    private static Properties properties = new Properties();
    private static CustomPropertiesEditAllower editAllower = null;
    private static CustomPropertiesOperations operations = null;

    static {
        init();
    }

    private CustomPropertiesUtils() {
    }

    /**
     * Returns an edit allower. Its class name is specified in
     * editAllower.properties under property 'customPropertiesEditAllower'.
     * If property is omitted, default 'allower' is returned which actually
     * does not allow anything.
     */
    public static CustomPropertiesEditAllower getEditAllower() {
        return editAllower;
    }

    /**
     * Returns an operations. Their class name is specified in
     * editAllower.properties under property 'customPropertiesOperations'.
     * If property is omitted, default operations are returned which
     * allow everything.
     */
    public static CustomPropertiesOperations getOperations() {
        return operations;
    }

    /**
     * Returns property of custom properties.
     *
     * @param property name
     * @return property, or null if no such property
     */
    public static String getProperty(String name) {
        return properties.getProperty(name);
    }

    /**
     * Initializes: reads properties from customProperties.properties,
     * instantiates required objects.
     */
    private static void init() {
		InputStream is = null;
		try {
		    is = CustomPropertiesUtils.class.getClassLoader().getResourceAsStream("customProperties.properties");
			if (is == null) {
			    logger.warn("customProperties.properties file cannot be found");
			} else {
			    properties.load(is);
			}
		} catch (IOException e) {				
			logger.error(e.getMessage(),e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}

        initEditAllower();
        initOperations();
    }

    private static void initEditAllower() {
        String name = properties.getProperty("customPropertiesEditAllower");
        if (name != null) {
            try {
                Class clazz = Class.forName(name);
                editAllower = (CustomPropertiesEditAllower) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Cannot init edit allower", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot init edit allower", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot init edit allower", e);
            }
        } else {
            editAllower = new ForbiddingAllower();
        }
        logger.debug("Edit allower class is " + editAllower.getClass().getName());
    }

    private static void initOperations() {
        String name = properties.getProperty("customPropertiesOperations");
        if (name != null) {
            try {
                Class clazz = Class.forName(name);
                operations = (CustomPropertiesOperations) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Cannot init operations", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot init operations", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot init operations", e);
            }
        } else {
            operations = new DefaultOperations();
        }
        logger.debug("Operations class is " + operations.getClass().getName());
    }
}