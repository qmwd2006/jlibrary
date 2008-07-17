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
package org.jlibrary.client.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.ui.error.ExceptionsHelper;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.security.SecurityException;

/**
 * 
 * <p>
 * This class will run several JobTasks. It always executes the long/short 
 * running tasks on their own thread, and then starts a poll (configurable) 
 * to see if the user has pressed cancel. So, the user will be able to cancel 
 * a Job even when a long server call is pending.
 * </p>
 * <p>
 * Consequences:
 * <ul>
 * <li> Better interaction with the Job cancel button</li>
 * <li> If a server operation takes too much time, the user can cancel that 
 * operation and the UI does not freeze.</li>
 * </ul>
 * Note that with this JobRunner, is the JobTask implementation responsability 
 * to check the monitor status after each call to the server, as the user could 
 * have stopped the Job and then the job task must end as soon as possible. 
 *
 * @author martin
 *
 */
public class JobRunner {

	private IStatus taskStatus = null;
	private static final int SHORT_WAIT_TIME = 100;
	private static final int LONG_WAIT_TIME = 100;
	
	/**
	 * Executes a JobTask
	 * 
	 * @param task Task to be executed.
	 */
	public void run(final JobTask task) {
		
		Job job = new Job(task.getTaskName()) {
			protected IStatus run(final IProgressMonitor monitor) {
				
				taskStatus = null;
				Thread t = new Thread() {
					public void run() {
						try {
							taskStatus = task.run(monitor);
						} catch (JobTaskException jte) {
							Throwable cause = jte.getCause();
							if (cause instanceof ResourceLockedException) {
								taskStatus = ExceptionsHelper.getStatus(
										(ResourceLockedException)cause);
							} else if (cause instanceof SecurityException) {
								taskStatus = ExceptionsHelper.getStatus(
										(SecurityException)cause);
							} else {
								String message = cause.getMessage();
								if (message == null) {
									message = "";
								}
								taskStatus = new Status(Status.ERROR,
														JLibraryPlugin.PLUGIN_ID,
														Status.OK,
														message,
														cause);
							}
						} catch (OperationCanceledException oce) {
							taskStatus = Status.CANCEL_STATUS;
						}						
					};
				};
				t.start();
				
				while (taskStatus == null) {
					try {
						if (task.getPriority() == Job.SHORT) {
							Thread.sleep(SHORT_WAIT_TIME);
						} else {
							Thread.sleep(LONG_WAIT_TIME);
						}
					} catch (InterruptedException e) {						
						e.printStackTrace();
						taskStatus = Status.CANCEL_STATUS;
						break;
					}
				}
				
				if (taskStatus == Status.OK_STATUS) {
					if (task.isUIPostJobTasks()) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(
						  new Runnable() {
							public void run() {
								try {
									task.postJobTasks();
								} catch (JobTaskException e) {
									// Asynch execution, only print stack trace
									e.printStackTrace();
								}
							}
						});					
					} else {
						try {
							task.postJobTasks();
						} catch (JobTaskException jte) {
							Throwable cause = jte.getCause();
							taskStatus = new Status(Status.ERROR,
													JLibraryPlugin.PLUGIN_ID,
													Status.OK,
													cause.getMessage(),
													cause);	
						}
					}
				}				
				return taskStatus;
			}
		};
		
		job.setUser(task.isUserTask());
		job.setPriority(task.getPriority());
		job.schedule();		
	}
}
