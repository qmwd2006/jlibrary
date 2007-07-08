<#macro directories>

    <div class="sideBox LHS">
      <div>${directory.name}</div>
      <#list directory.nodes as menuitem>
	    	<li>
		    	<#if menuitem.directory>
		    		<A href="${filename(menuitem.id)}/index.html">${menuitem.name}</A>
				<#else>
		    		<A href="${filename(menuitem.id)}">${menuitem.name}</A>
				</#if>	    	
			</li>
	  </#list>      
    </div>
</#macro>

<#macro categories>
    <div class="sideBox LHS">
      <div>${category.name}</div>
      <#if category.categories?exists>
	      <#list category.categories as menuitem>
			<A href="${categoryURL(menuitem.id)}">${menuitem.name}</A>
		  </#list>     		
	  </#if>
	</div>    
</#macro>

<#macro print>
  <div class="sideBox LHS">
    <div><A href="${print_file}">Print <img src="${root_url}/resources/print.gif" border="0"/></A></div>
  </div>
</#macro>