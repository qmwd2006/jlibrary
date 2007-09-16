<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:t="http://myfaces.apache.org/tomahawk"
      xmlns:fck="http://www.fck-faces.org/fck-faces">
<body>
<ui:composition template="/template1.jsp">
		<ui:define name="title">
			<f:loadBundle basename="org.jlibrary.web.labels" var="labels"/>
			<h:outputText value="#{labels.comments}"/>
		</ui:define>
		<ui:define name="body" enctype="multipart/form-data">
			<t:saveState value="#{documentsManager.node}" id="nod"/>
			<h:form id="form">
				<h:outputLabel value="Add your comment:" for="name"/>
				<h:inputText id="name" required="true" value="#{documentsManager.note.note}"/>
				<h:message for="name"/>
				<h:commandButton action="#{documentsManager.saveComment}">
					<h:outputText value="Guardar"/>
				</h:commandButton>
				<h:inputHidden value="#{documentsManager.referer}"/>
			</h:form>
		</ui:define>
	</ui:composition>
</body>
</html>