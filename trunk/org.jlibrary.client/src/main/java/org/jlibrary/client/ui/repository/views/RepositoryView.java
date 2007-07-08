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
package org.jlibrary.client.ui.repository.views;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.RepositoryViewer;
import org.jlibrary.client.ui.repository.actions.RepositoryActionGroup;
import org.jlibrary.client.ui.repository.filter.NameFilter;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 * 
 * View from a repository. It contents a tree that will display repository
 * documents and folders
 */
public class RepositoryView extends ViewPart implements IMenuListener {

	private static RepositoryView instance;

	public static String VIEW_ID = "org.jlibrary.client.ui.repository.views.repositoryView";

	private RepositoryViewer viewer;

	// private static RepositoryContextMenu actionManager;
	private Menu fContextMenu;

	private RepositoryActionGroup fActionSet;

	public RepositoryView() {

		instance = this;
	}

	public static RepositoryViewer getRepositoryViewer() {

		if (instance == null) {
			return null;
		}
		return instance.viewer;
	}

	public void createPartControl(Composite parent) {

		viewer = new RepositoryViewer(parent, getViewSite());
		getViewSite().setSelectionProvider(viewer);

		// Register viewer with site. This must be done before making the
		// actions.
		/*
		 * actionManager = new RepositoryContextMenu( getViewSite(), viewer,
		 * "#PopupRepository");
		 */

		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		fContextMenu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(fContextMenu);

		IWorkbenchPartSite site = getSite();
		site.registerContextMenu(menuMgr, viewer);
		site.setSelectionProvider(viewer);

		viewer.addFilter(new NameFilter());
		try {
			viewer.setInput(RepositoryRegistry.getInstance()
					.getOpenedRepositories());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		makeActions();

		fillActionBars();

		initEvents();
	}

	private void initEvents() {

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				fActionSet.handleOpen(event);
			}
		});
		viewer.getControl().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				fActionSet.handleKeyPressed(event);
			}

			public void keyReleased(KeyEvent event) {
				fActionSet.handleKeyReleased(event);
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {

				fActionSet.handleSelectionChanged(event);
			}
		});

	}

	private void fillActionBars() {

		IActionBars actionBars = getViewSite().getActionBars();
		fActionSet.fillActionBars(actionBars);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {

		if (viewer != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().setFocus();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {

		if (fActionSet != null)
			fActionSet.dispose();

		if (fContextMenu != null && !fContextMenu.isDisposed())
			fContextMenu.dispose();

	}

	/**
	 * @return Returns the actionManager.
	 */
	public RepositoryActionGroup getActionManager() {

		return fActionSet;
	}

	/**
	 * Returns current selected directory or null if no directory is selected.
	 * If a document is selected then the method returns document's parent
	 * 
	 * @return Repository Current directory or <code>null</code> if no
	 *         repository is being used. If a document is selected then the
	 *         method returns document's parent
	 */
	public Directory getCurrentDirectory() {

		Object item = viewer.getSelectedItem();
		if (item instanceof Repository) {
			return ((Repository) item).getRoot();
		}
		Node node = (Node) viewer.getSelectedItem();
		if (node == null) {
			return null;
		}
		if (node.isDirectory()) {
			return (Directory) node;
		} else if (node.isDocument()) {
			return (Directory) ((TreeItem) item).getParentItem().getData();
		}
		return null;
	}

	/**
	 * Returns current repository or null if no repository is being used
	 * 
	 * @return Repository Current repository or <code>null</code> if no
	 *         repository is being used
	 */
	public Repository getCurrentRepository() {

		if (viewer == null) {
			return null;
		}

		Object item = viewer.getSelectedItem();

		if (item == null) {
			return null;
		}

		if (item instanceof Repository) {
			return ((Repository) item);
		}
		Node node = (Node) viewer.getSelectedItem();
		if (node == null) {
			return null;
		}
		return RepositoryRegistry.getInstance().getRepository(
				node.getRepository());
	}

	/**
	 * Returns current selected node or null if no node is selected
	 * 
	 * @return Node Current selected node or <code>null</code> if no node
	 *         selected
	 */
	public Node getCurrentNode() {

		if (viewer == null) {
			return null;
		}

		Object item = viewer.getSelectedItem();
		if (item instanceof Repository) {
			return ((Repository) item).getRoot();
		}
		Node node = (Node) viewer.getSelectedItem();
		return node;
	}

	public Node getParentNode(Node node) {

		if (viewer == null) {
			return null;
		}

		return viewer.getParentDirectory(node);
	}

	public Node getParentNode(TreeItem item) {

		return (Node) item.getParent().getData();
	}

	/**
	 * @return Node[] Array with current selection or <code>null</code> if no
	 *         node is selected
	 */
	public Node[] getSelectedNodes() {

		if (viewer == null) {
			return null;
		}

		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		Object[] nodes = selection.toArray();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof Repository) {
				nodes[i] = ((Repository) nodes[i]).getRoot();
			}
		}
		return (Node[]) nodes;
	}

	public Repository getRepositoryForNode(Node node) {

		if (viewer == null) {
			return null;
		}
		return viewer.getRepositoryForNode(node);
	}

	public static RepositoryView getInstance() {

		return instance;
	}

	/**
	 * This callback method will serve us to adapt context menu to selected
	 * items
	 * 
	 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	public void menuAboutToShow(IMenuManager menu) {

		fActionSet.setContext(new ActionContext(getSelection()));

		fActionSet.fillContextMenu(menu);

		fActionSet.setContext(null);

	}

	/**
	 * Returns the current selection.
	 */
	private ISelection getSelection() {

		return viewer.getSelection();
	}

	private void makeActions() {

		fActionSet = new RepositoryActionGroup(this);
	}

	/**
	 * Changes a tree node for a new node
	 * 
	 * @param originalNode
	 *            original node
	 * @param newNode
	 *            new nodw
	 */
	public void changeNode(Node originalNode, Node newNode) {

		if (viewer != null) {
			Node parent = getParentNode(originalNode);
			if (parent == null) {
				return;
			}
			parent.getNodes().remove(originalNode);
			parent.getNodes().add(newNode);

			Repository repository = RepositoryRegistry.getInstance()
					.getRepository(originalNode.getRepository());
			if (repository.getRoot().equals(parent)) {
				viewer.refresh(repository);
			} else {
				viewer.refresh(parent);
			}
		}
	}
}