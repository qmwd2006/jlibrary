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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.core.entities.Group;

public class GroupMetadataSection extends SectionPart {
	
	private Text descriptionText;
	private Text nameText;
	
	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
		
			formMetadata.propertiesModified();
		}
	};
	
	private Group group;
	private GroupFormMetadata formMetadata;
	
	public GroupMetadataSection(FormToolkit toolkit,
							    GroupFormMetadata formMetadata, 
							    Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED );
		
		this.formMetadata = formMetadata;
		this.group = formMetadata.getGroup();
		getSection().clientVerticalSpacing = 4;
		getSection().setData("part", this);

		createClient(toolkit);
	}
	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("metadata_section"));
		section.setDescription(Messages.getMessage("metadata_section_description"));
	
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
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 10;
		leftSection.setLayout(layout);
		toolkit.paintBordersFor(leftSection);
		
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;		
		Label labName = toolkit.createLabel(leftSection,"");
		labName.setText (Messages.getMessage("properties_name"));
		labName.setLayoutData (data);
		
		data = new GridData();
		data.widthHint = 200;
		nameText = toolkit.createText(leftSection,"", SWT.WRAP | SWT.FLAT);
		toolkit.adapt(nameText, true, true);
		nameText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			nameText.addModifyListener(modifyListener);
		}

		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;		
		data.heightHint = 100;
		Label labDescription = toolkit.createLabel(leftSection,"");
		labDescription.setText (Messages.getMessage("properties_description"));
		labDescription.setLayoutData (data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 300;
		data.heightHint = 100;
		descriptionText = toolkit.createText(leftSection,"", SWT.WRAP | SWT.V_SCROLL);
		descriptionText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			descriptionText.addModifyListener(modifyListener);
		}
		
		if (group.getName().equals(Group.ADMINS_GROUP_NAME) ||
			group.getName().equals(Group.READERS_GROUP_NAME) ||
			group.getName().equals(Group.PUBLISHERS_GROUP_NAME)) {
			
			nameText.setText(Messages.getMessage(group.getName()));
			nameText.setEnabled(false);
			descriptionText.setText(Messages.getMessage(group.getDescription()));
			descriptionText.setEnabled(false);
		} else {
			nameText.setText(group.getName());
			descriptionText.setText(group.getDescription());
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

		if (nameText != null) {
			nameText.setFocus();
		}	
	}

	public String getDescription() {

		return descriptionText.getText();
	}
	
	public String getName() {
		
		return nameText.getText();
	}
}
