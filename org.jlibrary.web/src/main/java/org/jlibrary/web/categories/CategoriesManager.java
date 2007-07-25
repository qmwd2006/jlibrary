package org.jlibrary.web.categories;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.model.ListDataModel;

import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.web.AbstractManager;
import org.jlibrary.web.Messages;
/**
 * 
 * @author Dani
 *
 */
public class CategoriesManager extends AbstractManager {
	private ListDataModel list;
	private Category category=null;
	private Logger log=Logger.getLogger(CategoriesManager.class);
	private String id;
	private String parentId;
	public CategoriesManager(){}
	
	public ListDataModel getList(){
		Set cats;
		try {
			log.debug("Listando las categorias");
			if(parentId!=null){
				log.debug("categoria padre: "+parentId);
				cats=category.getCategories();
			}else{
				cats=getRepository().getCategories();
			}
			List categories=new ArrayList(cats);
			list=new ListDataModel(categories);
			category=new Category();
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
		return list;
	}
	
	public String subCategories(){
		parentId=category.getId();
		return "categories$list";
	}
	
	public String parentCategory(){
		if(category!=null){
			parentId=category.getParent().getId();
		}
		return "categories$list";
	}
	
	public String create(){
		category=new Category();
		log.debug("categoria instanciada, parentId:"+parentId);
		return "categories$form";
	}
	
	public String details(){
		log.debug("detalle de:"+category.getId());
		return "categories$form";
	}
	
	public String save(){
		CategoryProperties properties;
		if(category.getId()!=null){
			log.debug("modifica");
			properties=category.dumpProperties();
			try {
				jlibrary.getRepositoryService().updateCategory(getTicket(),category.getId(),properties);
			} catch (CategoryAlreadyExistsException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			} catch (RepositoryException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			} catch (SecurityException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			}
		}else{
			log.debug("nueva");
			properties=new CategoryProperties();
			try {
				properties.addProperty(CategoryProperties.CATEGORY_NAME, category.getName());
				properties.addProperty(CategoryProperties.CATEGORY_DESCRIPTION, category.getDescription());
				properties.addProperty(CategoryProperties.CATEGORY_REPOSITORY, getTicket().getRepositoryId());
				if (parentId != null) {
					properties.addProperty(CategoryProperties.CATEGORY_PARENT, parentId);				
				}
			} catch (PropertyNotFoundException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			} catch (InvalidPropertyTypeException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			}				
			try {
				Category nuevaCat=jlibrary.getRepositoryService().createCategory(getTicket(),properties);
			} catch (CategoryAlreadyExistsException e) {
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
		log.debug("categoria guardada");
		return "categories$saved";
	}
	
	public String delete(){
		log.debug("eliminar");
		try {
			jlibrary.getRepositoryService().deleteCategory(getTicket(),category.getId());
		} catch (RepositoryException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		} catch (SecurityException e) {
			Messages.setMessageError(e);
			e.printStackTrace();
		}
		return "categories$deleted";
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		log.debug("seteando categoria");
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if(id!=null){
			try {
				category=jlibrary.getRepositoryService().findCategoryById(getTicket(),id);
			} catch (CategoryNotFoundException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			} catch (RepositoryException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			}
		}else{
			category=null;
		}
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		log.debug("seteando padre"+parentId);
		this.parentId = parentId;
	}
}