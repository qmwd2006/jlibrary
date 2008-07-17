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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.core.entities.Document;

public class KeywordsSection extends SectionPart {

	
	public static final int CLIENT_VSPACING = 4;
	private Text keywordsText;
	
	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			
			formMetadata.propertiesModified();
		}
	};
	private DocumentFormMetadata formMetadata;
	private Document document;
	
	public KeywordsSection(FormToolkit toolkit,
			   			   DocumentFormMetadata formMetadata, 
						   Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE );
		
		this.formMetadata = formMetadata;
		this.document = formMetadata.getDocument();
		
		getSection().clientVerticalSpacing = CLIENT_VSPACING;
		getSection().setData("part", this);

		createClient(toolkit);
	}

	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("keywords_section"));
		section.setDescription(Messages.getMessage("keywords_section_description"));

		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		
		client.setLayout(layout);
		section.setClient(client);
		toolkit.paintBordersFor(client);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.heightHint = 200;
		keywordsText = toolkit.createText(client,"");
		keywordsText.setLayoutData (gd);
		if (formMetadata.canUpdate()) {
			keywordsText.addModifyListener(modifyListener);
		}

		keywordsText.setText(document.getMetaData().getKeywords());
	}



	public void dispose() {

		super.dispose();
	}

	public String getKeywords() {
		
		return keywordsText.getText();
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}
}
