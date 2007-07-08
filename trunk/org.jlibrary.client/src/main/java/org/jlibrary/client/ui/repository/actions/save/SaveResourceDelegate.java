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
package org.jlibrary.client.ui.repository.actions.save;

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.categories.DocumentsView;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.repository.views.ResourcesView;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that handles resource saving
 * 
 * @author mpermar
 *
 */
public class SaveResourceDelegate extends SavingDelegate {

	static Logger logger = LoggerFactory.getLogger(SaveResourceDelegate.class);
	
	private ResourceNode updatedResource;

	public int loadProcessLength(Object object) {

		return 1;
	}
		
	public IStatus doSave(IProgressMonitor monitor,
						  final JLibraryEditor editor) {

		ResourceNode resource = (ResourceNode)editor.getModel();
	    Repository repository = RepositoryRegistry.
	    		getInstance().getRepository(resource.getRepository());

		final ServerProfile profile = repository.getServerProfile();
		final Ticket ticket = repository.getTicket();
		
		logger.debug("Saving resource");
		
		RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			ResourceNodeProperties resourceProperties = 
										resource.dumpProperties();
			
			if (editor instanceof JLibraryEditor) {
				((JLibraryEditor) editor).setDirty(false);
			}

			monitor.subTask(Messages.getAndParseValue("job_save_resource", "%1",resource.getName()));
			updatedResource = 
				service.updateResourceNode(ticket,resourceProperties);

			boolean dirty = isEditorDirty(editor);
			if (dirty) {
				InputStream is = ((FileEditorInput) editor.getEditorInput())
						.getFile().getContents();
				service.updateContent(ticket, resource.getId(), is);
			}
			
			monitor.worked(1);

			// Update the entity registry
			EntityRegistry.getInstance().addNode(updatedResource);
			return Status.OK_STATUS;
			
		} catch (final SecurityException se) {
			return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,Messages.getMessage("not_enough_permissions"),se);
		} catch (final Exception e) {
			e.printStackTrace();
			return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,e.getMessage(),e);
		}	
		
	}
	
	public void postJobTasks(JLibraryEditor editor, Object object) {
	
		ResourceNode resource = (ResourceNode)object;
		RepositoryView.getInstance().changeNode(resource,updatedResource);
		
		((JLibraryEditor)editor).updateContents();
		// The name could have changed
		((JLibraryEditor)editor).updateTitle(resource.getName());

	    Repository repository = 
			RepositoryRegistry.getInstance().getRepository(resource.getRepository());				
        Node parent = EntityRegistry.getInstance().getNode(
        		resource.getParent(),resource.getRepository());
		if (parent == null) {
			RepositoryView.getRepositoryViewer().refresh(repository);
			RepositoryView.getRepositoryViewer().expandToLevel(repository,1);		 		
		} else {
			RepositoryView.getRepositoryViewer().refresh(parent);
			RepositoryView.getRepositoryViewer().expandToLevel(parent,1);
		}
		
		if (CategoriesView.getInstance() != null) {
			CategoriesView.getInstance().refresh();
		}
		if (DocumentsView.getInstance() != null) {
			DocumentsView.getInstance().refreshDocuments();
		}
		
		// refresh relations view
		if (RelationsView.getInstance() != null) {
			RelationsView.getInstance().refresh();
		}

		ResourcesView.refresh();
		
		editor.updateTitle();
		editor.editorSaved();
		
		// Update properties view
		PropertySheet propertySheet = JLibraryPlugin.findPropertiesView();
		if (propertySheet != null) {
			PropertySheetPage page = (PropertySheetPage)propertySheet.getCurrentPage();
			if (page != null) {
				page.refresh();
			}
		}		
	}
}
