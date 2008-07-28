<#macro directories>
	<h3>SubDirectories</h3>
	<div class="content">
		<ul class="linklist">
			<#assign hasDirectories=false/>
			<#list directory.nodes as menuitem>
				<#if menuitem.directory>
  				  <#assign hasDirectories=true/>
  				  <li class="first"><a href="${repository_url}${menuitem.path?replace("%", "%25")}">${menuitem.name}</a></li>
				</#if>	    	
			</#list>
			<#if !hasDirectories> 
				<li>There are no directories.</li>
			</#if>
		</ul>
	</div>
</#macro>

<!-- This macro will be used when the directory has an index.html file that will be shown as the 
     page content. So this will provide a way to access to the documents in that folder -->
<#macro documents>
	<h3>See also here</h3>
	<div class="content">
			<#assign hasDocuments=false/>
			<#list directory.nodes as menuitem>
				<#if menuitem.document && menuitem.name != "index.html" && menuitem.name != "index">
  			  <#assign hasDocuments=true/>
  				<a href="${repository_url}${menuitem.path?replace("%", "%25")}">${menuitem.name}</a>
				</#if>
			</#list>
			<#if !hasDocuments> 
			  <p>No more documents.</p>
			</#if>
	</div>
</#macro>

<#macro actions>
	<h3>Actions</h3>
	<div class="content">
	  <#assign hasActions=false/>
	  <#if ticket.user.admin || (ticket.user.editor && ("-1" != directory.creator))>
	    <a href="${root_url}/forward?method=createform&amp;type=directory&amp;repository=${repository.name}&amp;id=${directory.id}">Create directory</a>
           <#assign hasActions=true/>
           <br/>
	  </#if>
         <#if ticket.user.admin || (ticket.user.editor && (ticket.user.id == directory.creator))>
	    <#assign hasActions=true/>
	    <a href="${root_url}/forward?method=updateform&amp;type=node&amp;repository=${repository.name}&amp;id=${directory.id}">Update directory</a>
	    <br/>
	    <a href="${root_url}/forward?method=delete&amp;type=node&amp;repository=${repository.name}&amp;id=${directory.id}">Delete directory</a>
	    <br/>
	  </#if>
	  <#if ticket.user.admin || (ticket.user.editor && ("-1" != directory.creator))>
 	    <#assign hasActions=true/>
	    <a href="${root_url}/forward?method=createform&amp;type=document&amp;repository=${repository.name}&amp;id=${directory.id}">Create document</a>
	    <br/>
	    <a href="${root_url}/forward?method=createform&amp;type=documentupload&amp;repository=${repository.name}&amp;id=${directory.id}">Upload document</a>
	    <br/>
	  </#if>
         <#if !hasActions>
	   <p>No actions available.</p>
	  </#if>
	</div>
</#macro>

<#macro actionsCategory>
	<h3>Actions</h3>
	<div class="content">
         <#assign hasActions=false/>
	  <#if ticket.user.admin>
            <#assign hasActions=true/>
		  <a href="${root_url}/forward?method=updateform&amp;type=category&amp;repository=${repository.name}&amp;id=${category.id}">Update category</a><br/>
		  <a href="${root_url}/forward?method=delete&amp;type=category&amp;repository=${repository.name}&amp;id=${category.id}">Delete category</a><br/>
	  </#if>
	</div>
         <#if !hasActions>
	   <p>No actions available.</p>
	  </#if>
</#macro>

<#macro infoDirectory>
  <h3>Directory Information</h3>
	<div class="content">
	  	<strong>Added:</strong> ${directory.date?string("yyyy-MM-dd HH:mm:ss")}<br/>
	  	<strong>From:</strong> ${node_creator}<br/>
  </div>
</#macro>

<#macro admin>
  <h3>Actions</h3>
  <div class="content">
    <#assign hasActions=false/>
    <#if ticket.user.admin || ticket.user.editor>
      <#assign hasActions=true/>
      <a href="${root_url}/forward?method=updateform&amp;type=node&amp;repository=${repository.name}&amp;id=${document.id}">Update document</a><br/>
      <#if ticket.user.admin || (ticket.user.editor && (ticket.user.id == document.creator))>
        <a href="${root_url}/forward?method=delete&amp;type=node&amp;repository=${repository.name}&amp;id=${document.id}">Delete document</a><br/>
      </#if>
    </#if>
    <#if !hasActions>
      <p>No actions available.</p>
    </#if>
  </div>  
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
            <li><a href="${repository_url}${children.path?replace("%", "%25")}">${children.name}</a></li>
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
      <#assign hasCategories=false/>
		  <#list document_categories as category>
		    <#if category.id != "-1">
		      <p><a href="${categories_root_url}/${category.name}">${category.name}</a></p>
		      <#assign hasCategories=true/>
		    </#if>
		  </#list>
			<#if !hasCategories> 
				<p>Not categorized.</p>
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