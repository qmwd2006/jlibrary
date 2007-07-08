/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
package org.jlibrary.core.jcr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;

import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.search.SearchException;
import org.jlibrary.core.search.SearchHit;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.core.search.algorithms.DefaultSearchAlgorithm;
import org.jlibrary.core.search.algorithms.SearchAlgorithm;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the JCR based SearchService implementation. It uses the default 
 * search algorithm implementation.
 * 
 * @author mpermar
 *
 */
public class JCRSearchService implements SearchService {
	
	static Logger logger = LoggerFactory.getLogger(JCRSearchService.class);
	
	public JCRSearchService() {}
	
	private SearchAlgorithm searchAlgorithm = new DefaultSearchAlgorithm();
	
	//TODO: Test and improve XPath queries
	public Collection search(Ticket ticket, 
							 String phrase, 
							 String searchType) throws SearchException {

		String query = null;
		if (searchType.equals(SearchService.SEARCH_KEYWORDS)) {
			query = "//element(*,nt:file)[jcr:contains(@jlib:keywords,'" +
				    phrase +
				    "') and @jlib:active='true']";
		} else if (searchType.equals(SearchService.SEARCH_CONTENT)) {
			query = "//element(*,nt:resource)[jcr:contains(.,'" +
					phrase + 
					"') and @jlib:active='true']";
		}		

		RepositoryManager manager = RepositoryManager.getInstance();
		
		Session session = manager.getRepositoryState(ticket).
								getSession(ticket.getRepositoryId());
		
		return search(ticket,session,query);
	}	
	
	public Collection search(Ticket ticket, 
			 				 String xpathQuery) throws SearchException {

		String query = "//element(*,nt:file)" + xpathQuery; 
		
		RepositoryManager manager = RepositoryManager.getInstance();

		Session session = manager.getRepositoryState(ticket).
										getSession(ticket.getRepositoryId());

		return search(ticket,session,query);
	}	
	
	private Collection search(Ticket ticket,
							  javax.jcr.Session session, 
			 				  String strQuery) throws SearchException {

		Set results = new HashSet();
		try {
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			javax.jcr.Node rootNode = JCRUtils.getRootNode(session);
			String rootPath = rootNode.getPath();
			
			String statement = "/jcr:root" + rootPath + strQuery;
			

			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();

			RowIterator it = result.getRows();
			NodeIterator nodeIterator = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.query.Row row = (javax.jcr.query.Row) it.nextRow();
				javax.jcr.Node node = (javax.jcr.Node)nodeIterator.nextNode();
				if (node.isNodeType("nt:frozenNode")) continue;
				if (node.isNodeType(JLibraryConstants.CONTENT_MIXIN)) {
					node = node.getParent();
				}
				try {
					if (!JCRSecurityService.canRead(node,
									  				ticket.getUser().getId())) {
						continue;
					}
				} catch (SecurityException se) {
					logger.error(se.getMessage(),se);
					continue;
				}
				double score = row.getValue(JCRConstants.JCR_SCORE).getDouble();

				SearchHit sh = new SearchHit();
				sh.setRepository(ticket.getRepositoryId());
				sh.setId(node.getUUID());
				sh.setName(node.getProperty(
						JLibraryConstants.JLIBRARY_NAME).getString());
				sh.setPath(node.getProperty(
						JLibraryConstants.JLIBRARY_PATH).getString());
				sh.setImportance(new Integer((int)node.getProperty(
						JLibraryConstants.JLIBRARY_IMPORTANCE).getLong()));
				
				sh.setScore(score/1000);
				results.add(sh);
			}
			
			//TODO: Allow for plugabble search algorithms
			results = searchAlgorithm.filterSearchResults(results);
		} catch (InvalidQueryException iqe) {
			logger.error(iqe.getMessage());
			return results;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SearchException(e);
		}

		return results;
	}	
}