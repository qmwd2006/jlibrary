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
package org.jlibrary.client.ui.repository.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.SelectionDispatchAction;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.part.FileEditorInput;
import org.jlibrary.client.part.NodeEditorFactory;
import org.jlibrary.client.security.SecurityManager;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.editor.AuthorEditorInput;
import org.jlibrary.client.ui.editor.CategoryEditorInput;
import org.jlibrary.client.ui.editor.DirectoryEditorInput;
import org.jlibrary.client.ui.editor.EditorsRegistry;
import org.jlibrary.client.ui.editor.GroupEditorInput;
import org.jlibrary.client.ui.editor.RepositoryEditorInput;
import org.jlibrary.client.ui.editor.RolEditorInput;
import org.jlibrary.client.ui.editor.UserEditorInput;
import org.jlibrary.client.ui.editor.impl.AuthorEditor;
import org.jlibrary.client.ui.editor.impl.CategoryEditor;
import org.jlibrary.client.ui.editor.impl.DirectoryEditor;
import org.jlibrary.client.ui.editor.impl.GroupEditor;
import org.jlibrary.client.ui.editor.impl.RepositoryEditor;
import org.jlibrary.client.ui.editor.impl.RolEditor;
import org.jlibrary.client.ui.editor.impl.SystemGenericEditor;
import org.jlibrary.client.ui.editor.impl.UserEditor;
import org.jlibrary.client.ui.error.ExceptionsHelper;
import org.jlibrary.client.ui.relations.RelationsView;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.util.NodeUtils;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 * 
 * This action will be called to open a document
 */
public class OpenAction extends SelectionDispatchAction {

	static Logger logger = LoggerFactory.getLogger(OpenAction.class);
	
	private IEditorPart openedEditorPart;

	public OpenAction() {
	}

	/**
	 * Constructor
	 * 
	 * @param Window
	 *            application window
	 */
	public OpenAction(IWorkbenchSite site) {

		super(site);

		setText(Messages.getMessage("item_open"));
		setToolTipText(Messages.getMessage("tooltip_open"));
		setImageDescriptor(SharedImages
				.getImageDescriptor(SharedImages.IMAGE_OPEN));
		setImageDescriptor(SharedImages
				.getImageDescriptor(SharedImages.IMAGE_OPEN_DISABLED));

		update(getSelection());
	}

