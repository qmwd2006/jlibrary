<#macro directories>
	<h3>SubDirectories</h3>
	<div class="content">
		<ul class="linklist">
			<#assign hasDirectories=false/>
			<#list directory.nodes as menuitem>
				<#if menuitem.directory>
					<#if !isResourcesDir(menuitem.id)>
  					<#assign hasDirectories=true/>
  					<li class="first"><a href="${repository_url}${menuitem.path}">${menuitem.name}</a></li>
					</#if>
				</#if>	    	
			</#list>
			<#if !hasDirectories> 
				<li>There are no directories.</li>
			</#if>
		</ul>
	</div>
</#macro>

<#macro actions>
	<h3>Actions</h3>
	<div class="content">
	  <#if ticket.user.admin>
			<a href="${root_url}/forward?method=updateform&amp;type=node&amp;repository=${repository.name}&amp;id=${directory.id}">Update directory</a>
			<br/>
			<a href="${root_url}/forward?method=createform&amp;type=directory&amp;repository=${repository.name}&amp;id=${directory.id}">Create directory</a>
			<br/>
			<#if directory.parent??>
		    	<a href="${root_url}/forward?method=delete&amp;type=node&amp;repository=${repository.name}&amp;id=${directory.id}">Delete directory</a><br/>
		    </#if>
			<#if !directory.parent??>
				<a href="${root_url}/forward?method=createform&amp;type=category&amp;repository=${repository.name}&amp;id=${directory.id}">Create category</a><br/>
			</#if>
			<a href="${root_url}/forward?method=createform&amp;type=document&amp;repository=${repository.name}&amp;id=${directory.id}">Create document</a>
			<br/>
		</#if>
	</div>
</#macro>

<#macro actionsCategory>
	<h3>Actions</h3>
	<div class="content">
	  <#if ticket.user.admin>
		  <a href="${root_url}/forward?method=updateform&amp;type=category&amp;repository=${repository.name}&amp;id=${category.id}">Update category</a><br/>
		  <a href="${root_url}/forward?method=delete&amp;type=category&amp;repository=${repository.name}&amp;id=${category.id}">Delete category</a><br/>
		</#if>
	</div>
</#macro>

<#macro infoDirectory>
  <h3>Directory Information</h3>
	<div class="content">
	  	<strong>Added:</strong> ${directory.date?string("yyyy-MM-dd HH:mm:ss")}<br/>
	  	<strong>From:</strong> ${node_creator}<br/>
  </div>
</#macro>

<#macro admin>
    <#if !ticket.user.name.equals("guest")>
	  	<h3>Admin</h3>
	  	<div class="content">
			  	<a href="${root_url}/forward?method=updateform&amp;type=node&amp;repository=${repository.name}&amp;id=${document.id}">Update document</a><br/>
			  	<a href="${root_url}/forward?method=delete&amp;type=node&amp;repository=${repository.name}&amp;id=${document.id}">Delete document</a><br/>
			</div>  
		</#if>
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
            <li><a href="${repository_url}${children.path}">${children.name}</a></li>
		  </#if>
	  </#list> 			         
    </ul>
  </#if>
</#macro>

<#macro favorites>
  <h3>Favoritos</h3>
  <div class="content">
	  <#if category.favorites?size = 0>
	  	<p>No favorites</p>
	  <#else>
		  <#list category.favorites as children>
		  	<p><a href="${nodeURL(children.document)}">
			  		${node(children.document).name}</a></p>
		  </#list> 			         
	  </#if>
	</div>  
</#macro>

<#macro infoDocument>
  <h3>Document Information</h3>
	<div class="content">
	  <p><strong>Added:</strong> ${document.date?string("yyyy-MM-dd HH:mm:ss")}</p>
	  <p><strong>From:</strong> ${node_author}</p>
	  <p><strong>Updated:</strong> ${document_update_date?string("yyyy-MM-dd HH:mm:ss")}</p>
  </div>
</#macro>

<#macro categoriesDocument>
  <h3>Categories</h3>
  <div class="content">
	  <#if document_categories?exists>
		  <#if document_categories?size = 0>
		    <p>Not categorized.</p>
		  <#else>
		    <#list document_categories as category>
		      <p><a href="${categoryURL(category.id)}">${category.name}</a></p>
		     </#list>
		  </#if> 
	  </#if>
	</div>  
</#macro>

<#macro resources>
  <h3>Attachments</h3>
  <div class="content">
	  <#if document.resourceNodes?size = 0>
	    <p>No attachments.</p>
	  <#else>
	    <#list document.resourceNodes as resource>
	          <p><a href="${nodeURL(resource.id)}">${node(resource.id).name}</a></p>
	     </#list>
	  </#if> 
	</div>
</#macro>

<#macro relationsDocument>
  <h3>See also</h3>
  <div class="content">
	  <#if document.relations?size = 0>
	    <p>No recommendations.</p>
	  <#else>
	    <#list document.relations as relation>
	          <p><a href="${nodeURL(relation.id)}">${relation.name}</a></p>
	     </#list>
	  </#if> 
	</div>  
</#macro>