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
package org.jlibrary.client.ui.error;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.repository.EntityRegistry;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.security.MembersRegistry;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.security.SecurityException;

/**
 * <p>This helper class will contain utility methods to create and show 
 * dialog and status items for exceptions.</p>
 * 
 * @author martin
 *
 */
public class ExceptionsHelper {

	public final static int REPOSITORY_EXCEPTION = 100;
	public final static int SECURITY_EXCEPTION = 101;
	public final static int RESOURCE_LOCKED_EXCEPTION = 102;
	
	/**
	 * Gets an status message for a resource locked exception
	 * 
	 * @param e Exception with information about the lock
	 */
	public static IStatus getStatus(final ResourceLockedException e) {

		Throwable we = wrapException(e);
		return new Status(IStatus.ERROR,
				     	  JLibraryPlugin.PLUGIN_ID,
				     	  RESOURCE_LOCKED_EXCEPTION,
				     	  Messages.getMessage("locking_exception_cause") + 
				     	  "\n\n" + 
				     	  we.toString(),
				     	  we);
		
	}
	
	/**
	 * Gets an status message for a security exception
	 * 
	 * @param e Exception with information about the lock
	 */
	public static IStatus getStatus(final SecurityException se) {

		return new Status(IStatus.ERROR,
						  JLibraryPlugin.PLUGIN_ID,
				     	  SECURITY_EXCEPTION,
				     	  Messages.getMessage("security_exception_cause"),
				     	  se);
		
	}	
	
	/**
	 * Shows an error dialog for an exception due to a security resource
	 * 
	 * @param e Exception with information about the lock
	 */
	public static void showSecurityDialog(final SecurityException e) {
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(new Shell(),
						  Messages.getMessage("security_exception_title"),
						  Messages.getMessage("security_exception"),
						  getStatus(e));
				StatusLine.setErrorMessage(Messages.getMessage("security_exception"));
			}
		});			
	}	
	
	/**
	 * Shows an error dialog for an exception due to a locked resource
	 * 
	 * @param e Exception with information about the lock
	 */
	public static void showResourceLockedDialog(final ResourceLockedException e) {
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(new Shell(),
						  Messages.getMessage("locking_exception_title"),
						  Messages.getMessage("locking_exception"),
						  getStatus(e));
				StatusLine.setErrorMessage(Messages.getMessage("locking_exception"));
			}
		});			
	}

	private static Throwable wrapException(final Throwable e) {
		return new Exception() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String toString() {
				return getDetailsMessage(e);
			};
		};
	}
	
	private static String getDetailsMessage(Throwable t) {
		
		if (t instanceof ResourceLockedException) {
			ResourceLockedException e = (ResourceLockedException)t;
			Lock lock = e.getLock();
			StringBuilder buffer = new StringBuilder();
			
			buffer.append(Messages.getMessage("locking_exception_detail_rn"));
			if (lock != null) {
				Repository repository = RepositoryRegistry.getInstance().
					getRepository(lock.getRepository()); 
				buffer.append(repository.getName());
			}
			buffer.append("   ");
			buffer.append(Messages.getMessage("locking_exception_detail_nn"));
			if (lock != null) {
				Node node = EntityRegistry.getInstance().
					getNode(lock.getId(),lock.getRepository());
				buffer.append(node.getName());
			}
			buffer.append("   ");
			buffer.append(Messages.getMessage("locking_exception_detail_un"));
			if (lock != null) {
				Member user = MembersRegistry.getInstance().getMember(lock.getUserId());
				if (user.getName().equals(User.ADMIN_NAME)) {
					buffer.append(Messages.getMessage(user.getName()));
				} else {
					buffer.append(user.getName());
				}
			}
			return buffer.toString();			
		}
		
		return t.toString();
	}
}
