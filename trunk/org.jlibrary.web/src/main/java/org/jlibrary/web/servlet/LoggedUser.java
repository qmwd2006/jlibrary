package org.jlibrary.web.servlet;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.jlibrary.web.services.StatsService;
/**
 * Clase auxiliar para controlar los usuarios autentificados
 * 
 * @author dlatorre
 *
 */
public class LoggedUser implements HttpSessionBindingListener {
	
	private StatsService statsService=StatsService.newInstance();
	
	public void valueBound(HttpSessionBindingEvent arg0) {
		statsService.incLoggedUsers();
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		statsService.decLoggedUsers();
	}

}
