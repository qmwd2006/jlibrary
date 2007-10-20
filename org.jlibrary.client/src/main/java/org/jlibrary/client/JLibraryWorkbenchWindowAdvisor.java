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

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.jlibrary.client.preferences.JLibraryPreferences;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.client.ui.repository.actions.CCPActionGroup;
import org.jlibrary.client.ui.repository.actions.RepositoryActionGroup;
import org.jlibrary.client.ui.repository.decorators.DecoratorNode;
import org.jlibrary.client.ui.repository.views.RepositoryView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JLibraryWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	static Logger logger = LoggerFactory.getLogger(JLibraryWorkbenchWindowAdvisor.class);
	
	public JLibraryWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		
		super(configurer);
	}
	
	public boolean preWindowShellClose() {
		
		JLibraryPlugin.getDefault().setGoingToClose(true);
		return super.preWindowShellClose();
	}
	
	public void postWindowOpen() {	
		
		super.postWindowOpen();
		synchronized (RepositoryRegistry.getInstance()) {
			if (!RepositoryRegistry.getInstance().isReopenedRepositories()) {
				RepositoryRegistry.getInstance().reopenRepositories();
			}
		}
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		Window.setDefaultImage(SharedImages.getImage(SharedImages.IMAGE_JLIBRARY));

		configurer.setTitle(Messages.getMessage("jlibrary_title"));	

		//TrayManager.initTray(configurer.getWindow());
		
		configurer.getWorkbenchConfigurer().getWorkbench().addWindowListener(new IWindowListener() {
			public void windowActivated(IWorkbenchWindow window) {
				if (RepositoryView.getInstance() != null) {
					RepositoryActionGroup group = RepositoryView.getInstance().getActionManager();
					CCPActionGroup ccp = group.getCCPActionGroup();
					ccp.getPasteAction().update(RepositoryView.getRepositoryViewer().getSelection());
				}
			}
			public void windowClosed(IWorkbenchWindow window) {}
			public void windowDeactivated(IWorkbenchWindow window) {
				if (RepositoryView.getInstance() != null) {
					RepositoryActionGroup group = RepositoryView.getInstance().getActionManager();
					CCPActionGroup ccp = group.getCCPActionGroup();
					ccp.getPasteAction().update(RepositoryView.getRepositoryViewer().getSelection());
				}
			}
			public void windowOpened(IWorkbenchWindow window) {}
		});
		
		Shell shell = configurer.getWindow().getShell(); 
		shell.setImage(SharedImages.getImage(SharedImages.IMAGE_JLIBRARY));
	}
	
	public void preWindowOpen() {

		
		RepositoryRegistry.getInstance();		
		
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		
		configurer.setShowCoolBar(true);
		configurer.setShowFastViewBars(true);
		configurer.setShowMenuBar(true);
		configurer.setShowPerspectiveBar(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowStatusLine(true);
		
		initPreferences();
		initDecorators();	
	}
	
	
	
	private void initPreferences() {
		
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		JFacePreferences.setPreferenceStore(store);
		JLibraryPreferences.newInstance();
	}
	
	private void initDecorators() {
		
		IDecoratorManager decoratorManager = PlatformUI.getWorkbench().getDecoratorManager();
		try {
			//decoratorManager.setEnabled(DecoratorNode.DECORATOR_ID,true);
			
			logger.debug(String.valueOf(decoratorManager.getEnabled(DecoratorNode.DECORATOR_ID)));
		} catch (Exception e) {
			
            logger.error(e.getMessage(),e);
		}
		
	}
	
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		
		return new JLibraryActionBarAdvisor(configurer);
	}
	
	public void openIntro() {
		
		/**
		 * TODO: This is a hack
		 * 
		 * There is some mistake in Eclipse 3.1 that makes that intro pages 
		 * do not work properly. Intro always appear, it seems that the 
		 * preferences aren't saved.
		 * 
		 * So to solve this I do all the check stuff by hand, and with my 
		 * own preferences.
		 */
		Preferences prefs = JLibraryPlugin.getDefault().getPluginPreferences();
		
		boolean showIntro = prefs.getBoolean("showIntro");
		if (showIntro) {
			super.openIntro();
			prefs.setDefault("showIntro", false);
		}
	}
}
