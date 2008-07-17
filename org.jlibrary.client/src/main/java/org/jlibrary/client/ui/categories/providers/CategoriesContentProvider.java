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
package org.jlibrary.client.ui.categories.providers;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Repository;

/**
 * Content provider for favorites tree
 */
public class CategoriesContentProvider implements ITreeContentProvider {

	private final static Comparator comparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			
			
			if (o1 instanceof Repository) {
				Repository r1 = (Repository)o1;
				if (o2 instanceof Repository) {
					Repository r2 = (Repository)o2;
					return r1.getName().compareTo(r2.getName());
				} else {
					Category category2 = (Category)o2;
					Repository r2 = RepositoryRegistry.getInstance().getRepository(category2.getRepository());
					return r1.getName().compareTo(r2.getName());
				}
			} 
			if (o2 instanceof Repository) {
				Repository r2 = (Repository)o2;
				if (o1 instanceof Repository) {
					Repository r1 = (Repository)o1;
					return r2.getName().compareTo(r1.getName());
				} else {
					Category category1 = (Category)o1;
					Repository r1 = RepositoryRegistry.getInstance().getRepository(category1.getRepository());
					return r2.getName().compareTo(r1.getName());
				}				
			}
			
			Category Category1 = (Category)o1;
			Category category2 = (Category)o2;
			
			Collator collator = Collator.getInstance();
			return collator.compare(Category1.getName(), category2.getName());
		}
	};
	
	public CategoriesContentProvider() {}
		
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element) {

		if (element instanceof Category) {
			Category category = (Category)element;
			if (category.getCategories() != null) {
				Object[] children = category.getCategories().toArray();
				Arrays.sort(children,comparator);
				return children;
			} else {
				return new Object[]{};
			}
		}
		Repository repository = (Repository)element;
		if (!((Repository)element).isConnected()) {
			return new Object[]{};
		}		
		Object[] categories = repository.getFilteredCategories().toArray();
		Arrays.sort(categories,comparator);
		return categories;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		
		if (element instanceof Category) {
			Category category = (Category)element;
			return category.getParent();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		
		if (element instanceof Category) {
			Category category = (Category)element;
			if (category.getCategories() != null) {
				return !category.getCategories().isEmpty();
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object element) {
		
		if (element instanceof Repository) {
			return ((Repository)element).getCategories().toArray();
		}
		Object[] elements = (Object[])element;
		Arrays.sort(elements,comparator);
		return elements;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}

}
