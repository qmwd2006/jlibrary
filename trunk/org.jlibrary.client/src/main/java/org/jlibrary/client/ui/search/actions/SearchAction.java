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
package org.jlibrary.client.ui.search.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.SharedImages;
import org.jlibrary.client.actions.ActionFactory;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.ui.dialogs.MessageDialog;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.search.SearchView;
import org.jlibrary.client.ui.search.dialogs.SearchDialog;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.search.SearchException;
import org.jlibrary.core.search.SearchHit;
import org.jlibrary.core.search.SearchResultSet;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * This action will be called to show search dialog
 */
public class SearchAction 	extends Action 
							implements ActionFactory.IWorkbenchAction {
	

	static Logger logger = LoggerFactory.getLogger(SearchAction.class);
	
	private IWorkbenchWindow window;
	private boolean searchContents;
	private boolean searchKeywords;
	private boolean searchXPath;
	private String searchText;

	private Collection repositories;
	protected SearchResultSet resultSet;

	/**
	 * Constructor
	 * 
	 * @param Window application window
	 */
	public SearchAction(IWorkbenchWindow window) {
		
		super();
		this.window = window;
		setText(Messages.getMessage("item_search"));
		setToolTipText(Messages.getMessage("tooltip_search"));
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_SEARCH));
		setDisabledImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMAGE_SEARCH_DISABLED));
	}

	public boolean isEnabled() {

		return true;
	}	
	
	public void run() {
		run(null);
	}
	
	public void run(Repository repository) {
		
		
		logger.info("Searching ");

		if (RepositoryRegistry.getInstance().getRepositoryCount() == 0) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.getMessage("search_no_repository_title"), 
					Messages.getMessage("search_no_repository_text"));
			return;

		}
		
		SearchDialog searchDialog = SearchDialog.getInstance(window);
		int result;
		if (repository == null) {
			result = searchDialog.open();
		} else {
			result = searchDialog.open(repository);
		}
		
		if (result == IDialogConstants.OK_ID) {

			boolean searchAll = searchDialog.isSearchAllEnabled();
			repositories = RepositoryRegistry.getInstance().getOpenedRepositories();
			if (!searchAll) {
				repositories = new ArrayList();
				repositories.add(searchDialog.getRepository());
			}
			
			searchContents = searchDialog.isSearchContentsEnabled();
			searchKeywords = searchDialog.isSearchKeywordsEnabled();
			searchXPath = searchDialog.isSearchXPathEnabled();
			searchText = searchDialog.getSearchText();
			
			JobTask jobTask = new JobTask(Messages.getMessage("search_job_name")) {
			
				public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

					monitor.beginTask(Messages.getMessage("search_job_name"),
									  IProgressMonitor.UNKNOWN);
					monitor.subTask(Messages.getMessage("search_job_name"));

					try {
						resultSet = search(repositories,monitor);
					} catch (SecurityException se) {
						throw new JobTaskException(se);
					} catch (SearchException se) {
						throw new JobTaskException(se);
					} catch (Exception e) {
						throw new JobTaskException(e);
					}

					monitor.done();
					return Status.OK_STATUS;					
				}
				
				public void postJobTasks() throws JobTaskException {

					SearchView.searchPerformed(resultSet);
					JLibraryPlugin.showView(SearchView.VIEW_ID);
				}
			};
			
			new JobRunner().run(jobTask);
		}
	}
	private SearchResultSet search(Collection repositories, 
								   IProgressMonitor monitor) throws SecurityException,
								   									SearchException,
								   									RepositoryException {
		
		SearchResultSet resultSet = new SearchResultSet();
		ArrayList results = new ArrayList();
		Iterator it = repositories.iterator();
		while (it.hasNext()) {
			Repository repository = (Repository) it.next();
			monitor.beginTask(Messages.getAndParseValue("search_job_step","%1",repository.getName()),1);
			results.addAll(search(	repository,						
						 			searchText,
						 			searchXPath,
									searchContents,
									searchKeywords));
			monitor.worked(1);
			createResultSet(results,resultSet);
			results.clear();
		}					
		
		if (searchText.length() > 100) {
			resultSet.setDescription(searchText.substring(0,100) + "...");
		} else {
			resultSet.setDescription(searchText);
		}
		
		resultSet.setDescription(resultSet.getDescription() + 
								 Messages.getAndParseValue("search_found",
								 						   "%1",
								 						   String.valueOf(resultSet.getItems().size())));
		
		return resultSet;
	}

	private SearchResultSet createResultSet(List results,
											SearchResultSet resultSet) throws RepositoryException, 
																		  	  SecurityException {
		
		Iterator it = results.iterator();
		while (it.hasNext()) {
			SearchHit hit = (SearchHit)it.next();
			resultSet.add(hit);
		}
		return resultSet;
	}

	private List search(Repository repository, 
						String text,
						boolean searchXPath,
						boolean searchContents,
						boolean searchKeywords) throws SearchException {
		
		ArrayList results = new ArrayList();
		
		ServerProfile serverProfile = repository.getServerProfile();
		Ticket ticket = repository.getTicket();
		SearchService searchService = 
			JLibraryServiceFactory.getInstance(serverProfile).getSearchService();
		
		if (searchContents) {
			results.addAll(searchService.search(ticket,
												text,
												SearchService.SEARCH_CONTENT));
		}
		if (searchKeywords) {
			Collection result = searchService.search(ticket,
												text,
												SearchService.SEARCH_KEYWORDS);
			results.addAll(result);
		}
		if (searchXPath) {
			Collection result = searchService.search(ticket,text);
			results.addAll(result);
		}
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {

	}
}
