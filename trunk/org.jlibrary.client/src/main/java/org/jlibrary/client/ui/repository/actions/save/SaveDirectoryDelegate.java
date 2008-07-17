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
package org.jlibrary.client.ui.repository.actions.save;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.categories.DocumentsView;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.repository.views.ResourcesView;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that handles directory saving
 * 
 * @author mpermar
 *
 */
public class SaveDirectoryDelegate extends SavingDelegate {

	static Logger logger = LoggerFactory.getLogger(SaveDirectoryDelegate.class);
	
	private Directory updatedDirectory;

	public int loadProcessLength(Object object) {

		return 1;
	}
		
	public IStatus doSave(IProgressMonitor monitor,
						  final JLibraryEditor editor) {

		Directory directory = (Directory)editor.getModel();
	    Repository repository = RepositoryRegistry.
	    		getInstance().getRepository(directory.getRepository());

		final ServerProfile profile = repository.getServerProfile();
		final Ticket ticket = repository.getTicket();
		
		logger.debug("Saving directory");
		
		RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			
			DirectoryProperties directoryProperties = directory.dumpProperties();

			if (editor instanceof JLibraryEditor) {
				((JLibraryEditor) editor).setDirty(false);
			}

			monitor.subTask(Messages.getAndParseValue("job_save_resource", "%1",
					directory.getName()));
			updatedDirectory = 
				service.updateDirectory(ticket, directoryProperties);

			monitor.worked(1);

			// Update the entity registry
			EntityRegistry.getInstance().addNode(updatedDirectory);
						
			return Status.OK_STATUS;
			
		} catch (final SecurityException se) {
			return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,Messages.getMessage("not_enough_permissions"),se);
		} catch (final Exception e) {
			e.printStackTrace();
			return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,e.getMessage(),e);
		}	
		
	}
	
	public void postJobTasks(JLibraryEditor editor, Object object) {
	
		Directory node = (Directory)object;
		RepositoryView.getInstance().changeNode(node,updatedDirectory);
		
		((JLibraryEditor)editor).updateContents();
		// The name could have changed
		((JLibraryEditor)editor).updateTitle(node.getName());

	    Repository repository = 
			RepositoryRegistry.getInstance().getRepository(node.getRepository());				
        Node parent = EntityRegistry.getInstance().getNode(
        		node.getParent(),node.getRepository());
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
