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

import java.io.File;

import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.ccp.PasteException;
import org.jlibrary.client.ui.ccp.PasteService;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.client.ui.repository.RepositoryViewer;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.IResource;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JFace node drop adapter
 * 
 * @author martin
 */
public class NodeDropListener extends ViewerDropAdapter {

	static Logger logger = LoggerFactory.getLogger(NodeDropListener.class);
	
   public NodeDropListener(RepositoryViewer viewer) {
	   
	   super(viewer);
   }
         
   /**
    * Method declared on ViewerDropAdapter
    */
   public boolean performDrop(Object data) {
	  
	   IResource target = (IResource)getCurrentTarget();
	   Object[] toDrop = DNDItems.getItems();
	   boolean move = true;
	   if (toDrop.length > 0) {
		   String repository = ((IResource)toDrop[0]).getRepository();
		   //cannot drop a node onto itself
		   for (int i = 0; i < toDrop.length; i++) {
			   if (toDrop[i].equals(target)) {
				   return false;
			   }
			   String toDropRepo = ((IResource)toDrop[i]).getRepository();
			   if (!toDropRepo.equals(repository)) {
				   // We want to drop things from several repositories. Not 
				   // allowed				   
				   return false;
			   }
		   }
		   move = target.getRepository().equals(repository);
		   drop(toDrop,target,move);
		   DNDItems.clear();
	   } else {
		   // Check if it's a file system drop
		   if (data instanceof String[]) {
			   String[] filenames = (String[])data;
			   File[] files = new File[filenames.length];
			   for (int i=0; i<filenames.length; i++) {
				   files[i] = new File(filenames[i]);
			   }
			   drop(files,target,move);
		   }
	   }
	   return true;
   }
   
   private void drop(Object[] nodes, Object target, boolean move) {
	   
	   try {
		   PasteService pasteService = PasteService.getInstance();
		   pasteService.paste(nodes, target, move);
	   } catch (PasteException e) {
		   
		   logger.error(e.getMessage(),e);
	   }	   
   }
   
   /**
    * Method declared on ViewerDropAdapter
    */
   public boolean validateDrop(Object target, int op, TransferData type) {
	   
	   SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
	   boolean isValid = 
		   TextTransfer.getInstance().isSupportedType(type) ||
		   FileTransfer.getInstance().isSupportedType(type);
	   
	   if (!isValid) {
		   return false;
	   }
	   if (target == null) {
		   return false;
	   }
	   if (target instanceof Document) {
		   return false;
	   }
	   
	   Node targetNode = null;
	   if (target instanceof Repository) {
		   targetNode = ((Repository)target).getRoot();
	   } else {
		   targetNode = (Node)target;
	   }
	   
		if (!securityManager.canPerformAction(
				targetNode.getRepository(),
				targetNode,
				SecurityManager.CREATE_DOCUMENT)) {
			return false;
		}
	   
	   Object[] dndItems = DNDItems.getItems();
	   for (int i = 0; i < dndItems.length; i++) {
		   if (!(dndItems[i] instanceof Node)) {
			   return false;
		   }
		   if (((Node)dndItems[i]).getParent().equals(targetNode.getId())) {
			   return false;
	   }
	}
	   
	   return true;
   }		
}
