<#macro header>

    <div id="header">
        <span class="headerTitle">
	    <p><a href="${root_url}/index.html" title="${repository.name}">${repository.name}</a></p>
        </span>
        <div class="headerLinks">
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

<#macro location>
    <#if location_url="">
    <#else>
	    <div class="location">
	        <span>Location : ${location_url}</span>
	    </div>
	</#if>
</#macro>