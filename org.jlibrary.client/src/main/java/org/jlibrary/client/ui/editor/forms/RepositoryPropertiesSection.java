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
package org.jlibrary.client.ui.editor.forms;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.wizard.NewPropertyWizard;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Section part for repository custom properties definition
 */
public class RepositoryPropertiesSection extends SectionPart {
	
	private Table propertiesTable;

	private Button newPropertyButton;
	private Button deletePropertyButton;
	
	private Repository repository;
	private RepositoryFormMetadata formMetadata;
	
	public RepositoryPropertiesSection(FormToolkit toolkit,
									 RepositoryFormMetadata formMetadata, 
									 Composite parent) {

		super(parent, toolkit, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE );
		
		this.formMetadata = formMetadata;
		this.repository = formMetadata.getRepository();
		getSection().clientVerticalSpacing = 4;
		getSection().setData("part", this);

		createClient(toolkit);
	}
	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("repository_properties_custom"));
		section.setDescription(Messages.getMessage("repository_properties_custom_description"));
	
		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.TOP);
		td.grabHorizontal = true;
		td.colspan = 2;
		section.setLayoutData(td);
	
		Composite sectionClient = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.verticalSpacing = 10;
		gridLayout.makeColumnsEqualWidth = true;

		sectionClient.setLayout(gridLayout);
		section.setClient(sectionClient);
		toolkit.paintBordersFor(sectionClient);
	
		Composite leftSection = toolkit.createComposite(sectionClient);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		leftSection.setLayoutData(data);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 10;
		leftSection.setLayout(layout);
		toolkit.paintBordersFor(leftSection);
				
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		propertiesTable = toolkit.createTable(
				leftSection, SWT.CHECK | SWT.FLAT);
		toolkit.adapt(propertiesTable, true, true);
		propertiesTable.setLayoutData (data);
		propertiesTable.setHeaderVisible(true);
		
		TableColumn columnKey = new TableColumn(propertiesTable,SWT.NULL,0);
		columnKey.setText(Messages.getMessage("repository_properties_key"));
		columnKey.setWidth(200);
		
		TableColumn columnDefaultValue = new TableColumn(propertiesTable,SWT.NULL,1);
		columnDefaultValue.setText(Messages.getMessage("repository_properties_default"));
		columnDefaultValue.setWidth(200);		
		
		TableColumn columnAutoAdd = new TableColumn(propertiesTable,SWT.NULL,2);
		columnAutoAdd.setText(Messages.getMessage("repository_properties_auto"));
		columnAutoAdd.setWidth(100);
		
		Composite buttonContainer = toolkit.createComposite(leftSection);		
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		buttonContainer.setLayoutData(gd);
		
		GridLayout buttonLayout = new GridLayout();
		buttonContainer.setLayout(buttonLayout);

		gd = new GridData();
		gd.widthHint = 120;
		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		newPropertyButton = toolkit.createButton(buttonContainer,"",SWT.PUSH);
		newPropertyButton.setText(Messages.getMessage("repository_properties_new"));
		newPropertyButton.setToolTipText(Messages.getMessage("repository_properties_new_tooltip"));
		newPropertyButton.setLayoutData(gd);

		gd = new GridData();
		gd.widthHint = 120;
		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		deletePropertyButton = toolkit.createButton(buttonContainer,"",SWT.PUSH);
		deletePropertyButton.setText(Messages.getMessage("repository_properties_delete"));
		deletePropertyButton.setToolTipText(Messages.getMessage("repository_properties_delete_tooltip"));
		deletePropertyButton.setLayoutData(gd);	
		deletePropertyButton.setEnabled(false);
		
		newPropertyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createNewProperty();
			}
		});
	}

	protected void createNewProperty() {

	    NewPropertyWizard ndw = new NewPropertyWizard(repository);
	    WizardDialog wd = new WizardDialog(getSection().getShell(),ndw) {
			protected Control createDialogArea(Composite parent) {
				Control control = super.createDialogArea(parent);
				getShell().setImage(SharedImages.getImage(
						SharedImages.IMAGE_JLIBRARY));
				return control;
			}
	    };
	    wd.open();
		
	    if (wd.getReturnCode() == IDialogConstants.OK_ID) {
	    	// Refresh properties
		}		
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}

	public void dispose() {

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#setFocus()
	 */
	public void setFocus() {

		if (propertiesTable != null) {
			propertiesTable.setFocus();
		}	
	}
}
