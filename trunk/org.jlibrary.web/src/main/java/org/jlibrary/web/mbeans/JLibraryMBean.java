package org.jlibrary.web.mbeans;

import org.jlibrary.web.services.StatsService;
import org.jlibrary.web.services.TicketService;
/**
 * 
 * @author dlatorre
 *
 */
public class JLibraryMBean {
	private StatsService statsService;
	public JLibraryMBean(){
		statsService=StatsService.newInstance();
	}
	
	public Integer getLoggedUsers() {
		return statsService.getLoggedUsers();
	}
	
	public Integer getAnonymousUsers() {
		return statsService.getAnonymousUsers();
	}
	
	public Integer getTotalUsers() {
		return statsService.getTotalUsers();
	}
}
