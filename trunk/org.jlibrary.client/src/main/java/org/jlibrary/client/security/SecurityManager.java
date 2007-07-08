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
package org.jlibrary.client.security;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;

/**
 * This class will be in charge of managing user interface security access. 
 * 
 * Different jLibrary widgets will have to explicitly ask this class if the 
 * user has permission to perform operations. Operations are defined within 
 * this class as constant fields. 
 * 
 * @author martin
 *
 */
public class SecurityManager {

/*
 * These are constant fields to define client UI operations. In a future, 
 * this constant fields could be attached to roles into repositories, so the 
 * user could be able to add/remove permissions from custom defined roles, and 
 * so we will get a fine grained security access.
 * 
 * This string keys will also allow us to easily internationalize permission
 * messages on a future.
 */

	public static final String ADD_DIRECTORY = "add-directory";
	public static final String ADD_RESOURCES_DIRECTORY = "add-resources-directory";
	public static final String BROWSE_VERSIONS = "browse-versions";
	public static final String CATEGORY_ADD_DOCUMENTS = "category-add-documents";
	public static final String CATEGORY_REMOVE_DOCUMENTS = "category-remove-documents";
	public static final String CLOSE_REPOSITORY = "close-repository";
	public static final String CONNECT_REPOSITORY = "connect-repository";
	public static final String COPY = "copy";
	public static final String CREATE_AUTHOR = "create-author";
	public static final String CREATE_BOOKMARK = "create-bookmark";
	public static final String CREATE_BOOKMARK_FOLDER = "create-bookmark-folder";
	public static final String CREATE_CATEGORY = "create-category";
	public static final String CREATE_DIRECTORY = "create-directory";
	public static final String CREATE_DOCUMENT = "create-document";
	public static final String CREATE_FAVOURITE = "create-favourite";
	public static final String CREATE_GROUP = "create-group";
	public static final String CREATE_RESOURCE = "create-resource";
	public static final String CREATE_RESTRICTION = "create-restriction";
	public static final String CREATE_ROLE = "create-role";
	public static final String CREATE_USER = "create-user";
	public static final String CUT = "cut";
	public static final String DELETE_AUTHOR = "delete-author";
	public static final String DELETE_BOOKMARK = "remove-bookmark";
	public static final String DELETE_CATEGORY = "delete-category";
	public static final String DELETE_DIRECTORY = "delete-directory";
	public static final String DELETE_DOCUMENT = "delete-document";
	public static final String DELETE_FAVOURITE = "remove-favourite";
	public static final String DELETE_GROUP = "delete-group";
	public static final String DELETE_REPOSITORY = "delete-repository";
	public static final String DELETE_RESOURCE = "delete-resource";
	public static final String DELETE_RESTRICTION = "remove-restriction";
	public static final String DELETE_ROLE = "delete-role";
	public static final String DELETE_USER = "delete-user";
	public static final String EXPORT_REPOSITORY = "export-repository";
	public static final String IMPORT_REPOSITORY = "import-repository";
	public static final String HTMLEXPORT_REPOSITORY = "htmlexport-repository";
	public static final String LOAD_CONTENT = "load-content";
	public static final String LOCK_DOCUMENT = "lock-document";
	public static final String MANAGE_TOOLS = "manage-tools";
	public static final String OPEN_BOOKMARK = "open-bookmark";	
	public static final String OPEN_AUTHOR = "open-author";
	public static final String OPEN_CATEGORY = "open-category";
	public static final String OPEN_DIRECTORY = "open-directory";
	public static final String OPEN_DOCUMENT = "open-document";
	public static final String OPEN_GROUP = "open-group";
	public static final String OPEN_REPOSITORY = "open-repository";
	public static final String OPEN_USER = "open-user";
	public static final String OPEN_RESOURCE = "open-resource";
	public static final String OPEN_ROL = "open-rol";
	public static final String PASTE = "paste";
	public static final String REFRESH_CONTENTS = "refresh-contents";
	public static final String RENAME = "rename";
	public static final String RESTORE_VERSIONS = "restore-versions";
	public static final String SAVE_AUTHOR = "save-author";
	public static final String SAVE_BOOKMARK = "save-bookmark";
	public static final String SAVE_CATEGORY = "save-category";
	public static final String SAVE_CONTENT= "save-content";
	public static final String SAVE_DIRECTORY = "save-directory";
	public static final String SAVE_DOCUMENT = "save-document";
	public static final String SAVE_GROUP = "save-group";
	public static final String SAVE_REPOSITORY = "save-repository";
	public static final String SAVE_RESOURCE = "save-resource";
	public static final String SAVE_ROLE = "save-role";
	public static final String SAVE_USER = "save-user";
	public static final String SEARCH = "search";
	public static final String UNLOCK_DOCUMENT = "unlock-document";
	public static final String VIEW_AUTHORS = "view-authors";
	public static final String VIEW_GROUPS = "view_groups";
	public static final String VIEW_RESTRICTIONS = "view_restrictions";	
	public static final String VIEW_ROLES = "view_roles";
	public static final String VIEW_USERS = "view-users";
	public static final String WEB_BROWSING = "web-browsing";
	public static final String WEB_CRAWLING = "web-crawling";
	
	
	// Mapping from roles to permissions. In a future, this mappings will come
	// directly embedded on the roles entities, and the user will be able to 
	// customize role permissions
	private static String[] READER_ACTIONS = new String[]{
		BROWSE_VERSIONS,
		CLOSE_REPOSITORY,
		CONNECT_REPOSITORY,
		COPY,
		CREATE_BOOKMARK,
		CREATE_BOOKMARK_FOLDER,
		DELETE_BOOKMARK,
		HTMLEXPORT_REPOSITORY,
		MANAGE_TOOLS,
		OPEN_BOOKMARK,
		OPEN_DIRECTORY,
		OPEN_DOCUMENT,
		OPEN_REPOSITORY,
		OPEN_RESOURCE,
		REFRESH_CONTENTS,
		SAVE_BOOKMARK,		
		SAVE_CONTENT,
		SEARCH,
		WEB_BROWSING		
	};

