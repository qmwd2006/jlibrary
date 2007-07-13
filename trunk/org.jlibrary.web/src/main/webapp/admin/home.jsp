<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<html>
<f:view>
<f:loadBundle basename="org.jlibrary.web.labels" var="labels"/>
<head>
	<title><h:outputText value="#{labels.admin}"/>-<h:outputText value="#{labels.title}"/></title>
</head>
<body>
<h1><h:outputText value="#{labels.admin}"/></h1>
<h:form id="menu">
	<h:commandLink action="#{categoriesManager.list}" ><h:outputText value="#{labels.categories}"/></h:commandLink>
</h:form>
</body>
</f:view>
</html>