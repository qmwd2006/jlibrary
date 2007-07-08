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

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.i18n.LocaleService;
import org.jlibrary.client.ui.categories.actions.CategoriesActionGroup;
import org.jlibrary.client.ui.dialogs.AuthorSelectionDialog;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Wizard data page for document metadata
 */
public class DocumentMetadataPage extends WizardPage {
	
	static Logger logger = LoggerFactory.getLogger(CategoriesActionGroup.class);
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			
			checkButtonsEnabled();
		}
	};
	private Text titleText;
	private Text authorText;
	private Author author;
	private Button authorButton;
	private Text urlText;
	private Text keywordsText;
	private Date date;
	private CCombo languageCombo;
	
    public DocumentMetadataPage(String pageName, String description) {
        
        super(pageName);
        setTitle(pageName);
        setPageComplete(true);
        setDescription(description);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_NEW_DOCUMENT_WIZARD)); 
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        data.heightHint = 250;
        parent.setLayoutData(data);

        Composite outer = new Composite(parent, SWT.NONE);
        
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 10;
		outer.setLayout (gridLayout);
	
		Label labTitle = new Label (outer, SWT.NONE);
		labTitle.setText (Messages.getMessage("new_document_dialog_titulo"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labTitle.setLayoutData (data);
		
		titleText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		data.widthHint = 430;
		data.horizontalSpan = 2;
		titleText.setLayoutData (data);
		
		Label labAuthor = new Label (outer, SWT.NONE);
		labAuthor.setText (Messages.getMessage("new_document_dialog_author"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labAuthor.setLayoutData (data);
				
		authorText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 300;
		authorText.setEditable(false);
		authorText.setLayoutData (data);
		authorText.setText(Messages.getMessage(Author.UNKNOWN.getName()));
		author = Author.UNKNOWN;
		
		authorButton = new Button(outer, SWT.NONE);
		authorButton.setImage(SharedImages.getImage(SharedImages.IMAGE_USER));
		data = new GridData();
		authorButton.setLayoutData(data);		
		
		Label labURL = new Label (outer, SWT.NONE);
		labURL.setText (Messages.getMessage("new_document_dialog_url"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labURL.setLayoutData (data);
	
		urlText = new Text (outer, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		urlText.setLayoutData (data);
		
		Label labCombo = new Label (outer, SWT.NONE);
		labCombo.setText (Messages.getMessage("document_language"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labCombo.setLayoutData (data);
	
		languageCombo = new CCombo (outer, SWT.BORDER);
		data = new GridData ();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		languageCombo.setLayoutData (data);
		String[] languages = 
			LocaleService.getInstance().getSupportedDocumentLanguageDescriptions();
		for (int i = 0; i < languages.length; i++) {
			languageCombo.add(languages[i]);
		}
		languageCombo.setEditable(false);
		
		Label labKeywords = new Label (outer, SWT.NONE);
		labKeywords.setText (Messages.getMessage("new_document_dialog_keywords"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		data.horizontalSpan = 3;
		labKeywords.setLayoutData (data);
		
		keywordsText = new Text (outer, SWT.BORDER | SWT.WRAP);
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 100;
		data.horizontalSpan = 3;
		keywordsText.setLayoutData (data);						
		
		setMessage(getDescription());
		
		titleText.addModifyListener(modifyListener);
		authorText.addModifyListener(modifyListener);
		keywordsText.addModifyListener(modifyListener);
						
		authorText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		});
		
		authorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				Repository repository = JLibraryPlugin.getCurrentRepository();
				RepositoryService service = 
					JLibraryServiceFactory.getInstance(repository.getServerProfile()).getRepositoryService();
				Ticket ticket = repository.getTicket();
				
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
				
				
				AuthorSelectionDialog ald = new AuthorSelectionDialog(getShell());
				ald.open(authors);
				
				if (ald.getReturnCode() == IDialogConstants.OK_ID) {
					author = ald.getAuthor();
					if (author.getName().equals(Author.UNKNOWN_NAME)) {
						authorText.setText(
								Messages.getMessage(author.getName()));
					} else {
						authorText.setText(author.getName());
					}
				}			
			}
		});
        
        setControl(outer);
    }
    

    
    private void checkButtonsEnabled() {
        
    	return;
    }


	public void setDocumentTitle(String title) {
		
		titleText.setText(title);
	}

	public void setKeywords(String keywords) {
		
		keywordsText.setText(keywords);
	}

	public void setLanguage(String language) {
		
		String description = 
			LocaleService.getInstance().getDescriptionForLanguage(language);
		languageCombo.select(languageCombo.indexOf(description));
	}	
	
	public void setDate(Date date) {
		
		this.date = date;
	}
	
	public DocumentMetaData getMetadata() {
		
		DocumentMetaData metadata = new DocumentMetaData();
		metadata.setTitle(titleText.getText());
		metadata.setAuthor(author);
		if (date != null) {
			metadata.setDate(date);
		} else {
			metadata.setDate(new Date());
		}
		metadata.setKeywords(keywordsText.getText());
		metadata.setUrl(urlText.getText());
		
		String language = languageCombo.getText();
		if (language.equals("")) {
			language = null;
		} else {
			language = LocaleService.getInstance().getLanguageForDescription(language);
		}
		metadata.setLanguage(language);
		
		return metadata;
	}
}
