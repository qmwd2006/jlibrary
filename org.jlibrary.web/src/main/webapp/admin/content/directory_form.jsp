<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:t="http://myfaces.apache.org/tomahawk">
<body>
<ui:composition template="/template1.jsp">
		<ui:define name="title">
			<f:loadBundle basename="org.jlibrary.web.labels" var="labels"/>
			<h:outputText value="#{labels.documents}"/>
		</ui:define>
		<ui:define name="body">
			<t:saveState value="#{documentsManager.node}" id="cat"/>
			<t:saveState id="parent" value="#{documentsManager.parent}"/>
			<h:form id="form">
				<h:outputLabel value="nombre:" for="name"/>
				<h:inputText id="name" required="true" value="#{documentsManager.node.name}"/>
				<h:message for="name"/>
				<br/>
				<h:outputLabel value="descripcion:" for="description"/>
				<h:inputTextarea id="description" required="true" value="#{documentsManager.node.description}"/>
				<h:message for="description"/>
				<br/>
				<h:commandButton action="#{documentsManager.save}">
					<h:outputText value="Guardar"/>
				</h:commandButton>
				<h:commandLink action="content$back" immediate="true">
					<h:outputText value="Atras"/>
				</h:commandLink>
			</h:form>
		</ui:define>
	</ui:composition>
</body>
</html>