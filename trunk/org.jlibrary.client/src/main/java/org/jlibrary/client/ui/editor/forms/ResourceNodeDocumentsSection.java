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
package org.jlibrary.client.ui.editor.forms;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.providers.SimpleNodeContentProvider;
import org.jlibrary.client.ui.repository.providers.SimpleNodeLabelProvider;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This section shows the documents relationed with this resource 
 * 
 * @author martin
 */
public class ResourceNodeDocumentsSection extends SectionPart {
	
	static Logger logger = LoggerFactory.getLogger(ResourceNodeDocumentsSection.class);
	
	private ListViewer viewer;
	
	private ResourceNode resource;
	
	public ResourceNodeDocumentsSection(FormToolkit toolkit,
									   ResourceNodeFormMetadata formMetadata, 
									   Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED );
		this.resource = formMetadata.getResourceNode();

		getSection().clientVerticalSpacing = 4;
		getSection().setData("part", this);

		createClient(toolkit);
	}
	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("resources_docs_section"));
		section.setDescription(Messages.getMessage("resources_docs_section_description"));
	
		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.TOP);
		td.grabHorizontal = true;
		td.colspan = 1;
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
		
		viewer = new ListViewer(leftSection, 
				SWT.MULTI | SWT.H_SCROLL | SWT.FLAT | SWT.BORDER,
				new SimpleNodeLabelProvider(),
				new SimpleNodeContentProvider());	
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 300;
		gd.heightHint = 100;
		data.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		viewer.getControl().setLayoutData(gd);

		TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setWidth(20);
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setWidth(120);
		//column.setText(Messages.getMessage("documents_view_name"));
		
		
		loadDocuments();
	}
	private void loadDocuments() {
		
		Repository repository = 
			RepositoryRegistry.getInstance().getRepository(resource.getRepository());
		Ticket ticket = repository.getTicket();
		RepositoryService service = 
			JLibraryServiceFactory.getInstance(repository.getServerProfile()).getRepositoryService();
		try {
			List nodes = service.findNodesForResource(ticket,resource.getId());
			viewer.setInput(nodes);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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

		if (viewer != null) {
			viewer.getControl().setFocus();
		}	
	}
}
