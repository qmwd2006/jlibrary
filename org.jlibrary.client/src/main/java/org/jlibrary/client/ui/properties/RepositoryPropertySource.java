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
package org.jlibrary.client.ui.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Property source for a repository object
 * 
 * @author martin
 *
 */
public class RepositoryPropertySource implements IPropertySource {

	static Logger logger = LoggerFactory.getLogger(RepositoryPropertySource.class);
	
	private static final String GENERAL = "General";
	
	protected static final String PROPERTY_NAME = "name"; 	
	protected static final String PROPERTY_DESCRIPTION = "description";
	
	private final Object PropertiesTable[][] = 
		{ { PROPERTY_NAME, 
			new PropertyDescriptor(PROPERTY_NAME,"Name"),
			GENERAL,
			"Nombre del repositorio"
			},
			
		  { PROPERTY_DESCRIPTION, 
			new PropertyDescriptor(PROPERTY_DESCRIPTION,"Description"),
			GENERAL,
			"Descripción del repositorio"},		  
		};
	
	private Repository repository;

	public RepositoryPropertySource(Repository repository) {
		
		super();
		this.repository = repository;
		initProperties();
	}		
	
	private void initProperties() {
		
		if (repository == null) {
			// the GUIView is probably hidden in this case
			return;
		}	
	}
	
	protected void firePropertyChanged(String propName, Object value) {
		
		logger.debug("Warning properties should not change!!");
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */	
	public Object getEditableValue() {

		return this;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */	
	public IPropertyDescriptor[] getPropertyDescriptors() {

		// Create the property vector.
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[PropertiesTable.length];

		for (int i=0;i<PropertiesTable.length;i++) {				
			// Add each property supported.
			
			PropertyDescriptor descriptor;

			descriptor = (PropertyDescriptor)PropertiesTable[i][1];
			propertyDescriptors[i] = (IPropertyDescriptor)descriptor;
			descriptor.setCategory((String)PropertiesTable[i][2]);
			descriptor.setDescription((String)PropertiesTable[i][3]);
		}
				
		// Return it.
		return propertyDescriptors;		
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(Object)
	 */
	public Object getPropertyValue(Object name) {
		
		if (name.equals(PROPERTY_NAME))
			return repository.getName();
		if (name.equals(PROPERTY_DESCRIPTION))
			return repository.getDescription();

		return null;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(Object, Object)
	 */
	public void setPropertyValue(Object name, Object value) {

		firePropertyChanged((String)name,value);
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(Object)
	 */
	public void resetPropertyValue(Object id) {
	}	

}
