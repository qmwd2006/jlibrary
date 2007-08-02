package org.jlibrary.web.rest.restlet;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.web.rest.RestApplication;
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
public abstract class AbstractRestlet extends Restlet {


	protected String exceptionToString(Throwable t){
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	protected String getIdForResource(String value){
		if (value.indexOf('.') > 0){
			return value.substring(0, value.lastIndexOf('.'));
		}else{
			return value;
		}
	}

	protected String getFormatForResource(String value){
		if (value.indexOf('.') > 0){
			return value.substring(value.lastIndexOf('.') + 1);
		}else{
			return value;
		}		
	}
	
}
