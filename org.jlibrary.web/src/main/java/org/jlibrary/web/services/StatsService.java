package org.jlibrary.web.services;


/**
 * 
 * @author dlatorre
 *
 */
public class StatsService {
	private int loggedUsers;
	private int totalUsers;
	private static StatsService instance;
	public static final String SESSION_LOGGED_USER = "logged_user";
	
	private StatsService(){
		loggedUsers=0;
		totalUsers=0;
	}
	
	public static StatsService newInstance(){
		if (instance == null) {
			synchronized(StatsService.class) {
				if (instance == null)
					instance= new StatsService();
				}
	     	}
	    return instance;
	}

	public int getAnonymousUsers() {
		return totalUsers-loggedUsers;
	}

	public int getLoggedUsers() {
		return loggedUsers;
	}

	public void incLoggedUsers() {
		this.loggedUsers++;
	}
	
	public void decLoggedUsers() {
		if(this.loggedUsers>0)
			this.loggedUsers--;
	}

	public int getTotalUsers() {
		return totalUsers;
	}

	public void incTotalUsers() {
		this.totalUsers++;
	}
	
	public void decTotalUsers() {
		if(this.totalUsers>0)
			this.totalUsers--;
	}
}