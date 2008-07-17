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
package org.jlibrary.client.ui.bookmarks.dnd;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.bookmarks.BookmarksView;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;

/**
 * JFace node drop adapter
 * 
 * @author martin
 */
public class BookmarkDropListener extends ViewerDropAdapter {

   public BookmarkDropListener(TreeViewer viewer) {
	   
	   super(viewer);
   }
         
   /**
    * Method declared on ViewerDropAdapter
    */
   public boolean performDrop(Object data) {
	  
	   Bookmark targetBookmark;
	    if (getCurrentTarget() instanceof Repository) {
	    	targetBookmark = null;
	    } else {
	    	targetBookmark = (Bookmark)getCurrentTarget();
	    }
	    
		Object[] toDrop = DNDItems.getItems();		
		
		for (int i = 0; i < toDrop.length; i++) {
			if (toDrop[i].equals(targetBookmark)) {
				return false;
			}
			if ((((Bookmark)toDrop[i]).getParent() == null) && targetBookmark == null) {
				return false;
			}
		}		
		try {
			for (int i = 0; i < toDrop.length; i++) {
				Bookmark droppedBookmark = (Bookmark)toDrop[i];
				Bookmark previousParent = (Bookmark)droppedBookmark.getParent();
				droppedBookmark.setParent(targetBookmark);
				
				Repository repository = 
					RepositoryRegistry.getInstance().getRepository(targetBookmark.getRepository());
				RepositoryService service = JLibraryServiceFactory.getInstance(
							repository.getServerProfile()).getRepositoryService();
				Ticket ticket = JLibraryPlugin.getCurrentTicket();
				service.updateBookmark(ticket, droppedBookmark);
				
				if (targetBookmark != null) {
					targetBookmark.getBookmarks().add(droppedBookmark);
				} else {
					((Repository)getCurrentTarget()).getTicket().
								getUser().getBookmarks().add(droppedBookmark);
				}
				if (previousParent != null) {
					previousParent.getBookmarks().remove(droppedBookmark);
				}
			}
			BookmarksView.refresh();
		} catch (RepositoryException e) {
			ErrorDialog.openError(new Shell(),
					  "ERROR",
					  e.getMessage(),
					  new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
			StatusLine.setErrorMessage(e.getMessage());
			
		}
	   		
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
	   if (target instanceof Repository) {
			if (!securityManager.canPerformAction(
					((Repository)target).getId(),
					SecurityManager.CREATE_BOOKMARK)) {
				return false;
			}	
		   
		   return true;
	   }
	   Bookmark targetBookmark = (Bookmark)target;
	   if (!targetBookmark.isFolder()) {
		   return false;
	   }
	   
		if (!securityManager.canPerformAction(
				targetBookmark.getRepository(),
				SecurityManager.CREATE_BOOKMARK)) {
			return false;
		}	
	   
	   return true;
   }		
}
