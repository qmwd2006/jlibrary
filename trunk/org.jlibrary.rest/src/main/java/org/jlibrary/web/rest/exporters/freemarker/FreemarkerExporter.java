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
package org.jlibrary.web.rest.exporters.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.util.FileUtils;
import org.jlibrary.web.rest.exporters.AbstractExporter;
import org.jlibrary.web.rest.exporters.ExportException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * @author martin
 *
 * Freemarker based exporter
 */
public class FreemarkerExporter extends AbstractExporter {

	private Configuration cfg;

	public void init(){
		cfg = new Configuration();
		cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/"));
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}
	
	public String exportCategory(Category cat, String format) throws ExportException{
		Map dataModel = new HashMap();
		dataModel.put("category", cat);
		return processTemplate(format, "category", dataModel);
	}
	
	public String exportException(Exception e, String format){
		Map dataModel = new HashMap();
		dataModel.put("message", e.getMessage());
		dataModel.put("trace", super.exceptionToString(e));
		try {
			return processTemplate(format, "exception", dataModel);
		} catch (ExportException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}		
	}
	
	protected String processTemplate(String format, String templateName, Map dataModel) throws ExportException{
		try{
			Template t = getTemplate(format, templateName);
			StringWriter out = new StringWriter();
			t.process(dataModel, out);
			out.flush(); 
			return out.toString();
		}catch(Throwable t){
			throw new ExportException(t.getMessage(), t);
		}		
	}
	
	protected Template getTemplate(String format, String name) throws Throwable{
		return cfg.getTemplate("/templates/nodes/" + format + "/" + name + ".fmt");
	}


}
