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
package org.jlibrary.test.properties;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.math.RandomUtils;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.PropertyType;
import org.jlibrary.test.AbstractRepositoryTest;

public abstract class AbstractPropertyTest extends AbstractRepositoryTest {

	public static CustomPropertyDefinition customProperty;

	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
		
		InputStream is = getClass().getClassLoader().getResourceAsStream("test.properties");
		Properties properties = new Properties();
		properties.load(is);
				
		String customPropertyName = (String)properties.get("test.custom.property") + RandomUtils.nextInt();		
		
		customProperty = new CustomPropertyDefinition();
		customProperty.setName(customPropertyName);
		customProperty.setAutocreated(false);
		customProperty.setDefaultValues(null);
		customProperty.setType(PropertyType.STRING);
		customProperty.setMultivalued(false);
	}	
}
