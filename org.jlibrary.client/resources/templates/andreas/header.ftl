<#macro header>
<div id="header">
<div id="container">

<div id="sitename">
<h1><A href="${root_url}/index.html">${repository.name}</A></h1>
<h2>${repository.description}</h2>
</div>
<div id="mainmenu">
<ul>
<#list repository.categories as category>
  <#if !category.unknownCategory>
  	<#if !category.parent?exists>
    	<li><A href="${categoryURL(category.id)}">${category.name}</A></li>
    </#if>
  </#if>
</#list>
</ul>
</div> 
    <#if location_url="">
    <#else>
	    <div id="location">
	        <A href="${root_url}/index.html">jLibrary</A> ${location_url?replace("/<A"," &raquo; <A")}
	    </div>
	</#if>
<div id="wrap">
</#macro>