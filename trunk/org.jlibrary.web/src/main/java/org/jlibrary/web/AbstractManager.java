package org.jlibrary.web;

import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.web.conf.JLibraryConfiguration;

public abstract class AbstractManager {
	protected static final JLibraryConfiguration jlibrary=JLibraryConfiguration.newInstance();
	public Ticket getTicket() {
		return jlibrary.getTicket();
	}
	public void setTicket(Ticket ticket) {
		jlibrary.setTicket(ticket);
	}
	
	public Repository getRepository() throws RepositoryNotFoundException, RepositoryException, SecurityException {
		return jlibrary.getRepositoryService().findRepository(jlibrary.getRepositoryName(),getTicket());
	}
}
