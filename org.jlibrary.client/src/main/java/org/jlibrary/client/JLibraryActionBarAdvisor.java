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
package org.jlibrary.client;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.util.StatusLineContributionItem;
import org.jlibrary.client.actions.ActionFactory;
import org.jlibrary.client.actions.ManageConfigurationAction;
import org.jlibrary.client.actions.NewWizardMenu;
import org.jlibrary.client.actions.UpdateAction;
import org.jlibrary.client.preferences.actions.PreferencesAction;
import org.jlibrary.client.ui.security.actions.ChangeAdminPasswordAction;

public class JLibraryActionBarAdvisor extends ActionBarAdvisor {

	private PreferencesAction preferencesAction;
	private ChangeAdminPasswordAction changeAdminPasswordAction;

	private IWorkbenchAction searchAction;
	private IWorkbenchAction openAction;
        private IWorkbenchAction refreshAction;
	private IWorkbenchAction closeAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction exitAction;
	
	private IWorkbenchAction cutAction;
	private IWorkbenchAction copyAction;
	private IWorkbenchAction pasteAction;
	private IWorkbenchAction deleteAction;

	private IWorkbenchAction addDirectoriesAction;
	private IWorkbenchAction addResourcesAction;	
	
	private IWorkbenchAction exportAction;
	private IWorkbenchAction exportHTMLAction;
	private IWorkbenchAction importAction;
	
	private NewWizardMenu newWizardMenu;
	private StatusLineContributionItem statusLineItem;
	
	private IWorkbenchAction updateAction;
	private IWorkbenchAction manageConfigAction;	

	private IWorkbenchAction saveContentAction;	
	private IWorkbenchAction loadContentAction;	
	private IWorkbenchAction lockAction;	
	private IWorkbenchAction unlockAction;	
	private IWorkbenchAction addFavoritesAction;	
	private IWorkbenchAction addFavoritesDefaultAction;	
	private IWorkbenchAction restoreVersionAction;
	private IWorkbenchAction saveAction;
	private IWorkbenchAction saveAllAction;	
	
	public JLibraryActionBarAdvisor(IActionBarConfigurer configurer) {
		
		super(configurer);
	}
	
	protected void makeActions(IWorkbenchWindow window) {

		IActionBarConfigurer configurer = getActionBarConfigurer();
		
		searchAction = ActionFactory.SEARCH.create(window);
		configurer.registerGlobalAction(searchAction);
		
		openAction = ActionFactory.OPEN.create(window);
		configurer.registerGlobalAction(openAction);

		refreshAction = ActionFactory.REFRESH.create(window);
		configurer.registerGlobalAction(refreshAction);		
                
		closeAction = ActionFactory.CLOSE.create(window);
		configurer.registerGlobalAction(closeAction);
		
		exitAction = ActionFactory.EXIT.create(window);
		configurer.registerGlobalAction(exitAction);		
		
		aboutAction = ActionFactory.ABOUT.create(window);
		configurer.registerGlobalAction(aboutAction);
		
		updateAction = new UpdateAction(window);
		configurer.registerGlobalAction(updateAction);
		
		manageConfigAction = new ManageConfigurationAction(window);
		configurer.registerGlobalAction(manageConfigAction);
		
		cutAction = ActionFactory.CUT.create(window);
		configurer.registerGlobalAction(cutAction);		
		
		deleteAction = ActionFactory.DELETE.create(window);
		configurer.registerGlobalAction(deleteAction);
		
		copyAction = ActionFactory.COPY.create(window);
		configurer.registerGlobalAction(copyAction);
		
		pasteAction = ActionFactory.PASTE.create(window);
		configurer.registerGlobalAction(pasteAction);
		
		importAction = ActionFactory.IMPORT.create(window);
		configurer.registerGlobalAction(importAction);
		
		exportAction = ActionFactory.EXPORT.create(window);
		configurer.registerGlobalAction(exportAction);		

		exportHTMLAction = ActionFactory.EXPORT_HTML.create(window);
		configurer.registerGlobalAction(exportHTMLAction);		

		addDirectoriesAction = ActionFactory.ADD_DIRECTORIES.create(window);
		configurer.registerGlobalAction(addDirectoriesAction);		

		addResourcesAction = ActionFactory.ADD_RESOURCES.create(window);
		configurer.registerGlobalAction(addResourcesAction);				

		saveContentAction = ActionFactory.SAVE_CONTENT.create(window);
		configurer.registerGlobalAction(saveContentAction);				
		loadContentAction = ActionFactory.LOAD_CONTENT.create(window);
		configurer.registerGlobalAction(loadContentAction);				
		lockAction = ActionFactory.LOCK.create(window);
		configurer.registerGlobalAction(lockAction);				
		unlockAction = ActionFactory.UNLOCK.create(window);
		configurer.registerGlobalAction(unlockAction);				
		addFavoritesAction = ActionFactory.ADD_FAVORITES.create(window);
		configurer.registerGlobalAction(addFavoritesAction);				
		addFavoritesDefaultAction = ActionFactory.ADD_FAVORITES_DEFAULT.create(window);
		configurer.registerGlobalAction(addFavoritesDefaultAction);				
		restoreVersionAction = ActionFactory.RESTORE_VERSION.create(window);
		configurer.registerGlobalAction(restoreVersionAction);				
		
		
		preferencesAction = new PreferencesAction();
		changeAdminPasswordAction = new ChangeAdminPasswordAction(window);
		
		saveAction = org.eclipse.ui.actions.ActionFactory.SAVE.create(window);
		configurer.registerGlobalAction(saveAction);
		
		saveAllAction = org.eclipse.ui.actions.ActionFactory.SAVE_ALL.create(window);
		configurer.registerGlobalAction(saveAllAction);
		
		ActionFactory.NEW_WIZARD_DROP_DOWN.create(window);			
	}	
	
