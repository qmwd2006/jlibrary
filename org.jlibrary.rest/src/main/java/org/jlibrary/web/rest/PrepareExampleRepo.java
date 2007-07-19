package org.jlibrary.web.rest;

import java.net.ConnectException;
import java.util.List;

import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
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

import javax.servlet.ServletContext;

import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.factory.ServicesFactory;
import org.jlibrary.core.profiles.LocalServerProfile;

class PrepareExampleRepo {
	

    
	public void createWwwRepoIfNeeded(String repoName, SecurityService securityService, RepositoryService repositoryService) throws UserNotFoundException, AuthenticationException, SecurityException, ConnectException, RepositoryNotFoundException, RepositoryException, RepositoryAlreadyExistsException {
		Credentials creds = new Credentials();
		creds.setPassword(User.DEFAULT_PASSWORD);
		creds.setUser(User.ADMIN_NAME);	
		Ticket admTicket = securityService.login(creds, "system");
		try {
			repositoryService.findRepository(repoName, admTicket);
		} catch (RepositoryNotFoundException rnfe) {
			repositoryService.createRepository(
					admTicket, repoName, repoName, admTicket.getUser());				
		}
		securityService.disconnect(admTicket);
	}

	public void createTestCategoriesIfNeeded(RepositoryService repositoryService, Ticket ticket) throws RepositoryException, CategoryAlreadyExistsException, SecurityException, PropertyNotFoundException, InvalidPropertyTypeException {
		List cats = repositoryService.findAllCategories(ticket);
		if (cats.size() == 1){
			repositoryService.createCategory(ticket, createCategory("code snippets", ticket.getRepositoryId()));
			repositoryService.createCategory(ticket, createCategory("course", ticket.getRepositoryId()));
			repositoryService.createCategory(ticket, createCategory("article", ticket.getRepositoryId()));
			repositoryService.createCategory(ticket, createCategory("path", ticket.getRepositoryId()));
			cats = repositoryService.findAllCategories(ticket);
		}
		System.out.println("Existing cats: " + cats);
	}

	private CategoryProperties createCategory(String name, String repoId) throws PropertyNotFoundException, InvalidPropertyTypeException {
		CategoryProperties properties = new CategoryProperties();
		properties.addProperty(CategoryProperties.CATEGORY_NAME, name);
		properties.addProperty(CategoryProperties.CATEGORY_DESCRIPTION, name);
		properties.addProperty(CategoryProperties.CATEGORY_REPOSITORY, repoId);
		return properties;
	}    
	
	
}
