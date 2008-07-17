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
package org.jlibrary.client.ui.search;

import java.util.Iterator;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.Messages;
import org.jlibrary.client.actions.search.SearchHistoryAction;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.actions.OpenAction;
import org.jlibrary.client.ui.search.actions.DeleteAllSearchResultsAction;
import org.jlibrary.client.ui.search.actions.DeleteSearchResultAction;
import org.jlibrary.client.ui.search.dnd.SearchDragListener;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.search.SearchHit;
import org.jlibrary.core.search.SearchResultSet;

/**
 * @author martin
 *
 * View from a repository. It contents a tree that will display repository
 * documents and folders
 */
public class SearchView extends ViewPart {
	
	private Menu fContextMenu;
	private static TableViewer viewer;
	private static SearchView instance;
	
	private DeleteSearchResultAction deleteSearchResult;	
	private DeleteAllSearchResultsAction deleteAllSearchResults;
	private SearchHistoryAction fSortDropDownAction;
	
	private static SearchHistory history = new SearchHistory();
	
	public static final String VIEW_ID = "org.jlibrary.client.ui.search.searchView";
	private static SearchResultSet resultSet;
	
	public SearchView() {
	
		instance = this;
	}

	
	public void createPartControl(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.setContentProvider(new SearchContentProvider());
		viewer.setLabelProvider(new SearchLabelProvider());
		
		TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setWidth(20);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText(Messages.getMessage("search_name"));
		column.setWidth(140);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText(Messages.getMessage("search_repository"));
		column.setWidth(140);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText(Messages.getMessage("search_path"));
		column.setWidth(200);
		for (int i = 0; i <= 10; i++) {
			column = new TableColumn(viewer.getTable(), SWT.NONE);
			column.setText(String.valueOf(i));
			column.setWidth(15);
		}
		
		//viewer.getTable().setLinesVisible(true);

		getViewSite().setSelectionProvider(viewer);
		fillActionBars();
		
		initEvents();
		
		// check if a resultset exists
		if (resultSet != null) {
			showResultSet(resultSet);
			resultSet = null;
		}
	}
	
	private void initEvents() {
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				deleteSearchResult.update(getViewSite().getSelectionProvider().getSelection());
				deleteAllSearchResults.update(getViewSite().getSelectionProvider().getSelection());
			}
		});
		
		viewer.addOpenListener(new IOpenListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IOpenListener#open(org.eclipse.jface.viewers.OpenEvent)
			 */
			public void open(OpenEvent event) {
				if (event.getSelection().isEmpty()) {
					return;
				}
				
				SearchHit item = (SearchHit)((IStructuredSelection)
									event.getSelection()).getFirstElement();
				if (item == null) {
					return;
				}
				Node node = EntityRegistry.getInstance().
									getNode(item.getId(),item.getRepository());
				new OpenAction(getViewSite()).run(node);
			}
		});
		
		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDragSupport(operations,types,new SearchDragListener(viewer));
	}
	
	private void fillActionBars() {

		deleteSearchResult = new DeleteSearchResultAction(getViewSite());
		deleteAllSearchResults = new DeleteAllSearchResultsAction(getViewSite());
		fSortDropDownAction = new SearchHistoryAction(history);
		
		IActionBars actionBars= getViewSite().getActionBars();
		IToolBarManager toolbar = actionBars.getToolBarManager();
		toolbar.add(deleteSearchResult);
		toolbar.add(deleteAllSearchResults);
		toolbar.add(fSortDropDownAction);
		
		deleteSearchResult.update(getViewSite().getSelectionProvider().getSelection());
		deleteAllSearchResults.update(getViewSite().getSelectionProvider().getSelection());
		
		
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
	
	public static void setInput(SearchResultSet rs) {
		
		if ((viewer == null) || (viewer.getContentProvider() == null)) {
			// remember search
			resultSet = rs;
		} else {
			showResultSet(rs);
		}
	}

	private static void showResultSet(SearchResultSet resultSet){
		
		viewer.setInput(resultSet);
		instance.setPartName(	Messages.getMessage("search_title") + " " + 
								Messages.getAndParseValue("search_found", "%1", String.valueOf(resultSet.getItems().size())));

	}
	
	public static TableViewer getSearchViewer() {
		
		return viewer; 
	}

	public void removeFromSearchView(Node node) {
		
		SearchResultSet resultSet = (SearchResultSet)viewer.getInput();
		if (resultSet == null) {
			return;
		}
		SearchHit item = new SearchHit();
		item.setId(node.getId());
		resultSet.remove(item);
		viewer.refresh();
	}
	
	public void removeFromSearchView(Repository repository) {
		
		removeFromSearchView(repository.getRoot());
	}
	
	public void removeFromSearchView(Directory directory) {

		SearchResultSet resultSet = (SearchResultSet)viewer.getInput();
		if (resultSet == null) {
			return;
		}
		checkToRemove(resultSet,directory);
		viewer.refresh();
	}

	private void checkToRemove(SearchResultSet resultSet,Node node) {

		if (resultSet == null) {
			return;
		}
		if (node.isDocument()) {
			SearchHit item = new SearchHit();
			item.setId(node.getId());
			resultSet.remove(item);
		} else {
			Iterator it = node.getNodes().iterator();
			while (it.hasNext()) {
				Node child = (Node) it.next();
				checkToRemove(resultSet,child);
			}
		}
	}
	
	/**
	 * @param resultSet
	 */
	public static void searchPerformed(SearchResultSet resultSet) {

		history.addSearchResult(resultSet);
		setInput(resultSet);
	}
	
	public static SearchView getInstance() {
		
		return instance;
	}
}