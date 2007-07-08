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
package org.jlibrary.client.ui.authors.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author root
 * 
 * Wizard for creating a new resource
 */
public class NewAuthorWizard extends Wizard {

	static Logger logger = LoggerFactory.getLogger(NewAuthorWizard.class);
	
	private NewAuthorWizardPage authorPage;

	private Repository repository;

	private Author author = null;

	/**
	 * 
	 * @param repository
	 */
	public NewAuthorWizard(Repository repository) {

		super();
		this.repository = repository;
		setWindowTitle(Messages.getMessage("new_author_wizard_title"));
		setNeedsProgressMonitor(true);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		authorPage = new NewAuthorWizardPage(
				Messages.getMessage("new_author_wizard_name"),
				Messages.getMessage("new_author_wizard_description"));
		addPage(authorPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		if (repository == null) {
			return false;
		}
		logger.info("Trying to create a new Author");
		// Create author
		ServerProfile serverProfile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		RepositoryService repositoryService = JLibraryServiceFactory.getInstance(serverProfile).getRepositoryService();
		try {
			// OK create new Author in repository
			AuthorProperties properties = new AuthorProperties();
			properties.addProperty(
					AuthorProperties.AUTHOR_NAME,
					authorPage.getAuthorName());
			
			properties.addProperty(
					AuthorProperties.AUTHOR_BIO,
					authorPage.getAuthorBioDescription());

			author = repositoryService.createAuthor(ticket,properties);
		} catch (AuthorAlreadyExistsException aaex) {
			showError(Messages.getAndParseValue("author_already_exists","%1",authorPage.getAuthorName()));
			return false;
		} catch (final SecurityException se) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(new Shell(), "ERROR", Messages.getMessage("security_exception"), new Status(IStatus.ERROR, "JLibrary", 101, se
							.getMessage(), se));
					StatusLine.setErrorMessage(se.getMessage());
				}
			});
		} catch (RepositoryException e) {
			showError(Messages.getAndParseValue("author_already_exists","%1",authorPage.getAuthorName()));
			return false;
		} catch (final Exception e) {
			logger.error("Can't create new Author", e);
			MessageDialog.openInformation(getShell(), Messages.getMessage("error_create_author"), Messages.getMessage("error_create_author"));
		}
		return true;
	}

	/**
	 * 
	 * @param message
	 */
	private void showError(String message) {
		authorPage.setMessage(message, IMessageProvider.ERROR);
	}

	/**
	 * Return the Author Object
	 * 
	 * @return
	 */
	public Author getAuthor() {
		return author;
	}

}
