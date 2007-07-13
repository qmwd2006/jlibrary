package org.jlibrary.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jlibrary.web.login.LoginManager;

public class AuthFilter implements Filter {
	
	private Logger log=Logger.getLogger(AuthFilter.class);
	public void init(FilterConfig arg0) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		log.debug("filtrando");
		if (((HttpServletRequest)request).getSession().getAttribute(LoginManager.LOGGED)==null) {
			((HttpServletResponse)response).sendRedirect("/faces/admin/login.jsp");
			log.debug("no autorizado");
        }
	    chain.doFilter(request, response);
	}

	public void destroy() {

	}

}