	protected void fillCoolBar(ICoolBarManager coolBar) {

		IToolBarManager tBarMgr = new ToolBarManager(coolBar.getStyle());
		tBarMgr.add(newWizardMenu.getNewRepositoryAction());
		tBarMgr.add(openAction);
		tBarMgr.add(newWizardMenu.getNewDirectoryAction());
		tBarMgr.add(newWizardMenu.getNewDocumentAction());		
		tBarMgr.add(newWizardMenu.getNewResourceAction());
		tBarMgr.add(new Separator());
		tBarMgr.add(addDirectoriesAction);
		tBarMgr.add(addResourcesAction);
		coolBar.add(new ToolBarContributionItem(tBarMgr));	
		
		tBarMgr = new ToolBarManager(coolBar.getStyle());
		tBarMgr.add(closeAction);
		tBarMgr.add(importAction);
		tBarMgr.add(exportAction);
		tBarMgr.add(exportHTMLAction);
		tBarMgr.add(new Separator());/*
		tBarMgr.add(saveAction);
		tBarMgr.add(saveallAction);*/
		tBarMgr.add(saveAction);
		tBarMgr.add(saveAllAction);
        coolBar.add(new ToolBarContributionItem(tBarMgr));		
		
		tBarMgr = new ToolBarManager(coolBar.getStyle());
		tBarMgr.add(new Separator());
		tBarMgr.add(cutAction);
		tBarMgr.add(copyAction);
		tBarMgr.add(pasteAction);	
		tBarMgr.add(deleteAction);
        tBarMgr.add(refreshAction);
        coolBar.add(new ToolBarContributionItem(tBarMgr));

		tBarMgr = new ToolBarManager(coolBar.getStyle());
		tBarMgr.add(saveContentAction);
		tBarMgr.add(loadContentAction);
		tBarMgr.add(lockAction);	
		tBarMgr.add(unlockAction);
		tBarMgr.add(addFavoritesAction);
		tBarMgr.add(addFavoritesDefaultAction);
		tBarMgr.add(restoreVersionAction);	
		coolBar.add(new ToolBarContributionItem(tBarMgr));

		//tBarMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
	}
	
	protected void fillStatusLine(IStatusLineManager statusLine) {

		statusLineItem = new StatusLineContributionItem("ModeContributionItem");
		statusLine.add(statusLineItem);		
	}

	protected void fillMenuBar(IMenuManager menuBar) {

		IActionBarConfigurer configurer = getActionBarConfigurer();
		IWorkbenchWindow window = configurer.getWindowConfigurer().getWindow();
		
		IMenuManager menubar = configurer.getMenuManager();
		menubar.add(createFileMenu(window));
		menubar.add(createEditMenu(window));
		menubar.add(createSearchMenu(window));
		menubar.add(createAdminMenu(window));
		menubar.add(createWindowMenu(window));
		menubar.add(createHelpMenu(window));
	}
	
