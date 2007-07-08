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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.properties.PropertySheet;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.search.SearchView;
import org.jlibrary.core.entities.IResource;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main plugin class to be used in the desktop.
 */
public class JLibraryPlugin extends AbstractUIPlugin {

	static Logger logger = LoggerFactory.getLogger(JLibraryPlugin.class);
	
	public static final String PLUGIN_ID = "org.jlibrary.client";
	
	//The shared instance.
	private static JLibraryPlugin plugin;

	private boolean goingToClose = false;
	
	private SecurityManager securityManager = new SecurityManager();
	
	public JLibraryPlugin() {
		
		super();
		plugin = this;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#getImageRegistry()
	 */
	public ImageRegistry getImageRegistry() {
		
		return JFaceResources.getImageRegistry();
	}
		
	/**
	 * Returns the shared instance.
	 */
	public static JLibraryPlugin getDefault() {
		
		return plugin;
	}	

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
	
	/**
	 * Returns the plugin's font registry
	 * 
	 * @return FontRegistry plugin's font registry
	 */
	public FontRegistry getFontRegistry() {
		
		return JFaceResources.getFontRegistry();
	}
	
	public static IEditorPart getActiveEditor()
	{
		IWorkbenchPage page = getActivePage();
		
		if (page == null)
			return null;
		
		return page.getActiveEditor();
	}

	public static IWorkbenchPage getActivePage()
	{
		//AbstractUIPlugin plugin = (AbstractUIPlugin) Platform.getPlugin(JLibraryApplication.PLUGIN_ID);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;

		IWorkbenchPage page = window.getActivePage();
		if (page == null)
			return null;

		return page;
	}
	
	/**
	 * Returns the RepositoryView or null if it is not open.
	 * 
	 * @return RepositoryView
	 */
	public static RepositoryView findRepositoriesView() {
		
		if (RepositoryView.getInstance() != null) {
			return RepositoryView.getInstance();
		}
		
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			for (int j = 0; j < pages.length; j++) {
				if (pages[j] != null) {
					IViewPart view = pages[j].findView(RepositoryView.VIEW_ID);
					if (view instanceof RepositoryView) {
						return (RepositoryView) view;
					}
				}
			}
		}
		return RepositoryView.getInstance();
	}
	
