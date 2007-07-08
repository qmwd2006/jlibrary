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

import java.util.Date;

import org.eclipse.jface.wizard.Wizard;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Wizard for creating and updating node contents
 */
public class NewNoteWizard extends Wizard {

    private NoteDataPage noteDataPage;
    private Note note;
	private String repositoryId;

    public NewNoteWizard(String repositoryId) {
        
        super();
        this.repositoryId = repositoryId;
        
        setWindowTitle(Messages.getMessage("note_wizard_title"));
        setNeedsProgressMonitor(false);
    }

    public NewNoteWizard(Note note, String repositoryId) {
        
        this(repositoryId);
        
        this.note = note;
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        
        noteDataPage = new NoteDataPage(note,
        								Messages.getMessage("note_wizard_title"),
										Messages.getMessage("note_wizard_description"));
        
        addPage(noteDataPage);
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        
    	Repository repository = 
    		RepositoryRegistry.getInstance().getRepository(repositoryId);
    	if (note == null) {
	    	note = new Note();
    	}
    	note.setDate(new Date());
    	note.setNote(noteDataPage.getNoteText());
    	note.setCreator(repository.getTicket().getUser().getId());
		return true;
    }
    
    /**
     * @return Returns the note.
     */
    public Note getNote() {
    	    	
        return note;
    }
}
