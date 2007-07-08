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
package org.jlibrary.client.ui.ccp;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jlibrary.client.Messages;
import org.jlibrary.client.jobs.JobRunner;
import org.jlibrary.client.jobs.JobTask;
import org.jlibrary.client.jobs.JobTaskException;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;

/**
 * @author martin
 *
 * Service to handle object paste operations
 */
public class PasteService {

	private HashMap pasters = new HashMap();
	
	private static PasteService instance = null;

	private Object[] clipboardObjects;

	private boolean cutOperation;
	
	private PasteService() {
		
		pasters.put(Node.class.toString(), new NodePaster());
		pasters.put(Document.class.toString(), new NodePaster());
		pasters.put(Directory.class.toString(), new NodePaster());
		pasters.put(Category.class.toString(), new CategoryPaster());
		pasters.put(Repository.class.toString(), new NodePaster());
	}
	
	public void paste(final Object source, 
			  		  final Object destination,
					  final boolean move) throws PasteException {
		
		final Paster paster = (Paster)pasters.get(destination.getClass().toString());
		paste(source,destination,move,paster);
	}
	
	public void paste(final Object source, 
					  final Object destination,
					  final boolean move,
					  final Paster paster) throws PasteException {
		
		clipboardObjects = null;
		
		if (paster != null) {
			if (!paster.beforePaste(source,destination)) {
				return;
			}
			
			JobTask jobTask = new JobTask(Messages.getMessage("copy_job_name")) {
				
				public IStatus run(IProgressMonitor monitor) 
											throws OperationCanceledException, 
												   JobTaskException {

					IStatus status = null;
					try {
						paster.paste(source,destination,move,monitor);
						status = Status.OK_STATUS;
					} catch (OperationCanceledException oce) {
						status = Status.CANCEL_STATUS;
					} catch (PasteException e) {
						throw new JobTaskException(e);
					}						
					return status;					
				}
				
				public void postJobTasks() throws JobTaskException {

					paster.afterPaste(source,destination);
				}
			};
			jobTask.setPriority(Job.LONG);
			new JobRunner().run(jobTask);						
		}
	}

	
	
	/**
	 * Returns the unique instance of this service
	 * 
	 * @return PasteService instance
	 */
	public static PasteService getInstance() {
		
		if (instance == null) {
			instance = new PasteService();
		}
		return instance;
	}

	/**
	 * @param clipboardObject
	 */
	public void setClipboardObjects(Object[] clipboardObjects) {
		
		this.clipboardObjects = clipboardObjects;
	}
	
	public Object[] getClipboardObjects() {
		
		return clipboardObjects;
	}
	
	public boolean isCutOperation() {
		
		return cutOperation;
	}
	
	public void setCutOperation(boolean cutOperation) {
		
		this.cutOperation = cutOperation;
	}
}
