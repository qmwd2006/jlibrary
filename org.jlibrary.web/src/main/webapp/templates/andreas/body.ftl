<#macro content>
	<#if document_content="">
		Download: <A href="${repository_url}${document.path}">${document.name}</A>
	<#else>
		${document_content}
	</#if>
</#macro>

<#macro directoryContent>
  <#if directory_content="">
  	  <p>${directory.description}</p>
	  <#list directory.nodes as children>
		  <#if !children.directory>
		  	  <h1><A style="text-decoration:none" 
		  	         href="${repository_url}${children.path}">${children.name}</A></h1>
			  <p>${children.description}</p>
		  </#if>
	  </#list>
  <#else>
	 ${directory_content}
  </#if>
</#macro>

<#macro notesDocument>
  <#if document.notes?size != 0>
  	  <div id ="comments"></div>
      <h2>Comments</h2>
      <#list document.notes as note>
          <p><strong>${note.date?date} ${username(note.creator)} says:</strong> ${note.note}</p>
          <br/>
      </#list>
  </#if>
</#macro>

<#macro listCategory>
  <p>${category.description}</p>
  <p/>
  <#list category_documents as children>
	  <#if !children.directory>
	  	  <h2><A style="text-decoration:none" 
  	         href="${nodeURL(children.id)}">${children.name}</A></h2>
		  <p>${children.description}</p>
	  </#if>	  
  </#list> 
</#macro>

<#macro location>
    <#if location_url="">
    <#else>
  		<hr />
  		<span class="hidden">Location:</span>
  		${location_url?replace(">/","> &raquo; ")}
	</#if>
</#macro>