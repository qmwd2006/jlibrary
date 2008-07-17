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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.wizard.ImportRepositoryWizard;
import org.jlibrary.core.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to close a repository
 */
public class ImportRepositoryAction	extends SelectionDispatchAction {
	
	static Logger logger = LoggerFactory.getLogger(ImportRepositoryAction.class);
	
	private Shell shell;
	
	/**
	 * Constructor
	 */
	public ImportRepositoryAction(IWorkbenchSite site) {
		
		super(site);
		this.shell = site.getShell();

		setText(Messages.getMessage("item_import_rep"));
		setToolTipText(Messages.getMessage("tooltip_import_rep"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_IMPORT));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_IMPORT_DISABLED));
	}

	public ImportRepositoryAction(Shell shell) {
		
		super();
		this.shell = shell;
	}	
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
		
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
	
	/**
	 * @see org.jlibrary.client.actions.SelectionDispatchAction#run()
	 */
	public void run() {
		
		run((Object[])null);
	}
	
	public void run(Object[] elements) {

		
		logger.info("Importing repository");

	    ImportRepositoryWizard irw = new ImportRepositoryWizard();
	    WizardDialog wd = new WizardDialog(shell,irw) {
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_JLIBRARY));
				return control;
			}	    	
	    };
		wd.open();

		 if (wd.getReturnCode() == IDialogConstants.OK_ID) {
		     
		    Repository repository = irw.getRepository();
		    String repositoryName = irw.getRepositoryName();
		 	
		 	// Add repository to repository registry
		 	RepositoryRegistry.getInstance().addRepository(
		 			repository,repositoryName);
		 	
		 	// Update views
		 	RelationsView relationsView = JLibraryPlugin.findRelationsView();
		 	if (relationsView != null) {
		 		relationsView.refresh();
		 	}
		 	
		 	CategoriesView categoriesView = JLibraryPlugin.findCategoriesView();
		 	if (categoriesView != null) {
		 		categoriesView.refresh();
		 	}
		 }		
	}

}