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
package org.jlibrary.client.ui.repository.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.export.EclipseExportProgressMonitor;
import org.jlibrary.client.export.ExportException;
import org.jlibrary.client.export.Exporter;
import org.jlibrary.client.export.IExportProgressMonitor;
import org.jlibrary.client.export.RepositoryContext;
import org.jlibrary.client.export.freemarker.FreemarkerContext;
import org.jlibrary.client.export.freemarker.FreemarkerExporter;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.export.wizard.ExportWizard;
import org.jlibrary.core.entities.Repository;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to export repository contents
 */
public class ExportToHTMLAction extends SelectionDispatchAction {
 
	static Logger logger = LoggerFactory.getLogger(ExportToHTMLAction.class);
	
	private IWorkbenchSite site;
	
	/**
	 * Constructor
	 */
	public ExportToHTMLAction(IWorkbenchSite site) {
		
		super(site);
		this.site = site;

		setText(Messages.getMessage("item_export_static"));
		setToolTipText(Messages.getMessage("tooltip_export_static"));
		
		setImageDescriptor(SharedImages.getImageDescriptor(
									SharedImages.IMAGE_EXPORT_HTML));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(
				SharedImages.IMAGE_EXPORT_HTML_DISABLED));
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
				SecurityManager.HTMLEXPORT_REPOSITORY)) {
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
		
		
		logger.info("Exporting repository to static web");
		
		final Repository repository = (Repository)elements[0];

		// Temporary output directory. It will be overriden for the wizard 
		// user selection
		String tempDir = System.getProperty("java.io.tmpdir");
		File outputDirectory = new File(tempDir + repository.getName());
		if (outputDirectory.exists()) {
			outputDirectory.delete();
		}				
		
		// Load i18n templates directory
		Bundle bundle = Platform.getBundle("org.jlibrary.client");
		Path path = new Path("$nl$/resources/templates");
		String templatesPath = "workspace/org.jlibrary.client/resources/templates";
		try {
			java.net.URL fileURL = 
				Platform.asLocalURL(Platform.find(bundle,path));
			templatesPath = fileURL.getFile();
		} catch (IOException e) {
			// Don't worry. We'll use the default path
			logger.warn(e.getMessage(),e);
		}
		
		FreemarkerContext tempContext = 
			new FreemarkerContext(repository,
								  templatesPath,
								  outputDirectory.getAbsolutePath());
		final Exporter exporter = new FreemarkerExporter();
		
	    final ExportWizard ew = new ExportWizard(tempContext);
	    WizardDialog wd = new WizardDialog(site.getShell(),ew) {
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_JLIBRARY));
				return control;
			}	    	
	    };
		wd.open();
		
		 if (wd.getReturnCode() == IDialogConstants.OK_ID) {
		 	
		 	final FreemarkerContext context = ew.getContext();
		 	
		 	JobTask jobTask = new JobTask(Messages.getMessage("export_html_process")) {

		 		public IStatus run(IProgressMonitor monitor) 
		 									throws OperationCanceledException, 
		 										   JobTaskException {

					IStatus status = export(monitor,exporter,context);
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}					
					return status;
		 			
		 		}
			};
			
			jobTask.setPriority(Job.LONG);
			new JobRunner().run(jobTask);
		 }
	}
	
	protected IStatus export(IProgressMonitor monitor,
							 Exporter exporter,
							 RepositoryContext context) 
												throws JobTaskException {

		IExportProgressMonitor exportMonitor = 
			new EclipseExportProgressMonitor(monitor);


		try {
			exporter.export(context,exportMonitor);
		} catch (ExportException e) {
			throw new JobTaskException(e);				
		}
	
		return Status.OK_STATUS;

	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {}
}
