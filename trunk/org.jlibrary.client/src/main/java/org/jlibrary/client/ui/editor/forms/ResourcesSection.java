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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.ui.dialogs.ResourceSelectionDialog;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.providers.ResourceLabelProvider;
import org.jlibrary.client.ui.repository.providers.ResourcesListContentProvider;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.util.FileUtils;

public class ResourcesSection extends SectionPart {
	
	private ListViewer viewer;
	
	public static final int CLIENT_VSPACING = 4;

	private Button newFileButton;
	private Button newResourceButton;
	private Button deleteButton;

	private DocumentFormMetadata formMetadata;
	private Document document;
	public ArrayList currentResources = new ArrayList();
	
	/**
	 * Internal drop listener
	 * 
	 * @author martin
	 */
	class ResourceDropListener extends ViewerDropAdapter {

		public ResourceDropListener(ListViewer viewer) {
		   
			super(viewer);
		}

		/**
		 * Method declared on ViewerDropAdapter
		 */
		public boolean performDrop(Object data) {
		  
			Object target = getCurrentTarget();
			Object[] toDrop = DNDItems.getItems();
		  
			//cannot drop a node onto itself
			for (int i = 0; i < toDrop.length; i++) {
				if (toDrop[i].equals(target)) {
					return false;
				}
			}
			List resources = Arrays.asList(toDrop);
			createResources(resources);
			DNDItems.clear();
			
			return true;
	   	}
	   
		/**
		 * Method declared on ViewerDropAdapter
		 */
		public boolean validateDrop(Object target, int op, TransferData type) {
		   
			boolean isValid = TextTransfer.getInstance().isSupportedType(type);
			if (!isValid) {
				return false;
			}
					
			Object[] toDrop = DNDItems.getItems();
			for (int i = 0; i < toDrop.length; i++) {
				if (!(toDrop[i] instanceof ResourceNode)) {
					return false;
				}
			} 
		   
		   return true;
		}		
	}		
	
	public ResourcesSection(FormToolkit toolkit,
						    DocumentFormMetadata formMetadata, 
						    Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE );
		
		this.formMetadata = formMetadata;
		this.document = formMetadata.getDocument();
		if (document.getResourceNodes() != null) {
			this.currentResources = new ArrayList(document.getResourceNodes());
		} else {
			this.currentResources = new ArrayList();
		}
		
		getSection().clientVerticalSpacing = CLIENT_VSPACING;
		getSection().setData("part", this);
		
