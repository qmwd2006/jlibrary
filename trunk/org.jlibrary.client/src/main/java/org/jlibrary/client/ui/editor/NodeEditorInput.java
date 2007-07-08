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

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.jlibrary.cache.input.CacheAdapter;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.part.NodeEditorFactory;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.util.File;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 */
public class NodeEditorInput extends FileEditorInput
  implements INodeEditorInput
{
	private Node node;

	public NodeEditorInput(Node node) {
		
		this.node = node;
	}
	
	public String getFactoryId() {

		return NodeEditorFactory.ID;
	}
	
	/**
	 * @param file
	 */
	public NodeEditorInput(File file) {

		super(file);
		this.node = (Node)file.getFileContent();
	}

	/**
	 * @return Returns the node.
	 */
	public Node getNode() {
		return node;
	}	
	
	public String getName() {
		
		if (getFile() != null) {
			return super.getName();
		} else {
			return node.getName();
		}
	}
	
	public String getToolTipText() {

		if (getFile() != null) {
			return super.getToolTipText();
		} else {
			return node.getDescription();
		}
	}
	
	public Object getAdapter(Class adapter) {

		return CacheAdapter.getAdapter(getNode());
	}
	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		memento.putString("REPOSITORY", node.getRepository());
		memento.putString("NODE_ID", node.getId());
	}

	public IPersistableElement getPersistable()
	{
		Repository repository = RepositoryRegistry.getInstance().
			getRepository(node.getRepository());
		if (repository == null) {
			return null;
		}
		if(repository.getTicket().isAutoConnect())
			return this;
		return null;
	}

}
