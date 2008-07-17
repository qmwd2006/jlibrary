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
package org.jlibrary.client.ui.categories;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.SharedCursors;
import org.jlibrary.client.ui.ToolTipHandler;
import org.jlibrary.client.ui.categories.actions.CategoriesActionGroup;
import org.jlibrary.client.ui.categories.dnd.CategoryDragListener;
import org.jlibrary.client.ui.categories.dnd.CategoryDropListener;
import org.jlibrary.client.ui.categories.menu.CategoriesContextMenu;
import org.jlibrary.client.ui.categories.providers.CategoriesContentProvider;
import org.jlibrary.client.ui.categories.providers.CategoriesLabelProvider;
import org.jlibrary.client.ui.favorites.FavoritesView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * View from a repository. It contents a tree that will display categories
 */
public class CategoriesView extends ViewPart {
	
	static Logger logger = LoggerFactory.getLogger(CategoriesView.class);
	
	private static CategoriesView instance;
	private Menu fContextMenu;
	private static CategoriesActionGroup fActionSet;
	private static TreeViewer viewer;
	
	public static final String VIEW_ID = "org.jlibrary.client.ui.categories.CategoriesView";

	private static Category currentSelectedCategory;

	public CategoriesView() {
	
		instance = this;
	}
	
	public void createPartControl(Composite parent) {
		
		FillLayout layout = new FillLayout();
		parent.setLayout (layout);

		viewer = new TreeViewer(parent);
		viewer.setLabelProvider(new CategoriesLabelProvider());
		viewer.setContentProvider(new CategoriesContentProvider());

		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(viewer);
		
		ToolTipHandler handler = new ToolTipHandler(parent.getShell());
		handler.activateHoverHelp(viewer.getTree());
						
		fillActionBars();
		fillData();
		
		initEvents();
	}

	private void initEvents() {

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(final SelectionChangedEvent event) {
				
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						loadDocuments(event);
					}

				});
			}
		});
				
		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				fActionSet.handleOpen(event);
			}
		});
		
		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDragSupport(operations,types,new CategoryDragListener(viewer));
		viewer.addDropSupport(operations,types,new CategoryDropListener(viewer));
	}
	
	private void fillData() {
				
	    Repository[] repositories = (Repository[])RepositoryRegistry.getInstance().getOpenedRepositories().toArray(new Repository[]{});			
		if (viewer != null) { 
			viewer.setInput(repositories);
		}
	}
	
	public void refresh() {
		
		if ((viewer != null) && (viewer.getContentProvider() != null)) {
			
			fillData();
			viewer.refresh();
		}
	}

	private void fillActionBars() {
		
		IActionBars actionBars= getViewSite().getActionBars();
		fActionSet = new CategoriesActionGroup(this);
		fActionSet.fillActionBars(actionBars);
		
		MenuManager manager = new CategoriesContextMenu(fActionSet,"#PopupCategories");
		getViewSite().registerContextMenu(manager, viewer);
		
		fContextMenu = manager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(fContextMenu);

	}		
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		
		if (viewer != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().setFocus();
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {

		if (fContextMenu != null && !fContextMenu.isDisposed())
			fContextMenu.dispose();
		
	}
	
	public static void categoryCreated(Category category) {
		
		if (instance != null) {
			if ((viewer != null) && (viewer.getContentProvider() != null)) {				
				logger.info("category created");
			    if (category.getParent() != null) {
			    	viewer.expandToLevel(category.getParent(),1);
			    	viewer.refresh(category.getParent());
			    } else {
			    	Repository repository = 
			    		RepositoryRegistry.getInstance().getRepository(
			    				category.getRepository());
			    	viewer.expandToLevel(repository,1);
			    	viewer.refresh(repository);
			    }
			}
		}
	}
	
	public static void refresh(Object object) {
		
		if ((viewer != null) && (viewer.getContentProvider() != null)) {
			
			viewer.refresh(object);
		}
	}
	
	public static Category getSelectedCategory() {
		
		return currentSelectedCategory;
	}
	
	public static void categoryUpdated(Category category) {
		
		if ((viewer != null) && (viewer.getContentProvider() != null)) {
			
			viewer.refresh(category); //refresh the category in the view
			StructuredSelection selection = new StructuredSelection(category);
			viewer.setSelection(selection);
			
			if (DocumentsView.getInstance() != null) {
				DocumentsView.getInstance().loadDocuments(category);
			}
		}
	}
	
	public static void categoryDeleted(Category category) {
		
		if ((instance != null) && 
			(viewer != null) && 
			(viewer.getContentProvider() != null)) {
			
			
			logger.info("category deleted");

		    if (category.getParent() != null) {
		    	viewer.refresh(category.getParent());
		    } else {
		    	Repository repository = RepositoryRegistry.getInstance().getRepository(category.getRepository());
		    	viewer.refresh(repository);
		    }
		}
	}

	
	public static Object getSelectedObject() {
		
		if ((viewer != null) && (viewer.getContentProvider() != null)) {
	 		if (viewer.getSelection().isEmpty()) {
				return null;
			}
			return viewer.getTree().getSelection()[0].getData();
		} else {
			return null;
		}
	}

	public static CategoriesView getInstance() {
		
		return instance;
	}
	
	private void loadDocuments(final SelectionChangedEvent event) {
		
		DocumentsView documentsView = DocumentsView.getInstance();
		FavoritesView favoritesView = FavoritesView.getInstance();
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (!selection.isEmpty()) {
			Object object = selection.getFirstElement();
			if (object instanceof Category) {
				Category category = (Category)selection.getFirstElement();
				if (currentSelectedCategory != null) {
					if (currentSelectedCategory.equals(category)) {
						viewer.getControl().setCursor(SharedCursors.getInstance().getArrowCursor());
						return;
					}
				}
				currentSelectedCategory = category;
                if (DocumentsView.getInstance() != null){
				    DocumentsView.getInstance().loadDocuments(category);
                }
                if (FavoritesView.getInstance() != null){
                    FavoritesView.getInstance().loadFavorites(category);
                }
			}
		} else {
			if (documentsView != null) {
				documentsView.clearInput();
			}
			if (favoritesView != null) {
				favoritesView.clearInput();
			}
			currentSelectedCategory = null;
		}
	}
	
	public TreeViewer getCategoriesViewer() {
		
		return viewer;
	}
}