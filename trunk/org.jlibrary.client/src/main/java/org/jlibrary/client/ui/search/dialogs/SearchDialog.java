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
package org.jlibrary.client.ui.search.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.dialogs.RepositoryListDialog;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Dialog used for doing a search
 */
public class SearchDialog extends Dialog {

	private Text text;
	private Text repositoryText;
	private Repository repository;
	
	private Button searchContents;
	private Button searchKeywords;
	private Button searchXPath;
	
	private Button searchAll;
	private Button searchRepository;
	
	private Button browseButton;

	private static SearchDialog instance;
	private IWorkbenchWindow window;
	private boolean isSearchContents;
	private boolean isSearchKeywords;
	private boolean isSearchXPath;
	private boolean isSearchAll;
	private String searchText;
	private String recentSearchText;
	
	private IDialogSettings settings;
	
	/**
	 * Constructor
	 * 
	 * @param window Parent window
	 */
	private SearchDialog(IWorkbenchWindow window) {
		
		super(window.getShell());
		this.window = window;
		
		settings = JLibraryPlugin.getDefault().getDialogSettings().getSection("SEARCH_DIALOG");
		if (settings == null)
		{
			settings = JLibraryPlugin.getDefault().getDialogSettings().addNewSection("SEARCH_DIALOG");
		}
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		
		shell.setText(Messages.getMessage("search_title"));	
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_SEARCH));
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent,IDialogConstants.OK_ID,Messages.getMessage("search_option"),true);
		createButton(parent,IDialogConstants.CANCEL_ID, Messages.getMessage("cancel_option"),false);
		
		recentSearchText = settings.get("RECENT_SEARCH_TEXT");
		if (recentSearchText == null)
			getButton(IDialogConstants.OK_ID).setEnabled(false);	
	}

	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(final Composite parent) {
		Composite outer = (Composite)super.createDialogArea(parent);
		
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		
		GridLayout gridLayout2 = new GridLayout ();
		gridLayout2.numColumns = 3;
		gridLayout2.marginHeight = 10;
		gridLayout2.marginWidth = 10;
		gridLayout2.horizontalSpacing = 20;
		gridLayout2.verticalSpacing = 10;		

		GridLayout gridLayout3 = new GridLayout ();
		gridLayout3.numColumns = 2;
		gridLayout3.marginHeight = 10;
		gridLayout3.marginWidth = 10;
		gridLayout3.horizontalSpacing = 20;
		gridLayout3.verticalSpacing = 10;			
		
		outer.setLayout (gridLayout);

		Label labText = new Label (outer, SWT.NONE);
		labText.setText (Messages.getMessage("search_text_label"));
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		data.horizontalSpan = 3;
		labText.setLayoutData (data);
		//labText.setFont(SharedFonts.getFont(SharedFonts.DIALOG_BOLD));
		
		text = new Text (outer, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData ();
		data.horizontalSpan = 3;
		data.heightHint = 80;
		data.widthHint = 300;
		data.horizontalAlignment = GridData.FILL;
		text.setLayoutData (data);
		
		Group groupSearchIn = new Group(outer, SWT.NONE);
		groupSearchIn.setLayout(gridLayout3);
		groupSearchIn.setText (Messages.getMessage("search_in_label"));
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		groupSearchIn.setLayoutData (data);

		
		searchKeywords = new Button(groupSearchIn, SWT.RADIO);
		searchKeywords.setText(Messages.getMessage("search_keywords"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		data.grabExcessHorizontalSpace = true;
		searchKeywords.setLayoutData (data);
		
		searchContents = new Button(groupSearchIn, SWT.RADIO);
		searchContents.setText(Messages.getMessage("search_contents"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		data.grabExcessHorizontalSpace = true;
		searchContents.setLayoutData (data);	
			
		searchXPath = new Button(groupSearchIn, SWT.RADIO);
		searchXPath.setText(Messages.getMessage("search_xpath"));
		data = new GridData ();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		data.grabExcessHorizontalSpace = true;
		searchXPath.setLayoutData (data);		

		Label labelXPathRef = new Label(parent, SWT.NONE);
		labelXPathRef.setText(Messages.getMessage("search_xpath_ref"));
		data = new GridData ();
		data.horizontalIndent = 15;
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		data.grabExcessHorizontalSpace = true;
		labelXPathRef.setLayoutData (data);			
		
		Group groupRepository = new Group(outer, SWT.NONE);
		groupRepository.setLayout(gridLayout2);
		groupRepository.setText (Messages.getMessage("search_repository"));
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		groupRepository.setLayoutData (data);
		
		searchAll = new Button(groupRepository, SWT.RADIO);
		searchAll.setText(Messages.getMessage("search_all"));
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		searchAll.setLayoutData (data);
		searchAll.setSelection(true);

		searchRepository = new Button(groupRepository, SWT.RADIO);
		searchRepository.setText(Messages.getMessage("search_in_repository"));
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		searchRepository.setLayoutData (data);		
		
		repositoryText = new Text (groupRepository, SWT.BORDER);
		repositoryText.setEditable(false);
		data = new GridData ();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.widthHint = 200;
		repositoryText.setLayoutData (data);

		browseButton = new Button(groupRepository, SWT.NONE);
		browseButton.setImage(SharedImages.getImage(SharedImages.IMAGE_OPEN_REPOSITORY));
		data = new GridData();
		data.horizontalSpan = 1;
		browseButton.setLayoutData(data);		
		
		ModifyListener modifyListener = new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
		
		text.addTraverseListener(new TraverseListener () {
			public void keyTraversed(TraverseEvent e) {
				switch (e.detail) {
					case SWT.TRAVERSE_TAB_NEXT:
						e.doit = true;
					case SWT.TRAVERSE_TAB_PREVIOUS: {
						e.doit = true;
					}
				}
			}
		});
		
		text.addModifyListener(modifyListener);
		repositoryText.addModifyListener(modifyListener);
		
		SelectionListener selectionListener = new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				
				checkButtonsEnabled();
			}
		};
		
		searchAll.addSelectionListener(selectionListener);
		searchContents.addSelectionListener(selectionListener);
		searchKeywords.addSelectionListener(selectionListener);
		searchXPath.addSelectionListener(selectionListener);
		searchRepository.addSelectionListener(selectionListener);
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {

				loadRepository();
			}
		});
		
		/*
		 * Do we have recent search results in our history?
		 */
		
		String recentSearchWithin = settings.get("RECENT_SEARCH_WITHIN");
		if(recentSearchWithin != null)
		{
			if(recentSearchWithin.equals("keywords"))
				searchKeywords.setSelection(true);
			else if(recentSearchWithin.equals("contents"))
				searchContents.setSelection(true);
			else if(recentSearchWithin.equals("xpath"))
				searchXPath.setSelection(true);
		}
		else
			searchKeywords.setSelection(true);
		
		recentSearchText = settings.get("RECENT_SEARCH_TEXT");
		if (recentSearchText != null)
		{
			text.setText(recentSearchText);
		}

		if (repository == null) {
			String recentSearchRepos = settings.get("RECENT_SEARCH_REPOS");
			if(recentSearchRepos != null)
			{
				if(recentSearchRepos.equals("all"))
					searchAll.setSelection(true);
				else
				{
					searchRepository.setSelection(true);
					repository = RepositoryRegistry.getInstance().getRepositoryByName(recentSearchRepos);
					if(repository != null)
						repositoryText.setText(repository.getName());
					else
						searchAll.setSelection(true);
				}
			}
		} else {		
			// Explicitly opening a repository
			repositoryText.setText(repository.getName());
		}
		
		return outer;
	}

	private void loadRepository() {
		
		// Load the list of repositories
		List repositories = new ArrayList(RepositoryRegistry.getInstance().getOpenedRepositories());
		RepositoryListDialog rld = new RepositoryListDialog(window.getShell());
		rld.open(repositories);
		
		if (rld.getReturnCode() == IDialogConstants.OK_ID) {
			this.repository = (Repository)rld.getSelectedItem();
			repositoryText.setText(repository.getName());
			
		}
	}	
	
	/**
	 * This method checks if the new user button can be enabled
	 */
	private void checkButtonsEnabled() {
		
		Button okbutton = getButton(IDialogConstants.OK_ID);
		if(okbutton != null)
			okbutton.setEnabled(false);

		if (text.getText().trim().equals("")) {
			return;
		}

		if (!searchKeywords.getSelection() &&
			!searchContents.getSelection() &&
			!searchXPath.getSelection()) {
			return;
		}
		
		if (searchRepository.getSelection() && 
			repositoryText.getText().trim().equals("")) {
			return;
		}
		
		if (searchRepository.getSelection()) {
			if (!canAccessToRepository(repository)) {
				return;
			}
		} else {
			// check all repositories
			Collection repositories = 
				RepositoryRegistry.getInstance().getOpenedRepositories();
			Iterator it = repositories.iterator();
			while (it.hasNext()) {
				Repository repository = (Repository) it.next();
				if (!canAccessToRepository(repository)) {
					return;
				}
			}
		}
		
		if(okbutton != null)						
			okbutton.setEnabled(true);
	}

	private boolean canAccessToRepository(Repository repository) {
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();			
		if (!securityManager.canPerformAction(repository.getTicket(),
											  repository.getRoot(),
											  SecurityManager.SEARCH)) {
			return false;
		}			
		return true;
	}
	
	public boolean isSearchKeywordsEnabled() {
		
		return isSearchKeywords;
	}
	
	public boolean isSearchContentsEnabled() {
		
		return isSearchContents;
	}
	
	public boolean isSearchXPathEnabled() {
		
		return isSearchXPath;
	}
		
	public String getSearchText() {
		
		return searchText;
	}
	
	public Repository getRepository() {
		
		return repository;
	}
	
	public boolean isSearchAllEnabled() {
		
		return isSearchAll;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		
		isSearchContents = searchContents.getSelection();
		isSearchKeywords = searchKeywords.getSelection();
		isSearchXPath = searchXPath.getSelection();
		isSearchAll = searchAll.getSelection();
		
		searchText = text.getText();
		
		settings.put("RECENT_SEARCH_TEXT", text.getText());
		if(isSearchContents)
			settings.put("RECENT_SEARCH_WITHIN", "contents");
		else if(isSearchKeywords)
			settings.put("RECENT_SEARCH_WITHIN", "keywords");
		else if(isSearchXPath)
			settings.put("RECENT_SEARCH_WITHIN", "xpath");
		
		if(isSearchAll)
			settings.put("RECENT_SEARCH_REPOS", "all");
		else
			settings.put("RECENT_SEARCH_REPOS", repositoryText.getText());
		
		super.okPressed();
	}
	
	/**
	 * Returns an unique instance of this SearchDialog
	 * 
	 * @param window Parent window
	 * @return Unique instance of this dialog
	 */
	public static SearchDialog getInstance(IWorkbenchWindow window) {
		
		if (instance == null) {
			instance = new SearchDialog(window);
		}
		return instance;
	}
	
	public int open(Repository repository) {

		this.repository = repository;
		
		if (repositoryText != null) {
			repositoryText.setText(repository.getName());
		}
		
		return super.open();
	}
}
