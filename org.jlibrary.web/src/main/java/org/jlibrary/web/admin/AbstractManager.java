package org.jlibrary.web.admin;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.web.conf.JLibraryConfiguration;
import org.jlibrary.web.services.TicketService;

public abstract class AbstractManager {
	protected static final TicketService ticketService=TicketService.getTicketService();
	protected static final JLibraryConfiguration jlibrary=JLibraryConfiguration.newInstance();
	private static final String CURRENT_REPOSITORY="current";
	private String repositoryName;
	private String referer;
	private Logger log=Logger.getLogger(AbstractManager.class);
	public Ticket getTicket() {
		HttpServletRequest request=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		Ticket ticket;
		log.debug("repositorio:"+getRepositoryName());
		ticket=(Ticket) request.getSession(true).getAttribute(TicketService.SESSION_TICKET_ID+getRepositoryName());
		log.debug("ticket:"+ticket);
		return ticket;
	}
	
	public void setTicket(Ticket ticket){
		HttpServletRequest request=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		request.getSession(true).setAttribute(TicketService.SESSION_TICKET_ID+getRepositoryName(),ticket);
	}
	
	public Repository getRepository() throws RepositoryNotFoundException, RepositoryException, SecurityException {
		return jlibrary.getRepositoryService().findRepository(getRepositoryName(),getTicket());
	}

	public String getRepositoryName() {
		if(repositoryName==null){
			HttpServletRequest request=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			repositoryName=(String) request.getSession(true).getAttribute(CURRENT_REPOSITORY);
			log.debug("recupera el nombre del repositorio de sesion");
		}
		log.debug("recupera el nombre del repositorio"+repositoryName);
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		if(repositoryName!=null){
			HttpServletRequest request=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			request.getSession(true).setAttribute(CURRENT_REPOSITORY,repositoryName);
			log.debug("establece el nombre del repositorio"+repositoryName);
		}
		this.repositoryName = repositoryName;
	}
	
	/**
	 * Ejecuta un redirect al path indicado
	 * @param path
	 * @throws IOException 
	 */
	public void redirect(String path) throws IOException{
		HttpServletResponse response=(HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.sendRedirect(response.encodeRedirectURL(path));
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}
}
