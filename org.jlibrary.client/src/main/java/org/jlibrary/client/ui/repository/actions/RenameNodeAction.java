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

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called when the user wants to rename a node of the 
 * repository.
 */
public class RenameNodeAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(RenameNodeAction.class);
	
	private IWorkbenchSite site;
	private Tree tree;
	
	/**
	 * Constructor
	 */
	public RenameNodeAction(IWorkbenchSite site) {
		
		super(site);
		
		this.site = site;
		this.tree = RepositoryView.getRepositoryViewer().getTree();
		setText(Messages.getMessage("item_rename"));
		setToolTipText(Messages.getMessage("tooltip_rename"));
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
		
		if (selection.isEmpty())
			return false;
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();

		
		Object[] elements = selection.toArray();
		if (elements.length > 1) {
			return false;
		}
		
		if (elements[0] instanceof Repository) {
			Repository repository = ((Repository)elements[0]);
			if (!securityManager.canPerformAction(
					repository.getId(),
					repository.getRoot(),
					SecurityManager.RENAME)) {
				return false;
			}			if (!repository.isConnected()) {
				return false;
			}			
		} else {		
			Node node = (Node)elements[0];
			if (!securityManager.canPerformAction(
					node.getRepository(),
					node,
					SecurityManager.RENAME)) {
				return false;
			}
		}
		
		return true;
	}
		
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}
	
	public void run(Object[] elements) {
		
		renameInline();
		return;
		/*
		ClientInterface jci = JLibraryPlugin.getJCI();
		jci.getLoggingService().debug("[RenameNodeAction] Renaming node");
		
		renameNode(node, lastNodeName);
		
		setNode(null);
		setLastNodeName(null);
		*/
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.SelectionDispatchAction#run()
	 */
	public void run() {
		
		run((Object[])null);
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}

	/**
	 * @param event
	 */
	public void handleKeyPressed(KeyEvent event) {
		
		renameInline();
	}		
			
	private void renameInline() {
		
		IStructuredSelection selection = (IStructuredSelection)getSelection();
		if (selection.isEmpty() || (selection.size() > 1)) {
			return;
		}
		
		final Node node = !(selection.getFirstElement() instanceof Repository) 
		? (Node)selection.getFirstElement() 
		: ((Repository)selection.getFirstElement()).getRoot();
		
		final String lastNodeName = node.getName();
		
		final TreeItem item = tree.getSelection()[0];
		final TreeEditor editor = new TreeEditor (tree);
		final Composite composite = new Composite (tree, SWT.NONE);
		final Color black = site.getShell().getDisplay().getSystemColor (SWT.COLOR_BLACK);
		composite.setBackground (black);
		final Text text = new Text (composite, SWT.NONE);
		composite.addListener (SWT.Resize, new Listener () {
			public void handleEvent (Event e) {
				Rectangle rect = composite.getClientArea ();
				text.setBounds (rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
			}
		});
		Listener textListener = new Listener () {
			public void handleEvent (final Event e) {
				switch (e.type) {
					case SWT.DefaultSelection:
						if (isDuplicate(text.getText(),node)) {
							item.setText(lastNodeName);
							composite.dispose();
							return;
						}
						item.setText (text.getText ());
						//FALL THROUGH
					case SWT.FocusOut:
						if (isDuplicate(text.getText(),node)) {
							item.setText(lastNodeName);
							composite.dispose();							
							return;
						}
						try {
							// Save Item
							renameNode(node,text.getText());
						} catch (RepositoryException e1) {
							item.setText(lastNodeName);
							composite.dispose();
						} catch (final SecurityException se) {
						    item.setText(lastNodeName);
						    composite.dispose();
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									ErrorDialog.openError(new Shell(),
											  "ERROR",
											  Messages.getMessage("security_exception"),
											  new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
									StatusLine.setErrorMessage(se.getMessage());
								}
							});	
						}
						composite.dispose ();
						break;
					case SWT.Verify:
						String newText = text.getText ();
						String leftText = newText.substring (0, e.start);
						String rightText = newText.substring (e.end, newText.length ());
						GC gc = new GC (text);
						Point size = gc.textExtent (leftText + e.text + rightText);
						gc.dispose ();
						size = text.computeSize (size.x, SWT.DEFAULT);
						editor.horizontalAlignment = SWT.LEFT;
						Rectangle itemRect = item.getBounds (), rect = tree.getClientArea ();
						editor.minimumWidth = Math.max (size.x, itemRect.width) + 2;
						int left = itemRect.x, right = rect.x + rect.width;
						editor.minimumWidth = Math.min (editor.minimumWidth, right - left);
						editor.layout ();
						break;
					case SWT.Traverse:
						if (e.detail == SWT.TRAVERSE_ESCAPE) {
							composite.dispose ();
							e.doit = false;
						}
						break;
				}
			}

			private boolean isDuplicate(String name, Node node) {
				
				Repository repository = RepositoryRegistry.getInstance().getRepository(node.getRepository());
				if (node.equals(repository.getRoot())) {
					return false;
				}
				
				Node parent = RepositoryView.getInstance().getParentNode(node);
				Iterator it = parent.getNodes().iterator();
				while (it.hasNext()) {
					Node child = (Node) it.next();
					if (child.getName().equals(name)) {
						return true;
					}
				}
				return false;
			}
		};

		text.addListener (SWT.DefaultSelection, textListener);
		text.addListener (SWT.FocusOut, textListener);
		text.addListener (SWT.Traverse, textListener);
		text.addListener (SWT.Verify, textListener);
		editor.setEditor (composite, item);
		text.setText (item.getText ());
		text.selectAll ();
		text.setFocus ();
	}

	/**
	 * Renames a node
	 * 
	 * @param node Node to be renamed
	 * @param lastNodeName Last name of the node
	 */
	protected void renameNode(Node node, String name) throws RepositoryException, SecurityException {

		
		logger.debug("Renaming node");
		
		Repository repository = RepositoryRegistry.getInstance().getRepository(node.getRepository());
		Ticket ticket = repository.getTicket();
		RepositoryService service = 
			JLibraryServiceFactory.getInstance(repository.getServerProfile()).getRepositoryService();
		service.renameNode(ticket, node.getId(), name);
		
		
		// Update Entity Registry
		node.setName(name);
		EntityRegistry.getInstance().addNode(node);
	}	
	
}
