package org.jlibrary.web.rest;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Restlet;
import org.restlet.Router;

/**
 * @author Irfan Jamadar
 * @version 1.0
 */
public class RestManager {

	private Map<String, Restlet> resourceMappings = new HashMap<String, Restlet>();

	public void init(Router router) {
		for (String key : resourceMappings.keySet()) {
			router.attach(key, resourceMappings.get(key));
		}
	}
	public void setResourceMappings(HashMap<String, Restlet> resourceMappings) {
		this.resourceMappings = resourceMappings;
	}

}
