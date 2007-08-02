package org.jlibrary.web.conf;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.factory.ServicesFactory;
import org.jlibrary.core.jcr.RepositoryManager;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.core.security.SecurityService;
/**
 * 
 * @author Dani
 * 
 * Clase que establece la configuración para la conexión con Jlibrary
 *
 */
public class JLibraryConfiguration {
	private static JLibraryConfiguration instance= new JLibraryConfiguration();


	private SecurityService securityService;
	private RepositoryService repositoryService;
	private RepositoryManager repositoryManager;
	private SearchService searchService;
	private Logger log=Logger.getLogger(JLibraryConfiguration.class);
	private Repository repository;
	private Ticket ticket;
	
	private JLibraryConfiguration() {
		ServerProfile profile = new LocalServerProfile();
		ServicesFactory factory = JLibraryServiceFactory.getInstance(profile);
		securityService = factory.getSecurityService();
		repositoryService = factory.getRepositoryService();
		searchService = factory.getSearchService();
		log.debug("Configuración de jlibrary");
	}
	
	public static JLibraryConfiguration newInstance(){
		return instance;
	}
	
	
	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public SearchService getSearchService() {
		return searchService;
	}

	public SecurityService getSecurityService() {
		return securityService;
	}

	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public void setRepositoryManager(RepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}
	public Repository getRepository(){
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
	
}
