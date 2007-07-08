<#macro infoDocument>
    <div class="sideBox RHS">
      <div>Information</div>
      
      <span><strong>Created in:</strong> ${document.date?string("yyyy-MM-dd HH:mm:ss")}</span>
      <span><strong>Created by:</strong> ${node_creator}</span>
	  <span><strong>Last updated:</strong> ${document_update_date?string("yyyy-MM-dd HH:mm:ss")}</span>
    </div>
</#macro>

<#macro infoDirectory>
	<#if directory_content="">
	    <div class="sideBox RHS">
	      <div>Information</div>
	      
	      <span><strong>Created in:</strong> ${directory.date?string("yyyy-MM-dd HH:mm:ss")}</span>
	      <span><strong>Created by:</strong> ${node_creator}</span>
	    </div>
	</#if>
</#macro>

<#macro directoryDocuments>
  <#assign hasDocuments=false/>
  <#list directory.nodes as children>
  	<#if !children.directory>
  	  <#assign hasDocuments=true/>
  	</#if>
  </#list>
          
  <#if hasDocuments>          	  
      <div class="sideBox RHS">
          <div">Documentos</div>
		  <#list directory.nodes as children>
			  <#if !children.directory>
              	  <A href="${filename(children.id)}">${children.name}</A>
			  </#if>
		  </#list> 			         
      </div>
  </#if>
</#macro>

<#macro relationsDocument>
  <div class="sideBox RHS">
      <div>You may be interested in...</div>
	  
	  <#if document.relations?size = 0>
	    <span>There isn't related documents</span>
	  <#else>
	    <#list document.relations as relation>
        	<a href="${nodeURL(relation.id)}">
	        		${relation.name}</A>
         </#list>
	  </#if> 
  </div>
</#macro>

<#macro notesDocument>
  <#if document.notes?size != 0>
      <div class="sideBox RHS">
          <div>Notes</div>
	      <#list document.notes as note>
	          <span><strong>${note.date?date}:</strong> ${note.note}</span>
          </#list>
      </div>          
  </#if>
</#macro>

<#macro favorites>      
      <div class="sideBox RHS">
          <div>Favorites</div>
          <#if category.favorites?exists>          	  
			  <#list category.favorites as children>
			  	<a href="${nodeURL(children.document)}">
				  		${node(children.document).name}</A>
			  </#list> 			         
		  <#else>
		      <span>There are no favorites.</span>
          </#if>
      </div>
</#macro>

<#macro print>
  <div class="sideBox RHS">
    <div><A href="${print_file}">Print <img src="${root_url}/resources/print.gif" border="0"/></A></div>
  </div>
</#macro>

<#macro categoriesDocument>
  <div class="sideBox RHS">
      <div>Categories</div>
	  
	  <#if document_categories?size = 0>
	    <span>Document not classified</span>
	  <#else>
	    <#list document_categories as category>
              <span><A href="${categoryURL(category.id)}">${category.name}</A></span>
         </#list>
	  </#if> 
  </div> 
</#macro>