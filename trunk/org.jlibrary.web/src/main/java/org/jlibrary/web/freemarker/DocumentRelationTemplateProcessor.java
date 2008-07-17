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
package org.jlibrary.web.freemarker;

import java.util.Collection;

import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.repository.RepositoryService;

public class DocumentRelationTemplateProcessor extends
		DocumentTemplateProcessor {
	private String parentId;
	public DocumentRelationTemplateProcessor(FreemarkerExporter exporter,
			Document document, RepositoryContext context, String ftl) {
		super(exporter, document, context, ftl);
	}
	public DocumentRelationTemplateProcessor(FreemarkerExporter exporter,
			Document document, RepositoryContext context, String ftl,String parentId) {
		super(exporter, document, context, ftl);
		this.parentId=parentId;
	}
	@Override
	protected void exportContent(Page page) throws ExportException {
		super.exportContent(page);
		page.expose(FreemarkerVariables.NODE_COLLECTION, loadCollection());
		page.expose(FreemarkerVariables.DOCUMENT_PARENT, parentId);
	}
	
	private Collection loadCollection() throws ExportException {
		
		Repository repository = context.getRepository();
		ServerProfile profile = repository.getServerProfile();
		final Ticket ticket = repository.getTicket();
		final RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			Collection nodeCollection=null;
			if(parentId!=null && !"".equals(parentId)){
				nodeCollection=repositoryService.findNodeChildren(ticket, parentId);
			}
			return nodeCollection;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExportException(e);
		}
	}
}
