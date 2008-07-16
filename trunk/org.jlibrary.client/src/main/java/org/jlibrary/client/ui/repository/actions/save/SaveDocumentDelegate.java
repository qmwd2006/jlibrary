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
package org.jlibrary.client.ui.repository.actions.save;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jlibrary.cache.LocalCache;
import org.jlibrary.cache.LocalCacheException;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.cache.NodeContentHandler;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.ui.categories.CategoriesView;
import org.jlibrary.client.ui.categories.DocumentsView;
import org.jlibrary.client.ui.editor.GenericEditor;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.editor.forms.MetadataFormPage;
import org.jlibrary.client.ui.editor.forms.ResourcesSection;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryHelper;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.repository.views.ResourcesView;
import org.jlibrary.client.ui.versions.EditorVersionRegistry;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.UnknownMethodException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.JLibraryAPIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that handles document saving
 * 
 * @author mpermar
 *
 */
public class SaveDocumentDelegate extends SavingDelegate {

	static Logger logger = LoggerFactory.getLogger(SaveDocumentDelegate.class);
	
	private Document updatedDocument;

	public int loadProcessLength(Object object) {

		return 1;
	}
		
	public IStatus doSave(IProgressMonitor monitor,
						  final JLibraryEditor editor) {

		Document document = (Document)editor.getModel();
	    Repository repository = RepositoryRegistry.
	    		getInstance().getRepository(document.getRepository());

		final ServerProfile profile = repository.getServerProfile();
		final Ticket ticket = repository.getTicket();
		
		logger.debug("Saving document");
		
		RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			DocumentProperties docProperties = document.dumpProperties();
			boolean dirty = isEditorDirty(editor);			
			monitor.subTask(Messages.getAndParseValue("job_save_document","%1",document.getName()));

            String apiVersion;
            boolean version1_2;
            try {
                apiVersion = service.getJLibraryAPIVersion();
                version1_2 = JLibraryAPIUtils.equalsOrExceeds(apiVersion,
                        JLibraryConstants.VERSION_1_2);
            } catch (UnknownMethodException e) {
                // the server is old, it does not understand this
                version1_2 = false;
            }
			
            updatedDocument = null;
			if (dirty) {
				InputStream is = null;
				try {
					is = ((FileEditorInput) editor.getEditorInput()).getFile().getContents();
                    if (version1_2) {
                        // in this version, a method which allows to save both
                        // meta-data and data was added
    					updatedDocument = service.updateDocument(ticket, docProperties, is);
                    } else {
                        // using the old way: saving meta-data and data in the
                        // separate calls
                        updatedDocument = service.updateDocument(ticket, docProperties);
    				    service.updateContent(ticket, document.getId(), is);
                    }
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}
				}
			} else {
                // only the meta-data has to be updated
                updatedDocument = service.updateDocument(ticket,docProperties);
            }
			
			// With this we update the node tree versions.
			document.setLastVersionId(updatedDocument.getLastVersionId());
			// With this we deselect the node, forcing actions update on the 
			// context menu.
			// TODO: There must be another way to force selection update events
			// on the node.
			RepositoryView.getRepositoryViewer().setSelection(null);
			
			monitor.worked(1);
			
			updateResources(ticket,
							service,
							document.getInsertedResources(),
							document.getDeletedResources(),
							updatedDocument,
							monitor);
			//document.saveState();
			
			if (editor instanceof JLibraryEditor) {
				((JLibraryEditor)editor).setDirty(false);
			}		
			
			// Update the entity registry
			EntityRegistry.getInstance().addNode(updatedDocument);
			Node parent = EntityRegistry.getInstance().getNode(
					updatedDocument.getParent(),updatedDocument.getRepository());
			parent.getNodes().remove(document);
			parent.getNodes().add(updatedDocument);
			
