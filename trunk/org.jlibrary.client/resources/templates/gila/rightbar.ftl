<#macro infoDocument>
  <div class="rightSideBar">
      <p class="sideBarTitle">Information</p>

      <div class="sideBarText"><strong>Created in:</strong> ${document.date?string("yyyy-MM-dd HH:mm:ss")}</div>
      <div class="sideBarText"><strong>Creado by:</strong> ${node_creator}</div>
	  <div class="sideBarText"><strong>Last updated:</strong> ${document_update_date?string("yyyy-MM-dd HH:mm:ss")}</div>
  </div>
</#macro>

<#macro infoDirectory>
	  <div class="rightSideBar">
	      <p class="sideBarTitle">Information</p>
	      
	      <div class="sideBarText"><strong>Created in:</strong> ${directory.date?string("yyyy-MM-dd HH:mm:ss")}</div>
	      <div class="sideBarText"><strong>Created by:</strong> ${node_creator}</div>
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
      <div class="rightSideBar">
          <p class="sideBarTitle">Documents</p>
		  <#list directory.nodes as children>
			  <#if !children.directory>
                  <div class="sideBarText">
                        <A href="${filename(children.id)}">${children.name}</A>
                  </div>
			  </#if>
		  </#list> 			         
      </div>
  </#if>
</#macro>

<#macro relationsDocument>
  <div class="rightSideBar">
      <p class="sideBarTitle">You may be interested in...</p>
	  
	  <#if document.relations?size = 0>
	    <div class="sideBarText">There isn't related documents</div>
	  <#else>
	    <#list document.relations as relation>
              <div class="sideBarText"><a href="${nodeURL(relation.id)}">
	        		${relation.name}</A></div>
         </#list>
	  </#if> 
  </div>
</#macro>

<#macro notesDocument>
  <#if document.notes?size != 0>
      <div class="rightSideBar">
          <p class="sideBarTitle">Notes</p>
	      <#list document.notes as note>
	          <div class="sideBarText"><strong>${note.date?date}:</strong> ${note.note}</div>
	          <br/>
          </#list>
      </div>          
  </#if>
</#macro>

<#macro favorites>
      <div class="rightSideBar">
          <p class="sideBarTitle">Favorites</p>          
          <#if category.favorites?exists>          	  
			  <#list category.favorites as children>
			  	<div class="sideBarText">
			  	<a href="${nodeURL(children.document)}">
				  		${node(children.document).name}</A>
				</div>
			  </#list> 			         
		  <#else>
		      <div class="sideBarText">There are no favorites.</div>
          </#if>
      </div>
</#macro>

<#macro categoriesDocument>
  <div class="rightSideBar">
      <p class="sideBarTitle">Categories</p>
	  
	  <#if document_categories?size = 0>
	    <div class="sideBarText">Document not classified</div>
	  <#else>
	    <#list document_categories as category>
              <div class="sideBarText">
              <A href="${categoryURL(category.id)}">${category.name}</A>
			  </div>
         </#list>
	  </#if> 
  </div> 
</#macro>