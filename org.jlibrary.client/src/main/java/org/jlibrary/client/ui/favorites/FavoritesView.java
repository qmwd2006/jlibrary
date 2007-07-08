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
package org.jlibrary.client.ui.favorites;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.favorites.actions.FavoritesActionGroup;
import org.jlibrary.client.ui.favorites.dnd.FavoriteDropListener;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.actions.OpenAction;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * View from a repository. It contents a tree that will display repository
 * documents and folders
 */
public class FavoritesView extends ViewPart {
	
	static Logger logger = LoggerFactory.getLogger(FavoritesView.class);
	
	private Menu fContextMenu;
	
	private static TableViewer viewer;
	private static FavoritesActionGroup fActionSet;
	
	public static final String VIEW_ID = "org.jlibrary.client.ui.favorites.FavoritesView";

	private static FavoritesView instance;
	
	public FavoritesView() {
		
		instance = this;
	}
	
	public void createPartControl(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		viewer.setContentProvider(new FavoritesContentProvider());
		viewer.setLabelProvider(new FavoritesLabelProvider());
				
		TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setWidth(200);
		column.setText(Messages.getMessage("documents_view_name"));
		column = new TableColumn(viewer.getTable(), SWT.NONE);

		
		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(viewer);
		
		fActionSet = new FavoritesActionGroup(this);
		
		// Register viewer with site. This must be done before making the actions.
		MenuManager manager = new FavoritesContextMenu(getViewSite(),"#PopupFavorites");
		site.registerContextMenu(manager, viewer);
		
		fContextMenu = manager.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(fContextMenu);
		
		fillActionBars();
		
		initEvents();
	}

	public static void refresh() {
		
		if (viewer != null) {
			viewer.refresh();
		}
	}
	
	public void setInput(Category category) {
        
		if (viewer.getContentProvider() != null && viewer.getLabelProvider() != null){
            viewer.setInput(category);
        }
	}
	
	private void initEvents() {

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				handleOpen(event);
			}
		});

		viewer.getTable().addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				
				handleMouseHover(e);
			}
		});
				
		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDropSupport(operations,types,new FavoriteDropListener(viewer));


	}

	protected void handleMouseHover(MouseEvent me) {


	}

	protected void handleOpen(OpenEvent event) {

		if (event.getSelection().isEmpty()) {
			return;
		}
		
		Favorite favorite = (Favorite)((IStructuredSelection)event.getSelection()).getFirstElement();
		Document document = (Document)
			EntityRegistry.getInstance().getNode(
					favorite.getDocument(),
					favorite.getRepository());
		if (document == null) {
			return;
		}
		new OpenAction(getViewSite()).run(document);

	}

	private void fillActionBars() {
		
		IActionBars actionBars= getViewSite().getActionBars();
		fActionSet.fillActionBars(actionBars);
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
	
	public void removeFavorite(Favorite favorite) {
		
		Category category = (Category)viewer.getInput();
		category.getFavorites().remove(favorite);

		refresh();
	}
	
	public static FavoritesView getInstance() {
		return instance;
	}

	public void clearInput() {
		
		viewer.getTable().removeAll();
	}
	
	public void refreshCategory() {
		
		Category selectedCategory = CategoriesView.getSelectedCategory();
		if (selectedCategory != null) {
			if (FavoritesView.getInstance() != null) {
				FavoritesView.getInstance().loadFavorites(selectedCategory);
			}
		}
	}
	
	public void loadFavorites(Category category) {
		
        if (viewer.getContentProvider() != null && 
        	viewer.getLabelProvider() != null){
	
        	Repository repository = RepositoryRegistry.getInstance().getRepository(category.getRepository());
        	ServerProfile serverProfile = repository.getServerProfile();
        	RepositoryService repositoryService = 
        		JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
        	Ticket ticket = repository.getTicket();
	
        	try {
        		category = repositoryService.findCategoryById(ticket, category.getId());
        		viewer.setInput(category);
        	} catch (CategoryNotFoundException cnfe) {
				
		        logger.error(cnfe.getMessage(),cnfe);
			} catch (RepositoryException e) {
				
		        logger.error(e.getMessage(),e);
			}
        }
	}	
}