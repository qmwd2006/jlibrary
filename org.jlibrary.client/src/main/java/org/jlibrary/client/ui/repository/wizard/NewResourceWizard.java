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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.cache.CacheUtils;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.cache.NodeContentHandler;
import org.jlibrary.client.Messages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.categories.DocumentsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Wizard for creating a new resource
 */
public class NewResourceWizard extends Wizard {

	static Logger logger = LoggerFactory.getLogger(NewResourceWizard.class);
	
    private ResourceDescriptionDataPage descriptionDataPage;
    
    private Ticket ticket;
    private ResourceNode resource;
    private Repository repository;
    private Directory parent;
    
    private String resourceFilePath;
    
    public NewResourceWizard(Directory parent, Repository repository) {
        
        super();
        this.parent = parent;
        this.repository = repository;
        this.ticket = repository.getTicket();
        
        setWindowTitle(Messages.getMessage("resource_wizard_title"));
        setNeedsProgressMonitor(true);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
    	descriptionDataPage = new ResourceDescriptionDataPage(
            Messages.getMessage("resource_wizard_description"),
            Messages.getMessage("resource_wizard_description_desc"));
    	                        
        addPage(descriptionDataPage);
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {        		
    	
		resource = createResourceStructure();
    	return createResource();
    }
    
	private boolean createResource() {
		
		if (resource == null) {
			return false;
		}

		final File file = descriptionDataPage.getFile();
		if (file.exists()) {
			ClientConfig.setValue(ClientConfig.NEW_RESOURCE_DIRECTORY,
					  descriptionDataPage.getFile().getAbsolutePath());		
		}
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, 
															 InterruptedException {

				monitor.beginTask(Messages.getMessage("resource_wizard_creating"),1);
				
				try {				
					
					ResourceNodeProperties resourceProperties = resource.dumpProperties();
					monitor.subTask(Messages.getMessage("resource_wizard_add_resource"));
					monitor.internalWorked(1);

					// Create document		
					ServerProfile profile = repository.getServerProfile();
					RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
					
					resource = service.createResource(ticket,resourceProperties);

					final FileInputStream fis = new FileInputStream(file);
					try {
						if (file.exists()) {							
							service.updateContent(ticket, resource.getId(), fis);
							CacheUtils.addFileToCache(file, resource);
						}
					} catch (Exception e) {
						throw new RepositoryException(e);
					} finally {
						if (fis != null) {
							fis.close();
						}
					}
					EntityRegistry.getInstance().addNode(resource);
					
					resource.setParent(parent.getId());
					if (parent.getNodes() == null) {
						parent.setNodes(new HashSet());
					}
					parent.getNodes().add(resource);
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

    public ResourceNode getResource() {
		
        return resource;
    }
	
	public Repository getRepository() {
		return repository;
	}

	private ResourceNode createResourceStructure() {
		
		File file = descriptionDataPage.getFile();
		resourceFilePath = file.getAbsolutePath();
		
		final ResourceNode resource = new ResourceNode();
		
		resource.setName(descriptionDataPage.getName());
		resource.setDescription(descriptionDataPage.getDocumentDescription());
		resource.setPath(FileUtils.buildPath(parent,file.getName()));
		resource.setDate(new Date());
		resource.setImportance(new Integer(descriptionDataPage.getImportance()));
		
		
		User user = ticket.getUser();
		resource.setCreator(user.getId());
		resource.setParent(parent.getId());	
		resource.setTypecode(Types.getTypeForFile(resource.getPath()));
		
		BigDecimal bdsize = new BigDecimal(0f);
		resource.setSize(bdsize);
		
		return resource;
	}
}
