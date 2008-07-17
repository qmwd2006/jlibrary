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
package org.jlibrary.web.freemarker.methods;

import java.util.List;

import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.FreemarkerVariables;
import org.jlibrary.web.freemarker.Page;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * Obtains the absolute node path for a given node
 * 
 * @author martin
 *
 */
public class NodePathMethod implements TemplateMethodModel {

	static Logger logger = LoggerFactory.getLogger(NodePathMethod.class);
	
	private FreemarkerExporter exporter;

	public NodePathMethod(FreemarkerExporter exporter) {

		this.exporter = exporter;
	}
	
	public Object exec(List args) throws TemplateModelException {

		if ((args.size() != 1) && (args.size() != 2)) {
            throw new TemplateModelException("Wrong arguments");
        }
		
		Object arg = args.get(0);
		if (!(arg instanceof String)) {
			throw new TemplateModelException("Argument should be a string");
		}
		
		String id = (String)arg;
		RepositoryContext context = exporter.getContext();
		
		Ticket ticket = context.getRepository().getTicket();
		ServerProfile profile = context.getRepository().getServerProfile();
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		Node node;
		try {
			node = repositoryService.findNode(ticket, id);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
		
		if (node == null) {
			throw new TemplateModelException("Node with id:"+id+"not found");			
		}
		
		Page page = exporter.getCurrentPage();
		
		String rootURL = 
			((SimpleScalar)page.value(FreemarkerVariables.REPOSITORY_URL))
			.getAsString();
		
		return rootURL+node.getPath();
	}
}
