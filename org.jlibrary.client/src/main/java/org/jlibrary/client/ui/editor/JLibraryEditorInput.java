package org.jlibrary.client.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.part.NodeEditorFactory;

/**
 * Base class for all JLibrary Editor's input. 
 * @author nicolasjouanin
 *
 */
public class JLibraryEditorInput
	implements IEditorInput, IPersistableElement
{

	/**
	 * called by Workbench on startup to know if the input represented by this instance
	 * is still valid in the model.
	 * 
	 * @see org.eclipse.ui.IEditorInput@exists()
	 */
	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor()
	{
		return SharedImages.getImageDescriptor(SharedImages.IMAGE_UNK);
	}

	public String getName() {
		return null;
	}

	/**
	 * Tells if the current EditorInput instance will be restorable on startup.
	 * By default, it's not.
	 * Subclasses can test test their repository to know if they will be restorable on startup.
	 */
	public IPersistableElement getPersistable()
	{
		return null;
	}

	public String getToolTipText() {
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getFactoryId() { return NodeEditorFactory.ID;	}

	/**
	 * This implementation only stores the EditorInput type (the class name).
	 * Class extending JLibraryInput should add the needed information to restore them.
	 */
	public void saveState(IMemento memento)
	{
		memento.putString("TYPE", this.getClass().getCanonicalName());
	}

}
