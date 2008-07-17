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
package org.jlibrary.client.ui.versions;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;

/**
 * A <code>CompareViewerPane</code> is a convenience class which installs a
 * <code>CLabel</code> and a <code>Toolbar</code> in a <code>ViewForm</code>.
 * <P>
 * Double clicking onto the <code>CompareViewerPane</code>'s title bar maximizes
 * the <code>CompareViewerPane</code> to the size of an enclosing <code>Splitter</code>
 * (if there is one).
 * If more <code>Splitters</code> are nested maximizing walks up and
 * maximizes to the outermost <code>Splitter</code>.
 * 
 * @since 2.0
 */
public class CompareViewerPane extends ViewForm {
	
	private ToolBarManager fToolBarManager;

	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 *
	 * @param container a widget which will be the container of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 * </ul>
	 */		
	public CompareViewerPane(Composite container, int style) {
		super(container, style);
		
		marginWidth= 0;
		marginHeight= 0;
		
		CLabel label= new CLabel(this, SWT.NONE) {
			public Point computeSize(int wHint, int hHint, boolean changed) {
				return super.computeSize(wHint, Math.max(24, hHint), changed);
			}
		};
		setTopLeft(label);
		
		MouseAdapter ml= new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				Control content= getContent();
				if (content != null && content.getBounds().contains(e.x, e.y))
					return;
				Control parent= getParent();
				if (parent instanceof Splitter)
					((Splitter)parent).setMaximizedControl(CompareViewerPane.this);
			}
		};	
				
		addMouseListener(ml);
		label.addMouseListener(ml);
		
		
		
	}
	
	/**
	 * Set the pane's title text.
	 * The value <code>null</code> clears it.
	 * 
	 * @param label the text to be displayed in the pane or null
	 */
	public void setText(String label) {
		CLabel cl= (CLabel) getTopLeft();
		cl.setText(label);		
	}
	
	/**
	 * Set the pane's title Image.
	 * The value <code>null</code> clears it.
	 * 
	 * @param image the image to be displayed in the pane or null
	 */
	public void setImage(Image image) {
		CLabel cl= (CLabel) getTopLeft();
		cl.setImage(image);
	}
	
	/**
	 * Returns a <code>ToolBarManager</code> if the given parent is a
	 * <code>CompareViewerPane</code> or <code>null</code> otherwise.
	 * 
	 * @param parent a <code>Composite</code> or <code>null</code>
	 * @return a <code>ToolBarManager</code> if the given parent is a <code>CompareViewerPane</code> otherwise <code>null</code>
	 */
	public static ToolBarManager getToolBarManager(Composite parent) {
		if (parent instanceof CompareViewerPane) {
			CompareViewerPane pane= (CompareViewerPane) parent;
			return pane.getToolBarManager();
		}
		return null;
	}

	/**
	 * Clears tool items in the <code>CompareViewerPane</code>'s control bar.
	 * 
	 * @param parent a <code>Composite</code> or <code>null</code>
	 */
	public static void clearToolBar(Composite parent) {
		ToolBarManager tbm= getToolBarManager(parent);
		if (tbm != null) {
			tbm.removeAll();
			tbm.update(true);
		}
	}
	
	//---- private stuff
	
	private ToolBarManager getToolBarManager() {
		if (fToolBarManager == null) {
			ToolBar tb= new ToolBar(this, SWT.FLAT);
			setTopCenter(tb);
			fToolBarManager= new ToolBarManager(tb);
		}
		return fToolBarManager;
	}
}
