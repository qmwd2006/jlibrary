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

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.util.FileUtils;
import org.jlibrary.web.EntityRegistry;

/**
 * @author martin
 *
 * Freemarker based exporter
 */
public class FreemarkerExporter extends BaseExporter {

	private FreemarkerFactory factory;
	private ExportFilter filter;
	
	private CategoryHelper helper;
	
	private String rootURL;
	private String repositoryURL;
	
	public FreemarkerExporter() {

		factory = new FreemarkerFactory(this);
		filter = new FreemarkerExportFilter();
	}
	
	/**
	 * @see org.jlibrary.client.export.BaseExporter#initExportProcess(org.jlibrary.client.export.RepositoryContext)
	 */
	public void initExportProcess(RepositoryContext context) throws ExportException {

		factory.setTemplateCache(context.getTemplatesDirectory());
		
		copyResources(context);
		/*		
		helper = new CategoryHelper(context.getRepository());
	
		try {
			helper.loadData();
		} catch (RepositoryException e) {
			throw new ExportException(e);
		}
		*/
		
	}
	
	public void endExportProcess(RepositoryContext context) throws ExportException {

		helper.clear();
	}
	
	public String exportDocument(Document document, 
							   RepositoryContext context) throws ExportException {
		
		return new DocumentTemplateProcessor(this,document,context).processTemplate(factory);
	}
	
	public String exportResource(ResourceNode resource, 
							   RepositoryContext context) throws ExportException {
		
		return new ResourceTemplateProcessor(resource,context).processTemplate(factory);
	}	
	
	public String exportDirectory(Directory directory, 
							    RepositoryContext context) throws ExportException {

		return new DirectoryTemplateProcessor(this,directory,context).processTemplate(factory);
	}
	
	public String exportCategory(Category category, 
							   RepositoryContext context) throws ExportException {

		return new CategoryTemplateProcessor(this,category,context).processTemplate(factory);
	}
	
	public void filterRepository(RepositoryContext context) throws ExportException {

		context.applyFilter(filter);
	}
	
