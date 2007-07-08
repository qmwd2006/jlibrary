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
package org.jlibrary.client.ui.notes.wizard;

import java.text.SimpleDateFormat;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.core.entities.Note;

/**
 * @author Martin Perez
 *
 * Note creation/update data page
 */
public class NoteDataPage extends WizardPage {

	private Note note;
	private Text noteText;

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	
    public NoteDataPage(Note note, String pageName, String description) {
         
        super(pageName);
        this.note = note;
        setPageComplete(false);
        setMessage(description);
        setImageDescriptor(SharedImages.getImageDescriptor(
        		SharedImages.IMAGE_NOTE_WIZARD)); 
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite outer) {

        Composite parent = new Composite(outer, SWT.NONE);
        
        GridLayout pageLayout = new GridLayout();
        pageLayout.numColumns = 1;
		pageLayout.verticalSpacing = 10;
        parent.setLayout(pageLayout);
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL_HORIZONTAL;
        parent.setLayoutData(data);
        
		Label labdate = new Label (parent, SWT.NONE);
		String date = "";
		if (note != null) {
			date = sdf.format(note.getDate());
		}
		labdate.setText (Messages.getAndParseValue("note_wizard_date","%1",date));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labdate.setLayoutData (data);

		Label labContent = new Label (parent, SWT.NONE);
		labContent.setText (Messages.getMessage("note_wizard_content"));
		data = new GridData ();
		data.horizontalAlignment = GridData.BEGINNING;
		labContent.setLayoutData (data);		
		
		noteText = new Text (parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData ();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		noteText.setLayoutData (data);
		
		noteText.addTraverseListener(new TraverseListener () {
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
		
		if (note != null) {
			noteText.setText(note.getNote());
		}
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				checkButtonsEnabled();
			}
		};
		
		noteText.addModifyListener(modifyListener);
        setControl(parent);
    }
    
    private void checkButtonsEnabled() {

        setPageComplete(false);
        if (noteText.getText().trim().equals("")) {
            return;
        }     
        setPageComplete(true);
    }
    
    public String getNoteText() {
        
        return noteText.getText();
    }    
}
