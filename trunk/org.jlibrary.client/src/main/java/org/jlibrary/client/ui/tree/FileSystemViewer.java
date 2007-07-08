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
package org.jlibrary.client.ui.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * 
 * @author Martín Pérez
 *
 * Viewer for the repository tree
 */
public class FileSystemViewer extends CheckboxTreeViewer {
	
	public FileSystemViewer(Composite parent, 
							IWorkbenchWindow window) {
		
		super(parent);

		initViewer();
	}	

	/**
	 * Constructor for ContainerCheckedTreeViewer.
	 * @see CheckboxTreeViewer#CheckboxTreeViewer(Composite)
	 */
	public FileSystemViewer(Composite parent) {
		super(parent);
		initViewer();
	}
	
	/**
	 * Constructor for ContainerCheckedTreeViewer.
	 * @see CheckboxTreeViewer#CheckboxTreeViewer(Composite,int)
	 */
	public FileSystemViewer(Composite parent, int style) {
		super(parent, style);
		initViewer();
	}

	/**
	 * Constructor for ContainerCheckedTreeViewer.
	 * @see CheckboxTreeViewer#CheckboxTreeViewer(Tree)
	 */
	public FileSystemViewer(Tree tree) {
		super(tree);
		initViewer();
	}
	
	private void initViewer() {
		
		setContentProvider(new FileSystemContentProvider());
		setLabelProvider(new FileSystemLabelProvider());
		setSorter(new FileSystemViewerSorter());
		initListeners();
		
		setUseHashlookup(true);
		List files = new ArrayList(Arrays.asList(File.listRoots()));
		
		// We not support obsolete A: B: units
		Iterator it = files.iterator();
		while (it.hasNext()) {
			File file = (File) it.next();
			if ((file.getPath().equals("A:\\")) ||
				(file.getPath().equals("B:\\"))) {
				
				it.remove();
			}
		}
		
		setInput(files.toArray(new File[]{}));
		
		addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				doCheckStateChanged(event.getElement());
			}
		});
		addTreeListener(new ITreeViewerListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
			}
			public void treeExpanded(TreeExpansionEvent event) {
				Widget item= findItem(event.getElement());
				if (item instanceof TreeItem) {
					initializeItem((TreeItem) item);
				}
			}
		});
	}		
	
	protected void doCheckStateChanged(Object element) {
		Widget item= findItem(element);
		if (item instanceof TreeItem) {
			TreeItem treeItem= (TreeItem) item;
			treeItem.setGrayed(false);
			updateChildrenItems(treeItem);
			updateParentItems(treeItem.getParentItem());
		}
	}
	
	/**
	 * The item has expanded. Updates the checked state of its children. 
	 */
	private void initializeItem(TreeItem item) {
		if (item.getChecked() && !item.getGrayed()) {
			updateChildrenItems((TreeItem) item);
		}
	}

	/**
	 * Updates the check state of all created children
	 */	
	private void updateChildrenItems(TreeItem parent) {
		Item[] children= getChildren(parent);
		boolean state= parent.getChecked();
		for (int i= 0; i < children.length; i++) {
			TreeItem curr= (TreeItem) children[i];
			if (curr.getData() != null && ((curr.getChecked() != state) || curr.getGrayed())) {
				curr.setChecked(state);
				curr.setGrayed(false);
				updateChildrenItems(curr);
			}
		}
	}
	
	/**
	 * Updates the check / gray state of all parent items
	 */
	private void updateParentItems(TreeItem item) {
		if (item != null) {
			Item[] children= getChildren(item);
			boolean containsChecked= false;
			boolean containsUnchecked= false;
			for (int i= 0; i < children.length; i++) {
				TreeItem curr= (TreeItem) children[i];
				containsChecked |= curr.getChecked();
				containsUnchecked |= (!curr.getChecked() || curr.getGrayed());
			}
			item.setChecked(containsChecked);
			item.setGrayed(containsChecked && containsUnchecked);
			updateParentItems(item.getParentItem());
		}
	}

	/*
	 * @see ICheckable#setChecked(Object, boolean)
	 */
	public boolean setChecked(Object element, boolean state) {
		if (super.setChecked(element, state)) {
			doCheckStateChanged(element);
			return true;
		}
		return false;
	}

	/*
	 * @see CheckboxTreeViewer#setCheckedElements(Object[])
	 */
	public void setCheckedElements(Object[] elements) {
		super.setCheckedElements(elements);
		for (int i= 0; i < elements.length; i++) {
			doCheckStateChanged(elements[i]);
		}
	}
	
	/*
	 * @see AbstractTreeViewer#setExpanded(Item, boolean)
	 */
	protected void setExpanded(Item item, boolean expand) {
		super.setExpanded(item, expand);
		if (expand && item instanceof TreeItem) {
			initializeItem((TreeItem) item);
		}
	}

	/*
	 * @see CheckboxTreeViewer#getCheckedElements()
	 */
	public Object[] getCheckedElements() {
		
		Object[] checked=super.getCheckedElements();

		// add all items that are children of a checked node but not created yet
		ArrayList result= new ArrayList();
		for (int i= 0; i < checked.length; i++) {
			Object curr= checked[i];
			result.add(curr);
			Widget item= findItem(curr);
			if (item != null) {
				Item[] children= getChildren(item);
				// check if contains the dummy node
				if (children.length == 1 && children[0].getData() == null) {
					// not yet created
					collectChildren(curr, result);
				}
			}
		}
		checked = result.toArray();
		
		// Group items
		HashMap map = new HashMap();
		HashMap temp = new HashMap();
		for (int i = 0; i < checked.length; i++) {
			Widget item= findItem(checked[i]);
			if (item != null) {
				if (((TreeItem)item).getGrayed()) {
					continue;
				}
			}
			File file = (File)checked[i];
			FileSystemNode parent = (FileSystemNode)map.get(file.getParentFile());
			if (parent == null) {
				FileSystemNode tempParent = (FileSystemNode)temp.get(file.getParentFile());
				if (tempParent == null) {
					FileSystemNode node = new FileSystemNode();
					node.setChildren(new ArrayList());
					node.setFile(file);
					map.put(file,node);
					temp.put(file,node);
				} else {
					FileSystemNode node = new FileSystemNode();
					node.setChildren(new ArrayList());
					node.setFile(file);
					tempParent.getChildren().add(node);
					if (file.isDirectory()) {
						temp.put(file,node);
					}					
				}
			} else {
				FileSystemNode node = new FileSystemNode();
				node.setChildren(new ArrayList());
				node.setFile(file);
				
				parent.getChildren().add(node);
				if (file.isDirectory()) {
					temp.put(file,node);
				}
				
			}
		}		
		
		return map.values().toArray();
	}
	
	private void collectChildren(Object element, ArrayList result) {
		Object[] filteredChildren= getFilteredChildren(element);
		for (int i= 0; i < filteredChildren.length; i++) {
			Object curr= filteredChildren[i];
			result.add(curr);
			collectChildren(curr, result);
		}
	}
	
	private void initListeners() {}

}
