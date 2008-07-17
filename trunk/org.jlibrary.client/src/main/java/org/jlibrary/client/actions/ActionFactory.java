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
package org.jlibrary.client.actions;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.RetargetAction;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.actions.NewRepositoryAction;
import org.jlibrary.client.ui.repository.actions.OpenRepositoryAction;
import org.jlibrary.client.ui.search.actions.SearchAction;

/**
 * @author martin
 *
 * Holder for action classes
 */
public abstract class ActionFactory extends org.eclipse.ui.actions.ActionFactory {

	/*
	 * Global action handler ids
	 */
	public static final String ABOUT_ID = "about";
	public static final String ADD_DIRECTORIES_ID = "add_directories";
	public static final String ADD_FAVORITES_DEFAULT_ID = "add_favorites_unk";
	public static final String ADD_FAVORITES_ID = "add_favorites";
	public static final String ADD_RESOURCES_ID = "add_resources";
	public static final String CLOSE_ID = "close";
	public static final String CONNECT_ID = "connect";
	public static final String COPY_ID = "copy";
	public static final String CUT_ID = "cut";
	public static final String DELETE_ID = "delete";
	public static final String EXIT_ID = "exit";
	public static final String EXPORT_HTML_ID = "export_html";
	public static final String EXPORT_ID = "export";
	public static final String HELP_ID = "help";
	public static final String IMPORT_ID = "import";
	public static final String LOAD_CONTENT_ID = "load_content";
	public static final String LOCK_ID = "add_lock";
	public static final String NEW_DIRECTORY_ID = "new_directory";
	public static final String NEW_DOCUMENT_ID = "new_document";	
	public static final String NEW_REPOSITORY_ID = "new_repository";
	public static final String NEW_RESOURCE_ID = "new_resource";	
	public static final String OPEN_REPOSITORY_ID = "open_repository";
	public static final String PASTE_ID = "paste";
	public static final String REFRESH_ID = "refresh";
	public static final String RESTORE_VERSION_ID = "versions";
	public static final String SAVE_CONTENT_ID = "save_content";
	public static final String SEARCH_ID = "search";
	public static final String UNLOCK_ID = "add_unlock";
	public static final String WIZARD_DOWN_ID = "newWizardDropDown";

	/**
	 * Creates a new workbench action factory with the given id.
	 *
	 * @param actionId the id of actions created by this action factory
	 */
	protected ActionFactory(String actionId) {
		super(actionId);
	}


