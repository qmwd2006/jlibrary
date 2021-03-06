<#macro content>
  <div class="post">
	  <div class="header">
		  <h3>${document.name}</h3>
			<div class="date">${document.date?string("MMMM dd, yyyy")}
			  <a style="text-decoration:none" href="${repository_url}${document.path?replace("%", "%25")}?download=true"><img border="0" src="${root_url}/templates/terrafirma/images/download.png" alt="Download file"/></a>
			</div>
		</div>
		<div class="content">
		  <#if error??>
		    <p class="error-header">${error}</p>
		  </#if>
			<#if document_content!="">
				<br/>
				${document_content}
			<#else>
			  <#if document.image>
				  <img src="${repository_url}${document.path?replace("%", "%25")}?download=true"/>
			  <#else>
			    <p>This demo does not know how to display this type of document. However you can download it using the download icon just right next to the date.</p>
			  </#if>
			</#if>
			<br/><br/>
		</div>			
		<div class="footer">
		<#if context.loginEnabled>
			<h3>Comments</h3>
			<#if document.notes?size != 0>
      <#list document.notes as note>
          <p><strong>${note.date?datetime} ${username(document.repository,note.creator)} says:</strong><p>
          <p>${note.note}</p>
      </#list>
  		</#if>
  		<div id="comments">
  			<form id="comment" method="post" action="${root_url}/forward?method=comment&amp;repository=${repository.name}">				
				  <textarea name="text"></textarea>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="document" value="${document.id}"/>
					<input class="submit" type="submit" value="Comment!"/>				
  			</form>
			</div>
		</#if>
		</div>
	</div>
</#macro>

<#macro directoryContent>
  <div class="post">
	  <div class="header">
	    <#if directory_content="">
		  	<h3>Directory: ${directory.name}</h3>
		  </#if>
			<div class="date">
			  ${directory.date?string("MMMM dd, yyyy")} 
			  <a href="${repository_url}${directory.path?replace("%", "%25")}?rss=true" border=0><img border="0" src="${root_url}/templates/terrafirma/images/rss.png" alt="Suscribe in a reader" /></a>
			</div>
		</div>
		<div class="content">
		  <#if error??>
		    <p class="error-header">${error}</p>
		  </#if>
  		<#if directory_content="">
  	  	<p>${directory.description}</p>
	  		<#list directory.nodes as children>
		  		<#if children.document>
		  	  		<h2><a style="text-decoration:none" 
		  	         href="${repository_url}${children.path?replace("%", "%25")}">${children.name}</a>&nbsp;<a href="${repository_url}${children.path?replace("%", "%25")}?download=true"><img border="0" src="${root_url}/templates/terrafirma/images/download.png" alt="Download file"/></a></h2>
			  			<p>${children.description}</p>
		  		</#if>
	  		</#list>
  		<#else>
	 			${directory_content}
  		</#if>
		</div>
	</div>
</#macro>

<#macro notesDocument>
<#if context.loginEnabled>
  <#if document.notes?size != 0>
  	  <div id ="comments"></div>
      <h2>Comments</h2>
      <#list document.notes as note>
          <p><strong>${note.date?date} ${username(document.repository,note.creator)} says:</strong> ${note.note}</p>
          <br/>
      </#list>
  </#if>
  <br/>
  <a href="${root_url}/forward?method=comment&amp;repository=${repository.name}&amp;id=${document.id}">Add new comment</a>
</#if>
</#macro>

<#macro listCategory>
  <div class="post">
	  <div class="header">
		  <h3>Category contents: ${category.name}</h3>			
			<div class="date">
			${category.date?string("MMMM dd, yyyy")}
			<a href="${categories_root_url}/${category.name}?rss=true" border=0><img border="0" src="${root_url}/templates/terrafirma/images/rss.png" alt="Suscribe in a reader"/></a>			
			</div>
		</div>
		<div class="content">
		  <#if error??>
		    <p class="error-header">${error}</p>
		  </#if>		
	  	<p>${category.description}</p>
		  <#list category_documents as children>
			  <#if !children.directory>
			  	  <h2><a style="text-decoration:none" 
		  	         href="${nodeURL(children.id)}">${children.name}</a></h2>
				  <p>${children.description}</p>
			  </#if>	  
		  </#list> 
		</div>			
	</div>
</#macro>

<#macro location>
  <#if location_url="">
  <#else>
    <div class="location">
			<span>Location: </span>${location_url?replace(">/","> &raquo; ")}
		</div>
	</#if>
</#macro>