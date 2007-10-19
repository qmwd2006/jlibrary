package org.jlibrary.web.servlet.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jlibrary.web.services.StatsService;
/**
 * 
 * @author dlatorre
 *
 */
public class StatsListener implements HttpSessionListener {
	
	private StatsService statsService=StatsService.newInstance();
	
	public void sessionCreated(HttpSessionEvent arg0) {
    	statsService.incTotalUsers();
	}
	
	public void sessionDestroyed(HttpSessionEvent arg0) {
    	statsService.decTotalUsers();
	}
}
