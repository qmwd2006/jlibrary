<#macro header>
<div id="header">
<div id="container">

<div id="sitename">
<h1><A href="${repository_url}">${repository.name}</A></h1>
<h2>${repository.description}</h2>
</div>
<div id="mainmenu">
<ul>
<#list repository.categories as category>
  <#if !category.unknownCategory>
  	<#if !category.parent?exists>
    	<li><A href="${categories_root_url}/${category.name}">${category.name}</A></li>
    </#if>
  </#if>
</#list>
</ul>
</div> 
    <#if location_url="">
    <#else>
	    <div id="location">
	        <A href="${repository_url}">${repository.name}</A> ${location_url?replace("/<A"," &raquo; <A")}
	    </div>
	</#if>
<div id="connect_info">
<p>Welcome ${ticket.user.name}
<#if ticket.user.name="guest">
  <A href="${root_url}/login.jsf?repository=${repository.name}">(log in)</A>
<#else>
  <A href="${root_url}/logout.jsf?repository=${repository.name}">(log out)</A>
</#if> 
<#if ticket.user.admin>
  <A href="${root_url}/admin/home.jsf">Admin</A>
</#if> 
</p>
</div> 	
<div id="wrap">
</#macro>