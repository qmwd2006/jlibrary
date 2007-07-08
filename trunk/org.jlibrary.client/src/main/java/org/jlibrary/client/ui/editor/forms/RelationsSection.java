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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.JFacePreferences;
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
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.preferences.JLibraryRepositoryPreferences;
import org.jlibrary.client.ui.dialogs.DocumentSelectionDialog;
import org.jlibrary.client.ui.dnd.DNDItems;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.relations.RelationsContentProvider;
import org.jlibrary.client.ui.relations.RelationsLabelProvider;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Relation;
import org.jlibrary.core.entities.Repository;

/**
 * Relations management UI section
 * 
 * @author martin
 */
public class RelationsSection extends SectionPart {
	
	private ListViewer viewer;
	
	public static final int CLIENT_VSPACING = 4;

	private Button newRelationButton;
	private Button deleteRelationButton;

	private DocumentFormMetadata formMetadata;
	private Document document;
	public ArrayList currentRelations = new ArrayList();

	/**
	 * Internal drop listener
	 * 
	 * @author martin
	 */
	class RelationDropListener extends ViewerDropAdapter {

		public RelationDropListener(ListViewer viewer) {
		   
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
			List relations = Arrays.asList(toDrop);
			createRelations(relations);
			updateRelations();
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
				if (!(toDrop[i] instanceof Document)) {
					return false;
				}
			} 
		   
		   return true;
		}		
	}	
	
	public RelationsSection(FormToolkit toolkit,
						    DocumentFormMetadata formMetadata, 
						    Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE );
		
		this.formMetadata = formMetadata;
		this.document = formMetadata.getDocument();
		if (document.getRelations() != null) {
			this.currentRelations = new ArrayList(document.getRelations());
		} else {
			this.currentRelations = new ArrayList();
		}
		
		getSection().clientVerticalSpacing = CLIENT_VSPACING;
		getSection().setData("part", this);
		
		createClient(toolkit);
	}

	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("relations_section"));
		section.setDescription(Messages.getMessage("relations_section_description"));

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
				new RelationsLabelProvider(),
				new RelationsContentProvider());	
	
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
		newRelationButton = toolkit.createButton(buttonContainer,
											 	 Messages.getMessage("relations_section_add"),
											 	 SWT.PUSH);				
		newRelationButton.setLayoutData(gd);
        newRelationButton.setToolTipText(Messages.getMessage("relations_section_add_tooltip"));
		
		
		gd = new GridData();
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		gd.widthHint = 110;
		deleteRelationButton = toolkit.createButton(buttonContainer,
				  							Messages.getMessage("relations_section_del"),
											SWT.PUSH);
        deleteRelationButton.setToolTipText(Messages.getMessage("relations_section_del_tooltip"));
		deleteRelationButton.setLayoutData(gd);	
		deleteRelationButton.setEnabled(false);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				if (event.getSelection().isEmpty()) {
					deleteRelationButton.setEnabled(false);
				} else {
					deleteRelationButton.setEnabled(true);
				}
			}
		});
		
		
		
		// Add drag & drop support
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDropSupport(operations,
							  types,
							  new RelationDropListener(viewer));

		
		if (formMetadata.canUpdate()) {

			newRelationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					
					newRelation();
					updateRelations();
				}
			});
			
			
			deleteRelationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					
					IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
					Object[] relations = selection.toArray();
					for (int i = 0; i < relations.length; i++) {
						Document relation = (Document)relations[i];
						removeRelation(document,relation);
						currentRelations.remove(relation);
						viewer.remove(relation);
					}
					formMetadata.editorUpdated();
					updateRelations();
				}				
			});	
		}
		viewer.setInput(currentRelations);
	}

	public void removeRelation(Document source, Document destination) {
					
		// Remove relation
		Relation relation = new Relation();
		boolean bidirectional = JFacePreferences.getPreferenceStore().getBoolean(JLibraryRepositoryPreferences.P_BIDIRECTIONAL_RELATIONS);
		relation.setBidirectional(bidirectional);
		relation.setDestinationNode(destination);
		source.removeRelation(relation);
			
		if (bidirectional) {
			relation = new Relation();
			relation.setBidirectional(bidirectional);
			relation.setDestinationNode(source);
			destination.removeRelation(relation);
		}
			
		// Update views
		JLibraryPlugin.findRelationsView().refresh();
	}	
	
	private void newRelation() {

		Repository repository = 
			RepositoryRegistry.getInstance().getRepository(
					document.getRepository());
		DocumentSelectionDialog dsd = DocumentSelectionDialog.getInstance();
		dsd.open(repository);
		if (dsd.getReturnCode() == IDialogConstants.CANCEL_ID) {
			return;
		}
		
		List relations = dsd.getSelectedDocuments();
		createRelations(relations);
	}
	
	private void createRelations(Collection relations) {
		
		Iterator it = relations.iterator();
		while (it.hasNext()) {
			Document relation = (Document) it.next();
			if (document.equals(relation) || currentRelations.contains(relation)) {
				continue;
			}
			createRelation(document,relation);
		}
		viewer.refresh();
		formMetadata.editorUpdated();		
	}
	
	private void createRelation(Document source, 
							    Document destination) {
		
		if (source.equals(destination)) {
			return;
		}
		
		// Create the relation
		Relation relation = new Relation();
		boolean bidirectional = JFacePreferences.getPreferenceStore().getBoolean(JLibraryRepositoryPreferences.P_BIDIRECTIONAL_RELATIONS);
		relation.setBidirectional(bidirectional);
		relation.setDestinationNode(destination);
		source.addRelation(relation);
		
		if (bidirectional) {
			relation = new Relation();
			relation.setBidirectional(bidirectional);
			relation.setDestinationNode(source);
			destination.addRelation(relation);
		}
		
		// Update views
		JLibraryPlugin.findRelationsView().refresh();
		currentRelations.add(destination);
		viewer.add(destination);
	}	
	
	public void dispose() {

		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}
	
	/**
	 * Updates the relations graph
	 */
	public void updateRelations() {
		
		GenericEditor editor = (GenericEditor)JLibraryPlugin.getActiveEditor();
		editor.refreshRelations();
	}
}
