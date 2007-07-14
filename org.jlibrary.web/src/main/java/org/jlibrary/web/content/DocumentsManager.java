package org.jlibrary.web.content;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.web.AbstractManager;

public class DocumentsManager extends AbstractManager {
	private ListDataModel list;
	private Document document;
	private Logger log=Logger.getLogger(DocumentsManager.class);
	
	public ListDataModel getList(){
		List documents=new ArrayList();
		String name="";
		try {
			conf.getRepositoryService().findDocumentsByName(getTicket(),name);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		list=new ListDataModel(documents);
		return list;
	}
	
	public String save(){
		DocumentProperties properties=document.dumpProperties();
		try {
			if(document.getId()!=null){
				log.debug("modifica");
				conf.getRepositoryService().updateDocument(getTicket(),properties);
			}else{
				log.debug("nuevo");
				conf.getRepositoryService().createDocument(getTicket(),properties);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return "documents$saved";
	}
}