	/*
	 * (non-Javadoc) Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {

		setEnabled(canOperateOn(selection));
	}

	private boolean canOperateOn(IStructuredSelection selection) {

		if (selection.isEmpty()) {
			return false;
		}

		Object element = selection.toArray()[0];

		return checkCanRun(element);
	}

	/*
	 * (non-Javadoc) Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {

		if (!canOperateOn(selection))
			return;
		run(selection.toArray());
	}

	public void run(Node node) {

		run(new Object[] { node });
	}

	public void run(Object[] elements) {

		// This action can be called explicitly so is necessary to perform
		// additional security checks
		for (int i = 0; i < elements.length; i++) {
			if (!checkCanRun(elements[i])) {
				return;
			}
		}

		
		Object selectedElement = elements[0];

		try {
			if (selectedElement instanceof Repository) {
				IEditorPart iep = EditorsRegistry.getInstance().getEditor(
						((Repository) (selectedElement)).getId());
				if (iep == null)
					openRepository((Repository) selectedElement);
				else
					openedEditorPart = JLibraryPlugin.getActivePage()
							.openEditor(iep.getEditorInput(),
									RepositoryEditor.EDITOR_ID);
				return;
			}
			if (selectedElement instanceof Rol) {
				IEditorPart iep = EditorsRegistry.getInstance().getEditor(
						((Rol) (selectedElement)).getId());
				if (iep == null)
					openRol((Rol) selectedElement);
				else
					openedEditorPart = JLibraryPlugin.getActivePage()
							.openEditor(iep.getEditorInput(),
									RolEditor.EDITOR_ID);
				return;
			}

			if (selectedElement instanceof User) {
				IEditorPart iep = EditorsRegistry.getInstance().getEditor(
						((User) (selectedElement)).getId());
				if (iep == null)
					openUser((User) selectedElement);
				else
					openedEditorPart = JLibraryPlugin.getActivePage()
							.openEditor(iep.getEditorInput(),
									UserEditor.EDITOR_ID);
				return;
			}

			if (selectedElement instanceof Group) {
				IEditorPart iep = EditorsRegistry.getInstance().getEditor(
						((Group) (selectedElement)).getId());
				if (iep == null)
					openGroup((Group) selectedElement);
				else
					openedEditorPart = JLibraryPlugin.getActivePage()
							.openEditor(iep.getEditorInput(),
									GroupEditor.EDITOR_ID);
				return;
			}

			if (selectedElement instanceof Category) {
				IEditorPart iep = EditorsRegistry.getInstance().getEditor(
						((Category) (selectedElement)).getId());
				if (iep == null)
					openCategory((Category) selectedElement);
				else
					openedEditorPart = JLibraryPlugin.getActivePage()
							.openEditor(iep.getEditorInput(),
									CategoryEditor.EDITOR_ID);
				return;
			}

			if (selectedElement instanceof Author) {
				IEditorPart iep = EditorsRegistry.getInstance().getEditor(
						((Author) (selectedElement)).getId());
				if (iep == null)
					openAuthor((Author) selectedElement);
				else
					openedEditorPart = JLibraryPlugin.getActivePage()
							.openEditor(iep.getEditorInput(),
									AuthorEditor.EDITOR_ID);
				return;
			}

			// Always actualize the node with the more recent instance
			Node selectedNode = (Node) selectedElement;
			Repository repository = RepositoryRegistry.getInstance()
					.getRepository(selectedNode.getRepository());
			ServerProfile profile = repository.getServerProfile();
			Ticket ticket = repository.getTicket();
			RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
			Node node = service.findNode(ticket, selectedNode.getId());
			// Update the entity registry
			EntityRegistry.getInstance().addNode(node);

			if (node == null) {
				logger.info("Error when trying to open a document that isn't in the Entity Registry.");
				return;
			}

			if (node.isDocument()) {
				Document document = (Document) node;
				if (document.isExternal()) {
					// TODO: Add support for external documents
				}
				if (document.isReference()) {
					// TODO: Add support for URL references
				}
			}

			if (node.isDirectory()) {
				IEditorPart iep = EditorsRegistry.getInstance().getEditor(
						node.getId());
				if (iep == null)
					openDirectory((Directory) node);
				else
					openedEditorPart = JLibraryPlugin.getActivePage()
							.openEditor(iep.getEditorInput(),
									DirectoryEditor.EDITOR_ID);
			} else {
				openNode(ticket, node);
			}
		} catch (final ResourceLockedException rle) {
			ExceptionsHelper.showResourceLockedDialog(rle);
		} catch (final SecurityException se) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(new Shell(), "ERROR", Messages
							.getMessage("security_exception"),
							new Status(IStatus.ERROR, "JLibrary", 101, se
									.getMessage(), se));
					StatusLine.setErrorMessage(se.getMessage());
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void openRepository(Repository repository) throws Exception {

		Repository copy = createCopy(repository);
		RepositoryEditorInput fei = NodeEditorFactory
				.createEditorInput(copy);
		openedEditorPart = JLibraryPlugin.getActivePage().openEditor(fei,
				RepositoryEditor.EDITOR_ID);
		EditorsRegistry.getInstance().put(copy.getId(), openedEditorPart);
	}

	private Repository createCopy(Repository repository) {
		
		Repository copy = new Repository();
		copy.setId(repository.getId());
		copy.setCreator(repository.getCreator());
		copy.setDescription(repository.getDescription());
		copy.setName(repository.getName());
		copy.setConnected(repository.isConnected());
		copy.setPath(repository.getPath());
		copy.setRepositoryConfig(repository.getRepositoryConfig());
		copy.setTicket(repository.getTicket());
		copy.setRoot(repository.getRoot());
		copy.setServerProfile(repository.getServerProfile());
		copy.setCategories(repository.getCategories());
		
		return copy;
	}

	private void openDirectory(Directory directory) throws Exception {

		DirectoryEditorInput dei = NodeEditorFactory.createEditorInput(directory);
		openedEditorPart = JLibraryPlugin.getActivePage().openEditor(dei,
				DirectoryEditor.EDITOR_ID);
		EditorsRegistry.getInstance().put(directory.getId(), openedEditorPart);
	}

	private void openRol(Rol rol) throws Exception {

		// We will update the user for getting latest repository changes
		Repository repository = RepositoryRegistry.getInstance().getRepository(
				rol.getRepository());
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		SecurityService service = JLibraryServiceFactory.getInstance(profile).getSecurityService();
		rol = service.findRol(ticket, rol.getId());

		RolEditorInput fei = NodeEditorFactory.createEditorInput(rol);
		openedEditorPart = JLibraryPlugin.getActivePage().openEditor(fei,
				RolEditor.EDITOR_ID);
		EditorsRegistry.getInstance().put(rol.getId(), openedEditorPart);
	}

	private void openUser(User user) throws Exception {

		// We will update the user for getting latest repository changes
		Repository repository = RepositoryRegistry.getInstance().getRepository(
				user.getRepository());
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		SecurityService service = JLibraryServiceFactory.getInstance(profile).getSecurityService();
		user = service.findUserById(ticket, user.getId());

		UserEditorInput fei = NodeEditorFactory.createEditorInput(user);
		openedEditorPart = JLibraryPlugin.getActivePage().openEditor(fei,
				UserEditor.EDITOR_ID);
		EditorsRegistry.getInstance().put(user.getId(), openedEditorPart);
	}

	private void openGroup(Group group) throws Exception {

		// We will update the group for getting latest repository changes
		Repository repository = RepositoryRegistry.getInstance().getRepository(
				group.getRepository());
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		SecurityService service = JLibraryServiceFactory.getInstance(profile).getSecurityService();
		group = service.findGroupById(ticket, group.getId());
		GroupEditorInput fei = NodeEditorFactory.createEditorInput(group);
		openedEditorPart = JLibraryPlugin.getActivePage().openEditor(fei,
				GroupEditor.EDITOR_ID);
		EditorsRegistry.getInstance().put(group.getId(), openedEditorPart);
	}

	private void openCategory(Category category) throws Exception {

		// We will update the group for getting latest repository changes
		Repository repository = RepositoryRegistry.getInstance().getRepository(
				category.getRepository());
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		category = service.findCategoryById(ticket, category.getId());

		CategoryEditorInput fei = NodeEditorFactory.createEditorInput(category);
		openedEditorPart = JLibraryPlugin.getActivePage().openEditor(fei,
				CategoryEditor.EDITOR_ID);
		EditorsRegistry.getInstance().put(category.getId(), openedEditorPart);
	}

	private void openAuthor(Author author) throws Exception {

		// We will update the group for getting latest repository changes
		Repository repository = RepositoryRegistry.getInstance().getRepository(
				author.getRepository());
		ServerProfile profile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		RepositoryService service = JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		author = service.findAuthorById(ticket, author.getId());

		AuthorEditorInput fei = NodeEditorFactory.createEditorInput(author);
		openedEditorPart = JLibraryPlugin.getActivePage().openEditor(fei,
				AuthorEditor.EDITOR_ID);
		EditorsRegistry.getInstance().put(author.getId(), openedEditorPart);
	}

	private void openNode(Ticket ticket, Node node) throws Exception {

		if (node.getLock() != null) {
			if (!ticket.getUser().getId().equals(node.getLock().getUserId())) {
				SecurityManager securityManager = JLibraryPlugin.getDefault()
						.getSecurityManager();
				if (!securityManager.isAdmin(ticket)) {
					throw new ResourceLockedException(node.getLock());
				}
			}
		}

		FileEditorInput fei = FileEditorInput.createFileEditorInput(node);

		IEditorRegistry registry = PlatformUI.getWorkbench()
				.getEditorRegistry();

		String extension = null;
		IEditorDescriptor desc = null;

		if (NodeUtils.isExtensionUnknown(node)) {
			extension = NodeUtils.getGenericExtension(node);
			desc = registry.getDefaultEditor("default" + extension);
		} else {
			extension = FileUtils.getExtension(node.getPath());
			desc = registry.getDefaultEditor(extension);
			ClientConfig.setDefaultToolForExtension(ClientConfig.DEFAULT_TOOL,
					extension);
		}

		if (desc != null) {
			openedEditorPart = JLibraryPlugin.getActivePage().openEditor(fei,
					desc.getId());
		} else {
			// Open the document in an SYSTEM editor without a content viewer
			openedEditorPart = JLibraryPlugin.getActivePage().openEditor(fei,
					SystemGenericEditor.EDITOR_ID);

			// Open the document in the system editor
			JLibraryPlugin.getActivePage().openEditor(fei,
					IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
		}
		EditorsRegistry.getInstance().put(node.getId(), openedEditorPart);

		// updateViews
		RelationsView relationsView = JLibraryPlugin.findRelationsView();
		if (relationsView != null) {
			relationsView.refresh();
		}

	}

	/**
	 * @return Returns the openedEditorPart.
	 */
	public IEditorPart getOpenedEditorPart() {
		return openedEditorPart;
	}

