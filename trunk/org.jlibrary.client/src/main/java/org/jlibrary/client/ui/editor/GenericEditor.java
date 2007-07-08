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
package org.jlibrary.client.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.editor.forms.ContentsFormPage;
import org.jlibrary.client.ui.editor.forms.DirectoryMetadataFormPage;
import org.jlibrary.client.ui.editor.forms.DocumentMetadataFormPage;
import org.jlibrary.client.ui.editor.forms.GroupMetadataFormPage;
import org.jlibrary.client.ui.editor.forms.MetadataFormPage;
import org.jlibrary.client.ui.editor.forms.RepositoryMetadataFormPage;
import org.jlibrary.client.ui.editor.forms.ResourceNodeMetadataFormPage;
import org.jlibrary.client.ui.editor.forms.RolMetadataFormPage;
import org.jlibrary.client.ui.editor.forms.UserMetadataFormPage;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Generic jLibrary editor to open all kinds of documents and resources.
 * 
 * A generic editor has three tabs. A tab with 
 */
public class GenericEditor extends JLibraryEditor {

	static Logger logger = LoggerFactory.getLogger(GenericEditor.class);
	
	public static final String CONTENTS = "editor_contents";
	public static final String RELATIONS = "editor_relations";
	public static final String PROPERTIES = "editor_properties";
	
	private ContentsFormPage contentsFormPage;
	private MetadataFormPage metadataFormPage;
	private RelationsEditor relationsEditor;
	
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#addPages()
	 */
	protected void addPages() {

		try {
			contentsFormPage = createContentsPage();
			metadataFormPage = createMetadataPage();
			relationsEditor = createRelationsEditor();
			
			addStartPages();
			
			if (contentsFormPage != null) {
				addPage(contentsFormPage);
			}
			if (metadataFormPage != null) {
				addPage(metadataFormPage);
			}
			if (relationsEditor != null) {
				//addPage(relationsFormPage);
				relationsEditor = new RelationsEditor((Document)getModel());
				int index = addPage(relationsEditor,
									getEditorInput());
				setPageText(index,Messages.getMessage("editor_relations"));
			}
			
			addEndPages();
		} catch (PartInitException e) {
			
            logger.error(e.getMessage(),e);
		}			
	}
	
	/**
	 * Method that adds the contents page. You can overwrite it to add your 
	 * own contents page implementation
	 * 
	 * @return IFormpage contents page implementation
	 */
	protected ContentsFormPage createContentsPage() {
		
		return new ContentsFormPage(this,Messages.getMessage(CONTENTS));
	}
	
	/**
	 * Method that adds the metadata page. You can overwrite it to add your 
	 * own metadata page implementation
	 * 
	 * @return IFormpage metadata page implementation
	 */	
	protected MetadataFormPage createMetadataPage() {
		
		
		if (getModel() instanceof Document) {
			return new DocumentMetadataFormPage(this,Messages.getMessage(PROPERTIES));
		} else if (getModel() instanceof ResourceNode){
			return new ResourceNodeMetadataFormPage(this,Messages.getMessage(PROPERTIES));		
		} else if (getModel() instanceof Directory) {
			return new DirectoryMetadataFormPage(this, Messages.getMessage(PROPERTIES));
		} else if (getModel() instanceof Repository) {
			return new RepositoryMetadataFormPage(this, Messages.getMessage(PROPERTIES));
		} else if (getModel() instanceof Rol) {
			return new RolMetadataFormPage(this, Messages.getMessage(PROPERTIES));
		} else if (getModel() instanceof User) {
			return new UserMetadataFormPage(this, Messages.getMessage(PROPERTIES));
		} else if (getModel() instanceof Group) {
			return new GroupMetadataFormPage(this, Messages.getMessage(PROPERTIES));
		}
		return null;
	}
	
	/**
	 * Method that adds the relations editor part that will be used on the 
	 * relations page. You can overwrite it to add your own contents page 
	 * implementation.
	 * 
	 * @return IFormpage contents page implementation
	 */	
	protected RelationsEditor createRelationsEditor() {
		
		Node node = (Node)getModel();
		if (node.isDocument()) {
			return new RelationsEditor((Document)getModel());
		}
		return null;
	}
	
	/**
	 * Adds extra pages at the end of the editor. The default implementation 
	 * does nothing. You can overwrite this method to add extra pages using 
	 * the addPage methods
	 */
	protected void addEndPages() {}
	
	/**
	 * Adds extra pages at the start of the editor. The default implementation 
	 * does nothing. You can overwrite this method to add extra pages using 
	 * the addPage methods
	 */
	protected void addStartPages() {}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor#updateContents()
	 */
	public void updateContents() {
		
		if (contentsFormPage != null) {
			contentsFormPage.updateContent();
		}
	}
	
	public void refreshContents() {
		
		if (contentsFormPage != null) {
			contentsFormPage.refreshContents();
		}
	}
	
	public ContentsFormPage getContentsFormPage() {
		return contentsFormPage;
	}
	public void setContentsFormPage(ContentsFormPage contentsFormPage) {
		this.contentsFormPage = contentsFormPage;
	}
	public MetadataFormPage getMetadataFormPage() {
		return metadataFormPage;
	}
	public void setMetadataFormPage(DocumentMetadataFormPage metadataFormPage) {
		this.metadataFormPage = metadataFormPage;
	}
	
	public void setNotShowRelationsPage(boolean show) {
		
		if (!show) {
			relationsEditor = null;
		}
	}
	
	public void setNotShowMetadataPage(boolean show) {
		
		if (!show) {
			metadataFormPage = null;
		}
	}

	public void setNotShowContentsPage(boolean show) {
		
		if (!show) {
			contentsFormPage = null;
		}
	}
	
	protected void initModel()
	{
		setModel(((INodeEditorInput)getEditorInput()).getNode());
	}

	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.checkCanSave()
	 */	
	public boolean checkCanSave() {
		
		return true;			
	}
	
	/**
	 * @see org.jlibrary.client.ui.editor.JLibraryEditor.getTitleImage()
	 */
	public Image getTitleImage()
	{
		return SharedImages.getImageForNode((Node)getModel());
	}

	public void refreshRelations() {
		
		if (relationsEditor != null) {
			relationsEditor.refreshRelations();
		}
	}
	
	/**
	 * @see JLibraryEditor#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {

		int pageCount = getPageCount();
		for (int i = 0; i < pageCount; i++) {
			IEditorPart editor = getEditor(i);
			if (editor != null) {
				if (editor.isDirty()) {
					super.setDirty(true);
				}
				editor.doSave(monitor);
			}
		}
		super.doSave(monitor);
	}
}
