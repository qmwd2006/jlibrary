package org.jlibrary.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.jlibrary.web.services.TemplateService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@SuppressWarnings("serial")
public class JLibraryServlet extends HttpServlet{
	protected ApplicationContext context;
	
	@Override
	public void init() throws ServletException {
		context=WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		super.init();
	}
	
	public String getTemplate(){
		TemplateService templ=(TemplateService) context.getBean("template");
		return templ.getTemplateDirectory();		
	}
	
}
