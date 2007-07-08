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

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.web.HistoryTracker;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.IResource;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Listener for the different editors
 */
public class JLibraryEditorPartListener implements IPartListener {
	
	static Logger logger = LoggerFactory.getLogger(JLibraryEditorPartListener.class);
	
	StringBuffer buffer = new StringBuffer();
	
	public void partActivated(IWorkbenchPart part) {

		if (part instanceof JLibraryEditor) {
			buffer.delete(0,buffer.length());
			JLibraryEditor editor = (JLibraryEditor)part;
			if (editor.getModel() instanceof IResource) {
				if (part instanceof JLibraryEditor) {
					// Update views
					if (JLibraryPlugin.findRelationsView() != null) {
						JLibraryPlugin.findRelationsView().refresh();
					}
				}
				
				IResource resource = (IResource)editor.getModel();
				Repository repository = RepositoryRegistry.getInstance().
										getRepository(resource.getRepository());
				String userName = repository.getTicket().getUser().getName();
				if (repository.getTicket().getUser().equals(User.ADMIN_USER)) {
					userName = Messages.getMessage(userName);
				}
				
				ServerProfile profile = repository.getServerProfile();
				String profileName = profile.getLocation();
				if (profile.isLocal()) {
					profileName = Messages.getMessage(profileName);
				}
				
				buffer.append(Messages.getMessage("jlibrary_title"));
				buffer.append("  ");
				buffer.append(resource.getName());
				buffer.append(" - ");
				buffer.append(repository.getName());
				buffer.append(" (");
				buffer.append(profileName);
				buffer.append(") - ");
                buffer.append(Messages.getMessage("connected_as"));                                
                buffer.append(" ");
				buffer.append(userName);				
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setText(buffer.toString());
				
				editor.updateMenu();
			}
		}
	}
	public void partBroughtToTop(IWorkbenchPart part) {
		
		if (part instanceof JLibraryEditor) {
			JLibraryEditor editor = (JLibraryEditor)part;
			if (editor.getModel() instanceof Document) {
				if (part instanceof JLibraryEditor) {
					// Update views
					RelationsView relationsView = 
						JLibraryPlugin.findRelationsView();
					if (relationsView != null) {
						relationsView.refresh();
					}
				}			
			}
		}
	}

	public void partClosed(IWorkbenchPart part) {
	
		if (!(part instanceof JLibraryEditor)) {
			return;
			
		}
		JLibraryEditor editor = (JLibraryEditor)part;
		
		// Remove the editor from the editors registry
		EditorsRegistry.getInstance().remove(editor);
		
		if (EditorsRegistry.getInstance().size() == 0) {
			String title = Messages.getMessage("jlibrary_title");
			PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getShell().setText(title);
		}
		
		if (editor.isDirty()) {
			if (editor.getModel() instanceof Node) {
				Node node = (Node)editor.getModel();

				// In this case the document has been changed, but the 
				// changes have been cancelled.
				// So, we need to clear the document cache as the document
				// is in an invalid state
				LocalCache localCache = LocalCacheService.getInstance().getLocalCache();
				try {
					localCache.removeNodeFromCache((Node)editor.getModel());
				} catch (LocalCacheException e) {
					logger.error(e.getMessage(),e);
				}
				
				if (node.isDocument()) {
					Document document = (Document)node;
					document.restoreState();
				} else if (node.isResource()) {
					ResourceNode resourceNode = (ResourceNode)node;
					resourceNode.restoreState();
				}

			} else if (editor.getModel() instanceof User) {
				User user = (User)editor.getModel();
				user.restoreState();					
			} else if (editor.getModel() instanceof Group) {
				Group group = (Group)editor.getModel();
				group.restoreState();					
			} else if (editor.getModel() instanceof Rol) {
				Rol rol = (Rol)editor.getModel();
				rol.restoreState();					
			}

			HistoryTracker.remove(editor);

		}
	}
		
	public void partDeactivated(IWorkbenchPart part) {
		
	}
	public void partOpened(IWorkbenchPart part) {
		
		if (part instanceof JLibraryEditor) {
			JLibraryEditor editor = (JLibraryEditor)part;
			if (editor.getModel() instanceof Document) {
				Document document = (Document)editor.getModel();
				document.saveState();
			} else if (editor.getModel() instanceof ResourceNode) {
				ResourceNode resourceNode = (ResourceNode)editor.getModel();
				resourceNode.saveState();
			} else if (editor.getModel() instanceof User) {
				User user = (User)editor.getModel();
				user.saveState();
			} else if (editor.getModel() instanceof Rol) {
				Rol rol = (Rol)editor.getModel();
				rol.saveState();
			} else if (editor.getModel() instanceof Group) {
				Group group = (Group)editor.getModel();
				group.saveState();
			}
		}
	}
	
}