	/**
	 * Workbench action (id "close"): Close the active repository
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory CLOSE = new ActionFactory(CLOSE_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_close_rep"));   
			action.setToolTipText(Messages.getMessage("tooltip_close_rep")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_CLOSE_REPOSITORY));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_CLOSE_REPOSITORY_DISABLED));
			return action;

		}
	};

	
	
	/**
	 * Workbench action (id "refresh"): Refresh the active repository
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory REFRESH = new ActionFactory(REFRESH_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_refresh"));   
			action.setToolTipText(Messages.getMessage("tooltip_refresh")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_REFRESH_REPOSITORY));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_REFRESH_REPOSITORY_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "exit"): Exits jLibrary
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory EXIT = new ActionFactory(EXIT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new ExitAction(window);
			action.setId(getId());
			return action;

		}
	};

	/**
	 * Workbench action (id "open"): Open a new repository
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory ABOUT = new ActionFactory(ABOUT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new AboutAction(window);
			action.setId(getId());
			return action;
		}
	};

	/**
	 * Workbench action (id "copy"): Copy.
	 * This action is a {@link Retarget Retarget} action with
	 * id "copy". This action maintains its enablement state.
	 */
	public static final ActionFactory COPY = new ActionFactory(COPY_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_copy"));   
			action.setToolTipText(Messages.getMessage("tooltip_copy")); 
			window.getPartService().addPartListener(action);
			action.setActionDefinitionId("org.eclipse.ui.edit.copy"); 
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_COPY));
			action.setHoverImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_COPY_HOVER));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_COPY_DISABLED));
			return action;
		}
	};

	/**
	 * Workbench action (id "paste"): Paste.
	 * This action is a {@link Retarget Retarget} action with
	 * id "paste". This action maintains its enablement state.
	 */
	public static final ActionFactory PASTE = new ActionFactory(PASTE_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_paste"));   
			action.setToolTipText(Messages.getMessage("tooltip_paste")); 
			window.getPartService().addPartListener(action);
			action.setActionDefinitionId("org.eclipse.ui.edit.paste"); 
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_PASTE));
			action.setHoverImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_PASTE_HOVER));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_PASTE_DISABLED));
			return action;
		}
	};

	/**
	 * Workbench action (id "cut"): Cut.
	 * This action is a {@link Retarget Retarget} action with
	 * id "cut". This action maintains its enablement state.
	 */
	public static final ActionFactory CUT = new ActionFactory(CUT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_cut"));   
			action.setToolTipText(Messages.getMessage("tooltip_cut")); 
			window.getPartService().addPartListener(action);
			action.setActionDefinitionId("org.eclipse.ui.edit.cut"); 
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_CUT));
			action.setHoverImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_CUT_HOVER));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_CUT_DISABLED));
			return action;
		}
	};

	/**
	 * Workbench action (id "delete"): Deletes selected node
	 * 
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory DELETE = new ActionFactory(DELETE_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_delete"));   
			action.setToolTipText(Messages.getMessage("tooltip_delete")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_DISABLED));
			return action;

		}
	};	
	
	
	/**
	 * Workbench action (id "new_repository"): Creates a new repository
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory NEW_REPOSITORY = new ActionFactory(NEW_REPOSITORY_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new NewRepositoryAction(window);
			action.setId(getId());
			return action;
		}
	};



	/**
	 * Workbench action (id "open"): Open a new repository
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory OPEN = new ActionFactory(OPEN_REPOSITORY_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new OpenRepositoryAction(window);
			action.setId(getId());
			return action;
		}
	};
	
	public static final ActionFactory SEARCH = new ActionFactory(SEARCH_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new SearchAction(window);
			action.setId(getId());
			return action;
		}
	};

	/**
	 * IDE-specific workbench action: Opens the "new" wizard drop down.
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory NEW_WIZARD_DROP_DOWN = new ActionFactory(WIZARD_DOWN_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			// @issue we are creating a NEW action just to pass to NewWizardDropDownAction
			IWorkbenchAction action = new NewWizardDropDownAction(window);
			action.setId(getId());
			return action;
		}
	};

	/**
	 * Workbench action (id "new_directory"): Creates a new directory
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory NEW_DIRECTORY = new ActionFactory(NEW_DIRECTORY_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_new_directory"));   
			action.setToolTipText(Messages.getMessage("tooltip_new_directory")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DIRECTORY));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DIRECTORY_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "new_document"): Creates a new document
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory NEW_DOCUMENT = new ActionFactory(NEW_DOCUMENT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_new_document"));   
			action.setToolTipText(Messages.getMessage("tooltip_new_document")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DOCUMENT));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_DOCUMENT_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "new_resource"): Creates a new resource
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory NEW_RESOURCE = new ActionFactory(NEW_RESOURCE_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_new_resource"));   
			action.setToolTipText(Messages.getMessage("tooltip_new_resource")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_RESOURCE));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_NEW_RESOURCE_DISABLED));
			return action;

		}
	};	
	
	/**
	 * Workbench action (id "export"): Exports a repository
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory EXPORT = new ActionFactory(EXPORT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_export_rep"));   
			action.setToolTipText(Messages.getMessage("tooltip_export_rep")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_EXPORT));
			return action;

		}
	};

	/**
	 * Workbench action (id "export_html"): Exports a repository to HTML
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory EXPORT_HTML = 
		new ActionFactory(EXPORT_HTML_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_export_static"));   
			action.setToolTipText(Messages.getMessage("tooltip_export_static")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_EXPORT_HTML));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_EXPORT_HTML_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "add_directories"): Adds files and directories
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory ADD_DIRECTORIES = 
		new ActionFactory(ADD_DIRECTORIES_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_add_directory"));   
			action.setToolTipText(Messages.getMessage("tooltip_add_directory")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_ADD_DIRECTORY));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_ADD_DIRECTORY_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "add_directories"): Adds files and directories
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory ADD_RESOURCES = 
		new ActionFactory(ADD_RESOURCES_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_add_resources"));   
			action.setToolTipText(Messages.getMessage("tooltip_add_resources")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_ADD_RESOURCES));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_ADD_RESOURCES_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "save_content"): Saves document contents
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory SAVE_CONTENT = 
		new ActionFactory(SAVE_CONTENT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_save_content"));   
			action.setToolTipText(Messages.getMessage("tooltip_save_content")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_SAVE_CONTENT));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_SAVE_CONTENT_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "load_content"): Loads document contents
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory LOAD_CONTENT = 
		new ActionFactory(LOAD_CONTENT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_load_content"));   
			action.setToolTipText(Messages.getMessage("tooltip_load_content")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_LOAD_CONTENT));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_LOAD_CONTENT_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "lock"): Locks a file
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory LOCK = 
		new ActionFactory(LOCK_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_work_with"));   
			action.setToolTipText(Messages.getMessage("tooltip_work_with")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_WORK_WITH));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_WORK_WITH_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "unlock"): Unlocks a file
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory UNLOCK = 
		new ActionFactory(UNLOCK_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_stop_work_with"));   
			action.setToolTipText(Messages.getMessage("tooltip_stop_work_with")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_STOP_WORK_WITH));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_STOP_WORK_WITH_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "add_favorites"): Adds documents to favorites
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory ADD_FAVORITES = 
		new ActionFactory(ADD_FAVORITES_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_favorites_add"));   
			action.setToolTipText(Messages.getMessage("tooltip_favorites_add")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_ADD_FAVORITE));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_ADD_FAVORITE_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "add_directories"): Adds documents as favorites to 
	 * the default category
	 * 
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory ADD_FAVORITES_DEFAULT = 
		new ActionFactory(ADD_FAVORITES_DEFAULT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_favorites_add_default"));   
			action.setToolTipText(Messages.getMessage("tooltip_favorites_add_default")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_ADD_FAVORITE_DEFAULT));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_ADD_FAVORITE_DEFAULT_DISABLED));
			return action;

		}
	};

	/**
	 * Workbench action (id "versions"): Brings up version management dialog
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory RESTORE_VERSION = 
		new ActionFactory(RESTORE_VERSION_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), 
									Messages.getMessage("item_restore_version"));   
			action.setToolTipText(Messages.getMessage("tooltip_restore_version")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_RESTORE_VERSION));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(
							SharedImages.IMAGE_RESTORE_VERSION_DISABLED));
			return action;

		}
	};
	
	
	/**
	 * Workbench action (id "export"): Imports a repository
	 * This action maintains its enablement state.
	 */
	public static final ActionFactory IMPORT = new ActionFactory(IMPORT_ID) { 
		/* (non-javadoc) method declared on ActionFactory */
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			RetargetAction action = new RetargetAction(getId(), Messages.getMessage("item_import_rep"));   
			action.setToolTipText(Messages.getMessage("tooltip_import_rep")); 
			window.getPartService().addPartListener(action);
			action.setImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_IMPORT));
			action.setDisabledImageDescriptor(
					SharedImages.getImageDescriptor(SharedImages.IMAGE_IMPORT_DISABLED));

			return action;

		}
	};

}
