<#macro infoDirectory>
  <h1>Information</h1>
  <p><strong>Created in:</strong> ${directory.date?string("yyyy-MM-dd HH:mm:ss")}</p>
  <p><strong>Created by:</strong> ${node_creator}</p>
</#macro>

<#macro directoryDocuments>
  <#assign hasDocuments=false/>
  <#list directory.nodes as children>
  	<#if children.document>
  	  <#assign hasDocuments=true/>
  	</#if>
  </#list>
          
  <#if hasDocuments>          	  
  <h1>Documents:</h1>
	<ul class="linklist">
	  <#list directory.nodes as children>
		  <#if children.document>
            <li><A href="${repository_url}${children.path}">${children.name}</A></li>
		  </#if>
	  </#list> 			         
    </ul>
  </#if>
</#macro>

<#macro favorites>
  <h1>Favorites</h1>
  <#if category.favorites?size = 0>
  	<p>There are no favorites</p>
  <#else>
	  <#list category.favorites as children>
	  	<p><a href="${nodeURL(children.document)}">
		  		${children.document}</a></p>
	  </#list> 			         
  </#if>
</#macro>

<#macro infoDocument>
  <h1>Information</h1>

  <p><strong>Created in:</strong> ${document.date?string("yyyy-MM-dd HH:mm:ss")}</p>
  <p><strong>Author:</strong> ${node_author}</p>
  <p><strong>Size:</strong> ${document.size} bytes</p>
  <p><strong>Last updated:</strong> ${document_update_date?string("yyyy-MM-dd HH:mm:ss")}</p>
</#macro>

<#macro categoriesDocument>
  <h1>Categories</h1>
  <#if document_categories?exists>
	  <#if document_categories?size = 0>
	    <p>Document not classified</p>
	  <#else>
	    <#list document_categories as category>
	      <p><A href="${categoryURL(category.id)}">${category.name}</A></p>
	     </#list>
	  </#if> 
  </#if>
</#macro>

<#macro relationsDocument>
  <h1>You may be interested in...</h1>
  <#if document.relations?size = 0>
    <p>There isn't related documents</p>
  <#else>
    <#list document.relations as relation>
          <p><a href="${nodeURL(relation.id)}">${relation.name}</A></p>
     </#list>
  </#if> 
</#macro>

<#macro search>
<h1>Search</h1>
<p class="searchform">
<form action="${root_url}/search">
<input type="text" name="text" alt="Search" class="searchbox" />
<input type="hidden" name="repository" value="${repository.name}"/>
<input type="submit" value="Go!" class="searchbutton" />
</form>
</p>
</#macro>