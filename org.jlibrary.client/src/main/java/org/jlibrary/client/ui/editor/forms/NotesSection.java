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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.list.ListViewer;
import org.jlibrary.client.ui.notes.NotesContentProvider;
import org.jlibrary.client.ui.notes.NotesLabelProvider;
import org.jlibrary.client.ui.notes.wizard.NewNoteWizard;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Note;

public class NotesSection extends SectionPart {

	public static final int CLIENT_VSPACING = 4;
	
	private ListViewer viewer;
	private Note note;
	private Button newNoteButton;
	private Button deleteNoteButton;
	private Button updateNoteButton;
	//private Text notesText;

	private DocumentFormMetadata formMetadata;

	private Document document;

	protected String newNoteText;
	
	public NotesSection(FormToolkit toolkit,
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

		section.setText(Messages.getMessage("notes_section"));
		section.setDescription(Messages.getMessage("notes_section_description"));

		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.TOP);
		td.grabHorizontal = true;
		td.colspan = 2;
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
			    SharedImages.getImage(SharedImages.IMAGE_NOTE),
				SWT.FLAT | SWT.BORDER);
		viewer.setContentProvider(new NotesContentProvider());
		viewer.setLabelProvider(new NotesLabelProvider());
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalSpan = 2;
		viewer.getControl().setLayoutData(gd);
		
		
		Composite buttonContainer = toolkit.createComposite(client);		
		gd = new GridData(GridData.FILL_VERTICAL);
		buttonContainer.setLayoutData(gd);
		
		GridLayout buttonLayout = new GridLayout();
		buttonContainer.setLayout(buttonLayout);

		gd = new GridData();
		gd.widthHint = 120;
		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		newNoteButton = toolkit.createButton(buttonContainer,"",SWT.PUSH);
		newNoteButton.setText(Messages.getMessage("notes_new"));
        newNoteButton.setToolTipText(Messages.getMessage("notes_new_tooltip"));
		newNoteButton.setLayoutData(gd);
		
		gd = new GridData();
		gd.widthHint = 120;
		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		updateNoteButton = toolkit.createButton(buttonContainer,"",SWT.PUSH);
		updateNoteButton.setText(Messages.getMessage("notes_update"));
        updateNoteButton.setToolTipText(Messages.getMessage("notes_update_tooltip"));
		updateNoteButton.setLayoutData(gd);	
		updateNoteButton.setEnabled(false);

		gd = new GridData();
		gd.widthHint = 120;
		gd.grabExcessVerticalSpace = false;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		deleteNoteButton = toolkit.createButton(buttonContainer,"",SWT.PUSH);
		deleteNoteButton.setText(Messages.getMessage("notes_delete"));
        deleteNoteButton.setToolTipText(Messages.getMessage("notes_delete_tooltip"));
		deleteNoteButton.setLayoutData(gd);
		deleteNoteButton.setEnabled(false);
		
		
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if (selection.isEmpty()) {
					deleteNoteButton.setEnabled(false);
					updateNoteButton.setEnabled(false);
				} else {
					deleteNoteButton.setEnabled(true);
					updateNoteButton.setEnabled(true);
				}
			}
		});

		if (formMetadata.canUpdate()) {
			
			deleteNoteButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
					Object[] notes = selection.toArray();
					for (int i = 0; i < notes.length; i++) {
						Note note = (Note)notes[i];
						note.setNode(document);
						document.removeNote(note);
						viewer.remove(note);
					}
					formMetadata.editorUpdated();
				}
			});

			newNoteButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {

				    NewNoteWizard nnw = new NewNoteWizard(document.getRepository());
				    WizardDialog wd = new WizardDialog(getSection().getShell(),nnw) {
						protected Control createDialogArea(Composite parent) {
							Control control = super.createDialogArea(parent);
							getShell().setImage(SharedImages.getImage(
									SharedImages.IMAGE_JLIBRARY));
							return control;
						}
				    };
				    wd.open();
					
					 if (wd.getReturnCode() == IDialogConstants.OK_ID) {
					 	note = nnw.getNote();
					 	note.setNode(document);
					 	document.addNote(note);
					 	viewer.add(note);
					 	formMetadata.editorUpdated();
					 }
				}
			});
			
			updateNoteButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {

					IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
				    Note note = (Note)selection.getFirstElement();
				    
				    //Keep a copy of the original note... 
				    //will be used later to delete the original note from the repository.
				    Note origNote = new Note();
				    origNote.setNote(note.getNote());
				    origNote.setId(note.getId());
				    origNote.setNode(document);
				    
				    NewNoteWizard nnw = 
				    	new NewNoteWizard(note,document.getRepository());
				    WizardDialog wd = new WizardDialog(getSection().getShell(),nnw) {
						protected Control createDialogArea(Composite parent) {
							Control control = super.createDialogArea(parent);
							getShell().setImage(SharedImages.getImage(
									SharedImages.IMAGE_JLIBRARY));
							return control;
						}
				    };
				    wd.open();
					
					 if (wd.getReturnCode() == IDialogConstants.OK_ID) {
						 note = nnw.getNote();		
						 //remove the original note
						 document.removeNote(origNote);
						 note.setNode(document);
						 //add the updated note as a new note
						 document.addNote(note);
					 	 viewer.refresh(note);
					 	 formMetadata.editorUpdated();
					 }
				}
			});			
		}
		
		viewer.setInput(document);		
	}

	public void dispose() {

		super.dispose();
	}
	
	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}
}
