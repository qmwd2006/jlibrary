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
package org.jlibrary.client.ui.web;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.Messages;
import org.jlibrary.client.config.ClientConfig;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.client.ui.repository.RepositoryHelper;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.jlibrary.client.ui.web.dialogs.CrawlSelectionDialog;
import org.jlibrary.client.util.URL;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.search.extraction.HTMLExtractor;
import org.jlibrary.core.search.extraction.html.HTMLRipper;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author martin
*
* Manager for web crawling tasks
*/
public class CrawlManager {

	static Logger logger = LoggerFactory.getLogger(CrawlManager.class);
	
	private static CrawlManager instance;

	private Comparator directoryComparator = new Comparator() {
		public int compare(Object arg0, Object arg1) {

			File file0 = (File)arg0;
			File file1 = (File)arg1;

			if (file0.isDirectory() && file1.isFile()) {
				return 1;
			}
			if (file0.isFile() && file1.isDirectory()) {
				return -1;
			}
			return 0;
		}
	};

	private String crawlingDirectory;

	public CrawlManager() {

		crawlingDirectory = ClientConfig.getValue(ClientConfig.JLIBRARY_HOME) +
							System.getProperty("file.separator") +
							".webcache";
		File file = new File(crawlingDirectory);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	public void crawlMultiple(final URL url, final Directory directory)
	{
		final Repository repository = RepositoryRegistry.getInstance().
									getRepository(directory.getRepository());
		final String directoryPath = crawlingDirectory +
									 System.getProperty("file.separator") +
									 directory.getId();

		final File file = new File(directoryPath);
		if (!file.exists())
			file.mkdirs();

	    JobTask jobTaskExtractor = new JobTask(
	    		Messages.getMessage("analyzing_job_name"))
	    {
	    	Object resources;
	    	public IStatus run(IProgressMonitor monitor) 
	    								throws 	OperationCanceledException, 
	    										JobTaskException
	    	{
	    		IStatus status = null;
	            
	    		monitor.beginTask(Messages.getMessage("analyzing_job_name"),
	    						  IProgressMonitor.UNKNOWN);
	            try
	            {
	            	HTMLExtractor extractor = new HTMLExtractor();
	            	final String[] res = extractor.extractResources(url.getURL());
	            	if (monitor.isCanceled())
	            	{
	            		status = Status.CANCEL_STATUS;
	            		return status;
	            	}
	            	else
	            		status = Status.OK_STATUS;
	            	String urlString = url.getURL().toString();
	            	String parentURL = urlString.substring(
	            				0,urlString.lastIndexOf('/')+1);
					for (int i = 0; i < res.length; i++)
					{
						if (!res[i].startsWith("http://") &&!res[i].startsWith("ftp://"))
						{
							res[i] = parentURL + res[i];
							if (monitor.isCanceled())
							{
								status = Status.CANCEL_STATUS;
								return status;
							}
						}
					}

					resources = new String[res.length+1];
					monitor.internalWorked(1);
					System.arraycopy(res,0,resources,1,res.length);
					((String[])resources)[0] = url.getURL().toString();
					if (monitor.isCanceled())
						status = Status.CANCEL_STATUS;
					else
						status = Status.OK_STATUS;
					monitor.internalWorked(1);
					monitor.done();
            	}
	            catch (OperationCanceledException oce)
	            {
	            	status = Status.CANCEL_STATUS;
	            }
	            catch (Exception e)
	            {
	            	status = Status.CANCEL_STATUS;
	            	throw new JobTaskException(e);
	            }
	            return status;
	    	}
	    	public void postJobTasks() throws JobTaskException
	    	{
	    		crawlStorage((String[] )resources ,   directory,file,repository);
	    	}
	    };
	    jobTaskExtractor.setPriority(Job.LONG);
	    new JobRunner().run(jobTaskExtractor);
	}

	private void crawlStorage(final String resources [],	
							  final Directory 
							  directory, 
							  final File file,
							  final Repository repository)
	{
	    // Show the multiple selection dialog
	    CrawlSelectionDialog csd = new CrawlSelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
					resources);
	    csd.open();
		if (csd.getReturnCode() == IDialogConstants.OK_ID)
		{
			final Object urls[] = csd.getCheckedElements();
			JobTask jobTask = new JobTask(
			Messages.getMessage("spider_job_name"))
			{
			  public IStatus run(IProgressMonitor monitor)
					throws OperationCanceledException,JobTaskException
			  {

				monitor.beginTask(Messages.getMessage("spider_job_name"),urls.length*2);
				HTMLRipper ripper = new HTMLRipper();
				IStatus status = null;
				try
				{
				  for (int i=0; i< urls.length;i++)
				  {
					ripper.crawlFile(file,(String)urls[i]);
					monitor.internalWorked(1);
					createStructure(repository,directory,file,false,monitor);
					cleanCrawlingSubDirectories(file);
					if (monitor.isCanceled())
						break;
					monitor.internalWorked(1);
				  }
				  if (monitor.isCanceled())
				  	status = Status.CANCEL_STATUS;
			      else
			      	status = Status.OK_STATUS;
				  monitor.internalWorked(1);
				}
				catch (OperationCanceledException oce)
				{
					status = Status.CANCEL_STATUS;
				}
				catch (SecurityException se)
				{
					throw new JobTaskException(se);
				}
				catch (Exception e)
				{
					throw new JobTaskException(e);
				}
				monitor.done();
				return status;
			  }

			  public void postJobTasks() throws JobTaskException
			  {
				if (RepositoryView.getRepositoryViewer() != null)
				{
				  RepositoryView.getRepositoryViewer().refresh();
				  RepositoryView.getRepositoryViewer().refresh(directory);
				  RepositoryView.
				  getRepositoryViewer().expandToLevel(directory,1);
				}
			  }
			};
			jobTask.setPriority(Job.LONG);
			new JobRunner().run(jobTask);
		}
	}


