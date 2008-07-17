<#macro directoryUpdateForm>
  <div class="post">
	  <div class="header">
		  <h3>Directory: ${directory.name}</h3>
			<div class="date">${directory.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward">
				<fieldset>
					<legend>Directory details</legend>						
  				<label for="name">Name</label>				
					<input id="name" type="text" name="name" value="${directory.name}"/><br/>
					<label for="descriptionText">Description</label>
					<textarea id="descriptionText" name="description" rows="8" cols="auto">${directory.description}</textarea>						
					<button type="submit">Update</button>
					<a href="${repository_url}${directory.path}" class="button">Cancel</a>
					<input type="hidden" name="type" value="node"/>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="update"/>					
					<input type="hidden" name="id" value="${directory.id}"/>
					<input type="hidden" id="validation" name="validation" value="directory"/>
				</fieldset>
			</form>
		</div>			
	</div>			
</#macro>

<#macro register>
  <div class="post">
	  <div class="header">
		  <h3>Registration form</h3>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward">
				<fieldset>
					<legend>Enter your details</legend>						
  				<label for="username">Username</label>				
					<input type="text" name="username" id="username"/><br/>
					<label for="password">Password</label>
					<input type="password" name="password" id="password"/><br/>							
  				<label for="email">Email address</label>				
					<input type="text" name="email" id="email"/><br/>
					<label for="name">First name</label>
					<input type="text" name="name" id="name"/><br/>
					<label for="surname">Surname</label>
					<input type="text" name="surname" id="surname"/><br/>			
					
					<label for="j_captcha_response">Enter the text below</label>
					<input type="text" name="j_captcha_response" value="" id="j_captcha_response">
					<br/>
					<img src="${root_url}/jcaptcha">
					<button type="submit">Send</button>
					<button type="reset">Cancel</button>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="register"/>
					<input type="hidden" name="validation" id="validation" value="register"/>
				</fieldset>
			</form>
			<p>The password will be sent to your email address.</p>
		</div>			
	</div>
</#macro>

<#macro directoryCreateForm>
  <div class="post">
	  <div class="header">
		  <h3>Parent directory: ${directory.name}</h3>
			<div class="date">${directory.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward">
				<fieldset>
					<legend>Directory details</legend>						
  				<label for="name">Name</label>				
					<input type="text" id="name" name="name"/><br/>
					<label for="descriptionText">Description</label>
					<textarea name="description" rows="8" cols="auto" id="descriptionText"></textarea>						
					<button type="submit">Create</button>
					<a href="${repository_url}${directory.path}" class="button">Cancel</a>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="create"/>					
					<input type="hidden" name="id" value="${directory.id}"/>
					<input type="hidden" name="type" value="directory"/>
					<input type="hidden" name="validation" id="validation" value="directory"/>
				</fieldset>
			</form>
		</div>			
	</div>			
</#macro>

<#macro categoryUpdateForm>
  <div class="post">
	  <div class="header">
		  <h3>Category: ${category.name}</h3>
			<div class="date">${category.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward">
				<fieldset>
					<legend>Category details</legend>						
  				<label for="name">Name</label>				
					<input type="text" id="name" name="name" value="${category.name}"/><br/>
					<label for="descriptionText">Description</label>
					<textarea name="description" rows="8" cols="auto" id="descriptionText">${category.description}</textarea>						
					<button type="submit">Update</button>
					<a href="${repository_url}" class="button">Cancel</a>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="update"/>					
					<input type="hidden" name="id" value="${category.id}"/>
					<input type="hidden" name="type" value="category"/>
					<input type="hidden" name="validation" id="validation" value="category"/>
				</fieldset>
			</form>
		</div>			
	</div>			
</#macro>

<#macro categoryCreateForm>
  <div class="post">
	  <div class="header">
		  <h3>Create new category</h3>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward">
				<fieldset>
					<legend>Category details</legend>						
  				<label for="name">Name</label>				
					<input type="text" id="name" name="name"/><br/>
					<label for="descriptionText">Description</label>
					<textarea name="description" rows="8" cols="auto" id="descriptionText"></textarea>						
					<button type="submit">Create</button>
					<a href="${repository_url}" class="button">Cancel</a>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="create"/>					
					<input type="hidden" name="type" value="category"/>
					<input type="hidden" name="validation" id="validation" value="category"/>
				</fieldset>
			</form>
		</div>			
	</div>			
</#macro>

<#macro documentCreateForm>
  <div class="post">
	  <div class="header">
		  <h3>Parent directory: ${directory.name}</h3>
			<div class="date">${directory.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward">
				<fieldset>
					<legend>Document details</legend>						
  				<label for="name">Name</label>				
					<input type="text" id="name" name="name"/><br/>
					<label for="descriptionText">Description</label>
					<textarea name="description" rows="8" cols="auto" id="descriptionText"></textarea>
  				<label for="keywords">Keywords</label>				
					<input type="text" id="keywords" name="keywords"/><br/>
					<label for="content">Content</label>
					<textarea id="content" name="content"></textarea>
					<button type="submit">Create</button>
					<a href="${repository_url}${directory.path}" class="button">Cancel</a>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="create"/>					
					<input type="hidden" name="id" value="${directory.id}"/>
					<input type="hidden" name="type" value="document"/>
					<input type="hidden" name="baseUrl" id="baseUrl" value="${root_url}"/>
					<input type="hidden" name="validation" id="validation" value="document"/>
				</fieldset>
			</form>
			<script type="text/javascript" src="${root_url}/js/jlibrary_editor.js"></script>
		</div>			
	</div>			
</#macro>

<#macro documentCreateFormUpload>
  <div class="post">
	  <div class="header">
		  <h3>Parent directory: ${directory.name}</h3>
			<div class="date">${directory.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward?method=upload&amp;id=${directory.id}&amp;repository=${repository.name}" method="post" enctype="multipart/form-data">
				<fieldset>
					<legend>Document details</legend>						
  				<label for="name">Name</label>				
					<input type="text" id="name" name="name"/><br/>
					<label for="descriptionText">Description</label>
					<textarea name="description" rows="8" cols="auto" id="descriptionText"></textarea>
  				<label for="keywords">Keywords</label>				
					<input type="text" id="keywords" name="keywords"/><br/>
					<label for="file">File</label>
					<input type="file" name="file" id="file"/>				
					<button type="submit">Upload</button>
					<a href="${repository_url}${directory.path}" class="button">Cancel</a>
					<input type="hidden" name="validation" id="validation" value="documentUpload"/>
				</fieldset>
			</form>
		</div>			
	</div>			
</#macro>

<#macro documentUpdateForm>
  <div class="post">
	  <div class="header">
		  <h3>Document: ${document.name}</h3>
			<div class="date">${document.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward">
				<fieldset>
					<legend>Document details</legend>						
  				<label for="name">Name</label>				
					<input type="text" id="name" name="name" value="${document.name}"/><br/>
					<label for="descriptionText">Description</label>
					<textarea name="description" rows="8" cols="auto" id="descriptionText">${document.description}</textarea>
  				<label for="keywords">Keywords</label>				
					<input type="text" id="keywords" name="keywords" value="${document.metaData.keywords}"/><br/>
					<label for="content">Content</label>
					<textarea id="content" name="content">${document_content}</textarea>
					<button type="submit">Update</button>
					<a href="${repository_url}${document.path}" class="button">Cancel</a>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="update"/>					
					<input type="hidden" name="id" value="${document.id}"/>
					<input type="hidden" name="type" value="node"/>
					<input type="hidden" name="baseUrl" id="baseUrl" value="${root_url}"/>
					<input type="hidden" name="validation" id="validation" value="document"/>
				</fieldset>
			</form>
			<script type="text/javascript" src="${root_url}/js/jlibrary_editor.js"></script>
			</script>
		</div>			
	</div>
</#macro>

<#macro documentUpdateFormUpload>
  <div class="post">
	  <div class="header">
		  <h3>Document: ${document.name}</h3>
			<div class="date">${document.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward?repository=${repository.name}" method="post">
				<fieldset>
					<legend>Document details</legend>						
  				<label for="name">Name</label>				
					<input type="text" id="name" name="name" value="${document.name}"/><br/>
					<label for="descriptionText">Description</label>
					<textarea name="description" rows="8" cols="auto" id="descriptionText">${document.description}</textarea>
  				<label for="keywords">Keywords</label>				
					<input type="text" id="keywords" name="keywords" value="${document.metaData.keywords}"/><br/>
					<button type="submit">Update</button>
					<a href="${repository_url}${document.path}" class="button">Cancel</a>
					<input type="hidden" name="method" value="update"/>					
					<input type="hidden" name="id" value="${document.id}"/>
					<input type="hidden" name="type" value="node"/>
					<input type="hidden" name="baseUrl" id="baseUrl" value="${root_url}"/>
					<input type="hidden" name="validation" id="validation" value="documentUpload"/>
				</fieldset>
			</form>
		</div>			
	</div>			
</#macro>

<#macro documentCategories>
<div class="post">
	  <div class="header">
		  <h3>Categories for: ${document.name}</h3>
			<div class="date">${document.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet" action="${root_url}/forward">
				<fieldset>
					<legend>Categories for: ${document.name}</legend>
					<#assign hasCategories=false/>
					<#list repository.categories as category>
	  				<#if !category.unknownCategory>
	  					<#if !category.parent?exists>
	  						<#assign hasCategories=true/>
	    					<br/>
	    					<label for="cat${category.id}">${category.name}</label>
	    					<input type="checkbox" id="cat${category.id}" name="categories" value="${category.id}" <#if document_categories.contains(category)>checked="checked"</#if>/>
	    				</#if>
	  				</#if>
					</#list>
					<#if !hasCategories> 
						Categories do not exist
					</#if>
					<button type="submit">Next</button>
					<input type="hidden" name="method" id="method" value="updatedocumentcategories"/>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="id" value="${document.id}"/>
				</fieldset>
			</form>
		</div>			
	</div>
</#macro>

<#macro documentDocuments>
<script type="text/javascript" src="${root_url}/js/XHConn.js"></script>
<div class="post">
	  <div class="header">
		  <h3>Relations for: ${document.name}</h3>
			<div class="date">${document.date?string("MMMM dd, yyyy")}</div>
		</div>
		<div class="content">
  		<form class="sheet1" action="${root_url}/forward">
				<fieldset>
					<legend>Relations for: ${document.name}</legend>
					<div class="lst">
					<ul>
					<#list node_collection as relation>
					
					<#if relation.document>
					<li> 
	    				<input type="checkbox" id="doc${relation.id}" name="relations" value="${relation.id}" <#if document.relations.contains(relation)>checked="checked"</#if>/><label for="doc${relation.id}">${relation.name}</label>
	    			</li>
	    			</#if>
	    			<#if relation.directory>
	    			<li id="content${relation.id}"> 
		    			<a id="${relation.id}" onclick="loadDocuments(this);return false;" href="${root_url}/forward?method=documentdocuments&amp;repository=${repository.name}&amp;id=${document.id}&amp;parentId=${relation.id}">
		    				${relation.name}
		    			</a>
		    		</li>
	    			</#if>
	    			
	    			</#list>
	    			</ul>
	    			</div>
					<button type="submit">Next</button>
					<input type="hidden" name="method" id="method" value="updatedocumentrelations"/>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="id" value="${document.id}"/>
				</fieldset>
			</form>
		</div>			
	</div>
</#macro>