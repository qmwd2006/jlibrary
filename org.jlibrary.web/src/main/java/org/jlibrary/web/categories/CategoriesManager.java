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
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.web.AbstractManager;
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
		log.debug("Listando las categorias");
		if(parentId!=null){
			cats=category.getCategories();
		}else{
			cats=getRepository().getCategories();
		}
		List categories=new ArrayList(cats);
		log.debug(categories.size());
		list=new ListDataModel(categories);
		category=new Category();
		return list;
	}
	
	public String subcategories(){
		parentId=category.getId();
		return "categories$list";
	}
	
	public String create(){
		category=new Category();
		log.debug("categoria instanciada");
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
				conf.getRepositoryService().updateCategory(getTicket(),category.getId(),properties);
			} catch (CategoryAlreadyExistsException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}else{
			log.debug("nueva");
			properties=new CategoryProperties();
			try {
				properties.addProperty(CategoryProperties.CATEGORY_NAME, category.getName());
				properties.addProperty(CategoryProperties.CATEGORY_DESCRIPTION, category.getDescription());
				properties.addProperty(CategoryProperties.CATEGORY_REPOSITORY, getTicket().getRepositoryId());
				if (category.getParent() != null) {
					properties.addProperty(CategoryProperties.CATEGORY_PARENT, category.getParent().getId());				
				}
			} catch (PropertyNotFoundException e) {
				e.printStackTrace();
			} catch (InvalidPropertyTypeException e) {
				e.printStackTrace();
			}				
			try {
				Category nuevaCat=conf.getRepositoryService().createCategory(getTicket(),properties);
			} catch (CategoryAlreadyExistsException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		log.debug("categoria guardada");
		return "categories$saved";
	}
	
	public String delete(){
		log.debug("eliminar");
		try {
			conf.getRepositoryService().deleteCategory(getTicket(),category.getId());
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return "categories$deleted";
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		log.debug(id);
		try {
			category=conf.getRepositoryService().findCategoryById(getTicket(),id);
		} catch (CategoryNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		this.id = id;
	}
}
