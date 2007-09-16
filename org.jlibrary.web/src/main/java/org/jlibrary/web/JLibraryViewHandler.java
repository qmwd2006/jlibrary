package org.jlibrary.web;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.sun.facelets.FaceletViewHandler;

public class JLibraryViewHandler extends FaceletViewHandler {

	public JLibraryViewHandler(ViewHandler parent) {
		
		super(parent);
	}

	@Override
	protected String getRenderedViewId(FacesContext context, String actionId) {

		if (actionId.startsWith("#")) {
			return actionId;
		}
		return super.getRenderedViewId(context, actionId);
	}
	
	@Override
	public String getActionURL(FacesContext context, String viewId)
	{
	    String result = viewId;
	    if(viewId.startsWith("#")) {
	        ValueBinding vb = context.getApplication().createValueBinding(viewId);
	        result = vb.getValue(context).toString();
	    } else {	    
	    	result = super.getActionURL(context, viewId);
	    }
	    int queryStart = viewId.indexOf("?");
	    if((queryStart > 0) && (result.indexOf("?") == -1))
	    {
	        result = result + viewId.substring(queryStart);
	    }
	    return result;
	}
}
