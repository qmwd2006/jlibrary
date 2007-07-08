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
package org.jlibrary.client.ui.versions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.DocumentVersion;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionBrowsingDialog extends ResizableDialog {
	
	static Logger logger = LoggerFactory.getLogger(VersionBrowsingDialog.class);
	
	// SWT controls
	private CompareViewerSwitchingPane editorVersionPane;
	private CompareViewerSwitchingPane selectedVersionPane;
	private Button fCommitButton;
	private TreeViewer fEditionTree;
	private CompareViewerPane fEditionPane;
	private Document document;
	
	private FileEditorInput currentEditorInput;
	private Document currentDocument;
	
	public VersionBrowsingDialog(Shell parent, Document document) {
		
		super(parent);

		this.document = document;
	}
		
 	/* (non Javadoc)
 	 * Creates SWT control tree.
 	 */
	protected synchronized Control createDialogArea(Composite parent2) {
		
		Composite parent= (Composite) super.createDialogArea(parent2);

		getShell().setText(Messages.getMessage("restore_dialog_title"));
		getShell().setImage(SharedImages.getImage(SharedImages.IMAGE_RESTORE_VERSION));
		
		Splitter vsplitter= new Splitter(parent,  SWT.VERTICAL);
		vsplitter.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
					| GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL));

		fEditionPane= new CompareViewerPane(vsplitter, SWT.BORDER | SWT.FLAT);
		fEditionPane.setText(Messages.getMessage("restore_dialog_versions"));

		fEditionTree= new TreeViewer(fEditionPane, SWT.H_SCROLL + SWT.V_SCROLL);
		fEditionTree.setContentProvider(new CompareContentProvider());
		fEditionTree.setLabelProvider(new CompareLabelProvider());
		fEditionTree.setInput(createDocumentVersionNodes(document));
		
		fEditionPane.setContent(fEditionTree.getTree());		

		Splitter hsplitter= new Splitter(vsplitter,  SWT.HORIZONTAL);
		
		selectedVersionPane= new CompareViewerSwitchingPane(hsplitter, SWT.BORDER | SWT.FLAT) {
			protected Viewer getViewer(Viewer oldViewer, Object input) {
				return null;	
			}
		};
		selectedVersionPane.setText(Messages.getMessage("version_selection"));

		editorVersionPane= new CompareViewerSwitchingPane(hsplitter, SWT.BORDER | SWT.FLAT) {
			protected Viewer getViewer(Viewer oldViewer, Object input) {
				return null;	
			}
		};		
		editorVersionPane.setText(Messages.getMessage("version_current"));
		
		openDocument(editorVersionPane,document);


		vsplitter.setWeights(new int[] { 30, 70 });

		applyDialogFont(parent);
		
		fEditionTree.addSelectionChangedListener(new ISelectionChangedListener() {
			/**
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {

				StructuredSelection selection = (StructuredSelection)event.getSelection();
				if (selection.isEmpty()) {
					selectedVersionPane.setViewer(new NullViewer(selectedVersionPane));
					fCommitButton.setEnabled(false);
				}
				DocumentVersionNode node = (DocumentVersionNode)selection.getFirstElement();
				if (node.getChildren().size() > 0) {
					//It's a root
					selectedVersionPane.setViewer(new NullViewer(selectedVersionPane));
					fCommitButton.setEnabled(false);
					return;
				}
				// It's a version
				openVersion(selectedVersionPane, node);
				
				fCommitButton.setEnabled(true);
			}

		});
		
		return parent;
	}	
	

	/**
	 * @param editorVersionPane2
	 * @param document2
	 */
	private void openDocument(CompareViewerSwitchingPane editorPane, Document document) {
		
		FileEditorInput fei = null;
		try {
			fei = FileEditorInput.createFileEditorInput(document);
		} catch (Exception e) {
			
            logger.error(e.getMessage(),e);
		}
		editorPane.setViewer(new DocumentViewer(editorPane,fei,document));
	}

	private void openVersion(CompareViewerSwitchingPane editorPane,
							 DocumentVersionNode node) {
		
		try {
			currentDocument = createDocumentFromVersion(node.getVersion());
			currentEditorInput = FileEditorInput.createFileEditorInput(currentDocument,node.getVersion());
		} catch (Exception e) {
			
            logger.error(e.getMessage(),e);
		}
		
		editorPane.setViewer(new DocumentViewer(editorPane,currentEditorInput,currentDocument));
	}

	private DocumentVersionNode[] createDocumentVersionNodes(Document document) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			Repository repository = RepositoryRegistry.getInstance().
									getRepository(document.getRepository());
			ServerProfile serverProfile = repository.getServerProfile();
			RepositoryService service = 
				JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
			Ticket ticket = repository.getTicket();
			
			List versions = service.getVersions(ticket, document.getId());
			
			HashMap fechas = new HashMap();
			Iterator it = versions.iterator();
			while (it.hasNext()) {
				DocumentVersion version = (DocumentVersion) it.next();
				Date date = version.getDate();
				String fecha = sdf.format(date);
				if (!fechas.containsKey(fecha)) {
					DocumentVersionNode node = new DocumentVersionNode();
					node.setDate(date);
					fechas.put(fecha,node);
				}
				
				DocumentVersionNode node = (DocumentVersionNode)fechas.get(fecha);
				DocumentVersionNode nodeVersion = new DocumentVersionNode();
				nodeVersion.setDate(date);
				nodeVersion.setVersion(version);
				nodeVersion.setParent(node);
				node.addVersion(nodeVersion);
				
			}
			return (DocumentVersionNode[])fechas.values().toArray(new DocumentVersionNode[]{});
		} catch (Exception e) {

			e.printStackTrace();
			return new DocumentVersionNode[]{};
		}
	}


	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		String buttonLabel= Messages.getMessage("restore_dialog_accept");

		// a 'Cancel' and a 'Add/Replace' button
		fCommitButton= createButton(parent, IDialogConstants.OK_ID, buttonLabel, true);
		fCommitButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Overidden to disable dismiss on double click in compare mode.
	 * @since 2.0
	 */
	protected void okPressed() {

		// Restore selected version
		
		try {
			IEditorPart currentEditor = JLibraryPlugin.getActivePage().findEditor(currentEditorInput);
			if (currentEditor != null) {
				JLibraryPlugin.getActivePage().closeEditor(currentEditor,false);
			}
			
			currentDocument.setPath(document.getPath());
			
			// Register that a version of a document is opened. This is important because if the version is 
			// saved, then we can take oportune actions
			EditorVersionRegistry.addDocumentVersion(currentDocument, document);
			
			IEditorRegistry registry = 
				PlatformUI.getWorkbench().getEditorRegistry();
			IEditorDescriptor desc = 
				registry.getDefaultEditor(document.getPath()); 			
			JLibraryEditor openedEditorPart = (JLibraryEditor)
				JLibraryPlugin.getActivePage().openEditor(
						currentEditorInput,desc.getId());
			openedEditorPart.setDirty(true);
		} catch (Exception e) {
			
            logger.error(e.getMessage(),e);
		}
		
		super.okPressed();
	}


	
	/**
	 * @param version
	 * @return
	 */
	private Document createDocumentFromVersion(DocumentVersion version) {
	
		Repository repository = RepositoryRegistry.getInstance().getRepository(version.getRepository());
		ServerProfile serverProfile = repository.getServerProfile();
		RepositoryService service = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		Ticket ticket = repository.getTicket();

		
		Document newDocument = new Document();
		
		DocumentMetaData metadata = new DocumentMetaData();
		try {
			Author author = service.findAuthorById(ticket,version.getAuthor());
			if (author == null) {
				metadata.setAuthor(Author.UNKNOWN);
			} else {
				metadata.setAuthor(author);
			}
		} catch (AuthorNotFoundException anfe) {
			metadata.setAuthor(Author.UNKNOWN);
		} catch (RepositoryException e) {
			metadata.setAuthor(Author.UNKNOWN);
		}
		metadata.setDate(version.getDocumentDate());
		metadata.setId(document.getMetaData().getId());
		metadata.setKeywords(version.getKeywords());
		metadata.setTitle(version.getTitle());
		metadata.setUrl(version.getUrl());
		newDocument.setMetaData(metadata);
		
		newDocument.setPath(document.getPath());
		newDocument.setCreator(version.getCreator());
		newDocument.setDate(document.getDate());
		newDocument.setDescription(version.getDescription());
		newDocument.setImportance(version.getImportance());
		newDocument.setName(version.getName());
		newDocument.setTypecode(version.getTypecode());
		newDocument.setRepository(version.getRepository());
		
		newDocument.setNotes(document.getNotes());
		newDocument.setRelations(document.getRelations());
		
		newDocument.setId(version.getNode());
		newDocument.setParent(document.getParent());
		
		return newDocument;
	}
}