	public void crawl(final URL[] urls, final Directory directory) {

		final Repository repository = RepositoryRegistry.getInstance().
									getRepository(directory.getRepository());

		final String directoryPath = crawlingDirectory +
									 System.getProperty("file.separator") +
									 directory.getId();

		final File file = new File(directoryPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		JobTask jobTask = new JobTask(Messages.getMessage("spider_job_name")) {

			public IStatus run(IProgressMonitor monitor)
											throws OperationCanceledException,
												   JobTaskException {

				monitor.beginTask(Messages.getMessage("spider_job_name"),
								  urls.length*2);

				IStatus status = null;
				HTMLRipper ripper = new HTMLRipper();
				try {
					for (int i=0; i< urls.length;i++) {
						URL url = urls[i];
						ripper.crawlFileWithResources(
								file,url.getURL().toString());
						monitor.internalWorked(1);
						if (monitor.isCanceled()) {
							break;
						}
						createStructure(repository,directory,file,true,monitor);
						cleanCrawlingSubDirectories(file);
						if (monitor.isCanceled()) {
							break;
						}
						monitor.internalWorked(1);
					}
					if (monitor.isCanceled()) {
						status = Status.CANCEL_STATUS;
					} else {
						status = Status.OK_STATUS;
					}
					monitor.internalWorked(1);

				} catch (OperationCanceledException oce) {
					status = Status.CANCEL_STATUS;
				} catch (SecurityException se) {
					throw new JobTaskException(se);
				} catch (Exception e) {
					throw new JobTaskException(e);
				}
				monitor.done();
				return status;
			}

			public void postJobTasks() throws JobTaskException {

				if (RepositoryView.getRepositoryViewer() != null) {
					RepositoryView.getRepositoryViewer().refresh();
					RepositoryView.
					   getRepositoryViewer().refresh(directory);
					RepositoryView.
					   getRepositoryViewer().expandToLevel(directory,1);
				}
			}
		};

		jobTask.setPriority(Job.LONG);
		new JobRunner().run(jobTask);
	}

	private void createStructure(Repository repository,
								 Directory parent,
								 File file,
								 boolean crawlResources,
								 IProgressMonitor monitor)
											throws RepositoryException,
								 				   SecurityException,
								 				   OperationCanceledException {

		File[] fileList = file.listFiles();
		Arrays.sort(fileList,directoryComparator);
		for (int i = 0; i < fileList.length; i++) {
			createFileStructure(repository,
								parent,
								fileList[i],
								crawlResources,
								monitor);
		}
	}

	private void createFileStructure(Repository repository,
									 Directory parent,
									 File file,
									 boolean crawlResources,
									 IProgressMonitor monitor)
											throws RepositoryException,
									 			   SecurityException,
									 			   OperationCanceledException {

		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}

		if (!file.isDirectory()) {
			RepositoryHelper.createDocument(repository,
											parent,
											file,
											crawlResources);
		}
	}

	public static CrawlManager getInstance() {

		if (instance == null) {
			instance = new CrawlManager();
			instance.cleanCrawlingSubDirectories();
		}
		return instance;
	}

	private void cleanCrawlingSubDirectories() {

		// Garbage clean. Only for precaution purposes, because maybe if the
		// application is not shutdown correctly, we could have left some garbage.
		File dir = new File(crawlingDirectory);
		if (dir.exists()) {
			try {
				org.apache.commons.io.FileUtils.cleanDirectory(dir);
			} catch (IOException e) {
				
	            logger.error(e.getMessage(),e);
			}
		}
	}

	private void cleanCrawlingSubDirectories(File dir) {

		try {
			org.apache.commons.io.FileUtils.cleanDirectory(dir);
		} catch (IOException e) {
			
            logger.error(e.getMessage(),e);
		}
	}
}