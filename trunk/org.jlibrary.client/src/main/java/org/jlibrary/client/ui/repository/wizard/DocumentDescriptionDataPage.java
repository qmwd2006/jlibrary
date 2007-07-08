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
package org.jlibrary.client.ui.repository.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.extraction.MetaDataExtractor;
import org.jlibrary.client.ui.dialogs.ToolTipDialog;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.search.extraction.HeaderMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Wizard data page showing document description data
 */
public class DocumentDescriptionDataPage extends WizardPage {

	static Logger logger = LoggerFactory.getLogger(DocumentDescriptionDataPage.class);
	
	private Text pathText;
	private Button browseButton;
	private Text nameText;
	private Text descriptionText;
	private Scale importance;
	private Label labImportanceText;
	private Button externalButton;
	private Button referenceButton;
	private HeaderMetaData metadata;
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			
			checkButtonsEnabled();
		}
	};

	
    public DocumentDescriptionDataPage(String pageName, String description) {
        
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(false);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_NEW_DOCUMENT_WIZARD));        
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite outer) {
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = 250;
        outer.setLayoutData(data);

        Composite parent = new Composite(outer, SWT.NONE);
        
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		parent.setLayout (gridLayout);

		Label labPath = new Label (parent, SWT.NONE);
		labPath.setText (Messages.getMessage("new_document_dialog_path"));
		
		
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labPath.setLayoutData (data);

		pathText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 325;
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		pathText.setLayoutData (data);

		browseButton = new Button(parent, SWT.NONE);
		browseButton.setImage(SharedImages.getImage(SharedImages.IMAGE_DIRECTORY));
		data = new GridData();
		browseButton.setLayoutData(data);		

		
		Label labName = new Label (parent, SWT.NONE);
		labName.setText (Messages.getMessage("new_document_dialog_name"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labName.setLayoutData (data);
		
		nameText = new Text (parent, SWT.BORDER);
		data = new GridData ();
		data.widthHint = 300;
		data.horizontalSpan = 3;
		data.horizontalAlignment = GridData.FILL;
		nameText.setLayoutData (data);

		Label labDescription = new Label (parent, SWT.NONE);
		labDescription.setText (Messages.getMessage("new_document_dialog_description"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labDescription.setLayoutData (data);
		
		descriptionText = new Text (parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData ();
		data.widthHint = 370;
		data.heightHint = 30;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		descriptionText.setLayoutData (data);
		
		Label labImportance = new Label (parent, SWT.NONE);
		labImportance.setText (Messages.getMessage("new_document_dialog_importance"));
		data = new GridData ();
		data.horizontalSpan = 1;
		data.horizontalAlignment = GridData.BEGINNING;
		labImportance.setLayoutData (data);
		
		importance = new Scale(parent, SWT.HORIZONTAL);
		importance.setIncrement(1);
		importance.setPageIncrement(1);
		importance.setMinimum(Node.IMPORTANCE_LOWEST.intValue());
		importance.setMaximum(Node.IMPORTANCE_HIGHEST.intValue());
		importance.setSelection(Node.IMPORTANCE_MEDIUM.intValue());
		data = new GridData ();
		data.horizontalSpan = 1;
		data.widthHint = 300;
		data.horizontalAlignment = GridData.BEGINNING;
		importance.setLayoutData(data);
		
		labImportanceText = new Label (parent, SWT.NONE);
		labImportanceText.setText(Messages.getMessage("importance_medium"));
		data = new GridData ();
		data.horizontalSpan = 2;
		data.widthHint = 90;
		data.horizontalAlignment = GridData.BEGINNING;
		labImportanceText.setLayoutData(data);
				
		externalButton = new Button (parent, SWT.CHECK );
		externalButton.setText(Messages.getMessage("new_document_dialog_external"));
		data = new GridData ();
		data.horizontalAlignment = GridData.END;
		externalButton.setLayoutData (data);		
		
		referenceButton = new Button (parent, SWT.CHECK );
		referenceButton.setText(Messages.getMessage("new_document_dialog_reference"));
		data = new GridData ();
		data.horizontalAlignment = GridData.END;
		referenceButton.setLayoutData (data);		

		externalButton.setEnabled(false);
		referenceButton.setEnabled(false);

		descriptionText.addTraverseListener(new TraverseListener () {
			public void keyTraversed(TraverseEvent e) {
				switch (e.detail) {
					case SWT.TRAVERSE_TAB_NEXT:
						importance.setFocus();
						e.doit = true;
					case SWT.TRAVERSE_TAB_PREVIOUS: {
						nameText.setFocus();
						e.doit = true;
					}
				}
			}
		});
		
		importance.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				int value = importance.getSelection();
				setImportanceText(value);
			}
		});
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
								
				FileDialog fd = new FileDialog(getShell());
				String filter = ClientConfig.getValue(ClientConfig.NEW_DOCUMENT_DIRECTORY);
				if (filter != null) {
					File f = new File(filter);
					fd.setFilterPath(f.getAbsolutePath());
				}
				String dir = fd.open();

				if (dir == null) {
					pathText.setText("");
				} else {
					Repository repository = JLibraryPlugin.getCurrentRepository();
					if (repository.getRepositoryConfig().isExtractMetadata()) {
						if (ToolTipDialog.mustOpenToolTip(ToolTipDialog.TIP_EXTRACTION)) {
							ToolTipDialog warning = 
								new ToolTipDialog(getShell(),ToolTipDialog.TIP_EXTRACTION);
							warning.open();
						}
					}
					File file = new File(dir);
					if (file.exists()) {
						ClientConfig.setValue(ClientConfig.NEW_DOCUMENT_DIRECTORY,
								  file.getAbsolutePath());		
					}
					pathText.setText(dir);
					parseFile(pathText.getText());
				}
				
				checkButtonsEnabled();
			}
	
		});
				
		nameText.addFocusListener(new FocusAdapter() {
			private boolean noNameText = true;

			public void focusLost(FocusEvent e) {
				
				if (noNameText) {
					noNameText = false;
					if (descriptionText.getText().equals("")) {
						descriptionText.setText(nameText.getText());
						descriptionText.selectAll();
					}
					DocumentMetadataPage metadataPage = 
						((NewDocumentWizard)getWizard()).getMetadataPage();
					if ((metadataPage.getTitle() == null) ||
						(metadataPage.getTitle().equals(""))) {
						metadataPage.setTitle(nameText.getText());
					}
				}
				
				super.focusLost(e);
			}
		});
		
		nameText.addModifyListener(modifyListener);
		descriptionText.addModifyListener(modifyListener);
		pathText.addModifyListener(modifyListener);		
		
		externalButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent arg0) {
				
				referenceButton.setSelection(false);
			}

		});
		referenceButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent arg0) {
				
				externalButton.setSelection(false);
			}

		});
        
        setControl(parent);
    }
    

    
    private void checkButtonsEnabled() {
        
    	setPageComplete(false);

    	if (pathText.getText().trim().equals("")) {
			setMessage(Messages.getMessage("new_document_insert_path"), IMessageProvider.ERROR);
			return;
		}

		if (nameText.getText().trim().equals("")) {
			setMessage(Messages.getMessage("new_document_insert_name"), IMessageProvider.ERROR);
			return;
		}

		if (descriptionText.getText().trim().equals("")) {
			setMessage(Messages.getMessage("new_document_insert_description"), IMessageProvider.ERROR);
			return;
		}

		setErrorMessage(null);
		setMessage(getDescription());
		setPageComplete(true);
    }
	
	/**
	 * Parsees the file looking for metadata information, resources, etc.
	 * 
	 * @param file File to be parsed
	 */
	private void parseFile(String file)
	{
		final String f = file;
		final Repository repository = JLibraryPlugin.getCurrentRepository();
		
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)
			{
				monitor.beginTask(
						Messages.getMessage("new_document_wizard_progress"), 
						IProgressMonitor.UNKNOWN);
				if (repository.getRepositoryConfig().isExtractMetadata()) {
					metadata = MetaDataExtractor.extractMetaData(f);
				}

			}
		};
		WizardDialog wd = (WizardDialog)getContainer();
		try {
			wd.run(true,true,runnable);
		} catch (InvocationTargetException e) {
			
            logger.error(e.getMessage(),e);
		} catch (InterruptedException e) {
			
            logger.error(e.getMessage(),e);
		}


		File realFile = new File(file);
		String realName = realFile.getName();
		
		DocumentMetadataPage metadataPage = 
			((NewDocumentWizard)getWizard()).getMetadataPage();
		DocumentResourcesPage resourcesPage = 
			((NewDocumentWizard)getWizard()).getResourcesPage();
		
		if ((metadata != null) && metadata.getTitle() != null) {
			metadataPage.setDocumentTitle(metadata.getTitle());
			nameText.setText(metadata.getTitle());
			descriptionText.setText(metadata.getTitle());
		} else {
			metadataPage.setDocumentTitle(realName);
			nameText.setText(realName);
			descriptionText.setText(realName);
		}
		if ((metadata != null) && metadata.getKeywords() != null) {
			metadataPage.setKeywords(metadata.getKeywords());
		}
		if ((metadata != null) && metadata.getDate() != null) {
			metadataPage.setDate(metadata.getDate());
		}
			
		if ((metadata != null) && metadata.getDescription() != null) {
			descriptionText.setText(metadata.getDescription());
		} else {
			descriptionText.setText(realName);
		}
			
		if ((metadata != null) && metadata.getLanguage() != null) {
			metadataPage.setLanguage(metadata.getLanguage());
		} else {
			metadataPage.setLanguage(DocumentMetaData.UNKNOWN_LANGUAGE);
		}
		
		// Extract resources
		String[] resources = MetaDataExtractor.extractResources(file);
		String parentPath = realFile.getParentFile().getAbsolutePath();
		for (int i = 0; i < resources.length; i++) {
			resourcesPage.addResource(parentPath + 
									  System.getProperty("file.separator") + 
									  resources[i]);
		}
	}

	private void setImportanceText(int value) {
		
		if (value == Node.IMPORTANCE_LOW.intValue()) {
			labImportanceText.setText(Messages.getMessage("importance_lowest"));
			return;
		} else if (value < Node.IMPORTANCE_MEDIUM.intValue()) {
			labImportanceText.setText(Messages.getMessage("importance_low"));
			return;
		} else if (value < Node.IMPORTANCE_HIGH.intValue()) {
			labImportanceText.setText(Messages.getMessage("importance_medium"));
			return;
		} else if (value < Node.IMPORTANCE_HIGHEST.intValue()) {
			labImportanceText.setText(Messages.getMessage("importance_high"));
			return;
		} else {
			labImportanceText.setText(Messages.getMessage("importance_highest"));
			return;
		}
	}

	public String getName() {
		
		return nameText.getText();
	}
	
	public String getDocumentDescription() {
		
		return descriptionText.getText();
	}
	
	public File getFile() {
		
		return new File(pathText.getText());
	}
	
	public int getImportance() {
		
		return importance.getSelection();
	}
}
