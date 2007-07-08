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
package org.jlibrary.client.ui.repository.dnd;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.IWorkbenchPartSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.actions.WorkWithAction;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;

/**
 * JFace node drop adapter
 * 
 * @author martin
 */
public class WorkingSetDropListener extends ViewerDropAdapter {

	private WorkWithAction workWithAction;
	
    public WorkingSetDropListener(TreeViewer viewer,
		   						  IWorkbenchPartSite site) {
	   
		super(viewer);
		workWithAction = new WorkWithAction(site);
	}

	/**
    * Method declared on ViewerDropAdapter
    */
	public boolean performDrop(Object data) {
	  
		Object[] toDrop = DNDItems.getItems();
	  
		//cannot drop a node onto itself
		for (int i = 0; i < toDrop.length; i++) {
			Document document = (Document)toDrop[i];
			Repository repository = RepositoryRegistry.getInstance().getRepository(document.getRepository());
			if (repository == null) {
				continue;
			}
			workWithAction.run(document);
		}
		getViewer().refresh();
		DNDItems.clear();
		
		return true;
	}
   
	/**
	 * Method declared on ViewerDropAdapter
	 */
	public boolean validateDrop(Object target, int op, TransferData type) {
	   
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		boolean isValid = TextTransfer.getInstance().isSupportedType(type);
		if (!isValid) {
			return false;
		}
		
		Object[] toDrop = DNDItems.getItems();
		for (int i = 0; i < toDrop.length; i++) {
			if (!(toDrop[i] instanceof Document)) {
				return false;
			}
			Document document = ((Document)toDrop[i]);
			if (!securityManager.canPerformAction(
					document.getRepository(),
					document,
					SecurityManager.LOCK_DOCUMENT)) {
				return false;
			}
			
		} 
	   
	   return true;
	}		
}
