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
import org.jlibrary.core.entities.Author;

/**
 * @author turgayz
 *
 */
public class AuthorMetadataSection extends SectionPart {
	
	private Text nameText;
	private Text bioText;
	
	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
		
			formMetadata.propertiesModified();
		}
	};
	
	private Author author;
	private AuthorFormMetadata formMetadata;
	
	public AuthorMetadataSection(FormToolkit toolkit,
								 AuthorFormMetadata formMetadata, 
								 Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED );
		
		this.formMetadata = formMetadata;
		this.author = formMetadata.getAuthor();
		getSection().clientVerticalSpacing = 4;
		getSection().setData("part", this);

		createClient(toolkit);
	}
	
	protected void createClient(FormToolkit toolkit)
	{
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

		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;		
		data.heightHint = 100;
		
		Label labDescription = toolkit.createLabel(leftSection,"");
		labDescription.setText (Messages.getMessage("author_bio"));
		labDescription.setLayoutData (data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 300;
		data.heightHint = 100;
		bioText = toolkit.createText(leftSection,"", SWT.WRAP | SWT.V_SCROLL);
		bioText.setLayoutData (data);
		
		if (author.isUnknown()) {
			nameText.setText(Messages.getMessage("author_unknown"));
			bioText.setText("");
		} else {
			nameText.setText(author.getName());
			bioText.setText(author.getBio());
		}		
	
		if (formMetadata.canUpdate()) {
			nameText.addModifyListener(modifyListener);
			bioText.addModifyListener(modifyListener);
		} else {
			nameText.setEnabled(false);
			bioText.setEnabled(false);
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

	public String getName() {
		
		return nameText.getText();
	}
	
	public String getBio() {

		return bioText.getText();
	}

}
