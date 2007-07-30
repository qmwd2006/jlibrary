package org.jlibrary.web;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

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
	public Ticket getTicket() {
		HttpServletRequest request=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return ticketService.getTicket(request,jlibrary.getRepositoryName());
	}
	
	public void setTicket(Ticket ticket){
		HttpServletRequest request=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		request.getSession(true).setAttribute(TicketService.SESSION_TICKET_ID,ticket);
	}
	
	public Repository getRepository() throws RepositoryNotFoundException, RepositoryException, SecurityException {
		return jlibrary.getRepositoryService().findRepository(jlibrary.getRepositoryName(),getTicket());
	}
}
