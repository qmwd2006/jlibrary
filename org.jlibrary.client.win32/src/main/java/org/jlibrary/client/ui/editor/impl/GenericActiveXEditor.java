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
package org.jlibrary.client.ui.editor.impl;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.editor.forms.ActiveXFormPage;
import org.jlibrary.client.ui.editor.forms.ContentsFormPage;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Editor to open all HTML documents. The difference with the generic editor 
 * is that it shows an extra pane with the source code of the document.
 */
public class GenericActiveXEditor extends GenericEditor {

	static Logger logger = LoggerFactory.getLogger(GenericActiveXEditor.class);
	
	private ActiveXFormPage activeXPage;
	
    private IPartListener partListener = new IPartListener() {
        public void partActivated(IWorkbenchPart part) {
            activateClient(part);
        }

        public void partBroughtToTop(IWorkbenchPart part) {
        }

        public void partClosed(IWorkbenchPart part) {
        }

        public void partOpened(IWorkbenchPart part) {
        }

        public void partDeactivated(IWorkbenchPart part) {
            deactivateClient(part);
        }
    };
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#createPages()
	 */
	protected void createPages() {
		
		markAsDirty();
		super.createPages();
	}
	
	public void init(IEditorSite site, 
			         IEditorInput input) throws PartInitException {
		
		super.init(site, input);
		
        // Listen for part activation.
        site.getPage().addPartListener(partListener);
	}
	
    public void deactivateClient(IWorkbenchPart part) {
        
		if (activeXPage == null) {
			return;
		}
		
		if (activeXPage.getClientSite() == null) {
            return;
		}
		
		if (part == this) {
			activeXPage.deactivateClient(part);
		}
    }
    
    public void activateClient(IWorkbenchPart part) {
        
		if (activeXPage == null) {
			return;
		}
		
		if (activeXPage.getClientSite() == null) {
            return;
		}
		
		if (part == this) {
			activeXPage.activateClient(part);
		}
    }
	
    /**
     *	Print this object's contents
     */
    public void doPrint() {

		if (activeXPage == null) {
			return;
		}
		
		if (activeXPage.getClientSite() == null) {
            return;
		}
		
		final OleClientSite clientSite = activeXPage.getClientSite();    	
    	
        BusyIndicator.showWhile(clientSite.getDisplay(), new Runnable() {
            public void run() {
                clientSite.exec(OLE.OLECMDID_PRINT,
                        OLE.OLECMDEXECOPT_PROMPTUSER, null, null);
                // note: to check for success: above == SWTOLE.S_OK
            }
        });
    }
	
	/**
	 * @see org.jlibrary.client.ui.editor.GenericEditor#createContentsPage()
	 */
	protected ContentsFormPage createContentsPage() {
		
		Object model = getModel();
		Integer typeCode = Types.OTHER;
		String id = null;
		
		if (model instanceof Document) {
			typeCode = ((Document)model).getTypecode();
			if (typeCode.equals(Types.OTHER)) {
				typeCode = Types.getTypeForFile(((Document)model).getPath());
			}
		} else if (model instanceof ResourceNode) {
			typeCode = ((ResourceNode)model).getTypecode();
			if (typeCode.equals(Types.OTHER)) {
				typeCode = Types.getTypeForFile(((ResourceNode)model).getPath());
			}			
		}
		
		if (typeCode.equals(Types.OTHER)) {
			return null;
		}
		
		if (typeCode.equals(Types.POWERPOINT_DOCUMENT)) {
			id = "MsPowerPoint"; 
		} else if (typeCode.equals(Types.WORD_DOCUMENT)) {
			id = "msword";
		} else if (typeCode.equals(Types.EXCEL_DOCUMENT)) {
			id ="msexcell";
		}
		
		if (id == null) {
			return null;
		} else {
			activeXPage = new ActiveXFormPage(this, Messages.getMessage(CONTENTS),id);
			return activeXPage;
		}		
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#updateMenu()
	 */
	protected void updateMenu() {
		
		if (getContentsFormPage() != null) {
			((ActiveXFormPage)getContentsFormPage()).updateMenu();
		}
	}

	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryMultiPageEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		
		if (activeXPage == null) {
			return;
		}
		
		if (activeXPage.getClientSite() == null) {
            return;
		}
		
		final OleClientSite clientSite = activeXPage.getClientSite();
		
        BusyIndicator.showWhile(clientSite.getDisplay(), new Runnable() {

            public void run() {

                int result = clientSite.queryStatus(OLE.OLECMDID_SAVE);
                if ((result & OLE.OLECMDF_ENABLED) != 0) {
                    result = clientSite.exec(OLE.OLECMDID_SAVE,
                            OLE.OLECMDEXECOPT_PROMPTUSER, null, null);
                    if (result == OLE.S_OK) {
                        logger.error("No se ha guardado el documento OLE");
                        return;
                    }
                    logger.error("No se ha podido guardar el documento OLE");
                    return;
                }

                if (saveFile(clientSite)) {
                	logger.error("Se ha guardado con éxito el documento OLE");
                } else
                	logger.error("No se ha podido guardar el documento OLE");
            }
        });
        
		super.doSave(monitor);        
	}
	
    /**
     * Save the supplied file using the SWT API.
     * @param file java.io.File
     * @return <code>true</code> if the save was successful
     */	
	private boolean saveFile(OleClientSite clientSite) {
		
		if (!(getEditorInput() instanceof FileEditorInput)) {
			logger.error("ActiveX editor only support file inputs");
			return false;
		}
		
		FileEditorInput fei = (FileEditorInput)getEditorInput();
		File file = fei.getFile().getFullPath().toFile();
		
        File tempFile = new File(file.getAbsolutePath() + ".tmp"); //$NON-NLS-1$
        file.renameTo(tempFile);
        boolean saved = false;
        if (OLE.isOleFile(file) || usesStorageFiles(clientSite.getProgramID())) {
            saved = clientSite.save(file, true);
        } else {
            saved = clientSite.save(file, false);
        }

        if (saved) {
            // save was successful so discard the backup
            tempFile.delete();
            return true;
        }
        // save failed so restore the backup
        tempFile.renameTo(file);
        return false;
	}
	
    /**
     * See if it is one of the known types that use OLE Storage.
     * @param progID the type to test
     * @return <code>true</code> if it is one of the known types
     */
    private static boolean usesStorageFiles(String progID) {
        return (progID != null && (progID.startsWith("Word.", 0) //$NON-NLS-1$
                || progID.startsWith("MSGraph", 0) //$NON-NLS-1$
                || progID.startsWith("PowerPoint", 0) //$NON-NLS-1$
        || progID.startsWith("Excel", 0))); //$NON-NLS-1$
    }
    
    /**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#editorSaved()
	 */
	public void editorSaved() {

		markAsDirty();
	}
	
	private void markAsDirty() {
		
		super.setDirty(true);
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#isNotAskAndSave()
	 */
	public boolean isNotAskAndSave() {
		
		return true;
	}

	public void dispose() {
		
        if (getSite() != null && getSite().getPage() != null)
            getSite().getPage().removePartListener(partListener);
		
        if (activeXPage != null) {
        	activeXPage.dispose();
        }
        
		super.dispose();
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.checkCanSave()
	 */	
	public boolean checkCanSave() {
		
		Node node = (Node)getModel();
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		if (!securityManager.canPerformAction(
				node.getRepository(),
				node,
				SecurityManager.SAVE_DOCUMENT)) {
			return false;
		}			
		return true;		
	}	
}
