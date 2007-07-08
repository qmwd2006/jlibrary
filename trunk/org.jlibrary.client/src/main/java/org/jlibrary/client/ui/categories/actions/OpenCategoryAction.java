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
package org.jlibrary.client.ui.categories.actions;

/**
 * @author $Author: perez_martin $
 *
 * Manages Category open event
 */

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.part.NodeEditorFactory;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.editor.CategoryEditorInput;
import org.jlibrary.client.ui.editor.EditorsRegistry;
import org.jlibrary.client.ui.editor.impl.CategoryEditor;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenCategoryAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(OpenCategoryAction.class);
	
	private IEditorPart openedEditorPart;

	/**
	 * Constructor
	 */
	public OpenCategoryAction(IWorkbenchSite site) {
		
		super(site);

		setText(Messages.getMessage("item_open_category"));
		setToolTipText(Messages.getMessage("tooltip_open_category"));
		setImageDescriptor(SharedImages.getImageDescriptor(
									SharedImages.IMAGE_OPEN_CATEGORY));
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
						SecurityManager.OPEN_CATEGORY)) {
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
			run(selection);
		}
	}
	
	public void run(IStructuredSelection categories)
	{	
		
				
		for(Iterator it = categories.iterator(); it.hasNext();)
		{
			Category category = (Category)it.next();
			if (category == null)
				return;
			else
			{
				Repository repository = RepositoryRegistry.getInstance().getRepository(category.getRepository());
				ServerProfile serverProfile = repository.getServerProfile();
				RepositoryService repositoryService = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
				Ticket ticket = repository.getTicket();
				
				try
				{
					category = repositoryService.findCategoryById(ticket,category.getId());
					CategoryEditorInput fei = NodeEditorFactory.createEditorInput(category);
					openedEditorPart = JLibraryPlugin.getActivePage().openEditor(
											fei,CategoryEditor.EDITOR_ID);
					EditorsRegistry.getInstance().put(category.getId(),openedEditorPart);		
				}
				catch (CategoryNotFoundException e) {
					ErrorDialog.openError(new Shell(),
							"ERROR",
							Messages.getMessage("category_open_error"),
							new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
					StatusLine.setErrorMessage(Messages.getMessage("category_open_error"));				
				} catch (RepositoryException e)
				{
					ErrorDialog.openError(new Shell(),
							"ERROR",
							Messages.getMessage("category_open_error"),
							new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
					StatusLine.setErrorMessage(Messages.getMessage("category_open_error"));
				}
				catch (final SecurityException se)
				{
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
				catch (LocalCacheException e)
				{
		            logger.error(e.getMessage(),e);
				}
				catch (IOException e)
				{
		            logger.error(e.getMessage(),e);
				}
				catch (PartInitException e)
				{
		            logger.error(e.getMessage(),e);
				}
			}
		}
	}
}
