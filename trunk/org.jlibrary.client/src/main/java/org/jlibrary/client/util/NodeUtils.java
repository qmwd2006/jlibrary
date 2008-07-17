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
package org.jlibrary.client.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.util.FileUtils;

/**
 * Misc utils for dealing with nodes
 * 
 * @author martin
 *
 */
public class NodeUtils {

	private static Map mapTypes;
	private static String[] descriptionTypes;
	
	private static void initMapTypes() {
		
		mapTypes = new HashMap();
		
		mapTypes.put(Types.FOLDER,
				Messages.getMessage("properties_type_dir"));
		mapTypes.put(Types.AUTOCAD_DOCUMENT,
				Messages.getMessage("properties_type_acad"));
		mapTypes.put(Types.EXCEL_DOCUMENT,
				Messages.getMessage("properties_type_xls"));
		mapTypes.put(Types.HTML_DOCUMENT,
				Messages.getMessage("properties_type_html"));
		mapTypes.put(Types.IMAGE_DOCUMENT,
				Messages.getMessage("properties_type_image"));
		mapTypes.put(Types.OO_DOCUMENT,
				Messages.getMessage("properties_type_oo"));
		mapTypes.put(Types.PDF_DOCUMENT,
				Messages.getMessage("properties_type_pdf"));
		mapTypes.put(Types.POWERPOINT_DOCUMENT,
				Messages.getMessage("properties_type_ppt"));
		mapTypes.put(Types.TEXT_DOCUMENT,
				Messages.getMessage("properties_type_text"));
		mapTypes.put(Types.WORD_DOCUMENT,
				Messages.getMessage("properties_type_doc"));
		mapTypes.put(Types.XML_DOCUMENT,
				Messages.getMessage("properties_type_xml"));
		mapTypes.put(Types.OTHER,
				Messages.getMessage("properties_type_other"));
		
		descriptionTypes = (String[])mapTypes.values().toArray(new String[]{});
		Arrays.sort(descriptionTypes);
	}
	
	/**
	 * Returns the importance label for a given node
	 * 
	 * @param node Node
	 * 
	 * @return String Importance label
	 */
	public static String getImportanceText(Node node) {
		
		return getImportanceText(node.getImportance().intValue());
	}
	
	/**
	 * Returns the importance label for a given value
	 * 
	 * @param value Value
	 * 
	 * @return String Importance label
	 */
	public static String getImportanceText(int value) {
		
		if (value == Node.IMPORTANCE_LOWEST.intValue()) {
			return Messages.getMessage("importance_lowest");
		} else if (value < Node.IMPORTANCE_MEDIUM.intValue()) {
			return Messages.getMessage("importance_low");
		} else if (value < Node.IMPORTANCE_HIGH.intValue()) {
			return Messages.getMessage("importance_medium");
		} else if (value < Node.IMPORTANCE_HIGHEST.intValue()) {
			return Messages.getMessage("importance_high");
		} else {
			return Messages.getMessage("importance_highest");
		}
	}

	/**
	 * Returns the creator name for a node
	 * 
	 * @param Node node
	 * 
	 * @return String Creator's name
	 */
	public static String getCreatorName(Node node) {
		
		if (node.getCreator().equals(User.ADMIN_CODE)) {
			return Messages.getMessage(User.ADMIN_NAME);
		}
		
		User user = MembersRegistry.getInstance().getUser(
				node.getRepository(),node.getCreator());
		
		if (user != null) {
			String username = user.getName();
			if (username.equals(User.ADMIN_NAME)) {
				username = Messages.getMessage(User.ADMIN_NAME);
			}
			return username;
		}
		return null;
	}
	
	/**
	 * Returns the node's parent name
	 * 
	 * @param Node node
	 * 
	 * @return String PArent's name
	 */
	public static String getParentName(Node node) {
		
		String parentId = node.getParent();
		if (parentId == null) {
			return null;
		}
		Node parent = EntityRegistry.getInstance().getNode(
				node.getParent(),node.getRepository());
		return parent.getName();
	}
	
	/**
	 * returns document's author name
	 * 
	 * @param document Document
	 * 
	 * @return String Author's name
	 */
	public static String getAuthorName(Document document) {
		
		Author author = document.getMetaData().getAuthor();
		if (author.equals(Author.UNKNOWN)) {
			return Messages.getMessage(Author.UNKNOWN_NAME);
		}
		return author.getName();
	}
	
	/**
	 * Returns the node's typecode name
	 * 
	 * @param Node node
	 * 
	 * @return String Node's typecode label
	 */
	public static String getTypeName(Node node) {
		
		if (mapTypes == null) {
			initMapTypes();
		}
		
		if (node.getTypecode().equals(Types.OTHER)) {
			return Messages.getAndParseValue(
					"properties_type_unk",
					"%1",
					FileUtils.getExtension(node.getPath()));
		} else {
			return (String)mapTypes.get(node.getTypecode());
		}
	}	
	
	public static String getDescriptionForType(Node node) {
		
		if (mapTypes == null) {
			initMapTypes();
		}
		
		return (String)mapTypes.get(node.getTypecode());
	}	
	
	public static String[] getSupportedNodeTypes() {
		
		if (mapTypes == null) {
			initMapTypes();
		}
		
		return descriptionTypes;
	}
	
	public static Integer getTypeForDescription(String description) {
	
		if (mapTypes == null) {
			initMapTypes();
		}
		
		Iterator it = mapTypes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			if (entry.getValue().equals(description)) {
				return (Integer)entry.getKey();
			}
		}
		return Types.OTHER;
	}

	public static boolean isExtensionUnknown(Node node) {

		return isExtensionUnknown(node.getPath());
	}
	
	public static boolean isExtensionUnknown(String path) {

		Integer type = Types.getTypeForFile(path);
		if (type == Types.OTHER) {
			return true;
		} else {
			return false;
		}
	}	
	
	public static String getGenericExtension(Node node) {

		Integer type = node.getTypecode();
		if (type.equals(Types.AUTOCAD_DOCUMENT)) {
			return ".dwg";
		} else if (type.equals(Types.EXCEL_DOCUMENT)) {
			return ".xls";
		} else if (type.equals(Types.HTML_DOCUMENT)) {
			return ".html";
		} else if (type.equals(Types.IMAGE_DOCUMENT)) {
			return ".bmp";
		} else if (type.equals(Types.OO_DOCUMENT)) {
			return ".sxw";
		} else if (type.equals(Types.PDF_DOCUMENT)) {
			return ".pdf";
		} else if (type.equals(Types.POWERPOINT_DOCUMENT)) {
			return ".ppt";
		} else if (type.equals(Types.TEXT_DOCUMENT)) {
			return ".txt";
		} else if (type.equals(Types.WORD_DOCUMENT)) {
			return ".doc";
		} else if (type.equals(Types.XML_DOCUMENT)) {
			return ".xml";
		}

		return FileUtils.getExtension(node.getPath());
	}	
}
