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
package org.jlibrary.client.ui.relations;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.ToolTipHandler;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.actions.OpenAction;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * View from a repository. It contents a tree that will display repository
 * documents and folders
 */
public class RelationsView extends ViewPart {
	
	private Menu fContextMenu;
	
	private static RelationsView instance;
	private static ListViewer viewer;
	private static Document document;
	public static final String VIEW_ID = "org.jlibrary.client.ui.relations.RelationsView";


	
	public RelationsView() {
	
		instance = this;
	}
	
	public void createPartControl(Composite parent) {
		
		viewer = new ListViewer(parent, SharedImages.getImage(SharedImages.IMAGE_RELATION));
		viewer.setContentProvider(new RelationsContentProvider());
		viewer.setLabelProvider(new RelationsLabelProvider());
		
		IWorkbenchPartSite site = getSite();
		site.setSelectionProvider(viewer);		
				
		initEvents(parent);
		refresh();
	}
	
	public void refresh() {

		if ((viewer == null) || (viewer.getContentProvider() == null)) {
			return;
		}
		if (!viewer.getControl().isDisposed()) {
			if (document != null) {
				Node registryNode = EntityRegistry.getInstance().
					getNode(document.getId(),document.getRepository());
				if (registryNode == document) {
					viewer.refresh(document);
				} else {
					setDocument((Document)registryNode);
				}
			}
		}		
	}
	
	/**
	 * @param document The document to set.
	 */
	public static void setDocument(Document doc) {
            
		if ((viewer == null) || (viewer.getContentProvider() == null)) {
			return;
        }
        
		document = doc;		
		viewer.getTable().removeAll();
		viewer.setInput(doc);
	}	
	
	private void initEvents(Composite parent) {

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				handleOpen(event);
			}
		});

		ToolTipHandler handler = new ToolTipHandler(parent.getShell());
		handler.activateHoverHelp(viewer.getTable());		
	}


	protected void handleMouseHover(MouseEvent me) {

		TableItem item = viewer.getTable().getItem(new Point(me.x,me.y));
		if ((item == null) ) {
			viewer.getTable().setToolTipText(null);
			return;
		}
		if (!(item.getData() instanceof Document)) {
			return;
		}
		Document document = (Document)item.getData();
		viewer.getTable().setToolTipText(document.getDescription());
	}

	protected void handleOpen(OpenEvent event) {

		// Open a document
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		Document document = (Document)selection.getFirstElement();
		
		// Check if the document repository is open
		Repository repository = RepositoryRegistry.getInstance().getRepository(document.getRepository());
		if (RepositoryRegistry.getInstance().isOpened(repository)) {
			new OpenAction().run(document);
		} else {
			MessageDialog.openError(viewer.getControl().getShell(),
									Messages.getMessage("error_open_relation_title"), 
									Messages.getAndParseValue("error_open_relation_text","%1",repository.getName()));
		}
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

	public static void empty() {
		
		setDocument(null);
	}	
	
	public static RelationsView getInstance() {
		
		return instance;
	}
}