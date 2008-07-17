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
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.Page;
import org.jlibrary.client.actions.ActionFactory;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;

/**
 * Action group that adds the copy, cut, paste actions to a view part's context
 * menu and installs handlers for the corresponding global menu actions.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class CCPActionGroup extends ActionGroup {

	//private static final String GROUP_SELECTION= "selection.import";	
	
	private IWorkbenchSite fSite;
	private Clipboard fClipboard;

 	private SelectionDispatchAction[] fActions;

 	private DeleteAction fDeleteAction;
	private CopyAction fCopyAction;
	private PasteAction fPasteAction;
	private CutAction fCutAction;
	
	/**
	 * Creates a new <code>CCPActionGroup</code>. The group requires that
	 * the selection provided by the view part's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param part the view part that owns this action group
	 */
	public CCPActionGroup(IViewPart  part) {
		this(part.getSite());
	}
	
	/**
	 * Creates a new <code>CCPActionGroup</code>.  The group requires that
	 * the selection provided by the page's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param page the page that owns this action group
	 */
	public CCPActionGroup(Page page) {
		this(page.getSite());
	}

	public CCPActionGroup(IWorkbenchSite site) {
		fSite= site;
		fClipboard= new Clipboard(site.getShell().getDisplay());
		fPasteAction= new PasteAction(fSite, fClipboard);
		fCopyAction= new CopyAction(fSite, fClipboard, fPasteAction);
		fActions= new SelectionDispatchAction[] {	
			fCutAction= new CutAction(fSite, fClipboard, fPasteAction),
			fCopyAction,
			fPasteAction,
			fDeleteAction= new DeleteAction(fSite),
		};
		registerActionsAsSelectionChangeListeners();
		
		fDeleteAction.update(site.getSelectionProvider().getSelection());
	}

	private void registerActionsAsSelectionChangeListeners() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		for (int i= 0; i < fActions.length; i++) {
			provider.addSelectionChangedListener(fActions[i]);
		}
	}
	
	private void deregisterActionsAsSelectionChangeListeners() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		for (int i= 0; i < fActions.length; i++) {
			provider.removeSelectionChangedListener(fActions[i]);
		}
	}

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillActionBars(IActionBars actionBars) {
		
		super.fillActionBars(actionBars);
		
		actionBars.setGlobalActionHandler(ActionFactory.COPY_ID, fCopyAction);
		actionBars.setGlobalActionHandler(ActionFactory.CUT_ID, fCutAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE_ID, fPasteAction);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE_ID, fDeleteAction);
	}
	
	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		
		super.fillContextMenu(menu);
		
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		
		if (!menu.isEmpty()) {
			menu.add(new Separator());
		}
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel= (IStructuredSelection) selection;
			if (isCopyTarget(sel.getFirstElement())) {
				
				menu.add(fCutAction);
				menu.add(fCopyAction);
			}
			if (isDeleteTarget(sel)) {
				menu.add(fDeleteAction);
			}
			if (isPasteTarget(selection.getFirstElement())) {
				menu.add(fPasteAction);
			}
		}
	}		
	
	/*
	 * @see ActionGroup#dispose()
	 */
	public void dispose() {
		super.dispose();
		if (fClipboard != null){
			fClipboard.dispose();
			fClipboard= null;
		}
		deregisterActionsAsSelectionChangeListeners();
	}

	public CopyAction getCopyAction() {
		
		return fCopyAction;
	}
	
	public PasteAction getPasteAction() {
		
		return fPasteAction;
	}
	
	public CutAction getCutAction() {
		
		return fCutAction;
	}
	
	public DeleteAction getDeleteAction() {
		
		return fDeleteAction;
	}
	
	private boolean isCopyTarget(Object element) {
		
		if (element instanceof Document) {
			return true;
		}
		if (element instanceof Directory) {
			return true;
		}
		return false;
	}
	
	private boolean isPasteTarget(Object element) {
		
		if (element instanceof Directory) {
			return true;
		}
		return false;
	}

	private boolean isDeleteTarget(IStructuredSelection selection) {
		
		if (selection.getFirstElement() instanceof Repository) {
			return true;
		}

		return true;
	}	
}
