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
package org.jlibrary.client.ui.export.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.export.freemarker.FreemarkerContext;
import org.jlibrary.client.ui.list.ListViewer;

/**
 * @author martin
 *
 * user data page
 */
public class TemplatesDataPage extends WizardPage {

	private ListViewer templatesList;
	private List templateFiles;
	
	private Button browseButton;
	private CCombo directoryText;
	
	private static final String EXPORT_WIZARD = "EXPORT_WIZARD";
	private static final String EXPORT_RECENT_DESTINATIONS = "EXPORT_RECENT_DESTINATIONS";
	private static final int HISTORY_LENGTH = 5;
	private IDialogSettings settings;
	
    public TemplatesDataPage(FreemarkerContext context,
    						 String pageName, 
    						 String description) {
        
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(false);
        
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_EXPORT_HTML_WIZARD)); 
        
        templateFiles = context.getTemplateFilesList();
        
		settings = JLibraryPlugin.getDefault().
		getDialogSettings().getSection(EXPORT_WIZARD);
		if (settings == null) {
		settings = JLibraryPlugin.getDefault().
				getDialogSettings().addNewSection(EXPORT_WIZARD);
		}
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite outer) {
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        outer.setLayoutData(data);

        Composite parent = new Composite(outer, SWT.NONE);
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 10;
		parent.setLayout (gridLayout);

		data = new GridData();
		
		Label templatesLabel = new Label(parent, SWT.NONE);
		templatesLabel.setText(Messages.getMessage("export_wizard_templates_name"));
		templatesLabel.setLayoutData(data);
		
		data = new GridData();
		data.horizontalIndent = 30;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
        templatesList = new ListViewer(parent, SharedImages.getImage(SharedImages.IMAGE_DIRECTORY));
		templatesList.setLabelProvider(new TemplatesLabelProvider());
        templatesList.getControl().setLayoutData(data);
		templatesList.add(templateFiles.toArray());
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		Label separator = new Label (parent, SWT.HORIZONTAL);
		separator.setLayoutData(data);
		
		data = new GridData();
		
		Label destinationLabel = new Label(parent, SWT.NONE);
		destinationLabel.setText(Messages.getMessage("export_wizard_templates_destination"));
		destinationLabel.setLayoutData(data);
		
		Composite diskComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayoutDisk = new GridLayout ();
		gridLayoutDisk.numColumns = 3;
		gridLayoutDisk.marginHeight = 10;
		gridLayoutDisk.marginWidth = 10;
		gridLayoutDisk.horizontalSpacing = 20;
		gridLayoutDisk.verticalSpacing = 10;
		diskComposite.setLayout (gridLayoutDisk);
		
		data = new GridData();
		data.horizontalIndent = 30;
		Label directoryName = new Label(diskComposite, SWT.NONE);
		directoryName.setText(Messages.getMessage("export_wizard_type_directory"));
		directoryName.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 300;
		directoryText = new CCombo(diskComposite, SWT.BORDER);
		directoryText.setLayoutData(data);
		//directoryText.setText(ClientConfig.getValue(ClientConfig.EXPORT_WEB));
		
		String[] recentDestinations = settings.getArray(EXPORT_RECENT_DESTINATIONS);
		if (recentDestinations != null) {
			directoryText.setItems(recentDestinations);
			directoryText.select(0);
		}
		
		data = new GridData();
		browseButton = new Button(diskComposite, SWT.NONE);
		browseButton.setImage(SharedImages.getImage(SharedImages.IMAGE_DIRECTORY));
		browseButton.setLayoutData(data);

		
        setControl(parent);
        
        templatesList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {

				checkButtonsEnabled();
			}
        });
        
        KeyAdapter keyAdapter = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				checkButtonsEnabled();
			}
        };

        directoryText.addKeyListener(keyAdapter);
        
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				
				DirectoryDialog dd = new DirectoryDialog(getShell());
				String filter = ClientConfig.getValue(ClientConfig.EXPORT_WEB);
				if (filter != null) {
					File f = new File(filter);
					dd.setFilterPath(f.getAbsolutePath());
				}
				String dir = dd.open();
				if (dir == null) {
					directoryText.setText("");
				} else {
					directoryText.setText(dir);
				}
				
				checkButtonsEnabled();
			}
		});
		
		checkButtonsEnabled();
    }
    	
    private void checkButtonsEnabled() {
    	
    	setPageComplete(false);
    	
		IStructuredSelection selection = 
			(IStructuredSelection)templatesList.getSelection();
		if (selection.isEmpty()) {
			setMessage(Messages.getMessage("export_wizard_templates_select"), 
					   IMessageProvider.ERROR);
			return;
		}
    	
		if (!directoryText.getText().equals("")) {
			setMessage(Messages.getMessage("export_wizard_type_finish"), 
					   IMessageProvider.INFORMATION);
    		setPageComplete(true);    			
		} else {
			setMessage(Messages.getMessage("export_wizard_type_enter_directory"), 
					   IMessageProvider.ERROR);
		}
    }
    
    /**
     * Returns the directory that contains the desired template
     * 
     * @return File Directory that contains the desired template
     */
	public File getSelectedTemplate() {
		
		IStructuredSelection selection = ((IStructuredSelection)templatesList.getSelection());
		return (File)selection.getFirstElement();
	}
	
	/**
	 * Returns the output directory path
	 * 
	 * @return String output directory path
	 */
	public String getOutputDirectory() {
		
		// We will remember the directory
		String directory = directoryText.getText();
		
		String[] recentDestinations = 
			settings.getArray(EXPORT_RECENT_DESTINATIONS);
		List list = null;
		if (recentDestinations == null) {
			list = new ArrayList();
		} else {
			list = new ArrayList(Arrays.asList(recentDestinations));
		}
		if (!list.contains(directory)) {
			if (list.size() == HISTORY_LENGTH) {
				list.remove(list.size()-1);
			}
			list.add(0, directory);
		}
		
		settings.put(EXPORT_RECENT_DESTINATIONS,
					 (String[])list.toArray(new String[]{}));
		
		return directory;
	}	
}
