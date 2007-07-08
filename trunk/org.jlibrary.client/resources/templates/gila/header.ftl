<#macro header>

    <div id="header">
      <h1 class="headerTitle">
		<a href="${root_url}/index.html" title="${repository.name}">${repository.name}</a>
      </h1>

      <div class="subHeader">
        | 
	  	<span class="doNotDisplay">Navigation: </span>
		<#list repository.categories as category>
		  <#if !category.unknownCategory>
		  	<#if !category.parent?exists>
		    	<A href="${categoryURL(category.id)}">${category.name}</A>
		    </#if>
	      </#if>
		</#list>
      </div>
    </div>
</#macro>

<#macro location>
    <#if location_url="">
    <#else>
	    <div class="location">
	        <span>Location : ${location_url}</span>
	    </div>
	</#if>
</#macro>