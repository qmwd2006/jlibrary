<#macro directories>
	<div class="leftSideBar">
		<p class="sideBarTitle">
			<A href="${directory.name}/index.html">${directory.name}</A>
		</p>
		<ul>
			<#list directory.nodes as menuitem>
		    	<li>
		    	<#if menuitem.directory>
		    		<A href="${filename(menuitem.id)}/index.html">${menuitem.name}</A>
				<#else>
		    		<A href="${filename(menuitem.id)}">${menuitem.name}</A>
				</#if>	    	
			</li>
			</#list>
		</ul>
	</div>
</#macro>

<#macro categories>
	  <div class="leftSideBar">
		<p class="sideBarTitle">
		    <A href="${category.name}/index.html">${category.name}</A>
		</p>	  
	    <ul>
	    <#if category.categories?exists>
			<ul>
			<#list category.categories as menuitem>
				<li><A href="${categoryURL(menuitem.id)}">${menuitem.name}</A></li>
			</#list>
			</ul>			
		</#if>
	</div>    
</#macro>