package org.jlibrary.web;

import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.web.conf.JLibraryConfiguration;

public abstract class AbstractManager {
	protected static final JLibraryConfiguration conf=JLibraryConfiguration.newInstance();
	public Ticket getTicket() {
		return conf.getTicket();
	}
	public void setTicket(Ticket ticket) {
		conf.setTicket(ticket);
	}
	public void setRepository(Repository rep) {
		conf.setRepository(rep);
	}
	public Repository getRepository() {
		return conf.getRepository();
	}
}
