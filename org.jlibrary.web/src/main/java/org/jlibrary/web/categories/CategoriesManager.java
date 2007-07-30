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
	private Category parentCategory=null;
	private Logger log=Logger.getLogger(CategoriesManager.class);
	private String id;
	public CategoriesManager(){}
	
	public ListDataModel getList(){
		Set cats;
		try {
			if(parentCategory!=null){
				log.debug("categoria padre: "+parentCategory.getId());
				parentCategory=jlibrary.getRepositoryService().findCategoryById(getTicket(),parentCategory.getId());
				cats=parentCategory.getCategories();
			}else{
				log.debug("categorias raiz");
				cats=getRepository().getCategories();
			}
			List categories=new ArrayList(cats);
			list=new ListDataModel(categories);
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
		parentCategory=category;
		return "categories$list";
	}
	
	public String parent(){
		if(parentCategory!=null){
			parentCategory=parentCategory.getParent();
		}
		return "categories$list";
	}
	
	public String create(){
		category=new Category();
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
				if (parentCategory != null) {
					properties.addProperty(CategoryProperties.CATEGORY_PARENT, parentCategory.getId());				
				}
			} catch (PropertyNotFoundException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			} catch (InvalidPropertyTypeException e) {
				Messages.setMessageError(e);
				e.printStackTrace();
			}				
			try {
				jlibrary.getRepositoryService().createCategory(getTicket(),properties);
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
		log.debug(id);
		this.id = id;
	}

	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}
}