	/**
	 * Returns the PropertiesView or null if it is not open.
	 * 
	 * @return PropertySheet Properties view
	 */
	public static PropertySheet findPropertiesView() {
				
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			for (int j = 0; j < pages.length; j++) {
				if (pages[j] != null) {
					IViewPart view = pages[j].findView(IPageLayout.ID_PROP_SHEET);
					if (view != null)
						return (PropertySheet) view;
				}
			}
		}
		return null;
	}	

	public static void showPerspective(String perspectiveId) throws WorkbenchException {
		
		String lastExceptionMessage = "";
		
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			try {
				PlatformUI.getWorkbench().showPerspective(perspectiveId,windows[i]);
				return;
			} catch (WorkbenchException e) {
				lastExceptionMessage = e.getMessage();
				continue;
			}
		}
		throw new WorkbenchException(lastExceptionMessage);	
	}
	
	public static void showView(String viewId) {

		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			for (int j = 0; j < pages.length; j++) {
				if (pages[j] != null) {
					IViewPart view = pages[j].findView(viewId);
					if (view != null) {
						try {
							pages[j].showView(viewId);
						} catch (PartInitException e) {
							
			                logger.error(e.getMessage(),e);
						}
						return;
					}
				}
			}
		}
	}
	
	public static RelationsView findRelationsView()
	{
		if (RelationsView.getInstance() != null) {
			return RelationsView.getInstance();
		}
		
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			for (int j = 0; j < pages.length; j++) {
				if (pages[j] != null) {
					IViewPart view = pages[j].findView(RelationsView.VIEW_ID);
					if (view instanceof RelationsView) {
						return (RelationsView) view;
					}
				}
			}
		}

		return null;
	}
  
	
	public static SearchView findSearchView()
	{
		if (SearchView.getInstance() != null) {
			return SearchView.getInstance();
		}
		
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			for (int j = 0; j < pages.length; j++) {
				if (pages[j] != null) {
					IViewPart view = pages[j].findView(SearchView.VIEW_ID);
					if (view instanceof SearchView) {
						return (SearchView) view;
					}
				}
			}
		}

		return null;
	}
	
	public static CategoriesView findCategoriesView() {
		
		if (CategoriesView.getInstance() != null) {
			return CategoriesView.getInstance();
		}
		
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			for (int j = 0; j < pages.length; j++) {
				if (pages[j] != null) {
					IViewPart view = pages[j].findView(CategoriesView.VIEW_ID);
					if (view instanceof CategoriesView) {
						return (CategoriesView) view;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns the current active server profile 
	 * 
	 * @return Appropiate ClientInterface
	 */
	public static ServerProfile getCurrentServerProfile() {
		
		// Check if we are working with a repository
		RepositoryView repositoryView = findRepositoriesView();
		if (repositoryView != null) {
			Repository repository = repositoryView.getCurrentRepository();
			if (repository == null) {
				return null;
			}
			return repository.getServerProfile();
		}		
		// No repository found. Check if we are working with an editor
		
		return null;
	}
	
	public static Ticket getCurrentTicket() {
		
		// Check if we are working with a repository
		RepositoryView repositoryView = findRepositoriesView();
		if (repositoryView != null) {
			Repository repository = repositoryView.getCurrentRepository();
			
			if (repository != null) {
				return repository.getTicket();
			}
		}		
		// No repository found. Check if there are only one repository opened
		Collection repositories = RepositoryRegistry.getInstance().getOpenedRepositories();
		if (repositories == null) {
			return null;
		}
		if (repositories.size() > 0) {
			Iterator it = repositories.iterator();
			Repository repository = ((Repository)it.next());
			if (!repository.isConnected()) {
				return null;
			} else {
				return repository.getTicket();
			}
		}

		return null;
	}
	
	public static Repository getCurrentRepository() {
		
		// Check if we are working with a repository
		RepositoryView repositoryView = findRepositoriesView();
		if (repositoryView != null) {
			Repository repository = repositoryView.getCurrentRepository();
			
			if (repository != null) {
				return repository;
			}
		}		

		return null;
	}
	
	public static Color getSystemColor(int color) {
		
		return PlatformUI.getWorkbench().getDisplay().getSystemColor(color);		
	}
	
	public SecurityManager getSecurityManager() {
		
		return securityManager;
	}

	/**
	 * Tells is the plug-in, i.e. jLibrary, is going to be closed
	 * 
	 * @return boolean <code>true</code> if jLibrary is going to be closed and
	 * <code>false</code> otherwise.
	 */
	public boolean isGoingToClose() {
		return goingToClose;
	}

	/**
	 * Sets is the plug-in, i.e. jLibrary, is going to be closed
	 * 
	 * @param goingToClose boolean <code>true</code> if jLibrary is going to be 
	 * closed and <code>false</code> otherwise.
	 */	
	public void setGoingToClose(boolean goingToClose) {
		this.goingToClose = goingToClose;
	}
	
	public static void closeEditors(IWorkbenchSite site, Repository repository) {

		IEditorReference[] editorReferences = site.getPage().getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			IEditorPart editor = editorReferences[i].getEditor(false);
			if (editor instanceof JLibraryEditor) {
				Object model = ((JLibraryEditor)editor).getModel();
				if (model instanceof IResource) {
					IResource node = (IResource)model;
					if (node.getRepository().equals(repository.getId())) {
						site.getPage().closeEditor(editor,false);
					}
				}
			}
		}
	}	
	
	public static void closeEditors(IWorkbenchSite site,String id) {

		IEditorReference[] editorReferences = site.getPage().getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			IEditorPart editor = editorReferences[i].getEditor(false);
			if (editor instanceof JLibraryEditor) {
				Object model = ((JLibraryEditor)editor).getModel();
				if (model instanceof IResource) {
					IResource node = (IResource)model;
					if (node.getId().equals(id)) {
						site.getPage().closeEditor(editor,false);
					}
				}
			}
		}
	}		
}
