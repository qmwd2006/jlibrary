/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
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
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that handles repository saving
 * 
 * @author mpermar
 *
 */
public class SaveRepositoryDelegate extends SavingDelegate {

	static Logger logger = LoggerFactory.getLogger(SaveRepositoryDelegate.class);
	
	public int loadProcessLength(Object object) {

		return 1;
	}
		
	public IStatus doSave(IProgressMonitor monitor,
						  final JLibraryEditor editor) {

		Repository repository = (Repository)editor.getModel();

		final ServerProfile profile = repository.getServerProfile();
		final Ticket ticket = repository.getTicket();
		
		logger.debug("Saving repository");
		
		RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			RepositoryProperties repositoryProperties = 
									repository.dumpProperties();
			
			((JLibraryEditor) editor).setDirty(false);
			
			monitor.subTask(Messages.getAndParseValue("job_save_resource", "%1",
							repository.getName()));
			Repository updatedRepository = 
				service.updateRepository(ticket, repositoryProperties);

			repository.setName(updatedRepository.getName());
			repository.setDescription(updatedRepository.getDescription());
			
			monitor.worked(1);
						
			return Status.OK_STATUS;
			
		} catch (final SecurityException se) {
			return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,Messages.getMessage("not_enough_permissions"),se);
		} catch (final Exception e) {
			e.printStackTrace();
			return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,e.getMessage(),e);
		}	
		
	}
	
	public void postJobTasks(JLibraryEditor editor, Object object) {
	
		RepositoryView.getRepositoryViewer().refresh(object);
	}
	
}
