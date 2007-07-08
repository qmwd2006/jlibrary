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
package org.jlibrary.client.export.freemarker;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.jlibrary.client.export.ExportException;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker page
 *
 *@author   molpe
 *@since    28. August 2002
 *@version
 */
public class FreemarkerPage implements Page {
    
  /**
   *  Description of the Field
   *
   *@since
   */
  private String name;
  /**
   *  Description of the Field
   *
   *@since
   */
  private String templateName;

  /**
   *  Description of the Field
   *
   *@since
   */
  private SimpleHash root = new SimpleHash();

  private static Configuration cfg = Configuration.getDefaultConfiguration();


  /**
   *@param  name          Description of Parameter
   *@param  templateName  Description of Parameter
   *@since
   */
  public FreemarkerPage(String name, String templateName) {
      this.name = name;
      this.templateName = templateName;
  }


  /**
   *  Sets the templateCache attribute of the FreemarkerPage class
   *
   *@param  path  The new templateCache value
   *@since
   */
  public static void setTemplateCache(File path) {

  }


  /**
   *  Sets the templateDirectory attribute of the Page class
   *
   *@param  path  The new templateDirectory value
   *@since
   */
  public void setTemplateDirectory(String path) {
    setTemplateCache(new File(path));
  }


  /**
   *  Sets the templateDirectory attribute of the Page class
   *
   *@param  dir  The new templateDirectory value
   *@since
   */
  public void setTemplateDirectory(File dir) {
    setTemplateCache(dir);
  }


  /**
   *  Gets the name attribute of the Page object
   *
   *@return    The name value
   *@since
   */
  public String getName() {
    return name;
  }

  /**
   *  Process the Page with the default system locale
   * (defined by "language" in canyamo.xml) producing output as String
   *
   * @return  The html value
   */
  public String getAsString() throws ExportException {

    return getAsString(Page.LANGUAGE);
  }


  /**
   *  Process the Page with the given locale producing output as String
   *
   * @return  The html value
   */
  public String getAsString(String locale) throws ExportException {
    Template template = null;
    try {
      template = cfg.getTemplate(templateName);
      if (template == null) {
        return "No se pudo procesar " + templateName;
      }

      StringWriter writer = new StringWriter();
      PrintWriter pwriter = new PrintWriter(writer);

      template.process(root, pwriter);
      pwriter.flush();
      pwriter.close();
      writer.flush();
      writer.close();

      return writer.toString();
    } catch (Exception e) {
    	throw new ExportException(e);
    }
  }




  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  public void expose(String key, Object value) {
    try{
      root.put(key, BeansWrapper.getDefaultInstance().wrap(value));
    }
    catch(Exception e){
      root.put(key, "Exception: " + e.getMessage());
    }
  }


  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  public void expose(String key, TemplateModel value) {
    root.put(key, value);
  }


  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  public void expose(String key, String value) {
    root.put(key, new SimpleScalar(value));
  }


  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  public void expose(String key, long value) {
    expose(key, new Long(value));
  }


  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  public void expose(String key, int value) {
    expose(key, new Integer(value));
  }


  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  public void expose(String key, boolean value) {
    expose(key, String.valueOf(value));
  }


  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  public void expose(String key, float value) {
    expose(key, new Float(value));
  }


  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  public void expose(String key, double value) {
    expose(key, new Double(value));
  }


  /**
   *  Get a String description of this page
   *
   *@return    the Page as String
   *@since
   */
  public String toString() {
    return ("FreemarkerTemplate: " + templateName + "\t Page: " + name);
  }


  /**
   *  Description of the Method
   *
   *@param  key    Description of Parameter
   *@param  value  Description of Parameter
   *@since
   */
  private void expose(String key, Number value) {
    root.put(key, new SimpleNumber(value));
  }
  
  public Object value(String key) throws TemplateModelException {
	  
	  return root.get(key);
  }
}