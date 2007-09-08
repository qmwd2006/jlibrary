<#macro directories>
<h1>Directories</h1>
<p>
<#assign hasDirectories=false/>
<#list directory.nodes as menuitem>
  <#if menuitem.directory>
    <#if !isResourcesDir(menuitem.id)>
      <#assign hasDirectories=true/>
	  <A class="nav" href="${repository_url}${menuitem.path}">${menuitem.name}</A>
	</#if>
  </#if>	    	
</#list>
<#if !hasDirectories> 
  There are no directories
</#if>
<br/>
<A href="${root_url}/admin/content/document_form.jsf?repository=${repository.id}&parentId=${directory.id}">Create new document</A>
</p>
</#macro>

<#macro documents>
<h1>Documents</h1>
<p>
<#assign hasDocuments=false/>
<#list directory.nodes as menuitem>
  <#if menuitem.document>
      <#assign hasDocuments=true/>
      <p><A href="${repository_url}${menuitem.path}">${menuitem.name}</A></p>
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

<#macro search>
<h1>Search</h1>
<p class="searchform">
<input type="text" alt="Search" class="searchbox" />
<input type="submit" value="Go!" class="searchbutton" />
</p>
</#macro>