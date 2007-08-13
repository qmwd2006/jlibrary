package org.jlibrary.web.login;

import java.io.IOException;
import java.net.ConnectException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.web.AbstractManager;
import org.jlibrary.web.Messages;

public class LoginManager extends AbstractManager {
	private Credentials credentials;
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
			ticket=securityService.login(credentials,getRepositoryName());
			if(ticket!=null){
				setTicket(ticket);
				ret=LOGIN_OK;
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
			Messages.setMessageError(e);
			e.printStackTrace();
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		}
		return ret;
	}
	
	public String logout(){
		log.debug("Entra al logout");
		SecurityService securityService=jlibrary.getSecurityService();
		String repositorio="";
		try {
			securityService.disconnect(getTicket());
			repositorio=getRepositoryName();
			setTicket(null);
			setRepositoryName(null);
			redirect("repositories/"+repositorio);
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		} catch (IOException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		}
		return "logout$ok";
	}

	public Credentials getCredentials() {
		return credentials;
	}
}
