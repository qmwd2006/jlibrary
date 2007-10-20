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
package org.jlibrary.client.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This class encapsulates a jlibrary task. jLibrary tasks will be run within
 * a JobRunner instance.
 * 
 * @author martin
 *
 */
public class JobTask {

	/**
	 * Loading repositories family
	 */
	public static final String LOADING_REPOSITORIES = "LOADING_REPOSITORIES";
	
	private String taskName;
	private boolean systemTask = false;
	private boolean userTask = true;
	private int priority = Job.SHORT;
	private boolean UIPostJobTasks = true;

	/**
	 * Constructas a new JobTask instance. 
	 * 
	 * @param taskName This is the task name. It will be used as param to 
	 * IProgressMonitor instance to start the task
	 */
	public JobTask(String taskName) {
		
		this.taskName = taskName;
	}
	
	/**
	 * Returns the task name
	 * 
	 * @return String task name
	 */
	public String getTaskName() {
		
		return taskName;
	}
	
	/**
	 * Executes a task. 
	 * 
	 * @param monitor Monitor that can be used to update task progress 
	 * information
	 * 
	 * @return Status The task end status. This normally will take the values 
	 * <code>Status.OK_STATUS</code> or <code>Status.CANCEL_STATUS</code>, and 
	 * also custom error status messages.
	 * 
	 * @throws OperationCanceledException If the operation must be canceled 
	 * an OperationCanceledException can be thrown. Not that throw this 
	 * exception has the same effect that returning 
	 * <code>Status.CANCEL_STATUS</code>.
	 * 
	 * @throws JobTaskException You should wrap any other exception in a 
	 * JobTaskException. JobRunner will take the exception's cause and will 
	 * generate an error message automatically
	 */
	public IStatus run(IProgressMonitor monitor) 
										throws OperationCanceledException,
											   JobTaskException {
		
		return Status.OK_STATUS;
	}

	/**
	 * Execute several post job tasks. If this JobTask instance is tagged as 
	 * needing UIPostJobTasks then this method will be run  
	 * asynchronously within an UI thread.
	 * 
	 * @throws JobTaskException You should wrap any other exception in a 
	 * JobTaskException. JobRunner will take the exception's cause and will 
	 * generate an error message automatically
	 */
	public void postJobTasks() throws JobTaskException {}
	
	/**
	 * Tells if this task is an user task. An user task will be run with a 
	 * progress dialog
	 * 
	 * @return boolean <code>true</code> if the task is an UI task and 
	 * <code>false</code> otherwise
	 */
	public boolean isUserTask() {
		return userTask;
	}

	/**
	 * Establishes this tas as an user or not user task.
	 * 
	 * @param userTask If <code>true</code> the task is an user task, if 
	 * <code>false</code> the task is not a system task
	 */
	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
		this.systemTask = !userTask;
	}

	/**
	 * Returns the task priority. This priorities should be the same as the 
	 * defined on the Eclipse Job task
	 * 
	 * @return int Task priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Sets the task priority. Priority values should be the same as those 
	 * defined on the Eclipse Job task
	 * 
	 * @param priority Task priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Tells if the postjobtask steps should be run on its own UI thread
	 * 
	 * @return boolean <code>true</code> if the task should be run on its own 
	 * UI thread and <code>false</code> otherwise
	 */
	public boolean isUIPostJobTasks() {
		return UIPostJobTasks;
	}

	/**
	 * Sets if the postjobtask steps should be run on its own UI thread. 
	 * 
	 * @param postJobTasks It will be <code>true</code> if the task should 
	 * be run on its own UI thread and <code>false</code> otherwise
	 */	
	public void setUIPostJobTasks(boolean postJobTasks) {
		UIPostJobTasks = postJobTasks;
	}

	/**
	 * Tells if this task is a system task. A system task will be run without 
	 * a progress dialog
	 * 
	 * @return boolean <code>true</code> if the task is an UI task and 
	 * <code>false</code> otherwise
	 */
	public boolean isSystemTask() {
		return systemTask;
	}

	/**
	 * Establishes this tas as a system or not system task.
	 * 
	 * @param systemTask If <code>true</code> The task is a system one, if 
	 * <code>false</code> it is not a system task. 
	 */
	public void setSystemTask(boolean systemTask) {
		this.systemTask = systemTask;
		this.userTask = !systemTask;
	}
}
