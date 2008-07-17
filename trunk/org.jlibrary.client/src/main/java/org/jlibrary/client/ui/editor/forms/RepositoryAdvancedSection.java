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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Section part for repository advanced configuration
 */
public class RepositoryAdvancedSection extends SectionPart {
	
	private Button extractMetadataButton;
	private Button logicalDeleteButton;	
	private Button lazyLoadingButton;	
	
	private SelectionListener selectionListener = new SelectionAdapter() {

		public void widgetSelected(SelectionEvent e) {

			formMetadata.propertiesModified();
		}
	};
	private Repository repository;
	private RepositoryFormMetadata formMetadata;
	
	public RepositoryAdvancedSection(FormToolkit toolkit,
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
		
		section.setText(Messages.getMessage("repository_advanced_section"));
		section.setDescription(Messages.getMessage("repository_advanced_description"));
	
		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.TOP);
		td.grabHorizontal = true;
		td.colspan = 2;
		section.setLayoutData(td);
	
		Composite sectionClient = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
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
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 10;
		leftSection.setLayout(layout);
		toolkit.paintBordersFor(leftSection);
				
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		extractMetadataButton = toolkit.createButton(
				leftSection,"", SWT.CHECK | SWT.FLAT);
		toolkit.adapt(extractMetadataButton, true, true);
		extractMetadataButton.setLayoutData (data);
		extractMetadataButton.setText(
				Messages.getMessage("repository_advanced_metadata"));
		extractMetadataButton.setToolTipText(
				Messages.getMessage("repository_advanced_metadata_tooltip"));
				
		if (formMetadata.canUpdate()) {
			extractMetadataButton.addSelectionListener(selectionListener);
		}

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		logicalDeleteButton = toolkit.createButton(
				leftSection,"", SWT.CHECK | SWT.FLAT);
		toolkit.adapt(logicalDeleteButton, true, true);
		logicalDeleteButton.setLayoutData (data);
		logicalDeleteButton.setText(
				Messages.getMessage("repository_advanced_physical"));
		logicalDeleteButton.setToolTipText(
				Messages.getMessage("repository_advanced_physical_tooltip"));

		if (formMetadata.canUpdate()) {
			logicalDeleteButton.addSelectionListener(selectionListener);
		}		
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		lazyLoadingButton = toolkit.createButton(
				leftSection,"", SWT.CHECK | SWT.FLAT);
		toolkit.adapt(lazyLoadingButton, true, true);
		lazyLoadingButton.setLayoutData (data);
		lazyLoadingButton.setText(
				Messages.getMessage("repository_advanced_lazy"));
		lazyLoadingButton.setToolTipText(
				Messages.getMessage("repository_advanced_lazy_tooltip"));
		
		
		if (formMetadata.canUpdate()) {
			lazyLoadingButton.addSelectionListener(selectionListener);
		}		

		if (repository.getRepositoryConfig().isExtractMetadata()) {
			extractMetadataButton.setSelection(true);
		}
		if (!repository.getRepositoryConfig().isPhysicalDeleteDocuments()) {
			logicalDeleteButton.setSelection(true);
		}
		if (repository.getRepositoryConfig().isEnabledLazyLoading()) {
			lazyLoadingButton.setSelection(true);
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

		if (extractMetadataButton != null) {
			extractMetadataButton.setFocus();
		}	
	}

	public boolean isMetadataExtractionEnabled() {

		return extractMetadataButton.getSelection();
	}
	
	public boolean isLogicalDeleteEnabled() {
		
		return logicalDeleteButton.getSelection();
	}
	
	public boolean isLazyLoadingEnabled() {
		
		return lazyLoadingButton.getSelection();
	}	
}
