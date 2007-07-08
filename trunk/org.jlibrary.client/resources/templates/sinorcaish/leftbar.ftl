<#macro directories>
	<div id="sidebar">
	  <div>
	    <p class="title">Directories:</p>
	    <ul>
	      <#list directory.nodes as menuitem>	    	
	    	<#if menuitem.directory>
	    	  <#if !isResourcesDir(menuitem.id)>
	    	    <li>
	    		  <A href="${filename(menuitem.id)}/index.html">${menuitem.name}</A>
	    		</li>
	    	  </#if>
			</#if>	    	
	  	  </#list> 
	    </ul>
	  </div>
	</div>
	<div id="sidebar">
	  <div>
	    <p class="title">Documents:</p>
	    <ul>
	      <#list directory.nodes as menuitem>
		     <#if !menuitem.directory>
		       <li><A href="${filename(menuitem.id)}">${menuitem.name}</A></li>
			 </#if>	    	
	  	  </#list> 
	    </ul>
	  </div>
	</div>	
</#macro>

<#macro print>
	<div id="sidebar">
	  <div>
	    <p class="title">
	    	<A href="${print_file}">
	    		Print <img src="${root_url}/resources/print.gif" border="0"/>
	    	</A>
	    </p>
	  </div>
	</div> 
</#macro>

<#macro categories>
	<div id="sidebar">
	  <div>
	    <p class="title">${category.name}</p>
	    <#if category.categories?exists>
		    <ul>
		      <#list category.categories as menuitem>
				<li><A href="${categoryURL(menuitem.id)}">${menuitem.name}</A></li>
		  	  </#list>
		    </ul>
		</#if>
	  </div>
	</div>   
</#macro>

<#macro infoDocument>
	<div id="sidebar">
	  <div>
	    <p class="title">Information</p>
	    <ul>
	      <li><strong>Created in:</strong> ${document.date?string("yyyy-MM-dd HH:mm:ss")}</li>
      	  <li><strong>Created by:</strong> ${node_creator}</li>
	  	  <li><strong>Last updated:</strong> ${document_update_date?string("yyyy-MM-dd HH:mm:ss")}</li>
	    </ul>
	  </div>
	</div> 
</#macro>

<#macro infoDirectory>
	<div id="sidebar">
	  <div>
	    <p class="title">Information</p>
	    <ul>
	      <li><strong>Created in:</strong> ${directory.date?string("yyyy-MM-dd HH:mm:ss")}</li>
      	  <li><strong>Created by:</strong> ${node_creator}</li>
	    </ul>
	  </div>
	</div> 
</#macro>

<#macro directoryDocuments>
  <#assign hasDocuments=false/>
  <#list directory.nodes as children>
  	<#if !children.directory>
  	  <#assign hasDocuments=true/>
  	</#if>
  </#list>
          
  <#if hasDocuments>
	<div id="sidebar">
	  <div>
	    <p class="title">Documents</p>
	    <ul>
		  <#list directory.nodes as children>
			  <#if !children.directory>
              	  <li><A href="${filename(children.id)}">${children.name}</A></li>
			  </#if>
		  </#list>	    
		</ul>
	  </div>
	</div>            	  
  </#if>
</#macro>

<#macro relationsDocument>
	<div id="sidebar">
	  <div>
	    <p class="title">Relations</p>
	    <ul>
		  <#if document.relations?size = 0>
		    <li>There are no related documents</li>
		  <#else>
		    <#list document.relations as relation>
	        	<li><a href="${nodeURL(relation.id)}">
	        		${relation.name}</A></li>
	         </#list>
		  </#if> 	    
		</ul>
	  </div>
	</div>
</#macro>

<#macro notesDocument>
  <#if document.notes?size != 0>
  	<div id="sidebar">
	  <div>
	    <p class="title">Notes</p>
	    <ul>
	      <#list document.notes as note>
	          <li><strong>${note.date?date}:</strong> ${note.note}</li>
          </#list>	    
		</ul>
	  </div>
	</div>          
  </#if>
</#macro>

<#macro favorites>
	  	<div id="sidebar">
		  <div>
		    <p class="title">Favorites</p>
		    <ul>
	          <#if category.favorites?exists>          	  
				  <#list category.favorites as children>
				  	<li><a href="${nodeURL(children.document)}">
				  		${node(children.document).name}</A>
				  	</li>
				  </#list> 			         
			  <#else>
			      <li>There is no favorites</li>
	          </#if>    
			</ul>
		  </div>
		</div>      
</#macro>

<#macro categoriesDocument>
  	<div id="sidebar">
	  <div>
	    <p class="title">Categories</p>
	    <ul>
		  <#if document_categories?size = 0>
		    <li>Document not classified</li>
		  <#else>
		    <#list document_categories as category>
	              <li><A href="${categoryURL(category.id)}">${category.name}</A></li>
	         </#list>
		  </#if>   
		</ul>
	  </div>
	</div>
</#macro>