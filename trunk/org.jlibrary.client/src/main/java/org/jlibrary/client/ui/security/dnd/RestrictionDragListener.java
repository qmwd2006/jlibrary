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
package org.jlibrary.client.ui.security.dnd;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.TableItem;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.User;

/**
 * JFace restrictions drag adapter
 * 
 * @author martin
 */
public class RestrictionDragListener implements DragSourceListener {

	private TableViewer viewer;

	public RestrictionDragListener(TableViewer viewer) {
		
		this.viewer = viewer;
	}
	
	/**
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) {

		TableItem[] selection = viewer.getTable().getSelection();
		
		// Uncomment following comment to allow only drag and drop of tree leafs
		if (selection.length > 0) {
			event.doit = true;

			Member[] members = new Member[selection.length];
			for (int i = 0; i < selection.length; i++) {
				members[i] = (User)selection[i].getData();
			}
			event.doit = true;
			DNDItems.setItems(members);
		} else {
			event.doit = false;
		}
	};

	/**
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData (DragSourceEvent event) {

		event.data = DNDItems.RESTRICTIONS_VIEW;	
	}

	/**
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) {
				
		viewer.refresh();
	}
}
