package org.jlibrary.servlet;

import java.net.ConnectException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.servlet.*;


import org.jlibrary.core.jcr.SessionManager;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.factory.ServicesFactory;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.profiles.RemoteServerProfile;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JLibraryStartupContextListener implements ServletContextListener {
	
	static Logger logger = LoggerFactory.getLogger(JLibraryStartupContextListener.class);


	
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("Stoping jlibrary...");
		//TODO stop jlibrary server here
        SessionManager.shutdown();
		logger.info("JLibrary stopped!.");
	}

	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Configuring security environment...");
		configureEnvironment(sce.getServletContext());
		//logger.info("Configuring jlibrary connection...");
		//TODO start jlibrary server here
		logger.info("Configuration done!.");
	}

      private void configureEnvironment(ServletContext sc){
		try{
			//JLIBRARY_HOME = sc.getInitParameter("jlibrary.home");
			Properties env = new Properties();
			Enumeration names = sc.getInitParameterNames();
			while (names.hasMoreElements()) {
	    			String name = (String) names.nextElement();
	    			if (name.startsWith("java.naming.")) {
	    				env.put(name, sc.getInitParameter(name));
	    			}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}




}
