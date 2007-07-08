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
package org.jlibrary.client.ui.categories.dnd;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.ccp.CategoryPaster;
import org.jlibrary.client.ui.ccp.PasteException;
import org.jlibrary.client.ui.ccp.PasteService;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JFace node drop adapter
 * 
 * @author martin
 */
public class CategoryDropListener extends ViewerDropAdapter {

	static Logger logger = LoggerFactory.getLogger(CategoryDropListener.class);
	
	private CategoryPaster categoryPaster = new CategoryPaster();
	
	
   public CategoryDropListener(TreeViewer viewer) {
	   
	   super(viewer);
   }
         
   /**
    * Method declared on ViewerDropAdapter
    */
   public boolean performDrop(Object data) {
	  
	   Object target = getCurrentTarget();
	   Object[] toDrop = DNDItems.getItems();
	  
	   //cannot drop a node onto itself
	   for (int i = 0; i < toDrop.length; i++) {
		   if (toDrop[i].equals(target)) {
			   return false;
		   }
	   }
	  
		Object source = DNDItems.getItems();

		try {
			PasteService pasteService = PasteService.getInstance();
			pasteService.paste(source, target, true, categoryPaster);					
		} catch (PasteException e) {
			
            logger.error(e.getMessage(),e);
		}
		
	   return true;
   }
   
   /**
    * Method declared on ViewerDropAdapter
    */
   public boolean validateDrop(Object target, int op, TransferData type) {
	   
	   	boolean isValid = TextTransfer.getInstance().isSupportedType(type);
		if (!isValid) {
			return false;
		}
		if (target == null) {
			return false;
		}
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Object[] toDrop = DNDItems.getItems();
		for (int i = 0; i < toDrop.length; i++) {
			if (toDrop[i] instanceof Category) {

				Category sourceCategory = (Category)toDrop[i];
				if (target == sourceCategory) return false;
				if (target instanceof Repository) {
					if (!sourceCategory.getRepository().equals(((Repository)target).getId())) return false;
					if (sourceCategory.getParent() == null) return false;
					if (!securityManager.canPerformAction(
							((Repository)target).getId(),
							SecurityManager.CREATE_CATEGORY)) {
						return false;
					}	

				} else {
					Category targetCategory = (Category)target;
					if (!securityManager.canPerformAction(
							targetCategory.getRepository(),
							SecurityManager.CREATE_CATEGORY)) {
						return false;
					}	

					if (targetCategory.isUnknownCategory()) return false;
					if (sourceCategory.getParent() == targetCategory) return false;
					if (!(sourceCategory.getRepository().equals(targetCategory.getRepository()))) return false;
					while (targetCategory.getParent() != null) {
						if (targetCategory.getParent() == sourceCategory) return false;
						targetCategory = targetCategory.getParent();
					}
				}
			} else {
				if (target instanceof Repository) {
					return false;
				} else {					
					Category targetCategory = (Category)target;
					
					if (!securityManager.canPerformAction(
							targetCategory.getRepository(),
							SecurityManager.CATEGORY_ADD_DOCUMENTS)) {
						return false;
					}	

					
					Object[] sourceNodes = DNDItems.getItems();
					for (int j = 0; j < sourceNodes.length; j++) {
						Node sourceNode = (Node)sourceNodes[j];
						if (!sourceNode.getRepository().equals(targetCategory.getRepository())) {
							return false;
						}
					}
				}
			}
		} 

	   
	   return true;
   }		
}
