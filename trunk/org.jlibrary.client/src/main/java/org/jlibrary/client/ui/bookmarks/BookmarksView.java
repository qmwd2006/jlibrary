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
package org.jlibrary.client.ui.bookmarks;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.ToolTipHandler;
import org.jlibrary.client.ui.bookmarks.actions.BookmarksActionGroup;
import org.jlibrary.client.ui.bookmarks.dnd.BookmarkDragListener;
import org.jlibrary.client.ui.bookmarks.dnd.BookmarkDropListener;
import org.jlibrary.client.ui.bookmarks.menu.BookmarksContextMenu;
import org.jlibrary.client.ui.bookmarks.providers.BookmaksLabelProvider;
import org.jlibrary.client.ui.bookmarks.providers.BookmarksContentProvider;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.web.actions.NavigateNewAction;
import org.jlibrary.client.util.URL;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * View from a repository. It contents a tree that will display repository
 * documents and folders
 */
public class BookmarksView extends ViewPart {
	
	private Menu fContextMenu;
	
	private static BookmarksViewer viewer;

	private static BookmarksActionGroup fActionSet;
	public static final String VIEW_ID = "org.jlibrary.client.ui.bookmarks.BookmarksView";

	private Collection repositories = new ArrayList();
	
	public BookmarksView() {}
	
	public void createPartControl(Composite parent) {
		
		viewer = new BookmarksViewer(parent);
		viewer.setContentProvider(new BookmarksContentProvider());
		viewer.setLabelProvider(new BookmaksLabelProvider());

		repositories.addAll(RepositoryRegistry.getInstance().getOpenedRepositories());
		viewer.setInput(repositories);

		IWorkbenchPartSite site = getSite();
		site.getPage().addPartListener(new IPartListener() {
			public void partActivated(IWorkbenchPart part) {				
				Collection repositories = RepositoryRegistry.getInstance().getOpenedRepositories();
				Iterator it = repositories.iterator();
				while (it.hasNext()) {
					Repository repository = (Repository) it.next();
					if (!BookmarksView.this.repositories.contains(repository)) {
						BookmarksView.this.repositories.add(repository);
					}
				}
				
				it = repositories.iterator();
				while (it.hasNext()) {
					Repository repository = (Repository) it.next();
					if (!BookmarksView.this.repositories.contains(repository)) {
						BookmarksView.this.repositories.remove(repository);
						viewer.remove(repository);
					}
				}
				if (!viewer.getControl().isDisposed()) {
					viewer.refresh();
				}
			}
			public void partBroughtToTop(IWorkbenchPart part) {}
			public void partClosed(IWorkbenchPart part) {}
			public void partDeactivated(IWorkbenchPart part) {}
			public void partOpened(IWorkbenchPart part) {}
		});
		site.setSelectionProvider(viewer);
				
		fillActionBars();
		initEvents();
		
		ToolTipHandler handler = new ToolTipHandler(parent.getShell());
		handler.activateHoverHelp(viewer.getTree());
		
		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDragSupport(operations,types,new BookmarkDragListener(viewer));
		viewer.addDropSupport(operations,types,new BookmarkDropListener(viewer));		
	}

	
	
	public static void refresh() {
		
		if ((viewer != null) && (viewer.getContentProvider() != null)) {
			viewer.refresh();
		}
	}
	
	private void initEvents() {

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				handleOpen(event);
			}
		});
	}

	protected void handleOpen(OpenEvent event) {

		// Open a document
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		
		Object elem = selection.getFirstElement();
        if( !(elem instanceof Bookmark) ) return;
        Bookmark favorite = (Bookmark)elem;
		if (favorite.isFolder()) {
			return;
		}
		try {
			URL url = new URL(favorite.getUrl());
			new NavigateNewAction().run(url);
		} catch (MalformedURLException e) {
			StatusLine.setErrorMessage(Messages.getAndParseValue("error_open_url","%1",favorite.getUrl()));
			return;
		}
	}

	private void fillActionBars() {
		
		IActionBars actionBars= getViewSite().getActionBars();
		
		fActionSet = new BookmarksActionGroup(this);
		fActionSet.fillActionBars(actionBars);
		
		// Register viewer with site. This must be done before making the actions.
		MenuManager manager = new BookmarksContextMenu(fActionSet,"#PopupBookmarks");
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

	public static Repository findRepository(Bookmark bookmark) {
		
		return viewer.findRepository(bookmark);
	}

	public static void addRepository(Repository repository) {

		
		if (viewer == null || viewer.getControl().isDisposed()) {
			return;
		}
		viewer.getControl().setFocus();
		
		Collection repositories = (Collection)viewer.getInput();
		if (!repositories.contains(repository)) {
			repositories.add(repository);
			viewer.refresh();
		}
	}

	public static void removeRepository(Repository repository) {

		if (viewer == null || viewer.getControl().isDisposed()) {
			return;
		}
		viewer.getControl().setFocus();
		
		Collection repositories = (Collection)viewer.getInput();
		repositories.remove(repository);
		viewer.refresh();		
	}
	
	public static BookmarksViewer getBookmarksViewer() {
		
		return viewer;
	}
}