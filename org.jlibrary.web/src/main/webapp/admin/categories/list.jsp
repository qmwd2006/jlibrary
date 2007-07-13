<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:t="http://java.sun.com/jsf/tomahawk">
<body>
<ui:composition template="/template1.jsp">
		<ui:define name="title">
			<f:loadBundle basename="org.jlibrary.web.labels" var="labels"/>
			<h:outputText value="#{labels.categories}"/>
		</ui:define>
		<ui:define name="body">
			<h:form id="menu">
				<h:dataTable id="categories" value="#{categoriesManager.list}" var="item">
			     <h:column>     
			         <h:outputText value="#{item.name}" />
			     </h:column>
			     <h:column>     
			         <h:outputText value="#{item.description}" />
			     </h:column>
			     <h:column>     
			         <h:commandLink action="#{categoriesManager.details}">
				         <h:outputText value="detalles"/>
				         <f:setPropertyActionListener target="#{categoriesManager.id}" value="#{item.id}"/>
					</h:commandLink>
			     </h:column>
			     <h:column>     
			         <h:commandLink action="#{categoriesManager.delete}">
			         	<h:outputText value="eliminar"/>
			         	<f:setPropertyActionListener target="#{categoriesManager.id}" value="#{item.id}"/>
			         </h:commandLink>
			     </h:column>
			     <h:column>     
			         <h:commandLink action="#{categoriesManager.subcategories}">
			         	<h:outputText value="subcategorias"/>
			         	<f:setPropertyActionListener target="#{categoriesManager.id}" value="#{item.id}"/>
			         </h:commandLink>
			     </h:column>
			  </h:dataTable>
			  <h:commandLink action="#{categoriesManager.create}"><h:outputText value="nueva"/></h:commandLink>
			</h:form>
		</ui:define>
	</ui:composition>
</body>
</html>