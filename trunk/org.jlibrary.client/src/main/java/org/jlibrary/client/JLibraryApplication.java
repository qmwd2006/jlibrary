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

import java.io.File;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Martin
 *
 * Main program
 */
public class JLibraryApplication implements IPlatformRunnable {
	
	public static final String PLUGIN_ID="org.jlibrary.client";
	static Logger logger = LoggerFactory.getLogger(JLibraryApplication.class);
	
	/**
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	public Object run(Object args) throws Exception {
		
		checkHome();
		
		Display display = PlatformUI.createDisplay();
		try {
			int code = PlatformUI.createAndRunWorkbench(display,new JLibraryWorkbenchAdvisor());
			// exit the application with an appropriate return code
			return code == PlatformUI.RETURN_RESTART
					? EXIT_RESTART
					: EXIT_OK;
		} finally {
			if (display != null)
				display.dispose();
		}
	}

	private void checkHome() {
		
		String home = System.getProperty("jlibrary.home");
		if (home == null) {
			home = System.getenv("JLIBRARY_HOME");
			if (home == null) {
				//logger.info("jLibrary home is not defined");
				//logger.info("Either you have pass the -Djlibrary.home argument to jLibrary application or you must define JLIBRARY_HOME variable");
				File f = new File(".");
				logger.info("Using " + f.getAbsolutePath() + " as JLIBRARY_HOME");
				System.setProperty("jlibrary.home", f.getAbsolutePath());
				home = f.getAbsolutePath();
				//logger.info("jLibrary will exit now");
				//System.exit(0);
			}
		}
		File f = new File(home);
		if (!f.exists()) {
			logger.info("jLibrary home does not exists!");
			logger.info("jLibrary will exit now");
			System.exit(0);
		}
		
		// Set derby system home
		System.setProperty("derby.system.home",home);
	}
}