		createClient(toolkit);
	}

	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("resources_section"));
		section.setDescription(Messages.getMessage("resources_section_description"));

		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		
		client.setLayout(layout);
		section.setClient(client);
		toolkit.paintBordersFor(client);

		viewer = new ListViewer(client, 
				SWT.MULTI | SWT.H_SCROLL | SWT.FLAT | SWT.BORDER,
				new ResourceLabelProvider(),
				new ResourcesListContentProvider());	
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		viewer.getControl().setLayoutData(gd);
				
		Composite buttonContainer = toolkit.createComposite(client);
		
		gd = new GridData(GridData.FILL_VERTICAL);
		buttonContainer.setLayoutData(gd);
		
		GridLayout buttonLayout = new GridLayout();
		buttonContainer.setLayout(buttonLayout);
		
		
		gd = new GridData();
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		gd.widthHint = 110;
		newFileButton = toolkit.createButton(buttonContainer,
											 Messages.getMessage("new_document_add_file"),
											 SWT.PUSH);				
		newFileButton.setLayoutData(gd);
        newFileButton.setToolTipText(Messages.getMessage("tooltip_add_resource_file"));

		gd = new GridData();
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		gd.widthHint = 110;
		newResourceButton = toolkit.createButton(buttonContainer,
											 	 Messages.getMessage("new_document_add_resource"),
											 	 SWT.PUSH);				
		newResourceButton.setLayoutData(gd);
        newResourceButton.setToolTipText(Messages.getMessage("tooltip_add_resource"));
		
		
		gd = new GridData();
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		gd.widthHint = 110;
		deleteButton = toolkit.createButton(buttonContainer,
				  							Messages.getMessage("new_document_delete_resource"),
											SWT.PUSH);
        deleteButton.setToolTipText(Messages.getMessage("tooltip_remove_resource"));
		deleteButton.setLayoutData(gd);	
		deleteButton.setEnabled(false);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				if (event.getSelection().isEmpty()) {
					deleteButton.setEnabled(false);
				} else {
					deleteButton.setEnabled(true);
				}
			}
		});
		
		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDropSupport(operations,
							  types,
							  new ResourceDropListener(viewer));
		
		
		if (formMetadata.canUpdate()) {
			newFileButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					
					newFileResource();
				}
			});

			newResourceButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					
					newResource();
				}
			});
			
			
			deleteButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					
					IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
					Object[] resources = selection.toArray();
					for (int i = 0; i < resources.length; i++) {
						ResourceNode resource = (ResourceNode)resources[i];
						document.removeResource(resource);
						currentResources.remove(resource);
						
						formMetadata.closeEditors(resource);
						viewer.remove(resource);
					}
					formMetadata.editorUpdated();
				}				
			});	
		}
		viewer.setInput(currentResources);
	}
	
	private void newFileResource() {

		Repository repository = 
			RepositoryRegistry.getInstance().getRepository(document.getRepository());

		Shell shell = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fd = new FileDialog(shell, SWT.MULTI | SWT.OPEN);
		String filter = 
			ClientConfig.getValue(ClientConfig.NEW_RESOURCE_DIRECTORY);
		if (filter != null) {
			File f = new File(filter);
			fd.setFilterPath(f.getAbsolutePath());
		}
		fd.open();
		String[] files = fd.getFileNames();
		filter = fd.getFilterPath();
		for (int i = 0; i < files.length; i++) {
			File file = new File(filter + System.getProperty("file.separator") + files[i]);
			if (i == 0) {
				ClientConfig.setValue(ClientConfig.NEW_RESOURCE_DIRECTORY,
									  file.getParentFile().getAbsolutePath());
			}
			if (file.getName().equals(FileUtils.getExternalFile(repository,document).getName())) {
				if (files.length == 1) {
					return;
				} else {
					continue;
				}
			}
			
			ResourceNode resource = new ResourceNode();
			resource.setParent(document.getId());
			resource.setName(file.getName());
			resource.setPath(file.getAbsolutePath());
			resource.setTypecode(Types.getTypeForFile(file.getAbsolutePath()));
			
			document.addResource(resource);
			currentResources.add(resource);
			viewer.add(resource);
		}
		viewer.refresh();
		formMetadata.editorUpdated();
	}

	private void newResource() {

		Repository repository = RepositoryRegistry.getInstance().getRepository(
				document.getRepository());
		
		ResourceSelectionDialog rsd = ResourceSelectionDialog.getInstance();
		rsd.open(repository);
		if (rsd.getReturnCode() == IDialogConstants.CANCEL_ID) {
			return;
		}
		List resources = rsd.getSelectedResources();
		createResources(resources);
	}
	
	private void createResources(Collection resources) {
		
		Iterator it = resources.iterator();
		while (it.hasNext()) {
			ResourceNode resource = (ResourceNode)it.next();

			if (currentResources.contains(resource)) {
				continue;
			}
			document.addResource(resource);
				
			currentResources.add(resource);
			viewer.add(resource);
		}
		viewer.refresh();
		formMetadata.editorUpdated();	
	}	
	
	public void dispose() {

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}
	
	/**
	 * Updates the resources list
	 */
	public void updateResources() {
		
		Document document = (Document)
			EntityRegistry.getInstance().getNode(
					this.document.getId(),
					this.document.getRepository());
		currentResources.clear();
		currentResources.addAll(document.getResourceNodes());
		if (viewer != null) {
			if (!viewer.getControl().isDisposed()) {
				viewer.refresh();
			}
		}
	}
}
