<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:t="http://myfaces.apache.org/tomahawk"
      xmlns:c="http://java.sun.com/jstl/core">
<body>
<ui:composition template="/template1.jsp">
		<ui:define name="title">
			<f:loadBundle basename="org.jlibrary.web.labels" var="labels"/>
			<h:outputText value="#{labels.documents}"/>
		</ui:define>
		<ui:define name="body">
			<t:saveState id="parent" value="#{documentsManager.parent}"/>
			<h:form id="menu">
				<h:dataTable id="contents" value="#{documentsManager.list}" var="item">
			     <h:column>     
			         <h:outputText value="#{item.name}" />
			     </h:column>
			     <h:column>     
			         <h:outputText value="#{item.description}" />
			     </h:column>
			     <h:column>     
			         <h:commandLink id="det" action="#{documentsManager.details}">
				         <h:outputText value="detalles"/>
				         <t:updateActionListener property="#{documentsManager.id}" value="#{item.id}"/>
					</h:commandLink>
			     </h:column>
			     <h:column>     
			         <h:commandLink id="del" action="#{documentsManager.delete}">
			         	<h:outputText value="eliminar"/>
			         	<t:updateActionListener property="#{documentsManager.id}" value="#{item.id}"/>
			         </h:commandLink>
			     </h:column>
			     <h:column>
				     <h:commandLink id="sub" action="#{documentsManager.subNodes}">
			         	<h:outputText rendered="#{item.directory}" value="subdirectorios"/>
			         	<t:updateActionListener property="#{documentsManager.id}" value="#{item.id}"/>
			         </h:commandLink>
			     </h:column>
			  </h:dataTable>
			  <h:commandLink action="#{documentsManager.createDirectory}">
			  	<h:outputText value="nuevo directorio"/>
			  </h:commandLink>
			  <br/>
			  <h:commandLink action="#{documentsManager.createDocument}">
			  	<h:outputText value="nuevo documento"/>
			  </h:commandLink>
			  <br/>
			  <c:if test="#{not empty documentsManager.parent}">
			  <h:commandLink action="#{documentsManager.parentNode}">
			  	<h:outputText value="padre"/>
			  </h:commandLink>
			  </c:if>
			</h:form>
		</ui:define>
	</ui:composition>
</body>
</html>