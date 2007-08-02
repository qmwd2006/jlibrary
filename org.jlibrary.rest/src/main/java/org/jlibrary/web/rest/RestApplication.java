package org.jlibrary.web.rest;

import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.factory.ServicesFactory;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.ext.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Irfan Jamadar
 * @version 1.0
 */
public class RestApplication extends Application {
	
	static Logger logger = LoggerFactory.getLogger(RestApplication.class);	
	
	private SecurityService securityService;
    private static RepositoryService repositoryService;
	private static Ticket ticket;		

	private String repoName = "www";
	
	private static SpringContext springContext;
	
	public RestApplication(Context context) {
		super(context);
	}
	
	@Override
	public void start() throws Exception{
		super.start();
		logger.info("Starting REST navigator connection...");
		startJlibrary();		
		createDemoRepo();		
		if (ticket == null) {
			ticket = connectToJlibraryServer();
		}
		logger.info("Configuration done!.");	
	}
	
	public void stop() throws Exception{
		try {
			logger.info("Stoping REST navigator connection...");
			securityService.disconnect(ticket);
		} catch (SecurityException e) {
			e.printStackTrace();
		}		
		logger.info("JLibrary stopped!.");		
		super.stop();
	}

	@Override
	public Restlet createRoot() {
		Router router = new Router(getContext());

		springContext = new SpringContext(getContext());
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(springContext);
		xmlReader.loadBeanDefinitions(new ClassPathResource("applicationContext.xml"));

		springContext.refresh();
		RestManager manager = (RestManager) springContext.getBean("manager");
		manager.init(router);
		
		return router;

	}

	public void handle(Request request, Response response) {
		try {
			start();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		super.handle(request, response);
	}
	
    private void createDemoRepo() {
		PrepareExampleRepo per = new PrepareExampleRepo();
		try {
			per.createWwwRepoIfNeeded(repoName, securityService, repositoryService);
			ticket = connectToJlibraryServer();
			per.createTestCategoriesIfNeeded(repositoryService, ticket);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startJlibrary(){
    	ServerProfile profile = new LocalServerProfile(); 	
		profile.setName(repoName);

		ServicesFactory factory = JLibraryServiceFactory.getInstance(profile);
		
		try {
			securityService = factory.getSecurityService();
			repositoryService = factory.getRepositoryService();
		} catch (Exception e) {
			logger.error("Couldn't start JLibrary: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Ticket connectToJlibraryServer(){

		Credentials creds = new Credentials();
		creds.setPassword(User.DEFAULT_PASSWORD);
		creds.setUser(User.ADMIN_NAME);		
		
		try {
			Ticket ticket = securityService.login(creds, repoName);
			return ticket;
		} catch (Exception e) {
			logger.error("Couldn't connect to server: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static SpringContext getSpringContext(){
		return springContext;
	}
	
	public static RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public static void setRepositoryService(RepositoryService repositoryService) {
		RestApplication.repositoryService = repositoryService;
	}

	public static Ticket getTicket() {
		return ticket;
	}

	public static void setTicket(Ticket ticket) {
		RestApplication.ticket = ticket;
	}	

}
