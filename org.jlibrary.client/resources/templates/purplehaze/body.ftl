<#macro content>
    <div id="bodyText" style="margin: 10mm 13em 0 0">
		<#if document_content="">
			Download: <A href="${filename(document.id)}">${document.name}</A>
		<#else>
			${document_content}
		</#if>
    </div>
</#macro>

<#macro contentPrint>
    <div id="bodyText">
		<#if document_content="">
			Download: <A href="${filename(document.id)}">${document.name}</A>
		<#else>
			${document_content}
		</#if>
    </div>
</#macro>

<#macro directoryContent>
      <#if directory_content="">
          <div id="bodyText">
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
	    <div id="bodyText" style="margin: 10mm 0 0 13em">
	        ${directory_content}
	    </div>
	  </#if>
    </div>
</#macro>

<#macro listCategory>
    <div id="bodyText">
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