package org.jlibrary.web.admin.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Node.Types;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.GenericProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.web.admin.AbstractManager;
import org.jlibrary.web.admin.Messages;

public class DocumentsManager extends AbstractManager {
	private ListDataModel list;
	private Node node;
	private Node parent;
	private Logger log=Logger.getLogger(DocumentsManager.class);
	private String id;
	private UploadedFile file;
	private byte[] data;
	public ListDataModel getList(){
		List nodes=new ArrayList();
		try {
			if(parent==null){
				parent=getRepository().getRoot();
			}
			
			nodes=(List) jlibrary.getRepositoryService().findNodeChildren(getTicket(),parent.getId());
			log.debug("Nodo"+parent.getId()+" subnodos:"+nodes.size());
		} catch (RepositoryNotFoundException e) {
			Messages.setMessageError(e);
			log.error(e.getMessage());
		} catch (RepositoryException e) {
			Messages.setMessageError(e);
			log.error(e.getMessage());
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			log.error(e.getMessage());
		}
		list=new ListDataModel(nodes);
		return list;
	}
	
	public String subNodes(){
		parent=node;
		return "content$list";
	}
	
	public String parentNode(){
		if(parent!=null && parent.getParent()!=null){
				try {
					parent=jlibrary.getRepositoryService().findNode(getTicket(),parent.getParent());
					if(parent.getParent()==null){
						parent=null;
					}
				} catch (NodeNotFoundException e) {
					Messages.setMessageError(e);
					e.printStackTrace();
				} catch (RepositoryException e) {
					Messages.setMessageError(e);
					e.printStackTrace();
				} catch (SecurityException e) {
					Messages.setMessageError(e);
					e.printStackTrace();
				}
		}
		return "content$list";
	}
	
	public String details(){
		String ret="";
		if(node.isDirectory()){
			ret= "directory$details";
		}else if(node.isDocument()){
			if(file!=null){
				getNode().setName(file.getName());
				try {
					data=file.getBytes();
					log.debug(data.length);
				} catch (IOException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				}
			}
			ret= "document$details";
		}
		return ret;
	}
	
	public String createDirectory(){
		node=new Directory();
		return "directory$details";
	}
	
	public String createDocument(){
		Document doc=new Document();
		doc.setMetaData(new DocumentMetaData());
		node=doc;
		return "document$upload";
	}
	
	public String save(){
		GenericProperties properties;
		if(node.isDirectory()){
			Directory directory=(Directory) node;
			if(node.getId()!=null){
				properties=directory.dumpProperties();
				try {
					jlibrary.getRepositoryService().updateDirectory(getTicket(),(DirectoryProperties)properties);
				} catch (RepositoryException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				} catch (SecurityException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				}
			}else{
				properties=new DirectoryProperties();
				try {
					properties.addProperty(DirectoryProperties.DIRECTORY_NAME, directory.getName());
					properties.addProperty(DirectoryProperties.DIRECTORY_DESCRIPTION, directory.getDescription());
					properties.addProperty(DirectoryProperties.DIRECTORY_PARENT, parent.getId());
					properties.addProperty(DirectoryProperties.DIRECTORY_POSITION, 1);
				} catch (PropertyNotFoundException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				} catch (InvalidPropertyTypeException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				}				
				try {
					jlibrary.getRepositoryService().createDirectory(getTicket(),(DirectoryProperties)properties);
				} catch (RepositoryException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				} catch (SecurityException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				}
			}
		}else if(node.isDocument()){
			Document document=(Document) node;
			if(node.getId()!=null){
				log.debug("modifico");
				properties=document.dumpProperties();
				try {
					jlibrary.getRepositoryService().updateDocument(getTicket(),(DocumentProperties)properties);
				} catch (RepositoryException e) {
					Messages.setMessageError(e);
					e.printStackTrace();
				} catch (SecurityException e) {
					Messages.setMessageError(e);
					e.printStackTrace();
				}
			}else{
				properties=new DocumentProperties();
				try {
					properties.addProperty(DocumentProperties.DOCUMENT_NAME, document.getName());
					properties.addProperty(DocumentProperties.DOCUMENT_DESCRIPTION, document.getDescription());
					properties.addProperty(DocumentProperties.DOCUMENT_CONTENT, getData());
					log.debug(getData().length);
					properties.addProperty(DocumentProperties.DOCUMENT_CREATION_DATE, new Date());
					properties.addProperty(DocumentProperties.DOCUMENT_CREATOR, getTicket().getUser().getId());
					properties.addProperty(DocumentProperties.DOCUMENT_IMPORTANCE, Document.IMPORTANCE_MEDIUM);
					properties.addProperty(DocumentProperties.DOCUMENT_KEYWORDS, document.getMetaData().getKeywords());
					properties.addProperty(DocumentProperties.DOCUMENT_LANGUAGE, "es_ES");
					properties.addProperty(DocumentProperties.DOCUMENT_PARENT, parent.getId());
					properties.addProperty(DocumentProperties.DOCUMENT_POSITION, 1);
					properties.addProperty(DocumentProperties.DOCUMENT_TITLE, "asd");
					properties.addProperty(DocumentProperties.DOCUMENT_TYPECODE, Types.TEXT);
					properties.addProperty(DocumentProperties.DOCUMENT_URL, document.getMetaData().getUrl());
					properties.addProperty(DocumentProperties.DOCUMENT_AUTHOR, Author.UNKNOWN);
					properties.addProperty(DocumentProperties.DOCUMENT_PATH, file.getName());
				} catch (PropertyNotFoundException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				} catch (InvalidPropertyTypeException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				}
				
				try {
					jlibrary.getRepositoryService().createDocument(getTicket(),(DocumentProperties) properties);
				} catch (RepositoryException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				} catch (SecurityException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				}
			}
		}
		return "content$saved";
	}
	
	public String delete(){
		try {
			if(node.isDirectory()){
				jlibrary.getRepositoryService().removeDirectory(getTicket(),node.getId());
			}else if(node.isDocument()){
				jlibrary.getRepositoryService().removeDocument(getTicket(),node.getId());
			}
		} catch (RepositoryException e) {
			Messages.setMessageError(e);
			log.error(e.getMessage());
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			log.error(e.getMessage());
		}
		return "content$list";
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		try {
			setNode(jlibrary.getRepositoryService().findNode(getTicket(),id));
		} catch (NodeNotFoundException e) {
			Messages.setMessageError(e);
			log.error(e.getMessage());
		} catch (RepositoryException e) {
			Messages.setMessageError(e);
			log.error(e.getMessage());
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			log.error(e.getMessage());
		}
		this.id = id;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
