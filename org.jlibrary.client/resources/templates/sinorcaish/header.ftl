<#macro header>

	<div id="header">
	  <div class="left">
	    <p><a href="${root_url}/index.html" 
	    	  title="${repository.name}">${repository.name}</a></p>
	  </div>
	  
	  <div class="subheader">
	    | 
		<#list repository.categories as category>
		  <#if !category.unknownCategory>
		  	<#if !category.parent?exists>
		    	<A href="${categoryURL(category.id)}">${category.name}</A> | 
		    </#if>
	      </#if>
		</#list>
      </div>
    </div>
</#macro>