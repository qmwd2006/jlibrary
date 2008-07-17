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
package org.jlibrary.client.ui.categories.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.editor.forms.CategoriesSection;
import org.jlibrary.client.ui.editor.forms.MetadataFormPage;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;

/**
 * @author martin
 *
 * This action will be called to delete a category
 */
public class DeleteCategoryAction extends SelectionDispatchAction {

	private IWorkbenchSite site;
	
	/**
	 * Constructor
	 */
	public DeleteCategoryAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;
		setText(Messages.getMessage("item_delete_category"));
		setToolTipText(Messages.getMessage("tooltip_delete_category"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_DELETE_DISABLED));
	}
		
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
			
		if (selection.isEmpty()) {
			return false;
		}

		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Iterator it = selection.iterator();
		while (it.hasNext()) {
			Object selectedObject = it.next();
			if (selectedObject instanceof Category) {
				Category category = (Category) selectedObject;
				if (category.isUnknownCategory()) {
					return false;
				}
				if (!securityManager.canPerformAction(
						category.getRepository(),
						SecurityManager.DELETE_CATEGORY
						)) {
					return false;
				}		
			} else {
				return false;
			}
		}

		
		
		return true;
	}
	
	public void run() {
		
		IStructuredSelection selection = (IStructuredSelection)getSelection();
		if (!selection.isEmpty()) {
			run(selection.toArray());
		}
	}
	
	public void run(Object[] categories) {
				
		for (int i = 0; i < categories.length; i++) {		
			Category category = (Category)categories[i];
			if (category == null) {
				return;
			} else {
				JLibraryPlugin.closeEditors(getSite(),category.getId());
				Repository repository = RepositoryRegistry.getInstance().getRepository(category.getRepository());
				ServerProfile serverProfile = repository.getServerProfile();
				RepositoryService repositoryService = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
				Ticket ticket = repository.getTicket();
				
				try {
					repositoryService.deleteCategory(ticket,category.getId());
					if (category.getParent() != null) {
                            //esto devuelve siempre false, no la borra, aunque el ID
                            // este bien. Supongo que por la forma en que carga
                            //o que se pasa el "equals" por el arco del triunfo
				            category.getParent().getCategories().remove(category);
				            /*
		                        // así que lo hago  a mano
		                        Set tmpSet = new HashSet();                                            
		                        for (Iterator it = category.getParent().getCategories().iterator(); it.hasNext();){
		                            Category tmp = (Category)it.next();
		                            if (!tmp.getId().equals(category.getId())){
		                                tmpSet.add(tmp);
		                            }                                                    
		                        }
		                        category.getParent().setCategories(tmpSet);
                            */
					} else {
						repository.getCategories().remove(category);
					}
					
					CategoriesView.categoryDeleted(category);
					
					// We must update the categories of all the opened documents
					updateEditors();
					
				} catch (final SecurityException se) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							ErrorDialog.openError(new Shell(),
									  "ERROR",
									  Messages.getMessage("security_exception"),
									  new Status(IStatus.ERROR,"JLibrary",101,se.getMessage(),se));
							StatusLine.setErrorMessage(se.getMessage());
						}
					});	

				} catch (RepositoryException e) {
					ErrorDialog.openError(new Shell(),
							"ERROR",
							Messages.getMessage("category_delete_error"),
							new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
					StatusLine.setErrorMessage(Messages.getMessage("category_delete_error"));
				}
			}
		}
	}

	private void updateEditors() {

		IEditorReference[] editorReferences = site.getPage().getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			IEditorPart editor = editorReferences[i].getEditor(false);
			if (editor instanceof GenericEditor) {
				MetadataFormPage page = ((GenericEditor)editor).getMetadataFormPage();
				CategoriesSection section = page.getCategoriesSection();
				if (section != null) {
					section.updateCategories();
				}
			}
		}
	}	
}
