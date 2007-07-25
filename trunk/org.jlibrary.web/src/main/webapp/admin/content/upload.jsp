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
			<h:form id="MyForm" enctype="multipart/form-data" >
			    <h:messages globalOnly="true" styleClass="message"/>
			    <h:panelGrid columns="3" border="0" cellspacing="5">
			        <h:outputLabel for="myFileId" value="File: "/>
			        <t:inputFileUpload id="myFileId"
			            value="#{fileManager.file}"
			            storage="file"
			            required="true"/>
			        <h:message for="myFileId"/>
			        <h:commandButton value="Submit"
			            action="#{fileManager.processMyFile}"/>
			        <h:outputText value=" "/>
			    </h:panelGrid>
			</h:form>
		</ui:define>
	</ui:composition>
</body>
</html>