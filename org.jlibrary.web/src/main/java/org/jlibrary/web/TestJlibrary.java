package org.jlibrary.web;

import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;

public class TestJlibrary {
	private Ticket ticket=new Ticket();
	private User user;
	public TestJlibrary(){
		user=new User();
		user.setPassword("changeme");
	}
	
}
