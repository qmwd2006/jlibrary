package org.jlibrary.web.tags;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.web.conf.JLibraryConfiguration;

public class JLibraryContentHelper {

	private static Logger logger = Logger.getLogger(JLibraryContentHelper.class);
	
	public static final String getResource(String nodeId) {
		
		JLibraryConfiguration conf = JLibraryConfiguration.newInstance();
		RepositoryService repositroyService = conf.getRepositoryService();

		byte[] content;
		try {
			content = repositroyService.loadDocumentContent(nodeId, conf.getTicket());
			return new String(content);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
		} catch (SecurityException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	
	public static final Node getNode(String nodeId) {
		
		JLibraryConfiguration conf = JLibraryConfiguration.newInstance();
		RepositoryService repositoryService = conf.getRepositoryService();

		try {
			return repositoryService.findNode(conf.getTicket(), nodeId);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
		} catch (SecurityException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
