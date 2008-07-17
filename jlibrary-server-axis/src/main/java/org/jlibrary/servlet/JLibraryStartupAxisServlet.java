/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, Blandware (represented by
* Andrey Grebnev), and individual contributors as indicated by the
* @authors tag. See copyright.txt in the distribution for a full listing of
* individual contributors. All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.servlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.transport.http.AxisServlet;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This servlet is started at jlibrary server startup time
 * 
 * @author martin
 *
 */ 
public class JLibraryStartupAxisServlet extends AxisServlet {
	 
	private static final long serialVersionUID = 2958184797904116139L;
	static Logger logger = LoggerFactory.getLogger(JLibraryStartupAxisServlet.class);
	
	/** 
     * Called by the servlet container to indicate to a servlet that 
     * the servlet is being placed into service. 
     *       * @param javax.servlet.ServletConfig config 
     * @throws javax.servlet.ServletException ServletException 
     */ 
    public void init(ServletConfig config) throws ServletException { 

    	super.init(config);
    	
    	// Deploy web services  
    	deployWebServices(config.getServletContext());
    } 
    
    
    /** 
     * Returns information about the servlet, such as author, version, and copyright. By 
     * default, this method returns an empty string. Override this method to have it return 
     * a meaningful value. 
     *  
     * @return  
     */ 
    public java.lang.String getServletInfo() { 
        
    	return "jLibrary Startup servlet"; 
    } 
    
    /**
     * Always deploy web services last version at repository startup time
     *
     */
    public void deployWebServices(ServletContext context) {
    	
    	processWS("wsdd/undeploy-repository.wsdd");
    	processWS("wsdd/undeploy-security.wsdd");
    	processWS("wsdd/undeploy-search.wsdd");
    	processWS("wsdd/deploy-repository.wsdd");
    	processWS("wsdd/deploy-security.wsdd");
    	processWS("wsdd/deploy-search.wsdd");
    }
    
    private void processWS(String url) {
		    	
		logger.info("Processing {" + url + "}");
		try {    		
	    	InputStream is = getClass().getClassLoader().getResourceAsStream(url);
	    	byte[] array = IOUtils.toByteArray(is);
	    	ByteArrayInputStream bais = new ByteArrayInputStream(array);
	    	
	        AxisEngine engine = getEngine();	
	        Document doc = XMLUtils.newDocument(bais);

	        WSDDDocument wsddDoc = new WSDDDocument(doc.getDocumentElement());
	        EngineConfiguration config = engine.getConfig();
	        if (config instanceof WSDDEngineConfiguration) {
	            WSDDDeployment deployment =
	                ((WSDDEngineConfiguration)config).getDeployment();
	            wsddDoc.deploy(deployment);
	        }
	        engine.refreshGlobalOptions();
	        engine.saveConfiguration();
	        
	        bais.close();
	        is.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}  
        
    /**
     * destroy the servlet
     */
    public void destroy() {

    	super.destroy();
    }           
}