	private boolean checkCanRun(Object element) {

		SecurityManager securityManager = JLibraryPlugin.getDefault()
				.getSecurityManager();

		// Currently, repositories can't be opened
		if (element instanceof Node) {
			Node node = (Node) element;

			if (node.isDocument()) {
				Document document = (Document) node;
				if (document.isDeletedDocument()) {
					return false;
				}
				if (!securityManager.canPerformAction(node.getRepository(),
						node, SecurityManager.OPEN_DOCUMENT)) {
					return false;
				}
				return true;
			} else if (node.isDirectory()) {
				if (!securityManager.canPerformAction(node.getRepository(),
						node, SecurityManager.OPEN_DIRECTORY)) {
					return false;
				}
				return true;
			} else if (node.isResource()) {
				if (!securityManager.canPerformAction(node.getRepository(),
						node, SecurityManager.OPEN_RESOURCE)) {
					return false;
				}
				return true;
			}
			return false;
		} else if (element instanceof Repository) {
			Repository repository = ((Repository) element);
			if (!securityManager.canPerformAction(repository.getId(),
					repository.getRoot(), SecurityManager.OPEN_REPOSITORY)) {
				return false;
			}
			return true;
		} else if (element instanceof Rol) {
			if (!securityManager.canPerformAction(((Rol) element)
					.getRepository(), SecurityManager.OPEN_ROL)) {
				return false;
			}
			return true;
		} else if (element instanceof Group) {
			if (!securityManager.canPerformAction(((Group) element)
					.getRepository(), SecurityManager.OPEN_GROUP)) {
				return false;
			}
			return true;
		} else if (element instanceof User) {
			if (!securityManager.canPerformAction(((User) element)
					.getRepository(), SecurityManager.OPEN_USER)) {
				return false;
			}
			return true;
		} else if (element instanceof Category) {
			if (!securityManager.canPerformAction(((Category) element)
					.getRepository(), SecurityManager.OPEN_CATEGORY)) {
				return false;
			}
			return true;
		} else if (element instanceof Author) {
			if (!securityManager.canPerformAction(((Author) element)
					.getRepository(), SecurityManager.OPEN_AUTHOR)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
