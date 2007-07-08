/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
package org.jlibrary.core.jcr;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.version.OnParentVersionAction;

import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeDef;
import org.apache.jackrabbit.core.nodetype.NodeDefImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.PropDef;
import org.apache.jackrabbit.core.nodetype.PropDefImpl;
import org.apache.jackrabbit.core.value.InternalValue;
import org.apache.jackrabbit.name.QName;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTypeManager {

	static Logger logger = LoggerFactory.getLogger(NodeTypeManager.class);
	
	private static final QName ntBase =	new QName(JCRConstants.JCR_NT_URL,"base");
	
	// jLibrary types
	private static final QName jlibBase = new QName(JLibraryConstants.JLIBRARY_URL,"jlibrary");
	private static final QName jlibInternal = new QName(JLibraryConstants.JLIBRARY_URL,"internal");
	private static final QName jlibNode = new QName(JLibraryConstants.JLIBRARY_URL,"node");
	private static final QName jlibDirectory = new QName(JLibraryConstants.JLIBRARY_URL,"directory");
	private static final QName jlibResource = new QName(JLibraryConstants.JLIBRARY_URL,"resource");
	private static final QName jlibDocument = new QName(JLibraryConstants.JLIBRARY_URL,"document");
	private static final QName jlibAuthor = new QName(JLibraryConstants.JLIBRARY_URL,"author");
	private static final QName jlibCategory = new QName(JLibraryConstants.JLIBRARY_URL,"category");
	private static final QName jlibNote = new QName(JLibraryConstants.JLIBRARY_URL,"note");
	private static final QName jlibUser = new QName(JLibraryConstants.JLIBRARY_URL,"user");
	private static final QName jlibGroup = new QName(JLibraryConstants.JLIBRARY_URL,"group");
	private static final QName jlibRol = new QName(JLibraryConstants.JLIBRARY_URL,"rol");
	private static final QName jlibContent = new QName(JLibraryConstants.JLIBRARY_URL,"content");
	private static final QName jlibFavorite = new QName(JLibraryConstants.JLIBRARY_URL,"favorite");
	private static final QName jlibBookmark = new QName(JLibraryConstants.JLIBRARY_URL,"bookmark");
	private static final QName jlibFavorites = new QName(JLibraryConstants.JLIBRARY_URL,"favorites");
	private static final QName jlibBookmarks = new QName(JLibraryConstants.JLIBRARY_URL,"bookmarks");
	
	// jLibrary properties
	private static final QName pjlibName = new QName(JLibraryConstants.JLIBRARY_URL,"name");
	private static final QName pjlibDescription = new QName(JLibraryConstants.JLIBRARY_URL,"description");
	private static final QName pjlibCreator = new QName(JLibraryConstants.JLIBRARY_URL,"creator");
	private static final QName pjlibImportance = new QName(JLibraryConstants.JLIBRARY_URL,"importance");
	private static final QName pjlibTypecode = new QName(JLibraryConstants.JLIBRARY_URL,"typecode");
	private static final QName pjlibCreated = new QName(JLibraryConstants.JLIBRARY_URL,"created");
	private static final QName pjlibPath = new QName(JLibraryConstants.JLIBRARY_URL,"path");
	private static final QName pjlibPosition = new QName(JLibraryConstants.JLIBRARY_URL,"position");
	private static final QName pjlibAuthor = new QName(JLibraryConstants.JLIBRARY_URL,"author");
	private static final QName pjlibLanguage = new QName(JLibraryConstants.JLIBRARY_URL,"language");
	private static final QName pjlibKeywords = new QName(JLibraryConstants.JLIBRARY_URL,"keywords");
	private static final QName pjlibCreationDate = new QName(JLibraryConstants.JLIBRARY_URL,"creationDate");
	private static final QName pjlibURL = new QName(JLibraryConstants.JLIBRARY_URL,"url");
	private static final QName pjlibTitle = new QName(JLibraryConstants.JLIBRARY_URL,"title");
	private static final QName pjlibSize = new QName(JLibraryConstants.JLIBRARY_URL,"size");
	private static final QName pjlibCategories = new QName(JLibraryConstants.JLIBRARY_URL,"categories");
	private static final QName pjlibResources = new QName(JLibraryConstants.JLIBRARY_URL,"resources");
	private static final QName pjlibRestrictions = new QName(JLibraryConstants.JLIBRARY_URL,"restrictions");
	private static final QName pjlibRelations = new QName(JLibraryConstants.JLIBRARY_URL,"relations");
	private static final QName pjlibNodes = new QName(JLibraryConstants.JLIBRARY_URL,"nodes");
	private static final QName pjlibDate = new QName(JLibraryConstants.JLIBRARY_URL,"date");
	private static final QName pjlibText = new QName(JLibraryConstants.JLIBRARY_URL,"text");
	private static final QName pjlibBio = new QName(JLibraryConstants.JLIBRARY_URL,"bio");
	private static final QName pjlibUser = new QName(JLibraryConstants.JLIBRARY_URL,"user");
	private static final QName pjlibUsers = new QName(JLibraryConstants.JLIBRARY_URL,"users");
	private static final QName pjlibMembers = new QName(JLibraryConstants.JLIBRARY_URL,"members");
	private static final QName pjlibRoles = new QName(JLibraryConstants.JLIBRARY_URL,"roles");
	private static final QName pjlibGroups = new QName(JLibraryConstants.JLIBRARY_URL,"groups");
	private static final QName pjlibFirstName = new QName(JLibraryConstants.JLIBRARY_URL,"firstname");
	private static final QName pjlibLastName = new QName(JLibraryConstants.JLIBRARY_URL,"lastname");
	private static final QName pjlibEmail = new QName(JLibraryConstants.JLIBRARY_URL,"email");
	private static final QName pjlibPassword = new QName(JLibraryConstants.JLIBRARY_URL,"password");
	private static final QName pjlibIsAdmin = new QName(JLibraryConstants.JLIBRARY_URL,"isAdmin");
	private static final QName pjlibIsSysadmin = new QName(JLibraryConstants.JLIBRARY_URL,"sysAdmin");
	private static final QName pjlibNode = new QName(JLibraryConstants.JLIBRARY_URL,"node");
	private static final QName pjlibLockUser = new QName(JLibraryConstants.JLIBRARY_URL,"lockUser");
	private static final QName pjlibActive = new QName(JLibraryConstants.JLIBRARY_URL,"active");

	// Additional properties
	QName pmixLockToken = new QName(JCRConstants.JCR_MIX_URL,"lockToken");

	// Node definitions
	private NodeTypeDef ntdJLibrary;
	private NodeTypeDef ntdInternal;
	private NodeTypeDef ntdNode;
	private NodeTypeDef ntdResource;
	private NodeTypeDef ntdDirectory;
	private NodeTypeDef ntdDocument;
	private NodeTypeDef ntdAuthor;
	private NodeTypeDef ntdCategory;
	private NodeTypeDef ntdNote;
	private NodeTypeDef ntdUser;
	private NodeTypeDef ntdGroup;
	private NodeTypeDef ntdRol;
	private NodeTypeDef ntdContent;
	private NodeTypeDef ntdFavorite;
	private NodeTypeDef ntdBookmark;
	
	public NodeTypeManager() {
		
		buildNodeTypes();
	}
	
	private QName createCustomPropertyType(String propertyName) {
		
		int colon = propertyName.lastIndexOf(":");
		propertyName = propertyName.substring(colon+1,propertyName.length());
		return new QName(JLibraryConstants.JLIBRARY_EXTENDED_URL,propertyName);
	}
	
	private PropDef createCustomPropertyDef(
			String propertyName, 
			boolean multivalued, 
			int type,
			boolean autocreated,
			InternalValue[] defaultValues) {
		
		QName propQName = createCustomPropertyType(propertyName);
		PropDef customProperty = 
			createPropertyDef(propQName,
							  false, // This should be mandatory
							  multivalued,
							  type,
							  jlibDocument,
							  OnParentVersionAction.COPY,
							  autocreated,
							  defaultValues);
		return customProperty;
	}
	
	/**
	 * Registers a custom property for documents under a given session. The custom property 
	 * will be registered in the repository. All the documents will have that custom property 
	 * registered.
	 * <p/>
	 * Note that if you register a custom property within a Jackrabbit repository, that property 
	 * will be shared by all repositories. So this means that if you create for example a property 
	 * <code>jlib:rate</code> all the documents in all the repositories could potentially have that 
	 * custom property. <b>Please note that this can be a potential issue when you share the same 
	 * repository between different and heterogeneus applications.</b>
	 * 
	 * @param session Session
	 * @param propertyName Name of the property
	 * @param multivalued <code>true</code> if the property is multivalued and <code>false</code> otherwise.
	 * @param type Type of the property. 
	 * @param autocreated <code>true</code> if the property is autocreated and <code>false</code> otherwise.
	 * @param defaultValues Default values if autocreated, <code>null</code> otherwise.
	 * 
	 * @throws RepositoryException If the property can�t be created
	 */
	public void registerCustomProperty(
			javax.jcr.Session session, 
			CustomPropertyDefinition property) throws RepositoryException {
		
		try {
			InternalValue[] values = null;
			if (property.getDefaultValues() != null) {
				values = JCRUtils.getInternalValues(property.getDefaultValues());
			}			
			PropDef customProperty = createCustomPropertyDef(
					property.getName(), 
					property.isMultivalued(), 
					property.getType(),
					property.isAutocreated(),
					values);
			
			Workspace wsp = session.getWorkspace();
			javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
			NodeTypeRegistry ntReg = 
		        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
			NodeTypeDef def = ntReg.getNodeTypeDef(jlibDocument);
			PropDef[] properties = def.getPropertyDefs();

			if (!isCustomPropertyRegistered(session, property.getName())) {
				PropDef[] newProperties = new PropDef[properties.length+1];
				System.arraycopy(properties,0,newProperties,0,properties.length);
				newProperties[properties.length] = customProperty;				
				ntdDocument.setPropertyDefs(newProperties);
				ntReg.reregisterNodeType(ntdDocument);
			}
		} catch (InvalidNodeTypeDefException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}	
		session.save();
	}

	/**
	 * Unregisters a custom property for documents under a given session. The custom property 
	 * will be unregistered from the repository. All the documents will have that custom property 
	 * unregistered.
	 * <p/>
	 * Currently Jackrabbit <b>doesn�t support mandatory properties removal</b>, so the behaviour of this 
	 * method is not so predictable as it should be. Extensive testing needs to be done. Be sure to 
	 * don�t use this method with mandatory properties.
	 * <p/>
	 * This method doesn�t remove the properties from the documents. Properties should be removed 
	 * manually.
	 * 
	 * @param session Session
	 * @param propertyName Name of the property
	 * 
	 * @throws RepositoryException If the property can�t be unregistered
	 */
	public void unregisterCustomProperty(
			javax.jcr.Session session, 
			String propertyName) throws RepositoryException {
		
		try {
			QName propQName = createCustomPropertyType(propertyName);
			
			Workspace wsp = session.getWorkspace();
			javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
			NodeTypeRegistry ntReg = 
		        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
			NodeTypeDef def = ntReg.getNodeTypeDef(jlibDocument);
			PropDef[] properties = def.getPropertyDefs();

			if (isCustomPropertyRegistered(session, propertyName)) {
				PropDef[] newProps = new PropDef[properties.length-1];
				int j = 0;
				for (int i=0;i<properties.length;i++) {
					if (!properties[i].getName().equals(propQName)) {
						newProps[j] = properties[i];
						j++;
					}
				}
				def.setPropertyDefs(newProps);
				ntdDocument.setPropertyDefs(newProps);
				ntReg.reregisterNodeType(def);
			}
		} catch (InvalidNodeTypeDefException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}	
		session.save();
	}	
	
	/**
	 * Says if a custom property has been registered in a given repository.
	 *  
	 * @param session Session
	 * @param propertyName Name of the property to check
	 * 
	 * @return boolean <code>true</code> if the property has been already registered and <code>false</code> otherwise.
	 * 
	 * @throws RepositoryException If the check cannot be performed
	 */
	public boolean isCustomPropertyRegistered(
			javax.jcr.Session session, String propertyName) throws RepositoryException {
		
		QName propQName = createCustomPropertyType(propertyName);
		Workspace wsp = session.getWorkspace();
		javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
		NodeTypeRegistry ntReg = 
	        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
		NodeTypeDef def = ntReg.getNodeTypeDef(jlibDocument);
		PropDef[] properties = def.getPropertyDefs();
		// check
		boolean found = false;
		for (int i = 0; i < properties.length; i++) {
			if (properties[i].getName().equals(propQName)) {
				// Already exists
				found = true;
				break;
			}
		}		
		return found;		
	}
	
	/**
	 * Registers node types on a session
	 * 
	 * @param session Session
	 * 
	 * @throws RepositoryException If the node types can't be registered
	 */
	public void registerNodeTypes(Session session) 
									throws RepositoryException {
		
		Workspace wsp = session.getWorkspace();
		javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
		NodeTypeRegistry ntReg = 
	        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
		
		wsp.getNamespaceRegistry().registerNamespace(
								JLibraryConstants.JLIBRARY_PREFIX, 
								JLibraryConstants.JLIBRARY_URL);
		wsp.getNamespaceRegistry().registerNamespace(
				JLibraryConstants.JLIBRARY_EXTENDED_PREFIX, 
				JLibraryConstants.JLIBRARY_EXTENDED_URL);
		session.save();
         
		try {
			ntReg.registerNodeType(ntdJLibrary);
			ntReg.registerNodeType(ntdInternal);
			ntReg.registerNodeType(ntdNode);
			ntReg.registerNodeType(ntdResource);
			ntReg.registerNodeType(ntdDirectory);
			ntReg.registerNodeType(ntdDocument);
			ntReg.registerNodeType(ntdAuthor);
			ntReg.registerNodeType(ntdCategory);
			ntReg.registerNodeType(ntdNote);
			ntReg.registerNodeType(ntdUser);
			ntReg.registerNodeType(ntdGroup);
			ntReg.registerNodeType(ntdRol);
			ntReg.registerNodeType(ntdContent);
			ntReg.registerNodeType(ntdFavorite);
			ntReg.registerNodeType(ntdBookmark);
		} catch (InvalidNodeTypeDefException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
		session.save();
	}

	public void checkNodeTypes(Session session) throws RepositoryException {
		
		try {
			// update registries
			Workspace wsp = session.getWorkspace();
			javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
			NodeTypeRegistry ntReg = 
			    ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
			
			
			// New from jLibrary 1.0 final
			//TODO: Change this property to mandatory. Currently the only 
			// way to add this property is adding as non-mandatory, as 
			// Jackrabbit does not support to add mandatory properties and 
			// then change reregister/unregister node definitions
			//
			// Note that in new repositories, this property is added as 
			// mandatory.
			PropDef propNoteUser = 
				createPropertyDef(pjlibUser,
								  false, // This should be mandatory
								  false,
								  PropertyType.REFERENCE,
								  jlibNote,
								  OnParentVersionAction.COPY,
								  false,
								  null);
			
			NodeTypeDef def = ntReg.getNodeTypeDef(jlibNote);
			PropDef[] properties = def.getPropertyDefs();
			// check
			boolean found = false;
			for (int i = 0; i < properties.length; i++) {
				if (properties[i].getName().equals(propNoteUser.getName())) {
					// Already exists
					found = true;;
				}
			}				
			if (!found) {
				PropDef[] newProperties = new PropDef[properties.length+1];
				System.arraycopy(properties,0,newProperties,0,properties.length);
				newProperties[properties.length] = propNoteUser;				
				ntdNote.setPropertyDefs(newProperties);
				ntReg.reregisterNodeType(ntdNote);
			}
			
			InternalValue[] defaultValues = 
				new InternalValue[]{InternalValue.create(true)};
			PropDef propActive = 
				createPropertyDef(pjlibActive,
								  false, // This should also be mandatory
								  false,
								  PropertyType.BOOLEAN,
								  jlibBase,
								  OnParentVersionAction.COPY,
								  true,
								  defaultValues);
			
			def = ntReg.getNodeTypeDef(jlibBase);
			properties = def.getPropertyDefs();
			// check
			found = false;
			for (int i = 0; i < properties.length; i++) {
				if (properties[i].getName().equals(propActive.getName())) {
					// Already exists
					found = true;;
				}
			}		
			if (!found) {
				PropDef[] newProperties = new PropDef[properties.length+1];
				System.arraycopy(properties,0,newProperties,0,properties.length);
				newProperties[properties.length] = propActive;				
				ntdJLibrary.setPropertyDefs(newProperties);
				ntReg.reregisterNodeType(ntdJLibrary);
			}
			/*
			 * This would be great but you will get a not yet implemented
			 * exception because currently jackrabbit is not able to remove
			 * mandatory property definitions
			def = ntReg.getNodeTypeDef(jlibCategory);
			PropDef[] props = def.getPropertyDefs();
			QName pjlibRepository = 
				new QName(JLibraryConstants.JLIBRARY_URL,"repository");
			PropDef[] newProps = new PropDef[props.length-1];
			int j = 0;
			for (int i=0;i<props.length;i++) {
				if (!props[i].getName().equals(pjlibRepository)) {
					newProps[j] = props[i];
					j++;
				}
			}
			def.setPropertyDefs(newProps);
			ntReg.reregisterNodeType(def);
			*/
			session.save();
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	private void buildNodeTypes() {
		
		ntdJLibrary = createNodeTypeDef(new QName[]{ntBase},jlibBase,true);
		
		ntdInternal = createNodeTypeDef(new QName[]{jlibBase},jlibInternal,true);
		
		NodeDefImpl residual = new NodeDefImpl();
		residual.setAllowsSameNameSiblings(true);
		residual.setName(new QName("","*"));
		residual.setDeclaringNodeType(jlibInternal);
		ntdInternal.setChildNodeDefs(new NodeDef[]{residual});
		PropDefImpl residualProp = new PropDefImpl();
		residualProp.setMandatory(false);
		residualProp.setMultiple(false);
		residualProp.setDeclaringNodeType(jlibInternal);
		ntdInternal.setPropertyDefs(new PropDef[]{residualProp});		
		
		ntdNode = createNodeTypeDef(new QName[]{jlibBase},jlibNode,true);		
		ntdResource = createNodeTypeDef(new QName[]{jlibNode},jlibResource,true);
		ntdDirectory = createNodeTypeDef(new QName[]{jlibNode},jlibDirectory,true);
		ntdDocument = createNodeTypeDef(new QName[]{jlibNode},jlibDocument,true);
		ntdAuthor = createNodeTypeDef(new QName[]{jlibBase},jlibAuthor,true);
		ntdCategory = createNodeTypeDef(new QName[]{jlibBase},jlibCategory,true);			
		ntdNote = createNodeTypeDef(new QName[]{jlibBase},jlibNote,true);	
		ntdUser = createNodeTypeDef(new QName[]{jlibBase},jlibUser,true);			
		ntdGroup = createNodeTypeDef(new QName[]{jlibBase},jlibGroup,true);			
		ntdRol = createNodeTypeDef(new QName[]{jlibBase},jlibRol,true);	
		ntdContent = createNodeTypeDef(new QName[]{jlibBase},jlibContent,true);			
		ntdFavorite = createNodeTypeDef(new QName[]{jlibBase},jlibFavorite,true);			
		ntdBookmark = createNodeTypeDef(new QName[]{jlibBase},jlibBookmark,true);					
		
		registerNodeDef(new QName[]{jlibBase}, 
									jlibBase, ntdJLibrary,
						new int[]{OnParentVersionAction.COPY,
								  OnParentVersionAction.COPY});
		
		registerNodeDef(new QName[]{jlibDirectory,jlibDocument,jlibResource}, 
									jlibDirectory,ntdDirectory,
						new int[]{OnParentVersionAction.COPY,
				  				  OnParentVersionAction.COPY,
				  				  OnParentVersionAction.COPY});
		
		registerNodeDef(new QName[]{jlibContent,jlibNote},
									jlibDocument,ntdDocument,
						new int[]{OnParentVersionAction.COPY,
								  OnParentVersionAction.COPY});
		
		registerNodeDef(new QName[]{jlibCategory,jlibFavorites},
									jlibCategory,ntdCategory,
						new int[]{OnParentVersionAction.COPY,
								  OnParentVersionAction.IGNORE});
		
		registerNodeDef(new QName[]{jlibBookmarks},
									jlibUser,ntdUser,
						new int[]{OnParentVersionAction.IGNORE});

		registerNodeDef(new QName[]{jlibBookmark},
									jlibBookmark,ntdBookmark,
                        new int[]{OnParentVersionAction.IGNORE});
		
		
		InternalValue[] defaultTrue = 
			new InternalValue[]{InternalValue.create(true)};
		
		PropDef propName = createPropertyDef(pjlibName,false,false,PropertyType.STRING,jlibBase,OnParentVersionAction.COPY,false,null);
		PropDef propActive = createPropertyDef(pjlibActive,true,false,PropertyType.BOOLEAN,jlibBase,OnParentVersionAction.COPY,true,defaultTrue);
		
		PropDef propDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propCreator = createPropertyDef(pjlibCreator,true,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propImportance = createPropertyDef(pjlibImportance,true,false,PropertyType.LONG,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propTypecode = createPropertyDef(pjlibTypecode,true,false,PropertyType.LONG,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propCreated = createPropertyDef(pjlibCreated,true,false,PropertyType.DATE,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propPath = createPropertyDef(pjlibPath,true,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propPosition = createPropertyDef(pjlibPosition,true,false,PropertyType.LONG,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propSize = createPropertyDef(pjlibSize,true,false,PropertyType.LONG,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propRestrictions = createPropertyDef(pjlibRestrictions,true,true,PropertyType.REFERENCE,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propLockToken = createPropertyDef(pmixLockToken,false,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		PropDef propLockUser = createPropertyDef(pjlibLockUser,false,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		
		PropDef propAuthor = createPropertyDef(pjlibAuthor,true,false,PropertyType.REFERENCE,jlibDocument,OnParentVersionAction.COPY,false,null);
		PropDef propLanguage = createPropertyDef(pjlibLanguage,true,false,PropertyType.STRING,jlibDocument,OnParentVersionAction.COPY,false,null);
		PropDef propKeywords = createPropertyDef(pjlibKeywords,true,false,PropertyType.STRING,jlibDocument,OnParentVersionAction.COPY,false,null);
		PropDef propCreationDate = createPropertyDef(pjlibCreationDate,true,false,PropertyType.DATE,jlibDocument,OnParentVersionAction.COPY,false,null);
		PropDef propURL = createPropertyDef(pjlibURL,false,false,PropertyType.STRING,jlibDocument,OnParentVersionAction.COPY,false,null);
		PropDef propTitle = createPropertyDef(pjlibTitle,true,false,PropertyType.STRING,jlibDocument,OnParentVersionAction.COPY,false,null);
		PropDef propCategories = createPropertyDef(pjlibCategories,true,true,PropertyType.REFERENCE,jlibDocument,OnParentVersionAction.COPY,false,null);
		PropDef propResources = createPropertyDef(pjlibResources,true,true,PropertyType.REFERENCE,jlibDocument,OnParentVersionAction.COPY,false,null);
		PropDef propRelations = createPropertyDef(pjlibRelations,true,true,PropertyType.REFERENCE,jlibDocument,OnParentVersionAction.COPY,false,null);
		
		PropDef propNodes = createPropertyDef(pjlibNodes,true,true,PropertyType.REFERENCE,jlibCategory,OnParentVersionAction.COPY,false,null);
		PropDef propCategoryDate = createPropertyDef(pjlibDate,true,false,PropertyType.DATE,jlibCategory,OnParentVersionAction.COPY,false,null);
		PropDef propCategoryDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibCategory,OnParentVersionAction.COPY,false,null);

		PropDef propNoteDate = createPropertyDef(pjlibDate,true,false,PropertyType.DATE,jlibNote,OnParentVersionAction.COPY,false,null);		
		PropDef propText = createPropertyDef(pjlibText,true,false,PropertyType.STRING,jlibNote,OnParentVersionAction.COPY,false,null);		
		// New from jLibrary 1.0 final
		PropDef propNoteUser = createPropertyDef(pjlibUser,false,false,PropertyType.REFERENCE,jlibNote,OnParentVersionAction.COPY,false,null);		
		
		PropDef propBookmarkTypecode = createPropertyDef(pjlibTypecode,true,false,PropertyType.STRING,jlibBookmark,OnParentVersionAction.COPY,false,null);
		PropDef propBookmarkURL = createPropertyDef(pjlibURL,false,false,PropertyType.STRING,jlibBookmark,OnParentVersionAction.COPY,false,null);
		PropDef propBookmarkUSer = createPropertyDef(pjlibUser,true,false,PropertyType.STRING,jlibBookmark,OnParentVersionAction.COPY,false,null);
		PropDef propBookmarkDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibBookmark,OnParentVersionAction.COPY,false,null);
		
		PropDef propBio = createPropertyDef(pjlibBio,true,false,PropertyType.STRING,jlibAuthor,OnParentVersionAction.COPY,false,null);
		PropDef propAuthorUser = createPropertyDef(pjlibUser,false,false,PropertyType.STRING,jlibAuthor,OnParentVersionAction.COPY,false,null);

		PropDef propMembers = createPropertyDef(pjlibMembers,true,true,PropertyType.REFERENCE,jlibRol,OnParentVersionAction.COPY,false,null);
		PropDef propRolDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibRol,OnParentVersionAction.COPY,false,null);
		
		PropDef propFavoriteUser= createPropertyDef(pjlibUser,false,false,PropertyType.REFERENCE,jlibFavorite,OnParentVersionAction.COPY,false,null);
		PropDef propNode= createPropertyDef(pjlibNode,true,false,PropertyType.REFERENCE,jlibFavorite,OnParentVersionAction.COPY,false,null);
		
		PropDef propUsers = createPropertyDef(pjlibUsers,true,true,PropertyType.REFERENCE,jlibGroup,OnParentVersionAction.COPY,false,null);
		PropDef propGroupRoles = createPropertyDef(pjlibRoles,true,true,PropertyType.REFERENCE,jlibGroup,OnParentVersionAction.COPY,false,null);
		PropDef propGroupDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibGroup,OnParentVersionAction.COPY,false,null);
		
		PropDef propGroups = createPropertyDef(pjlibGroups,true,true,PropertyType.REFERENCE,jlibUser,OnParentVersionAction.COPY,false,null);
		PropDef propUserRoles = createPropertyDef(pjlibRoles,true,true,PropertyType.REFERENCE,jlibUser,OnParentVersionAction.COPY,false,null);
		PropDef propFirstName = createPropertyDef(pjlibFirstName,false,false,PropertyType.STRING,jlibUser,OnParentVersionAction.COPY,false,null);
		PropDef propLastName = createPropertyDef(pjlibLastName,false,false,PropertyType.STRING,jlibUser,OnParentVersionAction.COPY,false,null);
		PropDef propEmail = createPropertyDef(pjlibEmail,false,false,PropertyType.STRING,jlibUser,OnParentVersionAction.COPY,false,null);
		PropDef propPassword = createPropertyDef(pjlibPassword,true,false,PropertyType.STRING,jlibUser,OnParentVersionAction.COPY,false,null);
		PropDef propIsAdmin = createPropertyDef(pjlibIsAdmin,false,false,PropertyType.BOOLEAN,jlibUser,OnParentVersionAction.COPY,false,null);
		PropDef propIsSysAdmin = createPropertyDef(pjlibIsSysadmin,false,false,PropertyType.BOOLEAN,jlibUser,OnParentVersionAction.COPY,false,null);
		
		ntdJLibrary.setPropertyDefs(new PropDef[]{propName,
												  propActive});
		ntdNode.setPropertyDefs(new PropDef[]{propDescription,
											  propCreator,
											  propImportance,
											  propTypecode,
											  propCreated,
											  propPath,
											  propPosition,
											  propSize,
											  propRestrictions,
											  propLockToken,
											  propLockUser});	
		ntdDocument.setPropertyDefs(new PropDef[]{propAuthor,
												  propLanguage,
												  propKeywords,
												  propCreationDate,
												  propURL,
												  propTitle,
												  propCategories,
												  propResources,
												  propRelations});
		ntdCategory.setPropertyDefs(new PropDef[]{propNodes,
												  propCategoryDate,
												  propCategoryDescription});
		ntdBookmark.setPropertyDefs(new PropDef[]{propBookmarkTypecode,
												  propBookmarkURL,
												  propBookmarkUSer,
												  propBookmarkDescription});
		ntdNote.setPropertyDefs(new PropDef[]{propNoteDate,
											  propText,
											  propNoteUser});
		ntdAuthor.setPropertyDefs(new PropDef[]{propBio,
												propAuthorUser});
		ntdRol.setPropertyDefs(new PropDef[]{propMembers,
											 propRolDescription});
		ntdGroup.setPropertyDefs(new PropDef[]{propUsers,
											   propGroupRoles,
											   propGroupDescription});
		ntdUser.setPropertyDefs(new PropDef[]{propUserRoles,
											  propGroups,
											  propFirstName,
											  propLastName,
											  propEmail,
											  propPassword,
											  propIsAdmin,
											  propIsSysAdmin});
		ntdFavorite.setPropertyDefs(new PropDef[]{propFavoriteUser,
												  propNode});
		
	}
	
	private void registerNodeDef(QName[] qnameDefs,
								 QName declaringNodeType,
								 NodeTypeDef nodeType,
								 int[] onParentVersionAction) {
		
		NodeDefImpl[] defs = new NodeDefImpl[qnameDefs.length];
		
		for (int i = 0; i < defs.length; i++) {
			defs[i] = new NodeDefImpl();
			defs[i].setAllowsSameNameSiblings(true);
			defs[i].setName(qnameDefs[i]);
			defs[i].setDeclaringNodeType(declaringNodeType);
			defs[i].setOnParentVersion(onParentVersionAction[i]);
		}
		nodeType.setChildNodeDefs(defs);
	}

	private PropDef createPropertyDef(QName name,
									  boolean mandatory,
									  boolean multivalued,
									  int type,
									  QName declaringType,
									  int onParentVersionAction,
									  boolean autocreated,
									  InternalValue[] defaultValues) {
		
		PropDefImpl pdef = new PropDefImpl();
		pdef.setName(name);
		pdef.setMandatory(mandatory);
		pdef.setMultiple(multivalued);
		pdef.setRequiredType(type);
		pdef.setDeclaringNodeType(declaringType);
		pdef.setOnParentVersion(onParentVersionAction);
		pdef.setAutoCreated(autocreated);
		if (autocreated) {
			pdef.setDefaultValues(defaultValues);
		}
		return pdef;
	}	
	
	private NodeTypeDef createNodeTypeDef(QName[] supertypes, 
										  QName name,
										  boolean mixin) {
		
		NodeTypeDef nodeDef = new NodeTypeDef();
		nodeDef.setMixin(mixin);
		nodeDef.setName(name);
		nodeDef.setOrderableChildNodes(true);
		nodeDef.setSupertypes(supertypes);
		
		return nodeDef;
	}

}
