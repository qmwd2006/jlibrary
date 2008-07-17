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

import java.io.File;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.ResourceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Form page to embbed an ActiveX control
 */
public class ActiveXFormPage extends ContentsFormPage {
	
	static Logger logger = LoggerFactory.getLogger(ActiveXFormPage.class);
	
	private FormActiveXControl formContents;
	private IManagedForm form;
	private String programId;
	
	public ActiveXFormPage(JLibraryEditor editor, String title, String programId) {
		
		super(editor, title);

		this.programId = programId;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm form) {

		this.form = form;
		Composite body = form.getForm().getBody();
		formContents = new FormActiveXControl(getSite(),
											  programId,
											  (FileEditorInput)getEditor().getEditorInput(),
											  ((JLibraryEditor)getEditor()).getModel(),
											  body);
		updateFormTitle();				
	}

	public void updateMenu() {
		
		formContents.updateMenu();
	}
	
	public void initialize(FormEditor editor) {
		
		super.initialize(editor);
	}

	public void setFocus() {
		
		if(formContents != null)
			formContents.setFocus();
	}

	public void updateContent() {
		
		if(formContents != null) {
			formContents.initControl();
		}
	}
	
	protected void updateFormTitle() {
		
		Object modelObject = ((JLibraryEditor)getEditor()).getModel();
		
		if (modelObject instanceof Document) {
			Document document = (Document)modelObject;
			form.getForm().setText(document.getName());
		} else if (modelObject instanceof ResourceNode){
			ResourceNode resource = (ResourceNode)modelObject;
			form.getForm().setText(resource.getName());			
		} else if (modelObject instanceof File) {
			File file = (File)modelObject;
			form.getForm().setText(file.getName());
		}	
	}
	
	public void dispose() {
		
		super.dispose();
		
		if (formContents != null) {
			formContents.dispose();
		}
	}
	
	
	public static void main(String[] args) {
		
		// Este test se puede utilizar para probar aplicaciones.
		// Shell.Explorer = Internet Explorer
		// Word.Document = Microsoft word
		// MsPowerPoint = Microsoft PowerPoint
		
		String progID = "MsPowerPoint";
		
		Display display = new Display ();
		Shell shell = new Shell(display);
	
		OleFrame clientFrame = new OleFrame(shell, SWT.CLIP_CHILDREN);
		clientFrame.setBackground(
				JFaceColors.getBannerBackground(clientFrame.getDisplay()));
		
		clientFrame.setBounds(shell.getClientArea());

		try {
			//OleControlSite site = new OleControlSite(clientFrame, SWT.NONE, progID);
			File file = new File("F:\\java.ppt"); 
			OleClientSite clientSite = new OleClientSite(clientFrame, SWT.NONE, file);
			clientSite.setBackground(JFaceColors.getBannerBackground(clientFrame.getDisplay()));
			//new OleAutomation(clientSite);
			
			//clientSite.doVerb(OLE.OLEIVERB_SHOW);

			
		} catch (SWTException ex) {
			
			logger.error("Unable to open type library for "+progID,ex);
			return;
		}
		
		/*Label label = new Label (shell, SWT.CENTER);
		label.setText ("Hello_world");
		label.setBounds (shell.getClientArea ());*/
		shell.open();	
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}

		display.dispose ();		
	}
	
	public OleClientSite getClientSite() {
		
		if (formContents == null) {
			return null;
		}
		return formContents.getClientSite();
	}
	
    public void deactivateClient(IWorkbenchPart part) {
        
    	formContents.deactivateClient(part);
    }
    
    public void activateClient(IWorkbenchPart part) {
        
    	formContents.activateClient(part);
    }	
}
