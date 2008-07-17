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
package org.jlibrary.client.ui.export.wizard;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.jlibrary.client.Messages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.export.freemarker.FreemarkerContext;

/**
 * @author martin
 *
 * Wizard for creating a new document
 */
public class ExportWizard extends Wizard {

    private FreemarkerContext context;
    
    private TemplatesDataPage templatesPage;
    
    public ExportWizard(FreemarkerContext context) {
        
        super();
        this.context = context;
        
        setWindowTitle(Messages.getMessage("export_wizard_title"));
        setNeedsProgressMonitor(false);
        
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
    	templatesPage = new TemplatesDataPage(
    						context,
    						Messages.getMessage("export_wizard_templates_title"),
        					Messages.getMessage("export_wizard_templates_description"));    	
    	
        addPage(templatesPage);
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        		
		String outputDirectory = templatesPage.getOutputDirectory();
		File file = new File(outputDirectory);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				templatesPage.setMessage(
						Messages.getMessage("export_wizard_create_error"), 
						IMessageProvider.ERROR);
				return false;
			}
		}
		ClientConfig.setValue(ClientConfig.EXPORT_WEB,file.getAbsolutePath());
		context.setOutputDirectory(outputDirectory);
    	
    	context.setTemplatesDirectory(
    			templatesPage.getSelectedTemplate().getAbsolutePath());
    	
    	return true;
    }
    
	/**
	 * Returns the modified repository context
	 * 
	 * @return FreemarkerContext Updated repository context
	 */    
	public FreemarkerContext getContext() {
    	
        return context;
    }
}
