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
			<h:outputText value="#{labels.title}"/>
		</ui:define>
		<ui:define name="body">
			<h:form>
				<a href="repositories/www">Ir a la parte publica</a>
			</h:form>
		</ui:define>
	</ui:composition>
</body>
</html>