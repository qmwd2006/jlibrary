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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.customProperties.CustomPropertiesUtils;
import org.jlibrary.core.entities.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Section part which implements custom properties section.
 *
 * @author Roman Puchkovskiy
 */
public class CustomPropertiesSection extends SectionPart {
	
	static Logger logger = LoggerFactory.getLogger(CustomPropertiesSection.class);

    private Button addPropertyButton;
    private Button removePropertyButton;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			formMetadata.propertiesModified();
		}
	};
	
	private Document document;
    private SortedMap customProperties;
	private DocumentFormMetadata formMetadata;
	
	public CustomPropertiesSection(FormToolkit toolkit,
						   DocumentFormMetadata formMetadata, 
						   Composite parent) {

		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE );
		this.formMetadata = formMetadata;
		this.document = formMetadata.getDocument();
        this.customProperties = new TreeMap();
        if (document.getCustomProperties() != null) {
            this.customProperties = new TreeMap(document.getCustomProperties());
        } else {
            this.customProperties = new TreeMap();
        }
		//initialize(page.getManagedForm());
		getSection().clientVerticalSpacing = 4;
		getSection().setData("part", this);

		createClient(toolkit);
	}
	
	protected void createClient(FormToolkit toolkit) {
		
		Section section = getSection();
		
		section.setText(Messages.getMessage("custom_properties_section"));
		section.setDescription(Messages.getMessage("custom_properties_section_description"));
	
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

        GridData data;
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.widthHint = 200;
        data.horizontalSpan = 2;
	
        final Table table = toolkit.createTable(sectionClient, SWT.SINGLE | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        table.setLayoutData(data);
    	TableColumn column1 = new TableColumn(table, SWT.NONE);
	    TableColumn column2 = new TableColumn(table, SWT.NONE);

    	for (Iterator i = customProperties.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            String name = entry.getKey().toString();
            String value = entry.getValue().toString();
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] {name, value});
	    }
	    column1.pack();
	    column2.pack();
	
	    final TableEditor editor = new TableEditor(table);
	    //The editor must have the same size as the cell and must
	    //not be any smaller than 50 pixels.
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
	    editor.minimumWidth = 50;
	    
        final int NAMECOLUMN = 0;
        // editing the second column
	    final int EDITABLECOLUMN = 1;
	
	    table.addSelectionListener(new SelectionAdapter() {
		    public void widgetSelected(SelectionEvent e) {
			    // Clean up any previous editor control
			    Control oldEditor = editor.getEditor();
			    if (oldEditor != null) oldEditor.dispose();
	
			    // Identify the selected row
			    final TableItem item = (TableItem)e.item;
			    if (item == null) return;
	
			    // The control that will be the editor must be a child of the Table
			    Text newEditor = new Text(table, SWT.NONE);
			    newEditor.setText(item.getText(EDITABLECOLUMN));
			    newEditor.addModifyListener(new ModifyListener() {
				    public void modifyText(ModifyEvent e) {
					    Text text = (Text)editor.getEditor();
					    editor.getItem().setText(EDITABLECOLUMN, text.getText());
                        document.putProperty(item.getText(NAMECOLUMN), text.getText());
                        modifyListener.modifyText(e);
				    }
			    });
			    newEditor.selectAll();
			    newEditor.setFocus();
			    editor.setEditor(newEditor, item, EDITABLECOLUMN);
		    }
	    });

    	Composite buttonSection = toolkit.createComposite(sectionClient);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		buttonSection.setLayoutData(data);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 10;
		buttonSection.setLayout(layout);
		toolkit.paintBordersFor(buttonSection);

        if (formMetadata.canUpdate()) {
            addPropertyButton = toolkit.createButton(buttonSection,Messages.getMessage("custom_properties_add_property"),SWT.PUSH);
            addPropertyButton.setImage(SharedImages.getImage(SharedImages.IMAGE_NEW));
            addPropertyButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
                    InputDialog inputDialog = new InputDialog(table.getShell(), Messages.getMessage("custom_properties_property_name"), Messages.getMessage("custom_properties_enter_property_name"), "", new IInputValidator() {
                        public String isValid(String newText) {
                            if (newText == null || newText.trim().length() == 0) {
                                return Messages.getMessage("custom_properties_property_name_cannot_be_empty");
                            }
                            newText = newText.trim();
                            if (newText.startsWith("jcr:") || newText.startsWith("jlib:")) {
                                return Messages.getMessage("custom_properties_reserved_prefix");
                            }
                            if (!CustomPropertiesUtils.getOperations().isValidPropertyName(document, newText)) {
                                return Messages.getMessage("custom_properties_invalid_prefix");
                            }
                            Set names = new HashSet();
                            TableItem[] items = table.getItems();
                            for (int i = 0; i < items.length; i++) {
                                names.add(items[i].getText(NAMECOLUMN));
                            }
                            if (names.contains(newText)) {
                                return Messages.getMessage("custom_properties_exists");
                            }
                            // else all is ok, return null to say it's valid
                            return null;
                        }
                    });
                    inputDialog.open();
                    if (inputDialog.getValue() != null && inputDialog.getValue().trim().length() != 0) {
                        String propertyName = inputDialog.getValue();
                        String propertyValue = "";
                        document.putProperty(propertyName, propertyValue);
                        formMetadata.propertiesModified();
                        TableItem item = new TableItem(table, SWT.NONE);
                        item.setText(new String[] {propertyName, propertyValue});
                        table.select(table.getItemCount() - 1);
                    }
				}
			});
        }
        if (formMetadata.canUpdate()) {
            removePropertyButton = toolkit.createButton(buttonSection,Messages.getMessage("custom_properties_remove_property"),SWT.PUSH);
            removePropertyButton.setImage(SharedImages.getImage(SharedImages.IMAGE_DELETE));
            removePropertyButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
                    int index = table.getSelectionIndex();
                    if (index >= 0) {
            		    // Clean up any previous editor control
			            Control oldEditor = editor.getEditor();
			            if (oldEditor != null) oldEditor.dispose();
                        TableItem item = table.getItem(index);
                        String propertyName = item.getText(NAMECOLUMN);
                        if (CustomPropertiesUtils.getOperations().isRemovable(document, propertyName)) {
                            table.remove(index);
                            // putting null so property will be removed
                            document.putProperty(propertyName, null);
                            formMetadata.propertiesModified();
                        }
                    }
				}
			});
        }
	}

	/**
	 * @see org.eclipse.ui.forms.SectionPart#hookListeners()
	 */
	protected void hookListeners() {}

	public void dispose() {

		super.dispose();
	}

}
