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
package org.jlibrary.client;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.jlibrary.client.perspectives.RepositoryPerspective;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.util.JLibraryCleaner;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin
 *
 * JLibrary workbench advisor
 */
public class JLibraryWorkbenchAdvisor extends WorkbenchAdvisor {

	static Logger logger = LoggerFactory.getLogger(JLibraryWorkbenchAdvisor.class);
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	public void initialize(IWorkbenchConfigurer configurer) {

		super.initialize(configurer);
		
		setupPreferences();
		
		cleanTempFiles();

		checkFirstTimeUse();
		
		configurer.setSaveAndRestore(true);		
	}

	private void setupPreferences() {
		/*
		Preferences prefs = HelpBasePlugin.getDefault().getPluginPreferences();
		prefs.setDefault(HelpConstants.HELP_HOME_KEY,
				         HelpConstants.HELP_HOME_VALUE);
		
		prefs = AppserverPlugin.getDefault().getPluginPreferences();
		prefs.setDefault(HelpConstants.HELP_PORT_KEY,
						 HelpConstants.HELP_PORT_VALUE);
						 */
	}
	
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {

		return new JLibraryWorkbenchWindowAdvisor(configurer);
	}
	
	/** 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	public String getInitialWindowPerspectiveId() {
		
		return RepositoryPerspective.PERSPECTIVE_ID;
	}
		
	/**
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#preShutdown()
	 */
	public boolean preShutdown() {
		
		SharedCursors.getInstance().dispose();
				
		return true;
	}
	
	private void closeRepositoryConnections() {
		
		Collection repositories = RepositoryRegistry.getInstance().getOpenedRepositories();
		Iterator it = repositories.iterator();
		while (it.hasNext()) {
			Repository repository = (Repository) it.next();
			if (!repository.isConnected()) {
				continue;
			}
			ServerProfile serverProfile = repository.getServerProfile();
			Ticket ticket = repository.getTicket();
			SecurityService securityService = 
				JLibraryServiceFactory.getInstance(serverProfile).getSecurityService();
			try {
				if (serverProfile.isLocal()) {
					// We won't unregister remote repositories.
					securityService.disconnect(ticket);
				}
			} catch (SecurityException e) {
				
                logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#postShutdown()
	 */
	public void postShutdown() {
		
		super.postShutdown();
		
		closeRepositoryConnections();
		
		//TrayManager.disposeTray();
		//TooltipFactory.disposeTooltipFactory();
		//JLibrary.stopService();
	}
	
	/**
	 * Clean not deleted temp files
	 */
	private void cleanTempFiles() {
		
		new JLibraryCleaner().cleanTempFiles();
	}
	
	/**
	 * Checks if it's the first time user execution. In affirmative case 
	 * a dialog asking when to store user internal server data will be 
	 * shown
	 * 
	 * @param window Workbench window
	 */
	private void checkFirstTimeUse() {
		
		logger.debug("************* JLibrary home : " + System.getProperty("jlibrary.home"));
		
		/*
		 * This code has been removed since jLibrary 1.1 Kept here for reference.
		 * 
		if (ClientConfig.getValue(ClientConfig.FIRST_TIME_EXECUTION) == null) {
			
			Preferences prefs = JLibraryPlugin.getDefault().getPluginPreferences();
			prefs.setDefault("showIntro", true);
			
			Shell shell = new Shell(Display.getCurrent());
		    FirstTimeWizard ftw = new FirstTimeWizard();
		    WizardDialog wd = new WizardDialog(shell,ftw) {
				protected Control createDialogArea(Composite parent) {
					Control control = super.createDialogArea(parent);
					getShell().setImage(SharedImages.getImage(
							SharedImages.IMAGE_JLIBRARY));
					return control;
				}
		    };
		    wd.open();			
		    if (wd.getReturnCode() != IDialogConstants.OK_ID) {
		    	PlatformUI.getWorkbench().close();
		    }
		}
		*/
	}	
}
