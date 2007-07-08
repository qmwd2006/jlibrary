<#macro content>
    <div id="main">
		<#if document_content="">
			Download: <A href="${filename(document.id)}">${document.name}</A>
		<#else>
			${document_content}
		</#if>
    </div>
</#macro>

<#macro contentPrint>
    <div id="main">
		<#if document_content="">
			Download: <A href="${filename(document.id)}">${document.name}</A>
		<#else>
			${document_content}
		</#if>
    </div>
</#macro>

<#macro directoryContent>
      <#if directory_content="">
          <div id="main">
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
	    <div id="main">
	        ${directory_content}
	    </div>
	  </#if>
    </div>
</#macro>

<#macro listCategory>
    <div id="main">
	  <p>${category.description}</p>
	  <p/>
	  <#list category_documents as children>
	  	  <h1><A style="text-decoration:none" 
			  	 href="${nodeURL(children.id)}">${children.name}</A></h1>
		  <p>${children.description}</p>
	  </#list> 
    </div>
</#macro>