	//editor actions also include all reader actions
	private static String[] EDITOR_ACTIONS = new String[]{
		ADD_DIRECTORY,
		ADD_RESOURCES_DIRECTORY,
		CATEGORY_ADD_DOCUMENTS,
		CATEGORY_REMOVE_DOCUMENTS,
		CREATE_AUTHOR,
		CREATE_CATEGORY,
		CREATE_DIRECTORY,
		CREATE_DOCUMENT,
		CREATE_FAVOURITE,
		CREATE_RESOURCE,
		CUT,
		DELETE_AUTHOR,
		DELETE_CATEGORY,
		DELETE_DIRECTORY,
		DELETE_DOCUMENT,
		DELETE_FAVOURITE,
		DELETE_RESOURCE,
		LOAD_CONTENT,
		LOCK_DOCUMENT,
		OPEN_CATEGORY,
		OPEN_AUTHOR,
		PASTE,
		RENAME,
		RESTORE_VERSIONS,
		SAVE_AUTHOR,
		SAVE_CATEGORY,
		SAVE_DIRECTORY,
		SAVE_DOCUMENT,
		SAVE_REPOSITORY,
		SAVE_RESOURCE,
		UNLOCK_DOCUMENT,
		VIEW_AUTHORS,
		WEB_CRAWLING		
	};	
	
	//admin actions also include all reader and editor actions
	private static String[] ADMIN_ACTIONS = new String[]{
		CREATE_USER,
		CREATE_GROUP,
		CREATE_RESTRICTION,
		CREATE_ROLE,
		DELETE_GROUP,
		DELETE_REPOSITORY,
		DELETE_RESTRICTION,
		DELETE_ROLE,
		DELETE_USER,
		EXPORT_REPOSITORY,
		OPEN_GROUP,
		OPEN_ROL,
		OPEN_USER,
		SAVE_GROUP,
		SAVE_USER,
		SAVE_ROLE,
		VIEW_GROUPS,
		VIEW_RESTRICTIONS,
		VIEW_ROLES,
		VIEW_USERS,
	};	

	/**
	 * Checks if an user can performs an action. 
	 * 
	 * @param repositoryId Id of the repository to be checked
	 * @param node The node in which the action will be performed or 
	 * <code>null</code> if it's a generic action like Search for example	
	 * @param action Action to check if can be performed
	 */
	public boolean canPerformAction(String repositoryId, 
								    String action) {
		
		return canPerformAction(repositoryId,null,action);
	}
	
	/**
	 * Checks if an user can performs an action. 
	 * 
	 * @param repositoryId Id of the repository to be checked
	 * @param node The node in which the action will be performed or 
	 * <code>null</code> if it's a generic action like Search for example	
	 * @param action Action to check if can be performed
	 */
	public boolean canPerformAction(String repositoryId, 
								    Node node,
								    String action) {
		
		Repository repository = RepositoryRegistry.getInstance().
			getRepository(repositoryId);
		
		if (!repository.isConnected()) {
			if (!action.equals(SecurityManager.CONNECT_REPOSITORY)) {
				return false;
			} else {
				return true;
			}
		}
		
		return canPerformAction(repository.getTicket(),node,action);
	}

