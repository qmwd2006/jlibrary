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
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
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
public class CategoryListRestlet extends AbstractRestlet {

	
	public void handle(Request request, Response response) {

		if (request.getMethod().equals(Method.GET)) {
			RepositoryService rs = RestApplication.getRepositoryService();
			try {
				List cats = rs.findAllCategories(RestApplication.getTicket());
				String responseText = "<ul>";
				for (Iterator i = cats.iterator(); i.hasNext();){
					Category cat = (Category)i.next();
					responseText += "<li><a href=\"./cat/"+ cat.getId() +".html\">" + cat.getName() + "</a></li>";
				}
				responseText += "</ul>";
				response.setEntity(responseText, MediaType.TEXT_HTML);
			} catch (RepositoryException e) {
				response.setEntity(exporter.exportException(e, Exporter.FORMAT_HTML), 
				           MediaType.TEXT_HTML);
			}

        } else {
	       response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }
	}


	
}
