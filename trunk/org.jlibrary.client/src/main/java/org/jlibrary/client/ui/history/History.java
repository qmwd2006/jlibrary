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
package org.jlibrary.client.ui.history;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/** @author Hibernate CodeGenerator */
public class History implements Serializable {
	
	static final long serialVersionUID = -3636091437997234313L;
	
	private HistoryPage page;
	
    /** identifier field */
    private String id;

    private String url;

    private java.util.Date date;
    
    /** full constructor */
    public History(HistoryPage page,
				   String url,
				   java.util.Date date) {
    	
        this.url = url;
        this.date = date;
		this.page = page;
    }

    /** default constructor */
    public History() {}


    public java.lang.String getId() {
        return this.id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public String toString() {
		
		return url;
    }

    public boolean equals(Object other) {
        
    	if (other == null) return false;
    	if (!(other instanceof History)) {
    		return false;
    	}
    	
    	String url1 = getUrl();
    	String url2 = ((History)other).getUrl();
    	url1 = StringUtils.replace(url1,"http://","");
    	url2 = StringUtils.replace(url2,"http://","");
    	url1 = StringUtils.replace(url1,"/","");
    	url2 = StringUtils.replace(url2,"/","");
    	
    	return url1.equals(url2);
    }

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return Returns the date.
	 */
	public java.util.Date getDate() {
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public HistoryPage getPage() {
		return page;
	}

	public void setPage(HistoryPage page) {
		this.page = page;
	}
}