	/**
	 * Checks if an user can performs an action. 
	 * 
	 * @param ticket Ticket with user information
	 * @param action Action to check if can be performed
	 */	
	public boolean canPerformAction(Ticket ticket, String action) {
		
		return canPerformAction(ticket,null,action);
	}
	
	/**
	 * Checks if an user can performs an action. 
	 * 
	 * @param ticket Ticket with user information
	 * @param node The node in which the action will be performed or 
	 * <code>null</code> if it's a generic action like Search for example
	 * @param action Action to check if can be performed
	 */
	public boolean canPerformAction(Ticket ticket, Node node, String action) {
		
		User user = ticket.getUser();
		boolean isAdmin = false;
		boolean isReader = false;
		boolean isEditor = false;
		
		if (user.isAdmin()) {
			isAdmin = true;
		} else {
			
			Set roles = obtainRoles(ticket,user,node);
			
			
			// Not tagged directly as admin. Check nodes
			Iterator it = roles.iterator();
			while (it.hasNext()) {
				Rol rol = (Rol) it.next();
				if (rol.getName().equals(Rol.ADMIN_ROLE_NAME)) {
					isAdmin = true;
					break;
				}
				if (rol.getName().equals(Rol.PUBLISHER_ROLE_NAME)) {
					isEditor = true;
				}
				if (rol.getName().equals(Rol.READER_ROLE_NAME)) {
					isReader = true;
				}				
			}
		}

		for (int i = 0; i < ADMIN_ACTIONS.length; i++) {
			if (ADMIN_ACTIONS[i] == action) {
				return isAdmin;
			}
		}

		for (int i = 0; i < EDITOR_ACTIONS.length; i++) {
			if (EDITOR_ACTIONS[i] == action) {
				return isAdmin || isEditor;
			}
		}

		for (int i = 0; i < READER_ACTIONS.length; i++) {
			if (READER_ACTIONS[i] == action) {
				return isAdmin || isEditor || isReader;
			}
		}
		
		return false;
	}

	private Set obtainRoles(Ticket ticket, User user, Node node) {
		
		if (user.getGroups().isEmpty()) {
			return user.getRoles();
		}
		Set roles = new HashSet(user.getRoles());
		
		if (node == null) {
			String repositoryId = ticket.getRepositoryId();
			Repository repository = 
				RepositoryRegistry.getInstance().getRepository(repositoryId);
			node = repository.getRoot();
		}
		
		List restrictions = node.getRestrictions();
		Iterator it = user.getGroups().iterator();
		while (it.hasNext()) {
			Group group = (Group) it.next();
			boolean found = false;
			for (int j=0;j<restrictions.size();j++) {
				if (restrictions.get(j).equals(group.getId())) {
					found = true;break;
				}
			}
			if (!found) continue;
			
			// Add the group roles
			roles.addAll(group.getRoles());
		}
		
		return roles;
	}
	
	/**
	 * Checks if an user can performs an action over the current active ticket.
	 * If there is no active user ticket, then it will return <code>false</code>. 
	 * 
	 * @param action Action to check if can be performed
	 */
	public boolean canPerformAction(String action) {	

		Repository repository = JLibraryPlugin.getCurrentRepository();
		if ((repository == null) || !repository.isConnected()) {
			return false;
		}
		Ticket ticket = repository.getTicket();
		if (ticket == null) {
			return false;
		}
		return canPerformAction(ticket,action);
	}
	
	/**
	 * Checks if an user can performs an action over a repository.
	 * 
	 * @param repository Repository in which we want to check permissions
	 * @param action Action to check if can be performed
	 */
	public boolean canPerformAction(Repository repository, String action) {	

		Ticket ticket = repository.getTicket();
		return canPerformAction(ticket,action);
	}	
	
	/**
	 * Tells if a given user is or not is an admin
	 * 
	 * @param user User to check
	 * 
	 * @return boolean <code>true</code> if the user is an admin user and 
	 * <code>false</code> otherwise.
	 */
	public boolean isAdmin(Ticket ticket) {
		
		if (ticket.getUser().isAdmin()) return true;
		
		Set roles = obtainRoles(ticket,ticket.getUser(),null);
		Iterator it = roles.iterator();
		while (it.hasNext()) {
			Rol rol = (Rol) it.next();
			if (rol.getName().equals(Rol.ADMIN_ROLE_NAME)) {
				return true;
			}
		}
		return false;
	}
}
