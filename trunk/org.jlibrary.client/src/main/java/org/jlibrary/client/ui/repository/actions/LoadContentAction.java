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
package org.jlibrary.client.ui.repository.actions;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.editor.EditorsRegistry;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.util.NodeUtils;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 */
public class LoadContentAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(LoadContentAction.class);
	
	/**
	 * Constructor
	 */
	public LoadContentAction(IWorkbenchSite site) {

		super(site);

		setText(Messages.getMessage("item_load_content"));
		setToolTipText(Messages.getMessage("tooltip_load_content"));
		setImageDescriptor(SharedImages
				.getImageDescriptor(SharedImages.IMAGE_LOAD_CONTENT));
		setDisabledImageDescriptor(SharedImages
				.getImageDescriptor(SharedImages.IMAGE_LOAD_CONTENT_DISABLED));
	}

	/*
	 * (non-Javadoc) Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {

		setEnabled(checkEnabled(selection));
	}

	/**
	 * 
	 * @param selection
	 * @return
	 */
	private boolean checkEnabled(IStructuredSelection selection) {
		
		if (selection.isEmpty()) {
			return false;
		}
		
		SecurityManager securityManager = 
			JLibraryPlugin.getDefault().getSecurityManager();
		
		Object[] elements = selection.toArray();
		if (elements.length > 1) {
			return false;
		}
		if (!(elements[0] instanceof Document)) {

			return false;
		}
		Document document = (Document)elements[0];
		if (EditorsRegistry.getInstance().getEditor(
				document.getId()) != null) {
			return false;
		}
		
		if (!securityManager.canPerformAction(
				document.getRepository(),
				document,
				SecurityManager.LOAD_CONTENT)) {
			return false;
		}			
		
		return true;
	}

	/*
	 * (non-Javadoc) Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {

		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}

	public void run(Object[] elements) {

		
		logger.info("Loading file contents");

		final Document document = (Document) elements[0];
		FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		String filter = ClientConfig.getValue(ClientConfig.SAVE_DOCUMENT);
		if (filter != null) {
			File f = new File(filter);
			fd.setFilterPath(f.getAbsolutePath());
		}
		String extension = FileUtils.getExtension(document.getPath());
		fd.setFilterNames(new String[] { Messages.getAndParseValue(
				"load_content_extension", "%1", extension) });
		fd.setFilterExtensions(new String[] { "*" + extension });
		String result = fd.open();
		if (result == null) {
			// Cancel
			return;
		}
		ClientConfig.setValue(ClientConfig.SAVE_DOCUMENT, result);

		File f = new File(result);

		restoreFileContents(f, document);
	}

	/**
	 * @param f
	 */
	private void restoreFileContents(File f, Document document) {

		try {
			LocalCacheService.getInstance().getLocalCache().removeNodeFromCache(document);
			FileEditorInput currentEditorInput = FileEditorInput
					.createFileEditorInput(f, document);

			IEditorPart currentEditor = JLibraryPlugin.getActivePage()
					.findEditor(currentEditorInput);
			if (currentEditor != null) {
				JLibraryPlugin.getActivePage()
						.closeEditor(currentEditor, false);
			}

			IEditorDescriptor desc = null;
			IEditorRegistry registry = PlatformUI.getWorkbench()
					.getEditorRegistry();
			String extension = FileUtils.getExtension(f.getName());
			if (NodeUtils.isExtensionUnknown(extension)) {
				desc = registry.getDefaultEditor("default" + extension);
			} else {
				desc = registry.getDefaultEditor(extension);
				ClientConfig.setDefaultToolForExtension(ClientConfig.DEFAULT_TOOL,
						extension);
			}

			JLibraryEditor openedEditorPart = (JLibraryEditor) JLibraryPlugin
					.getActivePage().openEditor(currentEditorInput,
							desc.getId());
			openedEditorPart.setDirty(true);

		} catch (Exception e) {
			
			logger.error(e.getMessage(), e);
		}
	}

}