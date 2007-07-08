<#macro content>
    <div id="main-copy" style="margin: 0 0 0 0">
		<#if document_content="">
			Download: <A href="${filename(document.id)}">${document.name}</A>
		<#else>
			${document_content}
		</#if>
    </div>
</#macro>

<#macro directoryContent>
      <#if directory_content="">
          <div id="main-copy" >
          	  <p>${directory.description}</p>
			  <#list directory.nodes as children>
				  <#if !children.directory>
				  	  <h1><A style="text-decoration:none" 
				  	         href="${filename(children.id)}"</A></h1>
					  <p>${children.description}</p>
				  </#if>
			  </#list>
		  </div>
	  <#else>
	    <div id="main-copy" style="margin: 0 0 0 12.5em;padding: 0.5ex 2em 1em 1em;">
	        ${directory_content}
	    </div>
	  </#if>
    </div>
</#macro>

<#macro listCategory>
    <div id="main-copy">
	  <p>${category.description}</p>
	  <p/>
	  <#list category_documents as children>
		  <#if !children.directory>
		  	  <h1><A style="text-decoration:none" 
	  	         href="${nodeURL(children.id)}">${children.name}</A></h1>
			  <p>${children.description}</p>
		  </#if>	  
	  </#list> 
    </div>
</#macro>