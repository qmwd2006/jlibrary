package org.jlibrary.web.rest.restlet;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.web.rest.RestApplication;
import org.jlibrary.web.rest.exporters.Exporter;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * @author Irfan Jamadar
 * @version 1.0
 */
public class CategoryRestlet extends AbstractRestlet {

	public void handle(Request request, Response response) {

		if (request.getMethod().equals(Method.GET)) {    
			    
		    try {
		    	RepositoryService rs = RestApplication.getRepositoryService();
		    	String catId = super.getIdForResource((String) request.getAttributes().get("category"));
				Category cat = rs.findCategoryById(RestApplication.getTicket(), catId);
				String result = exporter.exportCategory(cat, Exporter.FORMAT_HTML);
	        	response.setEntity(result, MediaType.TEXT_HTML);
			} catch (Exception e) {
				response.setEntity(exporter.exportException(e, Exporter.FORMAT_HTML), 
						           MediaType.TEXT_HTML);
			}
        } else {
	       response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }
	}

	
}