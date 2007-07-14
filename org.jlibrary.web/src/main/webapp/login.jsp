<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core">
<body>
	<ui:composition template="/template1.jsp">
		<ui:define name="title">
			<f:loadBundle basename="org.jlibrary.web.labels" var="labels"/>
			<h:outputText value="#{labels.title}"/>
		</ui:define>
		<ui:define name="body">
			<h:form id="login">
				<h:outputLabel for="user" value="#{labels.user}:"/>
				<h:inputText id="user" value="#{loginManager.credentials.user}" required="true"/>
				<h:message for="user"/>
				<br/>
				<h:outputLabel for="password" value="#{labels.password}:"/>
				<h:inputText id="password" value="#{loginManager.credentials.password}" required="true"/>
				<h:message for="password"/>
				<br/>
				<h:commandButton id="login" action="#{loginManager.login}" value="#{labels.entrar}"/>	
			</h:form>
		</ui:define>
	</ui:composition>
</body>
</html>