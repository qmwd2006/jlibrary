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
package org.jlibrary.client.export.freemarker;

import java.io.File;

import org.jlibrary.client.export.ExportException;

import freemarker.template.TemplateModelException;

/**
 * Interface for freemarker pages
 *
 * @author molpe
 * @since  28. August 2002
 */
public interface Page {
    
    public static final String LANGUAGE = "es";

  /**
   *  Sets the templateDirectory attribute of the Page class
   *
   * @param  path The new templateDirectory value
   * @since
   */
  public void setTemplateDirectory(String path);


  /**
   *  Sets the templateDirectory attribute of the Page class
   *
   * @param  dir The new templateDirectory value
   * @since
   */
  public void setTemplateDirectory(File dir);


  /**
   *  Gets the name attribute of the Page object
   *
   * @return  The name value
   * @since
   */
  public String getName();


  /**
   *  Description of the Method
   *
   * @param  key Description of Parameter
   * @param  value Description of Parameter
   * @since
   */
  public void expose(String key, Object value);


  /**
   *  Description of the Method
   *
   * @param  key Description of Parameter
   * @param  value Description of Parameter
   * @since
   */
  public void expose(String key, String value);


  /**
   *  Description of the Method
   *
   * @param  key Description of Parameter
   * @param  value Description of Parameter
   * @since
   */
  public void expose(String key, long value);


  /**
   *  Description of the Method
   *
   * @param  key Description of Parameter
   * @param  value Description of Parameter
   * @since
   */
  public void expose(String key, int value);


  /**
   *  Description of the Method
   *
   * @param  key Description of Parameter
   * @param  value Description of Parameter
   * @since
   */
  public void expose(String key, boolean value);


  /**
   *  Description of the Method
   *
   * @param  key Description of Parameter
   * @param  value Description of Parameter
   * @since
   */
  public void expose(String key, float value);


  /**
   *  Description of the Method
   *
   * @param  key Description of Parameter
   * @param  value Description of Parameter
   * @since
   */
  public void expose(String key, double value);


  /**
   *  Description of the Method
   *
   * @return  Description of the Returned Value
   * @since
   */
  public String toString();


  /**
   *  Process the Page with the default system locale
   * (defined by "language" in canyamo.xml) producing output as String
   *
   * @return  The html value
   * @exception  Exception Description of Exception
   */
  public String getAsString() throws ExportException;

  public Object value(String key) throws TemplateModelException;
}