package org.jlibrary.web.content;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.GenericProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.web.AbstractManager;
import org.jlibrary.web.Messages;

public class DocumentsManager extends AbstractManager {
	private ListDataModel list;
	private Node node;
	private Node parent;
	private Logger log=Logger.getLogger(DocumentsManager.class);
	private String id;
	private UploadedFile file;

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
			e.printStackTrace();
		} catch (RepositoryException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		}
		list=new ListDataModel(nodes);
		return list;
	}
	
	public String subNodes(){
		parent=node;
		log.debug(parent.getId());
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
			ret= "document$details";
		}
		return ret;
	}
	
	public String createDirectory(){
		node=new Directory();
		return "directory$details";
	}
	
	public String createDocument(){
		node=new Document();
		return "document$details";
	}
	
	public String save(){
		GenericProperties properties;
		if(node.isDirectory()){
			Directory directory=(Directory) node;
			if(node.getId()!=null){
				log.debug("modifica");
				properties=directory.dumpProperties();
				try {
					jlibrary.getRepositoryService().updateDirectory(getTicket(),(DirectoryProperties)properties);
				} catch (RepositoryException e) {
					Messages.setMessageError(e);
					e.printStackTrace();
				} catch (SecurityException e) {
					Messages.setMessageError(e);
					e.printStackTrace();
				}
			}else{
				log.debug("nueva");
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
				log.debug("modifica");
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
				log.debug("nueva");
				properties=new DocumentProperties();
				try {
					properties.addProperty(DocumentProperties.DOCUMENT_NAME, document.getName());
					properties.addProperty(DocumentProperties.DOCUMENT_DESCRIPTION, document.getDescription());
					properties.addProperty(DocumentProperties.DOCUMENT_PARENT, parent.getId());
					properties.addProperty(DocumentProperties.DOCUMENT_POSITION, 1);
					properties.addProperty(DocumentProperties.DOCUMENT_PARENT, "path");
				} catch (PropertyNotFoundException e) {
					Messages.setMessageError(e);
					log.error(e.getMessage());
				} catch (InvalidPropertyTypeException e) {
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
			e.printStackTrace();
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (RepositoryException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
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

    public String processMyFile() {
    	log.debug("procesando");
        try {
            InputStream in = new BufferedInputStream(
            		file.getInputStream());
            log.debug("file:"+file.getName());
            return "content$saved";
        } catch (IOException e) {
        	Messages.setMessageError(e);
            e.printStackTrace();
            return null;
        }
    }
}
