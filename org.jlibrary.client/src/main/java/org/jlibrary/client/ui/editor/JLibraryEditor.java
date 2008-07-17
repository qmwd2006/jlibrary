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
package org.jlibrary.client.ui.editor;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.ui.repository.actions.save.SaveDelegateFactory;
import org.jlibrary.client.ui.repository.actions.save.SavingDelegate;
import org.jlibrary.core.entities.Node;

/**
 * 
 * @author martin
 *
 * Common base class for all the JLibrary editors. You should extend this class
 * if you want to create your own editor.
 * 
 */
public abstract class JLibraryEditor extends FormEditor {
		
	public boolean dirty = false;
	protected Object model;
	
	public static HashMap listeners = new HashMap();

	private boolean needToBeSaved;
	
	public JLibraryEditor() {
		super();
	}
	
	public JLibraryEditor(IWorkbenchPartSite site, IEditorInput input) {
		
		this();
		
		setSite(site);
		setInput(input);
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#pageChange(int)
	 */
	protected void pageChange(int index) {

		super.pageChange(index);
	}
	
	protected abstract void initModel();

	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#createPages()
	 */
	protected void createPages() {
		
		initModel();
		super.createPages();
	}
	
	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	protected void addPages() {}
	
	
	public String getTitle()
	{
		if (getEditorInput() == null) {
			return "";
		}
		//TODO: Replace this trick with the standard Eclipse way
		// I have tried several things, like putting a property on 
		// plugin_customization.ini, and setting the preference constant
		// but there is no way to make it work.
		
		String title = getEditorInput().getName();
		if (title.length() > 20) {
			return title.substring(0,20)+"...";
		}
		return title;
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
	 */
	public Image getTitleImage()
	{
		if (getModel() == null)
			return null;

		return SharedImages.getImage(SharedImages.IMAGE_UNK);
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		if (!(listeners.containsKey(site.getPage()))) {
			site.getPage().addPartListener(new JLibraryEditorPartListener());
			listeners.put(site.getPage(),null);
		}

		super.init(site, input);
	}

	public boolean isFragmentEditor() {
		return false;
	}
	protected boolean isModelCorrect(Object model) {
		return true;
	}
	protected boolean isModelDirty(Object model) {
		
		return false;
	}

	protected boolean updateModel() {

		return true;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#getPartName()
	 */
	public String getPartName() {
		
		//TODO: I didn't manage to get working the Eclipse dirty title update.
		//
		//If I call to super.xxx methods, Eclipse RCP seems to not perform a 
		// check on the editor dirty status, and by consequence the * symbol 
		// isn't shown. So, I emplemented a custom procedure to show the * symbol
		// on dirty editors.
		
		return getTitle();
		//return super.getPartName();
	}
		
	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {

	}
	
	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return null;
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
	}
	
	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#close(boolean)
	 */
	public void close(boolean save) {
		
		super.close(save);

		String key = null;
		Object modelObject = getModel();
		if (modelObject instanceof Node) {
			key = ((Node)modelObject).getId();
		} else {
			key = modelObject.toString();
		}
		
		EditorsRegistry.getInstance().remove(key);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
	 */
	public boolean isDirty() {
		
		if (!checkCanSave()) return false;
		return isMarkedDirty();
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded() {
		return super.isSaveOnCloseNeeded();
	}
	
	public boolean isMarkedDirty() {
		
		if (dirty) return true;
		
		return super.isDirty();
	}
	
	/**
	 * Checks if an editor can be saved
	 * 
	 * @return boolean <code>true</code> if the editor can be saved and 
	 * <code>false</code> otherwise.
	 */
	public abstract boolean checkCanSave();
	
	public void setDirty(boolean dirty) {
		
		this.dirty = dirty;		
		super.editorDirtyStateChanged();
	}

	public void updateTitle(String title) {
		firePropertyChange(IWorkbenchPartConstants.PROP_PART_NAME);
		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
		firePropertyChange(IWorkbenchPartConstants.PROP_TITLE);
	}
	
	public void updateTitle() {
		firePropertyChange(IWorkbenchPartConstants.PROP_PART_NAME);
		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
		firePropertyChange(IWorkbenchPartConstants.PROP_TITLE);
	}
	
	protected void updateMenu() {}
	
	public void updateContents() {}
	
	public void refreshContents() {}
	
	/**
	 * This is a callback method that informs the editor that its contents has 
	 * been successfully saved.
	 * 
	 * Default implementation does nothing
	 */
	public void editorSaved() {
		
		needToBeSaved = false;
	}
	
	public boolean isNeedToBeSaved() {
		return needToBeSaved;
	}
	
	/* (non-Javadoc)
	 * @see org.jlibrary.client.ui.editor.JLibraryMultiPageEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {

		final SavingDelegate delegate = 
			SaveDelegateFactory.getSavingDelegate(getModel());
		int taskLength = delegate.loadProcessLength(getModel());
		monitor.beginTask(Messages.getMessage("job_save"),taskLength);
		
		delegate.doSave(monitor,this);
		
		delegate.postJobTasks(this,getModel());
		
		updateTitle();
		editorSaved();						

		needToBeSaved = true;
		monitor.done();
	}
	
	/**
	 * If this method returns <code>true</code>, then the editor will ignore 
	 * the changes of the user and won't ask him for save changes.
	 * <p/>
	 * Default implementation returns <code>false</code>. If you need an editor 
	 * to ignore user changes and don't annoy him asking confirmation, you can 
	 * always return <code>true</code> from this method.
	 * 
	 * @return <code>true</code> to not ask the user for changes and to don't
	 * save those changes, <code>false</code> to have a normal behaviour, 
	 * asking the user for changes.
	 */
	public boolean isNotAskAndSave() {
		
		return false;
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		
		return false;
	}
	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */	
	public void doSaveAs() {}
	
	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}	
}

