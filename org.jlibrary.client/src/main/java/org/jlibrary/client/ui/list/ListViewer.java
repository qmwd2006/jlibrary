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

package org.jlibrary.client.ui.list;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 *
 * @author Martín Pérez
 *
 * Generic list. Support icon images
 */
public class ListViewer extends TableViewer {

	/**
	 * Constructor
	 *
	 * @param parent parent
	 * @param icon Image of elements list
	 */
	public ListViewer(Composite parent,Image icon) {
		this (parent,icon,SWT.SINGLE | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
	}

	/**
	 * Constructor
	 *
	 * @param parent parent
	 * @param icon Image of elements list
	 */
	public ListViewer(Composite parent,Image icon, int style) {

		super(parent, style);
		setLabelProvider(new ListLabelProvider(icon));
		setContentProvider(new ListContentProvider());
	}


	/**
	 * Constructor
	 *
	 * @param parent parent
	 */
	public ListViewer(Composite parent) {

		super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.BORDER);
		setContentProvider(new ListContentProvider());
	}

	/**
	 * Constructor
	 *
	 * @param parent parent
	 */
	public ListViewer(Composite parent, int style) {

		super(parent, style);
		setContentProvider(new ListContentProvider());
	}

	/**
	 * Constructor
	 *
	 * @param parent parent
	 */
	public ListViewer(Composite parent,
					  int style,
					  ITableLabelProvider labelProvider,
					  IStructuredContentProvider contentProvider) {

		super(parent, style);
		setLabelProvider(labelProvider);
		setContentProvider(contentProvider);
	}

	/**
	 * Adds a selection listener to this viewer
	 *
	 * @param listener Selection listener
	 */
	public void addSelectionListener(SelectionListener listener) {

		getTable().addSelectionListener(listener);
	}

/**
Referencia ambigua respecto a: TableViewer.add(Object[])

	public void add(Object element) {

		super.add(element);
	}
*/
	/**
	 * Returns list items
	 *
	 * @return List items
	 */
	public Collection getItems() {

		ArrayList list = new ArrayList();
		for (int i = 0; i < getTable().getItemCount(); i++) {
			list.add(getTable().getItem(i).getText());
		}
		return list;
	}
}
