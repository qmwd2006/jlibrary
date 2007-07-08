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
package org.jlibrary.client.ui.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jlibrary.client.Messages;
import org.jlibrary.client.util.NodeUtils;
import org.jlibrary.core.entities.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Property source for a node object
 * 
 * @author martin
 *
 */
public class NodePropertySource implements IPropertySource {

	static Logger logger = LoggerFactory.getLogger(NodePropertySource.class);
	
	protected static final String GENERAL = 
		Messages.getMessage("properties_category_general");
	
	protected static final String PROPERTY_NAME = "name"; 	
	protected static final String PROPERTY_CREATOR = "creator"; 	
	protected static final String PROPERTY_DESCRIPTION = "description";
	protected static final String PROPERTY_DATE = "date";
	protected static final String PROPERTY_IMPORTANCE = "importance";
	protected static final String PROPERTY_LOCK = "lock";
	protected static final String PROPERTY_TYPE = "type";
	protected static final String PROPERTY_POSITION = "position";
	protected static final String PROPERTY_PARENT = "parent";
	protected static final String PROPERTY_JCR_PATH = "JCR path";	
	protected static final String PROPERTY_PATH = "path";
	protected static final String PROPERTY_SIZE = "size";
	
	protected Object CustomPropertiesTable[][];
	
	private final Object PropertiesTable[][] = 
	{ { PROPERTY_NAME, 
		new PropertyDescriptor(PROPERTY_NAME,Messages.getMessage("properties_name")),
		GENERAL,
		Messages.getMessage("properties_name_desc")
		},
		
	  { PROPERTY_DESCRIPTION, 
		new PropertyDescriptor(PROPERTY_DESCRIPTION,Messages.getMessage("properties_description")),
		GENERAL,
		Messages.getMessage("properties_description_desc")},
		
	  { PROPERTY_CREATOR, 
		new PropertyDescriptor(PROPERTY_CREATOR,Messages.getMessage("properties_creator")),
		GENERAL,
		Messages.getMessage("properties_creator_desc")},
	  { PROPERTY_DATE, 
		new PropertyDescriptor(PROPERTY_DATE,Messages.getMessage("properties_date")),
		GENERAL,
		Messages.getMessage("properties_date_desc")},
	  { PROPERTY_IMPORTANCE, 
		new PropertyDescriptor(PROPERTY_IMPORTANCE,Messages.getMessage("properties_importance")),
		GENERAL,
		Messages.getMessage("properties_importance_desc")},
		
	  { PROPERTY_LOCK, 
		new PropertyDescriptor(PROPERTY_LOCK,Messages.getMessage("properties_lock")),
		GENERAL,
		Messages.getMessage("properties_lock_desc")},
		
	  { PROPERTY_TYPE, 
		new PropertyDescriptor(PROPERTY_TYPE,Messages.getMessage("properties_type")),
		GENERAL,
		Messages.getMessage("properties_type_desc")},
		
	  { PROPERTY_TYPE, 
		new PropertyDescriptor(PROPERTY_POSITION,Messages.getMessage("properties_position")),
		GENERAL,
		Messages.getMessage("properties_position_desc")},		
		
	  { PROPERTY_PARENT, 
		new PropertyDescriptor(PROPERTY_PARENT,Messages.getMessage("properties_parent")),
		GENERAL,
		Messages.getMessage("properties_parent_desc")},
		
	  { PROPERTY_JCR_PATH, 
		new PropertyDescriptor(PROPERTY_JCR_PATH,Messages.getMessage("properties_jcr_path")),
		GENERAL,
		Messages.getMessage("properties_jcr_path_desc")},
		
	  { PROPERTY_PATH, 
		new PropertyDescriptor(PROPERTY_PATH,Messages.getMessage("properties_path")),
		GENERAL,
		Messages.getMessage("properties_path_desc")},
		
	  { PROPERTY_SIZE, 
		new PropertyDescriptor(PROPERTY_SIZE,Messages.getMessage("properties_size")),
		GENERAL,
		Messages.getMessage("properties_size_desc")},
	};
	
	private Node node;

	public NodePropertySource(Node node) {
		
		super();
		this.node = node;
		
		initProperties();
	}		
	
	private void initProperties() {
		
		if (node == null) {
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
		int length = PropertiesTable.length;
		if (CustomPropertiesTable != null) {
			length+= CustomPropertiesTable.length;
		}
		IPropertyDescriptor[] propertyDescriptors = 
			new IPropertyDescriptor[length];

		for (int i=0;i<PropertiesTable.length;i++) {				
			// Add each property supported.
			
			PropertyDescriptor descriptor;

			descriptor = (PropertyDescriptor)PropertiesTable[i][1];
			propertyDescriptors[i] = (IPropertyDescriptor)descriptor;
			descriptor.setCategory((String)PropertiesTable[i][2]);
			descriptor.setDescription((String)PropertiesTable[i][3]);
		}
		
		if (CustomPropertiesTable != null) {
			int size = PropertiesTable.length;
			for (int i=0;i<CustomPropertiesTable.length;i++) {				
				// Add each property supported.
				
				PropertyDescriptor descriptor;
	
				descriptor = (PropertyDescriptor)CustomPropertiesTable[i][1];
				propertyDescriptors[size+i] = (IPropertyDescriptor)descriptor;
				descriptor.setCategory((String)CustomPropertiesTable[i][2]);
				descriptor.setDescription((String)CustomPropertiesTable[i][3]);
			}		
		}
		
		// Return it.
		return propertyDescriptors;		
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(Object)
	 */
	public Object getPropertyValue(Object name) {
		
		if (name.equals(PROPERTY_NAME))
			return node.getName();
		else if (name.equals(PROPERTY_DESCRIPTION))
			return node.getDescription();
		else if (name.equals(PROPERTY_CREATOR))
			return NodeUtils.getCreatorName(node);
		else if (name.equals(PROPERTY_DATE))
			return node.getDate();
		else if (name.equals(PROPERTY_IMPORTANCE))
			return NodeUtils.getImportanceText(node);
		else if (name.equals(PROPERTY_LOCK))
			return Boolean.valueOf(!(node.getLock() == null));		
		else if (name.equals(PROPERTY_TYPE)) 
			return NodeUtils.getTypeName(node);
		else if (name.equals(PROPERTY_POSITION)) 
			return node.getPosition();
		else if (name.equals(PROPERTY_JCR_PATH))
			return node.getJCRPath();		
		else if (name.equals(PROPERTY_PATH))
			return node.getPath();
		else if (name.equals(PROPERTY_PARENT))
			return NodeUtils.getParentName(node);
		else if (name.equals(PROPERTY_SIZE))
			return node.getSize() + " bytes";

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

	public Node getNode() {
		return node;
	}	

	
}
