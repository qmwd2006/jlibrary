/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual 
* contributors as indicated by the @authors tag. See copyright.txt in the
* distribution for a full listing of individual contributors.
* All rights reserved.
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
package org.jlibrary.web.freemarker;

import java.io.File;

import org.jlibrary.web.freemarker.methods.CategoryPathMethod;
import org.jlibrary.web.freemarker.methods.FileMethod;
import org.jlibrary.web.freemarker.methods.IsResourcesDirectoryMethod;
import org.jlibrary.web.freemarker.methods.NodeMethod;
import org.jlibrary.web.freemarker.methods.NodePathMethod;

import freemarker.log.Logger;
import freemarker.template.Configuration;

/**
 * Freemarker factory
 *
 *@author   molpe
 *@since    28. August 2002
 *@version
 */
public class FreemarkerFactory {

	private FreemarkerExporter exporter;
	private FreemarkerPage currentPage;
	
	public FreemarkerFactory(FreemarkerExporter exporter) {

		this.exporter = exporter;
		init();
	}
	
  /**
   *  Sets the templateCache attribute of the FreemarkerFactory object
   *
   *@param  dir  The new templateCache value
   *@since
   */
  public void setTemplateCache(String dir) {
    try{
      Configuration.getDefaultConfiguration().setDirectoryForTemplateLoading(new File(dir));
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }


  /**
   *  Gets the page attribute of the PageFactory class
   *
   *@param  templateName   Description of Parameter
   *@return                The page value
   *@exception  Exception  Description of Exception
   *@since
   */
  public Page getPage(String templateName) {
  	
    FreemarkerPage page = new FreemarkerPage(templateName, templateName);
    currentPage = page;
    
    return page;
  }


  /**  Initialites this factory */
  public void init() {
	  
    Configuration cfg = Configuration.getDefaultConfiguration();
    cfg.setNumberFormat("0");
    
    cfg.setSharedVariable("node",new NodeMethod());
    cfg.setSharedVariable("filename",new FileMethod());
    cfg.setSharedVariable("nodeURL",new NodePathMethod(exporter));
    cfg.setSharedVariable("categoryURL",new CategoryPathMethod(exporter));
    cfg.setSharedVariable("isResourcesDir", new IsResourcesDirectoryMethod());
    
    try {
      Logger.selectLoggerLibrary(Logger.LIBRARY_NONE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

	public FreemarkerPage getCurrentPage() {
		return currentPage;
	}
}