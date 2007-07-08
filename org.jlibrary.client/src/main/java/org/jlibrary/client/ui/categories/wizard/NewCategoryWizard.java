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
package org.jlibrary.client.ui.categories.wizard;

import java.util.HashSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nicolasjouanin
 *
 * Wizard for creating new categories
 */
public class NewCategoryWizard extends Wizard
{

	static Logger logger = LoggerFactory.getLogger(NewCategoryWizard.class);
	
	private Category category;
	private Category parentCategory;
	private Ticket ticket;
    private Repository repository;

    private CategoryWizardDescriptionPage descriptionDataPage;
	
    public NewCategoryWizard(Repository repository, Category parentCategory)
    {
        
        super();
        this.repository = repository;
        this.ticket = repository.getTicket();
        this.parentCategory = parentCategory;
        
        setWindowTitle(Messages.getMessage("new_category_wizard_title"));
        setNeedsProgressMonitor(true);
    }
    public void addPages()
    {
        
    	descriptionDataPage = new CategoryWizardDescriptionPage(
            Messages.getMessage("new_category_wizard_name"),
            Messages.getMessage("new_category_wizard_description"),
            repository);
    	if(parentCategory != null)
    		descriptionDataPage.setParentCategory(parentCategory);
                        
        addPage(descriptionDataPage);
    }

    public boolean performFinish()
    {
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
				
		CategoryProperties categoryProperties;
        try
        {
            categoryProperties = new CategoryProperties();
            
            categoryProperties.put(CategoryProperties.CATEGORY_NAME, 
            	   new PropertyDef(CategoryProperties.CATEGORY_NAME, descriptionDataPage.getName()));
            categoryProperties.put(CategoryProperties.CATEGORY_DESCRIPTION, 
            	   new PropertyDef(CategoryProperties.CATEGORY_DESCRIPTION, descriptionDataPage.getDescriptionText()));
            categoryProperties.put(CategoryProperties.CATEGORY_REPOSITORY, 
            	   new PropertyDef(CategoryProperties.CATEGORY_REPOSITORY, repository.getId()));
                    
            if (descriptionDataPage.getParentCategory() != null)
            {
            	categoryProperties.put(CategoryProperties.CATEGORY_PARENT, 
	          		   new PropertyDef(CategoryProperties.CATEGORY_PARENT, descriptionDataPage.getParentCategory().getId()));
            }
            else
            {
            	categoryProperties.put(CategoryProperties.CATEGORY_PARENT, 
	          		   new PropertyDef(CategoryProperties.CATEGORY_PARENT, null));
            }
        } catch (Exception e)
        {
        	
            logger.error(e.getMessage(),e);
            return false;
        }
        
		try
		{
			category = service.createCategory(ticket,categoryProperties); 
			parentCategory = descriptionDataPage.getParentCategory();
			if (parentCategory != null)
			{
				if (parentCategory.getCategories() == null)
				{
					parentCategory.setCategories(new HashSet());
				}
				parentCategory.getCategories().add(category);
			}
			else
				repository.getCategories().add(category);
			
		} catch (CategoryAlreadyExistsException caee)
		{
			showError(Messages.getAndParseValue("category_already_exists",		
											    "%1",
											    descriptionDataPage.getName()));
			return false;
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
		} catch (RepositoryException e) {
			ErrorDialog.openError(new Shell(),
					"ERROR",
					Messages.getMessage("category_create_error"),
					new Status(IStatus.ERROR,"JLibrary",101,e.getMessage(),e));
			StatusLine.setErrorMessage(Messages.getMessage("category_create_error"));
			return false;
		}					
		return true;
	}
    
	private void showError(String message) {
		
		descriptionDataPage.setMessage(message, IMessageProvider.ERROR);
	}
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

}
