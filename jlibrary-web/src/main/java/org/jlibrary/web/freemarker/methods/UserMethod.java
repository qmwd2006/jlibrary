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
package org.jlibrary.web.freemarker.methods;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.User;
import org.jlibrary.web.WebConstants;
import org.jlibrary.web.content.MembersRegistry;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * Obtains a user given an id
 * 
 * @author martin
 *
 */
public class UserMethod implements TemplateMethodModel {

	static Logger logger = LoggerFactory.getLogger(UserMethod.class);
	
	private FreemarkerExporter exporter;

	public UserMethod(FreemarkerExporter exporter) {

		this.exporter = exporter;
	}
	
	public Object exec(List args) throws TemplateModelException {

		if (args.size() != 2) {
            throw new TemplateModelException("Wrong arguments. Correct usage is UserMethod(repositoryId,userId)");
        }

		Object arg1 = args.get(0);
		if (!(arg1 instanceof String)) {
			throw new TemplateModelException("First argument should be a string");
		}
		Object arg2 = args.get(1);
		if (!(arg2 instanceof String)) {
			throw new TemplateModelException("Second argument should be a string");
		}

		String id = (String)arg2;
		String repositoryId = (String)arg1;
		Member member = MembersRegistry.getInstance().getUser(repositoryId,id);
		ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
		if (member.equals(User.ADMIN_USER)) {
			return bundle.getString(User.ADMIN_NAME);
		} else {
			String userName = member.getName();
			if (userName.equals(WebConstants.ANONYMOUS_WEB_USERNAME)) {
				userName = bundle.getString(WebConstants.ANONYMOUS_WEB_USERNAME);
			}
			return userName;
		}
	}
}
