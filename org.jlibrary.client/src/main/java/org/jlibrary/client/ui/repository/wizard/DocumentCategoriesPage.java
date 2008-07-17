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
package org.jlibrary.client.ui.repository.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.categories.dialogs.CategorySelectionDialog;
import org.jlibrary.client.ui.categories.providers.CategoriesLabelProvider;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;

/**
 * @author Martin Perez
 *
 */
public class DocumentCategoriesPage extends WizardPage {
	
	
    private ListViewer categoriesViewer;
	protected Category unknownCategory;

	public DocumentCategoriesPage(String pageName, String description) {
        
        super(pageName);
        setTitle(pageName);
        setPageComplete(true);
        setDescription(description);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_NEW_DOCUMENT_WIZARD));         
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = 250;
        parent.setLayoutData(data);

        Composite outer = new Composite(parent, SWT.NONE);
                
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		outer.setLayout (gridLayout);

		Label labCategories = new Label (outer, SWT.NONE);
		labCategories.setText (Messages.getMessage("categories_label"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		data.horizontalSpan = 3;
		labCategories.setLayoutData (data);
		
		categoriesViewer = new ListViewer(outer, SharedImages.getImage(SharedImages.IMAGE_CATEGORY));
		categoriesViewer.setLabelProvider(new CategoriesLabelProvider());
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 390;
		data.heightHint = 100;
		data.horizontalSpan = 2;
		data.verticalSpan = 2;
		categoriesViewer.getTable().setLayoutData (data);

		loadUnknownCategory();
		
		Button addCategory = new Button(outer, SWT.PUSH);
		addCategory.setImage(SharedImages.getImage(SharedImages.IMAGE_PLUS));
		data = new GridData ();
		data.horizontalSpan = 1;
		data.verticalAlignment = GridData.CENTER;
		addCategory.setLayoutData (data);		

		Button removeCategory = new Button(outer, SWT.PUSH);
		removeCategory.setImage(SharedImages.getImage(SharedImages.IMAGE_MINUS));
		data = new GridData ();
		data.horizontalSpan = 1;
		data.verticalAlignment = GridData.CENTER;
		removeCategory.setLayoutData (data);		
		
		addCategory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
						
				NewDocumentWizard wizard = (NewDocumentWizard)getWizard();
				Repository repository = wizard.getRepository();
				
				CategorySelectionDialog cld = 
					CategorySelectionDialog.getInstance(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				cld.open(repository);
				
				if (cld.getReturnCode() == IDialogConstants.OK_ID) {
					Category category = cld.getCategory();
					
					if (!category.isUnknownCategory()) {
						categoriesViewer.remove(unknownCategory);
					} else {
						categoriesViewer.getTable().removeAll();
						unknownCategory = category;
					}
					categoriesViewer.add(category);
				}			
			}
		});		
		
		removeCategory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				TableItem[] items = categoriesViewer.getTable().getSelection();
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					int index = categoriesViewer.getTable().indexOf(item);
					categoriesViewer.getTable().remove(index);
				}
				
				if (categoriesViewer.getTable().getItemCount() == 0) {
					categoriesViewer.add(unknownCategory);
				}				
			}
		});			
		

        
        setControl(outer);
    }



	private void loadUnknownCategory() {
		
		NewDocumentWizard ndw = (NewDocumentWizard)getWizard();
		Repository repository = ndw.getRepository();
		Iterator it = repository.getCategories().iterator();
		while (it.hasNext()) {
			Category category = (Category) it.next();
			if (category.getName().equals(Category.UNKNOWN_NAME)) {
				unknownCategory = category;
				categoriesViewer.add(unknownCategory);
				break;
			}
		}	
	}

	public Collection getCategories() {
		
		ArrayList list = new ArrayList();
		TableItem[] items = categoriesViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			list.add(items[i].getData());
		}
		return list;
	}	
}