			return Status.OK_STATUS;
			
		} catch (final SecurityException se) {
			return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,Messages.getMessage("not_enough_permissions"),se);
		} catch (final Exception e) {
			logger.error(e.getMessage(),e);
			return new Status(Status.ERROR,"org.jlibrary.client",Status.OK,e.getMessage(),e);
		}	
		
	}
	
	public void postJobTasks(JLibraryEditor editor, Object object) {
        if (updatedDocument == null) {
            // this helps to avoid NPE when something fails during document update
            return;
        }
		Document document = (Document)object;
		RepositoryView.getInstance().changeNode(document,updatedDocument);
		
		((JLibraryEditor)editor).updateContents();
		// The name could have changed
		((JLibraryEditor)editor).updateTitle(document.getName());

		if (editor instanceof GenericEditor) {
			MetadataFormPage metadataFormPage = ((GenericEditor)editor).getMetadataFormPage();
			if (metadataFormPage != null) {
				ResourcesSection resourcesSection = metadataFormPage.getResourcesSection();
				if (resourcesSection != null) {
					resourcesSection.updateResources();
				}
			}
		}
		
	    Repository repository = 
			RepositoryRegistry.getInstance().getRepository(document.getRepository());				
        
		if (EditorVersionRegistry.isDocumentVersion(document)) {
			Document original = EditorVersionRegistry.getOriginalDocument(document);
			Directory parent = (Directory)RepositoryView.getInstance().getParentNode(original);
			if (parent != null) {
				parent.getNodes().remove(original);
				parent.getNodes().add(document);
				RepositoryView.getRepositoryViewer().refresh(parent);
			}
		}
	    
	    Node parent = EntityRegistry.getInstance().getNode(
	    		document.getParent(),document.getRepository());
		if (parent == null) {
			RepositoryView.getRepositoryViewer().refresh(repository);
			RepositoryView.getRepositoryViewer().expandToLevel(repository,1);		 		
		} else {
			RepositoryView.getRepositoryViewer().refresh(parent);
			RepositoryView.getRepositoryViewer().expandToLevel(parent,1);
		}
		
		if (CategoriesView.getInstance() != null) {
			CategoriesView.getInstance().refresh();
		}
		if (DocumentsView.getInstance() != null) {
			DocumentsView.getInstance().refreshDocuments();
		}
		
		// refresh relations view
		if (RelationsView.getInstance() != null) {
			RelationsView.getInstance().refresh();
		}

		ResourcesView.refresh();
		
		editor.updateTitle();
		editor.editorSaved();
		
		// Update properties view
		PropertySheet propertySheet = JLibraryPlugin.findPropertiesView();
		if (propertySheet != null) {
			PropertySheetPage page = (PropertySheetPage)propertySheet.getCurrentPage();
			if (page != null) {
				page.refresh();
			}
		}		
	}
	
	private void updateResources(final Ticket ticket, 
								 final RepositoryService service,
								 Collection insertedResources,
								 Collection deletedResources,
								 Document updatedNode, 
								 IProgressMonitor monitor) throws RepositoryException, 
								 								  SecurityException {

		if (insertedResources != null) {
			Node parent = 
				EntityRegistry.getInstance().getNode(
						updatedNode.getParent(),updatedNode.getRepository());
			Repository repository =
				RepositoryRegistry.getInstance().getRepository(updatedNode.getRepository());

			Iterator it = insertedResources.iterator();
			while (it.hasNext()) {
				final ResourceNode resource = (ResourceNode) it.next();
				monitor.subTask(Messages.getAndParseValue(
						"job_save_add_resource","%1",resource.getName()));
				if (resource.getId() == null) {
					// 	New resource
					String path = resource.getPath();
					RepositoryHelper.createResource(repository,
													(Directory)parent,
													updatedNode,
													new File(path));
				} else {
					// Existing resource
					service.addResourceToDocument(ticket,
												  resource.getId(),
												  updatedNode.getId());
					LocalCache cache = 
						LocalCacheService.getInstance().getLocalCache();
					try {
						cache.addNodeToCache(resource,new NodeContentHandler() {
							public void copyTo(OutputStream os) throws LocalCacheException {

								try {
									service.loadResourceNodeContent(ticket, resource.getId(), os);
								} catch (Exception e) {
									logger.error(e.getMessage(),e);
									throw new LocalCacheException(e);
								}
							}
						});
						updatedNode.getResourceNodes().add(resource);
					} catch (LocalCacheException lce) {
						throw new RepositoryException(lce);
					}
				}
				monitor.worked(1);
			}
		}
		if (deletedResources != null) {
			Iterator it = deletedResources.iterator();
			while (it.hasNext()) {
				ResourceNode resource = (ResourceNode) it.next();
				monitor.subTask(Messages.getAndParseValue(
						"job_save_remove_resource","%1",resource.getName()));
				service.removeResourceNode(ticket,
										   resource.getId(),
										   updatedNode.getId());
				monitor.worked(1);
				updatedNode.getResourceNodes().remove(resource);
			}
		}
	}
}
