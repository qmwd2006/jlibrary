package org.jlibrary.web.login;

import java.net.ConnectException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.web.AbstractManager;
import org.jlibrary.web.Messages;

public class LoginManager extends AbstractManager {
	private Credentials credentials;
	public static final String LOGGED="usuario";
	private static final String LOGIN_OK="login$ok";
	private static final String LOGIN_KO="login$ko";
	private Logger log=Logger.getLogger(LoginManager.class);
	public LoginManager(){
		credentials=new Credentials();
		credentials.setUser(User.ADMIN_NAME);
		credentials.setPassword(User.DEFAULT_PASSWORD);
	}
	
	public String login(){
		log.debug("Entra al login");
		String ret=LOGIN_KO;
		SecurityService securityService=jlibrary.getSecurityService();
		Ticket ticket=null;
		try {
			ticket=securityService.login(credentials,jlibrary.getRepositoryName());
			if(ticket!=null){
				setTicket(ticket);
				ret=LOGIN_OK;
				log.debug("login OK");
			}
		} catch (ConnectException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		} catch (UserNotFoundException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		} catch (AuthenticationException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		} catch (RepositoryNotFoundException e) {
			jlibrary.createRepository(jlibrary.getRepositoryName());
			login();
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		}
		HttpServletRequest request = (HttpServletRequest)FacesContext.
	    getCurrentInstance().getExternalContext().getRequest();
		request.getSession().setAttribute(LOGGED,ticket);
		return ret;
	}

	public Credentials getCredentials() {
		return credentials;
	}
}
