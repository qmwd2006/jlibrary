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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.ui.categories.dialogs.CategorySelectionDialog;
import org.jlibrary.client.ui.categories.providers.CategoriesLabelProvider;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.repository.RepositoryHelper;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;

public class CategoriesSection extends SectionPart {
	

	public static final int CLIENT_VSPACING = 4;
	private ListViewer categoriesViewer;
	private Button addCategory;
	private Button removeCategory;
	private Category unknownCategory;
	private DocumentFormMetadata formMetadata;
	private Document document;
	
	public CategoriesSection(FormToolkit toolkit,
			   				 DocumentFormMetadata formMetadata, 
							 Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE );
		
		getSection().clientVerticalSpacing = CLIENT_VSPACING;
		getSection().setData("part", this);
		
		this.formMetadata = formMetadata;
		this.document = formMetadata.getDocument();
		
		createClient(toolkit);
	}

	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("categories_section"));
		section.setDescription(Messages.getMessage("categories_section_description"));

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

		categoriesViewer = new ListViewer(client, 
										  SharedImages.getImage(SharedImages.IMAGE_CATEGORY),
										  SWT.MULTI | SWT.H_SCROLL | SWT.FLAT | SWT.BORDER);
		categoriesViewer.setLabelProvider(new CategoriesLabelProvider());
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalSpan = 2;
		categoriesViewer.getControl().setLayoutData(gd);
		
		Composite buttonContainer = toolkit.createComposite(client);
		
		gd = new GridData(GridData.FILL_VERTICAL);
		buttonContainer.setLayoutData(gd);
		
		GridLayout buttonLayout = new GridLayout();
		buttonContainer.setLayout(buttonLayout);

		
		gd = new GridData();
		gd.grabExcessVerticalSpace = false;
		gd.widthHint = 110;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		addCategory = toolkit.createButton(buttonContainer,"",SWT.PUSH);
		addCategory.setText(Messages.getMessage("categories_section_add"));
		addCategory.setToolTipText(Messages.getMessage("categories_section_add_tooltip"));
		addCategory.setLayoutData (gd);		

		gd = new GridData ();
		gd.widthHint = 110;
		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		removeCategory = toolkit.createButton(buttonContainer, "",SWT.PUSH);
		removeCategory.setText(Messages.getMessage("categories_section_del"));
		removeCategory.setToolTipText(Messages.getMessage("categories_section_del_tooltip"));
		removeCategory.setLayoutData (gd);
		
		if (formMetadata.canUpdate()) {
			addCategory.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {

					Repository repository = RepositoryRegistry.getInstance().getRepository(document.getRepository());
	
					CategorySelectionDialog cld = CategorySelectionDialog.getInstance(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
					cld.open(repository);
					
					if (cld.getReturnCode() == IDialogConstants.OK_ID) {
						Category category = cld.getCategory();
						
						if (!category.equals(unknownCategory)) {
							categoriesViewer.remove(unknownCategory);
							document.removeCategory(unknownCategory);
						} else {
							TableItem items[] = categoriesViewer.getTable().getItems();
							for (int i = 0; i < items.length; i++) {
								Category item = (Category) items[i].getData();
								document.removeCategory(item);
							}
							categoriesViewer.getTable().removeAll();
						}
						document.addCategory(category);
						categoriesViewer.add(category);
	
						formMetadata.editorUpdated();		
					}
				}
			});
			
			removeCategory.addSelectionListener(new SelectionAdapter() {
	
				public void widgetSelected(SelectionEvent e) {
				    
					TableItem[] items = categoriesViewer.getTable().getSelection();
					for (int i = 0; i < items.length; i++) {
						TableItem item = items[i];
						Category category = (Category)item.getData();
						document.removeCategory(category);
						categoriesViewer.remove(category);
					}			
					if (categoriesViewer.getTable().getItems().length == 0) {
						document.addCategory(unknownCategory);
						categoriesViewer.add(unknownCategory);
					}
					
					formMetadata.editorUpdated();
				}
			});	
		}
		
		loadCategories();
		loadUnknownCategory();
	}

	public void updateCategories() {
		
		loadCategories();
	}

	private void loadCategories() {

		Repository repository = RepositoryRegistry.getInstance().
									getRepository(document.getRepository());
		final Ticket ticket = repository.getTicket();
		ServerProfile profile = repository.getServerProfile();
		final RepositoryService service = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			
		JobTask jobTask = new JobTask(Messages.getMessage("copy_job_name")) {

			private List categories = new ArrayList();
			
			public IStatus run(IProgressMonitor monitor) 
										throws OperationCanceledException, 
											   JobTaskException {
				categories.clear();
				IStatus status = Status.OK_STATUS;
					try {
						categories = service.findCategoriesForNode(
													ticket, document.getId());
					} catch (Exception e) {
						throw new JobTaskException(e);
					}
				return status;
			
			}			
			public void postJobTasks() throws JobTaskException {

				categoriesViewer.getTable().removeAll();
				categoriesViewer.add(categories.toArray());
			}
		};
		
		jobTask.setSystemTask(true);
		new JobRunner().run(jobTask);
	}

	public void dispose() {

		super.dispose();
	}

	private void loadUnknownCategory() {
		
		Repository repository = 
			RepositoryRegistry.getInstance().getRepository(
					document.getRepository());
		
		unknownCategory = 
			RepositoryHelper.findUnknownCategory(repository);		
	}
	
	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}
}
