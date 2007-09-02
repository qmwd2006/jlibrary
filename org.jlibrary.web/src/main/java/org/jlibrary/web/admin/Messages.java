package org.jlibrary.web.admin;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class Messages {
	
	public static void setMessageFatal(String name, String details){
		 setMessage(FacesMessage.SEVERITY_FATAL,name,details);
	}
	
	public static void setMessageError(Exception e){
		setMessageError(e.getClass().getName(),e.getMessage());
	}
	
	public static void setMessageError(String name, String details){
		setMessage(FacesMessage.SEVERITY_ERROR,name,details);
	}
	
	public static void setMessageInfo(String name, String details){
		setMessage(FacesMessage.SEVERITY_INFO,name,details);
	}
	
	public static void setMessage(FacesMessage.Severity severity,String name, String details){
		FacesMessage message = new FacesMessage(severity,name, details);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
