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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to export repository contents
 */
public class ExportRepositoryAction extends SelectionDispatchAction {
 
	static Logger logger = LoggerFactory.getLogger(ExportRepositoryAction.class);
	
	private IWorkbenchSite site;
	
	/**
	 * Constructor
	 */
	public ExportRepositoryAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;

		setText(Messages.getMessage("item_export_rep"));
		setToolTipText(Messages.getMessage("tooltip_export_rep"));
		setImageDescriptor(
				SharedImages.getImageDescriptor(SharedImages.IMAGE_EXPORT));
		setDisabledImageDescriptor(
				SharedImages.getImageDescriptor(SharedImages.IMAGE_EXPORT_DISABLED));
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
		
		Object[] elements = selection.toArray();
		if (elements.length > 1) {
			return false;
		}
		
		if (!(elements[0] instanceof Repository)) {
			return false;
		}
		
		Repository repository = ((Repository)elements[0]);
		if (!securityManager.canPerformAction(
				repository.getId(),
				repository.getRoot(),
				SecurityManager.EXPORT_REPOSITORY)) {
			return false;
		}
						
		if (!repository.isConnected()) {
			return false;
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

		
		logger.info("Exporting repository");
		
		final Repository repository = (Repository)elements[0];
		
		FileDialog fd = new FileDialog(site.getShell(), SWT.SAVE);
		String filter = ClientConfig.getValue(ClientConfig.EXPORT_REPOSITORY);
		if (filter != null) {
			File f = new File(filter);
			fd.setFilterPath(f.getAbsolutePath());
		}
		
		fd.setFilterNames (new String [] {Messages.getMessage("jlibrary_extension"), 
										  Messages.getMessage("all_extension")
										 });
		fd.setFilterExtensions (new String [] {"*.jlib", "*.*"});		
		
		String result = fd.open();
		if (result == null) {
			// Cancel
			return;
		}
		ClientConfig.setValue(ClientConfig.EXPORT_REPOSITORY,result);
		
		final File f = new File(result);
		if (f.exists()) {
			// Overwrite ?
			// Show ask dialog
			boolean confirm = MessageDialog.openYNConfirm(
					site.getWorkbenchWindow().getShell(),
					Messages.getMessage("overwrite_confirmation_title"),
					Messages.getMessage("export_overwrite_confirmation_text"));
			
			if (confirm) {
				// Delete original file
				f.delete();
			} else {
				// Cancel
				return;
			} 
		}
		
		JobTask jobTask = new JobTask(Messages.getMessage("export_job_name")) {

			public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

				monitor.beginTask(Messages.getMessage("export_job_name"),
								  IProgressMonitor.UNKNOWN);
				monitor.worked(1);
				
				ServerProfile serverProfile = repository.getServerProfile();
				final Ticket ticket = repository.getTicket();
				final RepositoryService service = 
								JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();

				FileOutputStream fos = null;
				try {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					fos = new FileOutputStream(f);
					service.exportRepository(ticket, fos);
				} catch (final SecurityException se) {
			 		throw new JobTaskException(se);
				} catch (Exception e) {
					throw new  JobTaskException(e);
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							logger.error(e.getMessage(),e);
						}
					}
				}

				return Status.OK_STATUS;
				
			}
		};
		
		jobTask.setPriority(Job.LONG);
		new JobRunner().run(jobTask);
	}

	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}
}