	/**
	 * Creates and returns the File menu.
	 */
	private MenuManager createFileMenu(IWorkbenchWindow window) {
		
		Separator separator = new Separator();
		MenuManager menu = new MenuManager(Messages.getMessage("item_file"), IWorkbenchActionConstants.M_FILE); //$NON-NLS-1$
		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		{
			MenuManager newMenu = new MenuManager(Messages.getMessage("item_new")); //$NON-NLS-1$
			this.newWizardMenu = new NewWizardMenu(window);
			newMenu.add(this.newWizardMenu);
			menu.add(newMenu);
		}
		
		menu.add(separator);
		menu.add(addDirectoriesAction);
		menu.add(addResourcesAction);
		menu.add(separator);
		menu.add(openAction);
        menu.add(refreshAction);
		menu.add(closeAction);
		menu.add(separator);
		menu.add(importAction);
		menu.add(exportAction);
		menu.add(exportHTMLAction);
		menu.add(separator);
		menu.add(new Separator());
		menu.add(saveContentAction);
		menu.add(loadContentAction);
		menu.add(lockAction);	
		menu.add(unlockAction);
		menu.add(addFavoritesAction);
		menu.add(addFavoritesDefaultAction);
		menu.add(restoreVersionAction);			
		menu.add(separator);
		menu.add(exitAction);
		
		return menu;
	}

	/**
	 * Creates and returns the Search menu.
	 */
	private MenuManager createSearchMenu(IWorkbenchWindow window) {
		
		MenuManager menu = new MenuManager(Messages.getMessage("item_search_top"), "search"); //$NON-NLS-1$

		menu.add(searchAction);
		
		return menu;
	}	
	
	/**
	 * Creates and returns the Window menu.
	 */
	private MenuManager createWindowMenu(IWorkbenchWindow window) {
		
		MenuManager menu = new MenuManager(Messages.getMessage("item_window"), IWorkbenchActionConstants.M_WINDOW); //$NON-NLS-1$

		MenuManager perspectiveMenu = new MenuManager(Messages.getMessage("item_show_perspective"));
		IContributionItem perspectiveList = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
		perspectiveMenu.add(perspectiveList);
		menu.add(perspectiveMenu);

		MenuManager viewMenu = new MenuManager(Messages.getMessage("item_show_view"));
		IContributionItem viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);		
		viewMenu.add(viewList);
		menu.add(viewMenu);
						
		return menu;
	}	
	
	/**
	 * Creates and returns the Edit menu.
	 */
	private MenuManager createEditMenu(IWorkbenchWindow window) {
		
		MenuManager menu = new MenuManager(Messages.getMessage("item_edit"), IWorkbenchActionConstants.M_EDIT); //$NON-NLS-1$
		
		menu.add(cutAction);
		menu.add(copyAction);
		menu.add(pasteAction);
		menu.add(deleteAction);
		
		return menu;
	}

	/**
	 * Creates and returns the Admin menu.
	 */
	private MenuManager createAdminMenu(IWorkbenchWindow window) {
		
		MenuManager menu = new MenuManager(Messages.getMessage("item_admin"), IWorkbenchActionConstants.M_FILE); //$NON-NLS-1$
		
		menu.add(preferencesAction);
		menu.add(new Separator());
		menu.add(changeAdminPasswordAction);
		
		return menu;
	}
	
	/**
	 * Creates and returns the Help menu.
	 */
	private MenuManager createHelpMenu(IWorkbenchWindow window) {
		
		MenuManager menu = new MenuManager(Messages.getMessage("item_help"), IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$

		//TODO: Help, intro and cheatsheets are disabled for lack of mainteinance time. 
		// Feel free to reactivate these features.
		/*
		menu.add(org.eclipse.ui.actions.ActionFactory.INTRO.create(window));
		menu.add(new Separator());
		menu.add(org.eclipse.ui.actions.ActionFactory.HELP_CONTENTS.create(window));
		menu.add(org.eclipse.ui.actions.ActionFactory.HELP_SEARCH.create(window));
		menu.add(org.eclipse.ui.actions.ActionFactory.DYNAMIC_HELP.create(window));
		menu.add(new Separator());
		*/
		menu.add(createUpdateMenu(window));
		menu.add(new Separator());
		
		menu.add(aboutAction);

		return menu;
	}

	/**
	 * 
	 * @param window
	 * @param menu
	 */
	private MenuManager createUpdateMenu(IWorkbenchWindow window) {
		MenuManager menu = new MenuManager(Messages.getMessage("update_software"));
		
		menu.add(updateAction);
		menu.add(manageConfigAction);
		
		return menu;
	}	
}
