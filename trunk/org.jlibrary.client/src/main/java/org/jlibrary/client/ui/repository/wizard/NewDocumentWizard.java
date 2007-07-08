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
package org.jlibrary.client.ui.repository.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.categories.DocumentsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryHelper;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Wizard for creating a new document
 */
public class NewDocumentWizard extends Wizard {

	static Logger logger = LoggerFactory.getLogger(NewDocumentWizard.class);
	
    private DocumentDescriptionDataPage descriptionDataPage;
    
    private Ticket ticket;
    private Document document;
    private DocumentResourcesPage resourcesPage;
    private DocumentMetadataPage metadataPage;
    private DocumentCategoriesPage categoriesPage;
    private Repository repository;
    private Directory parent;
    
    public NewDocumentWizard(Directory parent, Repository repository) {
        
        super();
        this.parent = parent;
        this.repository = repository;
        this.ticket = repository.getTicket();
        
        setWindowTitle(Messages.getMessage("document_wizard_title"));
        setNeedsProgressMonitor(true);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
    	descriptionDataPage = new DocumentDescriptionDataPage(
            Messages.getMessage("document_wizard_description"),
            Messages.getMessage("document_wizard_description_desc"));
    	
    	resourcesPage = new DocumentResourcesPage(
            Messages.getMessage("document_wizard_resources"),
            Messages.getMessage("document_wizard_resources_desc"));

        metadataPage = new DocumentMetadataPage(
            Messages.getMessage("document_wizard_metadata"),
            Messages.getMessage("document_wizard_metadata_desc"));

        categoriesPage = new DocumentCategoriesPage(
            Messages.getMessage("document_wizard_categories"),
            Messages.getMessage("document_wizard_categories_desc"));

                        
        addPage(descriptionDataPage);
        addPage(metadataPage);
        addPage(categoriesPage);
        addPage(resourcesPage);
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {        		
    	document = createDocumentStructure();
    	return createDocument();
    }
    
	private boolean createDocument() {
		
		if (document == null) {
			return false;
		}

		final File file = descriptionDataPage.getFile();
		
		final Collection resources = resourcesPage.getResources();
		final Collection categories = categoriesPage.getCategories();

		final boolean crawlResources = resources.size() > 0;
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, 
															 InterruptedException {

				int taskLength = 1 + resources.size();
				monitor.beginTask(Messages.getMessage("new_document_wizard_creating"),taskLength);
				
				try {				
					DocumentProperties docProperties = document.dumpProperties();
					
					docProperties.setProperty(
							DocumentProperties.DOCUMENT_PARENT,parent.getId());
					String path = FileUtils.buildPath(parent,file.getName());
					docProperties.setProperty(
							DocumentProperties.DOCUMENT_PATH,path);						
					docProperties.setProperty(
							DocumentProperties.DOCUMENT_CREATOR,
							ticket.getUser().getId());
					
					// Add categories
					Iterator it = categories.iterator();
					while (it.hasNext()) {
						Category category = (Category)it.next();
						docProperties.addProperty(
								DocumentProperties.DOCUMENT_ADD_CATEGORY,
								category.getId());
					}
					monitor.subTask(Messages.getMessage("new_document_wizard_add_document"));
					monitor.internalWorked(1);

					
					// Create document		
					document = 
						RepositoryHelper.createDocument(repository,
														parent,
														docProperties,
														file,
														crawlResources);
					EntityRegistry.getInstance().addNode(document);
					
					document.setParent(parent.getId());
					if (parent.getNodes() == null) {
						parent.setNodes(new HashSet());
					}
					parent.getNodes().add(document);
					monitor.done();
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
				} catch (final Exception e) {
					
                                        logger.error(e.getMessage(),e);
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							ErrorDialog.openError(new Shell(),
									  "ERROR",
									  Messages.getMessage("new_document_dialog_can't_create"),
									  new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
							StatusLine.setErrorMessage(Messages.getMessage("new_document_dialog_can't_create"));
						}
					});												
					return;
				}

				// Save preferences and update
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (CategoriesView.getInstance() != null) {
							CategoriesView.getInstance().refresh();
						}
						if (DocumentsView.getInstance() != null) {
							DocumentsView.getInstance().refreshDocuments();
						}
						
						StatusLine.setOKMessage(Messages.getMessage("new_document_created"));
					}
				});																
			}

		};		
		
		
		WizardDialog wd = (WizardDialog)getContainer();
		try {
			wd.run(false,true,runnable);
		} catch (InvocationTargetException e) {
			
                        logger.error(e.getMessage(),e);
		} catch (InterruptedException e) {
			
                        logger.error(e.getMessage(),e);
		}
		
		return true;
	}

    public Document getDocument() {    	
        return document;
    }


	public DocumentMetadataPage getMetadataPage() {		
		return metadataPage;
	}

	public DocumentResourcesPage getResourcesPage() {
		
		return resourcesPage;
	}	
	
	public Repository getRepository() {
		return repository;
	}

	private Document createDocumentStructure() {
		
		final Document document = new Document();
		DocumentMetaData dmd = metadataPage.getMetadata();
		if (dmd.getDate() == null) {
			dmd.setDate(new Date());
		}
		document.setMetaData(dmd);
		
		document.setName(descriptionDataPage.getName());
		document.setDescription(descriptionDataPage.getDocumentDescription());
		File file = descriptionDataPage.getFile();
		
		if (file.exists()) {
			document.setPath(descriptionDataPage.getFile().getAbsolutePath());
		} else {
			// Empty document
			document.setPath(file.getName());
		}
		document.setDate(new Date());
		document.setImportance(new Integer(descriptionDataPage.getImportance()));
		
		
		User user = ticket.getUser();
		document.setCreator(user.getId());
			
		document.setTypecode(Types.getTypeForFile(document.getPath()));
		
		BigDecimal bdsize = new BigDecimal(0.0d);
		if (document.getDate() == null) {
			document.setDate(new Date());
		}
		document.setSize(bdsize);
				
		document.setResourceNodes(new TreeSet());
		
		return document;
	}	
}