	/**
	 * Returns the relative root URL from the path of the document
	 * 
	 * @param document Document
	 * 
	 * @return String relative root URL
	 */
	public String getRootURL(Document document) {
		
		if (rootURL != null) {
			return rootURL;
		}
		
		// Otherwise try to compute a relative root URL
		StringBuffer buffer = new StringBuffer();
		String[] parts = StringUtils.split(document.getPath(),"/");
		for (int i = 0; i<parts.length-1; i++) {
			buffer.append("../");
		}
		if (buffer.length() > 0) {
			buffer.delete(buffer.length()-1,buffer.length());
		}
		if (buffer.length() == 0) {
			return ".";
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the relative root URL from the path of the directory
	 * 
	 * @param directory Directory
	 * 
	 * @return String relative root URL
	 */
	public String getRootURL(Directory directory) {

		if (rootURL != null) {
			return rootURL;
		}
		
		// Otherwise try to compute a relative root URL
		StringBuffer buffer = new StringBuffer("../");
		if (directory.getParent() == null) {
			buffer = new StringBuffer("./");
		}
		String[] parts = StringUtils.split(directory.getPath(),"/");
		for (int i = 0; i<parts.length-1; i++) {
			buffer.append("../");
		}
		if (buffer.length() > 0) {
			buffer.delete(buffer.length()-1,buffer.length());
		}
		return buffer.toString();
	}
		
	/**
	 * Returns the relative root URL based on the given category
	 * 
	 * @param Category category
	 * 
	 * @return String relative category URL
	 */
	public String getRootURL(Category category) {

		if (rootURL != null) {
			return rootURL;
		}
		
		// this if for the categories/ folder
		StringBuffer buffer = new StringBuffer("../..");
		
		while (category.getParent() != null) {
			buffer.append("/..");
			category = category.getParent();
		}		
		return buffer.toString();
	}
	
	/**
	 * Returns the filename of a document
	 * 
	 * @param document Document
	 * 
	 * @return String filename
	 */
	public String getFilename(Document document) {
		
		String path = document.getPath();
		int index = path.length()-1;
		while (path.charAt(index) != '/') {
			index--;
		}
		return path.substring(index,path.length());
	}
	
	/**
	 * Returns a relative location URL based in the passed node path. Each item
	 * in the returned location URL has a link to that item.
	 * 
	 * @param node Node
	 * 
	 * @return String Relative location URL
	 */
	public String getLocationURL(Node node) {
		
		boolean isDirectory = node.isDirectory();
		String location = node.getPath();
		return location;
		/*
		StringBuffer buffer = new StringBuffer();
		String[] parts = StringUtils.split(location,"/");
		
		if (parts.length == 0) return "";
		
		int k = parts.length-1;
		Node n = (Node)node;
		while ((n != null) && (k >= 0)) {
			if (k == parts.length-1) {
				if (!n.isDocument()) {
					parts[k] = n.getName();
				}
			} else {
				parts[k] = n.getName();
			}
			n = EntityRegistry.getInstance().
				getNode(n.getParent(),n.getRepository());
			k--;
		}
		
		String nodeHtmlName = "";
		if (parts.length > 1) {
			nodeHtmlName = parts[parts.length-1];
			if (node.isDocument()) {
				if (!((Document)node).getTypecode().equals(
						Types.HTML_DOCUMENT)) {
					nodeHtmlName+=".html";
				}
				parts[parts.length-1] = node.getName();
			}
		}
		
		for (int i = parts.length-2; i >=0; i--) {
			StringBuffer buffer2 = new StringBuffer();
			buffer2.append("/<A href=\"");
			int dots = parts.length-i-2;
			if (node.isDirectory()) {
				dots++;
			}
			if (dots == 0) {
				buffer2.append("./");
			} else {
				for (int j = 0; j<dots;j++) {
					buffer2.append("../");
				}
			}
			buffer2.append("index.html\">"+parts[i]+"</A>");
			buffer.insert(0,buffer2.toString());
		}
		if (isDirectory) {
			buffer.append("/<A href=\"./index.html\">"+
						  parts[parts.length-1]+
						  "</A>");
		} else {
			buffer.append("/<A href=\"" + 
						  nodeHtmlName+
						  "\">"+
						  parts[parts.length-1]+
						  "</A>");
		}
		return buffer.toString();
		*/
	}
	
	/**
	 * Returns a relative location URL based in the passed category. Each item
	 * in the returned location URL has a link to that item.
	 * 
	 * @param node Node
	 * 
	 * @return String Relative location URL
	 */
	public String getLocationURL(Category category) {
		
		String escapedCategoryName = Text.escape(category.getName());
		StringBuffer path = new StringBuffer("<A href=\"" + escapedCategoryName + "\">" + 
											 category.getName()+
											 "</A>");
		String href = "./";
		while(category.getParent() != null) {
			category = category.getParent();
			escapedCategoryName = Text.escape(category.getName());
			path.insert(0,"<A href=\" " + 
						  href+"/" + escapedCategoryName + "\">" + 
						  category.getName()+
						  "</A>/"); 
			href = href + "../";
		}
		path.insert(0,"/");
		return path.toString();
	}
	
	/**
	 * Copy all the resources of the template resources directory to a similar 
	 * directory in the output destination
	 * 
	 * @throws ExportException If the files can't be copied
	 */
	private void copyResources(RepositoryContext context) 
							   throws ExportException {
		
		File sourceDir = new File(context.getTemplatesDirectory() + 
								  File.separator + 
								  "resources");
		if (!sourceDir.exists()) {
			return;
		}
		
		File destinationDir = new File(context.getOutputDirectory() + 
									   File.separator + 
									   "resources");
		
		if (!destinationDir.mkdir()) {
			return;
		}
		
		try {
			File[] files = sourceDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				FileUtils.copyFile(files[i],destinationDir, false);
			}
		} catch (Exception e) {
			throw new ExportException(e);
		}
	}
	
	public Page getCurrentPage() {
		
		return factory.getCurrentPage();
	}
	
	public CategoryHelper getCategoryHelper() {
		
		return helper;
	}
	
	public void setRootURL(String rootURL) {
		
		this.rootURL = rootURL;
	}
	
	public String getRepositoryURL() {
		
		if (repositoryURL == null) {
			return rootURL;
		}
		return repositoryURL;
	}

	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}
}
