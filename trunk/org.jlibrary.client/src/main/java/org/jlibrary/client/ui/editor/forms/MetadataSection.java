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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.i18n.LocaleService;
import org.jlibrary.client.ui.dialogs.AuthorSelectionDialog;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.util.NodeUtils;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataSection extends SectionPart {
	
	static Logger logger = LoggerFactory.getLogger(MetadataSection.class);
	
	private Text titleText;
	private Author author;
	private Text authorText;
	private Button authorButton;
	private Text urlText;
	private Text descriptionText;
	private Text nameText;
	private Scale importance;
	private CCombo languageCombo;
	private CCombo typeCombo;
	private Label languageLabel;

	private Label labImportanceText;
	
	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			
			formMetadata.propertiesModified();
		}
	};

	private VerifyListener numberListener = new VerifyListener() {
		public void verifyText(VerifyEvent e) {
			if (!(e.keyCode == SWT.BS || e.keyCode == SWT.DEL)) {
				try {
					Integer.parseInt(e.text);
				} catch (NumberFormatException nfe) {
					e.doit = false;
				}
			}
		}
	};		
	
	private Document document;
	private DocumentFormMetadata formMetadata;
	private Text positionText;
	
	public MetadataSection(FormToolkit toolkit,
						   DocumentFormMetadata formMetadata, 
						   Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED );
		this.formMetadata = formMetadata;
		this.document = formMetadata.getDocument();
		//initialize(page.getManagedForm());
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
		gridLayout.numColumns = 2;
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
		labName.setText (Messages.getMessage("new_document_dialog_name"));
		labName.setLayoutData (data);
		
		data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
		data.widthHint = 200;
		nameText = toolkit.createText(leftSection,"", SWT.FLAT );
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
		labDescription.setText (Messages.getMessage("new_document_dialog_description"));
		labDescription.setLayoutData (data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
        data.widthHint = 200;
		data.heightHint = 100;
		descriptionText = toolkit.createText(leftSection,"", SWT.WRAP | SWT.V_SCROLL);
		descriptionText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			descriptionText.addModifyListener(modifyListener);
		}
		
		descriptionText.addTraverseListener(new TraverseListener () {
			public void keyTraversed(TraverseEvent e) {
				switch (e.detail) {
					case SWT.TRAVERSE_TAB_NEXT:
						positionText.setFocus();
						e.doit = true;
					case SWT.TRAVERSE_TAB_PREVIOUS: {
						nameText.setFocus();
						e.doit = true;
					}
				}
			}
		});
		
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;		
		Label labPositon = toolkit.createLabel(leftSection,"");
		labPositon.setText (Messages.getMessage("properties_position"));
		labPositon.setLayoutData (data);
		
		data = new GridData();
		data.widthHint = 100;
		positionText = toolkit.createText(leftSection,"", SWT.FLAT);
		positionText.setTextLimit(2);
		toolkit.adapt(positionText, true, true);
		positionText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			positionText.addModifyListener(modifyListener);
			positionText.addVerifyListener(numberListener);
		}		
		
		Composite rightSection = toolkit.createComposite(sectionClient);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		rightSection.setLayoutData(data);
		
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 10;
		rightSection.setLayout(layout);
		toolkit.paintBordersFor(rightSection);

		
		Label labTitle = toolkit.createLabel(rightSection,"");
		labTitle.setText (Messages.getMessage("new_document_dialog_titulo"));
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;	
		labTitle.setLayoutData(data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		titleText = toolkit.createText(rightSection,"",SWT.FLAT);
		titleText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			titleText.addModifyListener(modifyListener);
		}		
		
		Label labAuthor = toolkit.createLabel(rightSection,"");
		labAuthor.setText (Messages.getMessage("new_document_dialog_author"));
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.CENTER;	
		labAuthor.setLayoutData(data);
				
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		authorText = toolkit.createText(rightSection, ""); 
		authorText.setEditable(false);
		authorText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			authorText.addModifyListener(modifyListener);
		}
				
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.verticalAlignment = GridData.CENTER;
		data.widthHint = 30;
		
		authorButton = toolkit.createButton(rightSection,"",SWT.PUSH);
		authorButton.setImage(SharedImages.getImage(SharedImages.IMAGE_USER));
		authorButton.setLayoutData(data);			
		
		Label labURL = toolkit.createLabel(rightSection,"");
		labURL.setText (Messages.getMessage("new_document_dialog_url"));
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;	
		labURL.setLayoutData (data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 200;
		data.horizontalSpan = 2;
		
		urlText = toolkit.createText(rightSection,"");
		urlText.setLayoutData (data);
		if (formMetadata.canUpdate()) {
			urlText.addModifyListener(modifyListener);
		}
						
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;	
		
		Label labLanguage = toolkit.createLabel(rightSection,"");
		labLanguage.setText (Messages.getMessage("document_language"));
		labLanguage.setLayoutData (data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		//data.horizontalSpan = 2;
		
		languageCombo = new CCombo(rightSection, SWT.FLAT);
		languageCombo.setLayoutData (data);
		toolkit.adapt(languageCombo, true, true);
		String[] languages = 
			LocaleService.getInstance().getSupportedDocumentLanguageDescriptions();
		for (int i = 0; i < languages.length; i++) {
			languageCombo.add(languages[i]);
		}
		if (formMetadata.canUpdate()) {
			languageCombo.addModifyListener(modifyListener);
			languageCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					languageLabel.setImage(SharedImages.getImageForLanguage(
							document.getMetaData().getLanguage()));;
				}
			});
		}
		languageCombo.setEditable(false);
		languageCombo.setVisibleItemCount(10);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.verticalAlignment = GridData.CENTER;
		data.widthHint = 20;
		data.heightHint = 12;
		
		languageLabel = toolkit.createLabel(rightSection,"",SWT.FLAT);
		languageLabel.setImage(SharedImages.getImageForLanguage(
				document.getMetaData().getLanguage()));;
		languageLabel.setLayoutData(data);			
		
		
		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		
		Label labImportance = toolkit.createLabel(rightSection,"");
		labImportance.setText (Messages.getMessage("new_document_dialog_importance"));
		labImportance.setLayoutData(data);
	
		data = new GridData();
		data.widthHint = 150;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		
		importance = new Scale(rightSection, SWT.HORIZONTAL | SWT.FLAT);
		toolkit.adapt(importance,true,true);
		importance.setIncrement(1);
		importance.setPageIncrement(1);
		importance.setMinimum(Node.IMPORTANCE_LOWEST.intValue());
		importance.setMaximum(Node.IMPORTANCE_HIGHEST.intValue());
		importance.setSelection(Node.IMPORTANCE_MEDIUM.intValue());
		importance.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 150;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.BEGINNING;
		
		labImportanceText = toolkit.createLabel(rightSection,"");
		labImportanceText.setText(Messages.getMessage("importance_medium"));
		labImportanceText.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;			
		
		Label labType = toolkit.createLabel(rightSection,"");
		labType.setText (Messages.getMessage("properties_type"));
		labType.setLayoutData (data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
	
		typeCombo = new CCombo(rightSection, SWT.FLAT);
		typeCombo.setLayoutData (data);
		toolkit.adapt(typeCombo, true, true);
		String[] types = NodeUtils.getSupportedNodeTypes(); 
		for (int i = 0; i < types.length; i++) {
			typeCombo.add(types[i]);
		}
		if (formMetadata.canUpdate()) {
			typeCombo.addModifyListener(modifyListener);
		}
		typeCombo.setEditable(false);
		typeCombo.setVisibleItemCount(10);
		
		if (NodeUtils.isExtensionUnknown(document) || 
			(document.getTypecode().equals(Types.OTHER))) {
			typeCombo.setEnabled(true);
		} else {
			typeCombo.setEnabled(false);
		}
				
		if (formMetadata.canUpdate()) {
			importance.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
	
					int value = importance.getSelection();
					labImportanceText.setText(NodeUtils.getImportanceText(value));
					formMetadata.propertiesModified();
				}
			});
		}
		
		if (formMetadata.canUpdate()) {
			authorButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					
					Repository repository = RepositoryRegistry.getInstance().
										getRepository(document.getRepository());
					Ticket ticket = repository.getTicket();					
					RepositoryService service = 
						JLibraryServiceFactory.getInstance(repository.getServerProfile()).getRepositoryService();
					List authors = null;
					try {
						authors = service.findAllAuthors(ticket);
					} catch (RepositoryException re) {
						
						logger.error(re.getMessage(),re);						
						ErrorDialog.openError(	new Shell(),
								"ERROR",
								Messages.getMessage("authors_load_error"),
								new Status(IStatus.ERROR,"JLibrary",e.hashCode(),re.getMessage(),re));
					}
					
					
					AuthorSelectionDialog ald = new AuthorSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
					ald.open(authors);
					
					if (ald.getReturnCode() == IDialogConstants.OK_ID) {
						author = (Author)ald.getAuthor();
						
						if (author.equals(Author.UNKNOWN)) {
							authorText.setText(Messages.getMessage(Author.UNKNOWN_NAME));
						} else {
							authorText.setText(author.getName());
						}
						
						formMetadata.propertiesModified();
					}			
				}
			});
		}		
		titleText.setText(document.getMetaData().getTitle());
		author = document.getMetaData().getAuthor();
		if (author.equals(Author.UNKNOWN)) {
			authorText.setText(Messages.getMessage(Author.UNKNOWN_NAME));
		} else {
			authorText.setText(document.getMetaData().getAuthor().getName());
		}
		urlText.setText(document.getMetaData().getUrl());
		nameText.setText(document.getName());
		descriptionText.setText(document.getDescription());
		positionText.setText(document.getPosition().toString());
		if (document.getMetaData().getLanguage() != null) {
			String language = LocaleService.getInstance().getDescriptionForLanguage(
					document.getMetaData().getLanguage());
			if (language != null) {
				languageCombo.select(languageCombo.indexOf(language));
			}
		}
		if (document.getTypecode() != null) {
			String type = NodeUtils.getDescriptionForType(document);
			if (type != null) {
				typeCombo.select(typeCombo.indexOf(type));
			}
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

		if (titleText != null) {
			titleText.setFocus();
		}	
	}

	public String getTitle() {
		
		return titleText.getText();
	}

	public String getAuthorText() {
		
		return authorText.getText();
	}
	
	public Author getAuthor() {
		
		return author;
	}

	public String getDescription() {

		return descriptionText.getText();
	}
	
	public String getName() {
		
		return nameText.getText();
	}

	public String getLanguage() {
		
		String description = languageCombo.getText();
		return LocaleService.getInstance().getLanguageForDescription(description);
	}
	
	public int getImportance() {

		return importance.getSelection();
	}

	public String getUrl() {

		return urlText.getText();
	}
	
	public Integer getTypecode() {

		String description = typeCombo.getText();
		return NodeUtils.getTypeForDescription(description);
	}
	
	public String getPosition() {
		
		return positionText.getText();
	}
}
