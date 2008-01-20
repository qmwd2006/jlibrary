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
