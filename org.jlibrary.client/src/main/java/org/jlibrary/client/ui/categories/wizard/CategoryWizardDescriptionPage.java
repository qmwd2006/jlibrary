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
package org.jlibrary.client.ui.categories.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.categories.dialogs.CategorySelectionDialog;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;

/**
 * @author nicolasjouanin
 *
 * Wizard page for creating new categories
 */
public class CategoryWizardDescriptionPage extends WizardPage
{

	private Text parentCategoryText;
	private Button browseButton;
	private Text nameText;
	private Text descriptionText;
	
	private Category parentCategory;
	private Repository repository;


	protected CategoryWizardDescriptionPage(String pageName, String description, Repository repos)
	{
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(false);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_GENERIC_WIZARD));
        this.repository = repos;
	}

	public void createControl(Composite outer)
	{
        GridData outerData = new GridData();
        outerData.horizontalAlignment = GridData.FILL;
        outerData.heightHint = 250;
		outer.setLayoutData(outerData);
	
        Composite parent = new Composite(outer, SWT.NONE);

		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		parent.setLayout (gridLayout);
		
		Label labParent = new Label(parent, SWT.NONE);
		labParent.setText (Messages.getMessage("category_parent"));
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.END;
		labParent.setLayoutData (data);
		
		parentCategoryText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		parentCategoryText.setLayoutData (data);
		parentCategoryText.setEditable(false);
		
		browseButton = new Button(parent, SWT.NONE);
		browseButton.setImage(SharedImages.getImage(SharedImages.IMAGE_CATEGORY));
		data = new GridData();
		browseButton.setLayoutData(data);	
		
		Label labName = new Label (parent, SWT.NONE);
		labName.setText (Messages.getMessage("category_name"));
		data = new GridData ();
		data.horizontalAlignment = GridData.END;
		labName.setLayoutData (data);
		
		nameText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		nameText.setLayoutData (data);

		Label labDescription = new Label (parent, SWT.NONE);
		labDescription.setText (Messages.getMessage("category_description"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		data.horizontalSpan = 3;
		labDescription.setLayoutData (data);
		
		descriptionText = new Text (parent, SWT.BORDER | SWT.WRAP);
		data = new GridData ();
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		descriptionText.setLayoutData (data);

		if (parentCategory != null)
			parentCategoryText.setText(parentCategory.getName());

		ModifyListener modifyListener = new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
		
		descriptionText.addTraverseListener(new TraverseListener () {
			public void keyTraversed(TraverseEvent e) {
				switch (e.detail) {
					case SWT.TRAVERSE_TAB_NEXT:
						e.doit = true;
					case SWT.TRAVERSE_TAB_PREVIOUS: {
						nameText.setFocus();
						e.doit = true;
					}
				}
			}
		});
		
		nameText.addModifyListener(modifyListener);
		descriptionText.addModifyListener(modifyListener);
		parentCategoryText.addModifyListener(modifyListener);
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent se) {
				
				//Repository repository = RepositoryRegistry.getInstance().getRepository(repositoryId);
				CategorySelectionDialog csd = CategorySelectionDialog.getInstance(new Shell());
				csd.open(repository);
				
				if (csd.getReturnCode() == IDialogConstants.OK_ID) {
					Category category = csd.getCategory();
					if (category == null) {
						parentCategoryText.setText("");
						parentCategory = null;
					} else {					
						if (category.isUnknownCategory()) {
							parentCategoryText.setText("");
							parentCategory = null;
						} else {
							parentCategory = category;
							parentCategoryText.setText(category.getName());
						}
					}
				}
				checkButtonsEnabled();
			}
			
		});

		nameText.addFocusListener(new FocusAdapter() {
			private boolean noNameText = true;

			public void focusLost(FocusEvent e) {
				
				if (noNameText) {
					noNameText = false;
					if (descriptionText.getText().equals("")) {
						descriptionText.setText(nameText.getText());
						descriptionText.selectAll();
					}
				}
				
				super.focusLost(e);
			}
		});		
        setControl(parent);

	}

	/**
	 * This method checks if the new user button can be enabled
	 */
	private void checkButtonsEnabled()
	{
    	setPageComplete(false);
	
		if (nameText.getText().equals("")) {
			return;
		}	
		if (descriptionText.getText().equals("")) {
			return;
		}	
		
		setMessage(getDescription());
    	setPageComplete(true);
	}

	public String getName()
	{
		return nameText.getText();
	}
	public String getDescriptionText()
	{
		return descriptionText.getText();
	}
	public Category getParentCategory()
	{
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}
}
