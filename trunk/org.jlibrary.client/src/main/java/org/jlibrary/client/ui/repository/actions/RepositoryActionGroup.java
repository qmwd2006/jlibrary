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
package org.jlibrary.client.ui.repository.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.actions.CompositeActionGroup;
import org.jlibrary.client.ui.authors.AuthorsView;
import org.jlibrary.client.ui.properties.PropertiesSourceProvider;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.FilterManager;
import org.jlibrary.client.ui.repository.RepositoryViewer;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.repository.views.ResourcesView;
import org.jlibrary.client.ui.security.views.GroupsView;
import org.jlibrary.client.ui.security.views.RestrictionsView;
import org.jlibrary.client.ui.security.views.RolesView;
import org.jlibrary.client.ui.security.views.UsersView;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 * 
 */
public class RepositoryActionGroup extends CompositeActionGroup {

	private RepositoryView fView;

	private OpenActionGroup fOpenActionGroup;

	private NodeOperationsActionGroup fNodeOperationsActionGroup;

	private CCPActionGroup fCCPActionGroup;

	private PropertiesSourceProvider propertiesSourceProvider = new PropertiesSourceProvider();
	
	private VersionsActionGroup versionsActionGroup;

	public RepositoryActionGroup(RepositoryView view) {

		super();
		fView = view;

		IViewSite site = (IViewSite) fView.getSite();
		versionsActionGroup = new VersionsActionGroup(site);

		setGroups(new ActionGroup[] {
				new NewWizardsActionGroup(site),
				new AddWizardsActionGroup(site),
				fNodeOperationsActionGroup = new NodeOperationsActionGroup(site),
				new CloseActionGroup(site),
				fOpenActionGroup = new OpenActionGroup(site),
				fCCPActionGroup = new CCPActionGroup(site),
				new SearchActionGroup(site),
				new ContentActionGroup(site), new WorkActionGroup(site),
				new FavoritesActionGroup(site),versionsActionGroup ,
				new ImportExportActionGroup(site) });
	}

	public void dispose() {
		super.dispose();
	}

	// ---- Action Bars
	// ----------------------------------------------------------------------------
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		setGlobalActionHandlers(actionBars);
		fillToolBar(actionBars.getToolBarManager());
		fillViewMenu(actionBars.getMenuManager());
	}

	void updateActionBars(IActionBars actionBars) {
		actionBars.getToolBarManager().removeAll();
		actionBars.getMenuManager().removeAll();
		fillActionBars(actionBars);
		actionBars.updateActionBars();
	}

	private void setGlobalActionHandlers(IActionBars actionBars) {
	}

	void fillToolBar(IToolBarManager toolBar) {
	}

	void fillViewMenu(IMenuManager menu) {

		RepositoryViewer fViewer = RepositoryView.getRepositoryViewer();
		FilterManager manager = new FilterManager(fViewer);

		menu.add(new FilterNameAction(manager));
		menu.add(new FilterDateAction(manager));
		menu.add(new FilterTypeAction(manager));
		menu.add(new FilterPositionAction(manager));

		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu
				.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
						+ "-end"));
	}

	public void handleSelectionChanged(SelectionChangedEvent event) {

		PropertySheet propertySheet = JLibraryPlugin.findPropertiesView();
		if (propertySheet != null) {
			PropertySheetPage page = (PropertySheetPage) propertySheet
					.getCurrentPage();
			if (page != null) {
				page.setPropertySourceProvider(propertiesSourceProvider);
			}
		}
		RepositoryViewer fViewer = RepositoryView.getRepositoryViewer();
		ISelection selection = event.getSelection();

		if (selection.isEmpty()) {

			ResourcesView.empty();
			RelationsView.empty();
			RestrictionsView.empty();
			AuthorsView.empty();

		} else {
			Object object = fViewer.getSelectedItem();
			if (object instanceof Repository) {
				ResourcesView.empty();
				RelationsView.empty();
				AuthorsView.refresh();

			} else {
				Node node = (Node) object;
				if (!node.isDocument()) {
					ResourcesView.empty();
					RelationsView.empty();
				} else {
					ResourcesView.setDocument((Document) node);
					RelationsView.setDocument((Document) node);
				}
				AuthorsView.refresh();
			}
		}

		// Refresh restrictions
		RolesView.refreshAndDeselect();
		GroupsView.refreshAndDeselect();
		UsersView.refreshAndDeselect();
		RestrictionsView.refreshAndDeselect();
	}

	// ---- Context menu
	// -------------------------------------------------------------------------

	public void fillContextMenu(IMenuManager menu) {

		super.fillContextMenu(menu);

		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu
				.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
						+ "-end"));
	}

	// ---- Key board and mouse handling
	// ------------------------------------------------------------

	public void handleDoubleClick(DoubleClickEvent event) {

		RepositoryViewer fViewer = RepositoryView.getRepositoryViewer();

		// Double click on a folder
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		Object element = selection.getFirstElement();

		if (fViewer.isExpandable(element)) {
			fViewer.setExpandedState(element, !fViewer
					.getExpandedState(element));
		}
	}

	public void handleOpen(OpenEvent event) {

		fOpenActionGroup.runDefaultAction((IStructuredSelection) event
				.getSelection());
	}

	public void handleOpen(IStructuredSelection selection) {

		fOpenActionGroup.runDefaultAction(selection);
	}

	public void handleKeyPressed(KeyEvent event) {

		if (event.keyCode == SWT.F2) {
			RenameNodeAction fRenameNode = fNodeOperationsActionGroup
					.getRenameNodeAction();
			fRenameNode.handleKeyPressed(event);
		} else if (event.keyCode == SWT.DEL) {
			DeleteAction fDelete = fCCPActionGroup.getDeleteAction();
			fDelete.run();
		}
	}

	public void handleKeyReleased(KeyEvent event) {
	}

	public CCPActionGroup getCCPActionGroup() {

		return fCCPActionGroup;
	}
}
