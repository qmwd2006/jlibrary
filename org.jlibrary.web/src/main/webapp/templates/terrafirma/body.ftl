<#macro content>
  <div class="post">
	  <div class="header">
		  <h3>${document.name}</h3>
			<div class="date">${document.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
		  <#if error??>
		    <p class="error-header">${error}</p>
		  </#if>
			<#if document_content="">
				Download: <a href="${repository_url}${document.path}">${document.name}</a>
			<#else>
				${document_content}
			</#if>
			<br/><br/>
		</div>			
		<div class="footer">
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
		</div>
	</div>			
</#macro>

<#macro directoryContent>
  <div class="post">
	  <div class="header">
		  <h3>Directory: ${directory.name}</h3>
			<div class="date">
			  ${directory.date?string("MMMM dd, yyyy")} 
			  <A href="${repository_url}${directory.path}?rss=true" border=0><img border="0" src="${root_url}/templates/terrafirma/images/rss.png"></A>
			</div>
		</div>
		<div class="content">
		  <#if error??>
		    <p class="error-header">${error}</p>
		  </#if>
  		<#if directory_content="">
  	  	<p>${directory.description}</p>
	  		<#list directory.nodes as children>
		  		<#if !children.directory>
		  	  		<h2><a style="text-decoration:none" 
		  	         href="${repository_url}${children.path}">${children.name}</a></h2>
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

</#macro>

<#macro listCategory>
  <div class="post">
	  <div class="header">
		  <h3>Category contents: ${category.name}</h3>			
			<div class="date">
			${category.date?string("MMMM dd, yyyy")}
			<A href="${categories_root_url}/${category.name}?rss=true" border=0><img border="0" src="${root_url}/templates/terrafirma/images/rss.png"></A>			
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