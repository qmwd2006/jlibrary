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
import org.eclipse.swt.SWTError;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.jlibrary.client.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Reusable form for contents that uses an ActiveX control
 */
public class FormActiveXControl {

	static Logger logger = LoggerFactory.getLogger(FormActiveXControl.class);
	
	private FileEditorInput editorInput;
	private OleClientSite controlSite;
	private Menu clientFrameMenuBar;
	private OleFrame clientFrame;

    /**
     * Keep track of whether we have an active client so we do not
     * deactivate multiple times
     */
    private boolean clientActive = false;

    /**
     * Keep track of whether we have activated OLE or not as some applications
     * will only allow single activations.
     */
    private boolean oleActivated = false;	
	
	public FormActiveXControl(IWorkbenchSite site,
							  String programId,
						      FileEditorInput editorInput,
							  Object modelObject,
							  Composite body) {
		
		this.editorInput = editorInput;
		
		createFormContent(body);
	}
	
	protected void createFormContent(Composite body) {

		body.setLayout(new FillLayout());
		
		try {
			clientFrame = new OleFrame(body, SWT.CLIP_CHILDREN);
	        clientFrame.setBackground(JFaceColors.getBannerBackground(clientFrame
	                .getDisplay()));
			
			initializeWorkbenchMenus();
			
			clientFrame.setBounds(body.getClientArea());
			File file = editorInput.getPath().toFile();
			controlSite = new OleClientSite(clientFrame, SWT.NONE, file);
			controlSite.setBounds(body.getClientArea());
			controlSite.setBackground(
					JFaceColors.getBannerBackground(clientFrame.getDisplay()));

			//OleAutomation auto = new OleAutomation(controlSite);			
			//controlSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
			//controlSite.doVerb(OLE.OLEIVERB_SHOW);
			//controlSite.doVerb(OLE.OLEIVERB_SHOW);
		} catch (SWTError e) {
			// Maybe add an indication label
			logger.error("Unable to open an ActiveX Control",e);
			return;
		}

		initControl();
	}
	
    public void handleWord() {
    	
        OleAutomation dispInterface = new OleAutomation(controlSite);
        // Get Application
        int[] appId = dispInterface
                .getIDsOfNames(new String[] { "Application" }); //$NON-NLS-1$
        if (appId != null) {
            Variant pVarResult = dispInterface.getProperty(appId[0]);
            if (pVarResult != null) {
                OleAutomation application = pVarResult.getAutomation();
                int[] dispid = application
                        .getIDsOfNames(new String[] { "DisplayScrollBars" }); //$NON-NLS-1$
                if (dispid != null) {
                    Variant rgvarg = new Variant(true);
                    application.setProperty(dispid[0], rgvarg);
                }
                application.dispose();
            }
        }
        dispInterface.dispose();
        
    }	
	
    public void deactivateClient(IWorkbenchPart part) {

    	//Check the client active flag. Set it to false when we have deactivated
        //to prevent multiple deactivations.
        if (clientActive) {
            if (controlSite != null)
            	controlSite.deactivateInPlaceClient();
            this.clientActive = false;
            this.oleActivated = false;
        }
        
    }
    
    public void activateClient(IWorkbenchPart part) {

    	oleActivate();
        this.clientActive = true;
        
    }
    
    /**
     * Make ole active so that the controls are rendered.
     */
    private void oleActivate() {
        //If there was an OLE Error or nothing has been created yet
        if (controlSite == null || clientFrame == null
                || clientFrame.isDisposed())
            return;

        if (!oleActivated) {
        	controlSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
            oleActivated = true;
            String progId = controlSite.getProgramID();
            if (progId != null && progId.startsWith("Word.Document")) { //$NON-NLS-1$
                handleWord();
            }
        }
    }    
    
    public void dispose() {
    	
    	if ((controlSite != null) && !controlSite.isDisposed()) {
    		controlSite.dispose();
    	}
    	if ((clientFrame != null) && !clientFrame.isDisposed()) {
    		clientFrame.dispose();
    	}
    }
    
	protected void initializeWorkbenchMenus() {
		//If there was an OLE Error or nothing has been created yet
		if (clientFrame == null || clientFrame.isDisposed())
			return;		
		// Get the browser menubar.  If one does not exist then
		// create it.
		Shell shell = clientFrame.getShell();
		Menu menuBar = shell.getMenuBar();
		if (menuBar == null) {
			menuBar = new Menu(shell, SWT.BAR);
			shell.setMenuBar(menuBar);
		}
		
		// Swap the file and window menus.
		MenuItem[] fileMenu = new MenuItem[menuBar.getItemCount()];		

		for (int i = 0; i < menuBar.getItemCount(); i++) {
			MenuItem item = menuBar.getItem(i);
			fileMenu[i] = item;
		}
		clientFrame.setFileMenus(fileMenu);
		clientFrameMenuBar = clientFrame.getShell().getMenuBar();
	}

	public void updateMenu() {
		
		clientFrame.getShell().setMenuBar(clientFrameMenuBar);

	}
	
	public void initControl() {
		
	}
	
	public void setFocus() {
		
		if(controlSite != null)
			controlSite.setFocus();
	}
	
	public OleClientSite getClientSite() {
		
		return controlSite;
	}
}
