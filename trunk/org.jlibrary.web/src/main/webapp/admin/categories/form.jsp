<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:t="http://myfaces.apache.org/tomahawk">
<body>
<t:saveState value="#{categoriesManager.category}"/>
<ui:composition template="/template1.jsp">
		<ui:define name="title">
			<f:loadBundle basename="org.jlibrary.web.labels" var="labels"/>
			<h:outputText value="#{labels.categories}"/>
		</ui:define>
		<ui:define name="body">
			<t:saveState value="#{categoriesManager.category}" id="cat"/>
			<h:form id="form">
				<h:outputLabel value="nombre:" for="name"/>
				<h:inputText id="name" required="true" value="#{categoriesManager.category.name}"/>
				<h:message for="name"/>
				<br/>
				<h:outputLabel value="descripcion:" for="description"/>
				<h:inputTextarea id="description" required="true" value="#{categoriesManager.category.description}"/>
				<h:message for="description"/>
				<br/>
				<h:commandButton action="#{categoriesManager.save}"><h:outputText value="Guardar"/></h:commandButton>
				<h:commandLink action="categories$back" immediate="true"><h:outputText value="Atras"/></h:commandLink>
			</h:form>
		</ui:define>
	</ui:composition>
</body>
</html>