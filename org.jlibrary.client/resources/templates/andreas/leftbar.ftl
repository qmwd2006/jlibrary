<#macro directories>
<h1>Directories</h1>
<p>
<#assign hasDirectories=false/>
<#list directory.nodes as menuitem>
  <#if menuitem.directory>
    <#if !isResourcesDir(menuitem.id)>
      <#assign hasDirectories=true/>
	  <A class="nav" href="${filename(menuitem.id)}/index.html">${menuitem.name}</A>
	</#if>
  </#if>	    	
</#list>
<#if !hasDirectories> 
  There are no directories
</#if>
</p>
</#macro>

<#macro documents>
<h1>Documents</h1>
<p>
<#assign hasDocuments=false/>
<#list directory.nodes as menuitem>
  <#if menuitem.document>
      <#assign hasDocuments=true/>
      <p><A href="${filename(menuitem.id)}">${menuitem.name}</A></p>
  </#if>	    	
</#list>
<#if !hasDocuments> 
  There are no more documents on this directory
</#if>
</p>
</#macro>

<#macro categories>
<h1>Categories</h1>
<p>
    <#if category.categories?exists>
		<#list category.categories as menuitem>
			<A class="nav" href="${categoryURL(menuitem.id)}">${menuitem.name}</A>
		</#list>
	</#if>
</p>
</#macro>