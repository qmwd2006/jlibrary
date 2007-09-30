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
					<input type="text" name="name" value="${directory.name}"/><br/>
					<label for="description">Description</label>
					<textarea name="description" rows="8" cols="auto" id="description">${directory.description}</textarea>						
					<button type="submit">Update</button>
					<button type="reset">Cancel</button>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="update"/>					
					<input type="hidden" name="id" value="${directory.id}"/>
				</fieldset>
			</form>
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
					<input type="text" name="name""/><br/>
					<label for="description">Description</label>
					<textarea name="description" rows="8" cols="auto" id="description"></textarea>						
					<button type="submit">Create</button>
					<button type="reset">Cancel</button>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="create"/>					
					<input type="hidden" name="id" value="${directory.id}"/>
					<input type="hidden" name="type" value="directory"/>
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
					<input type="text" name="name" value="${category.name}"/><br/>
					<label for="description">Description</label>
					<textarea name="description" rows="8" cols="auto" id="description">${category.description}</textarea>						
					<button type="submit">Update</button>
					<button type="reset">Cancel</button>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="update"/>					
					<input type="hidden" name="id" value="${category.id}"/>
					<input type="hidden" name="type" value="category"/>
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
					<input type="text" name="name""/><br/>
					<label for="description">Description</label>
					<textarea name="description" rows="8" cols="auto" id="description"></textarea>						
					<button type="submit">Create</button>
					<button type="reset">Cancel</button>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="create"/>					
					<input type="hidden" name="type" value="category"/>
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
					<input type="text" name="name"/><br/>
					<label for="description">Description</label>
					<textarea name="description" rows="8" cols="auto" id="description"></textarea>
  				<label for="keywords">Keywords</label>				
					<input type="text" name="keywords"/><br/>
					<label for="content">Content</label>
					<script type="text/javascript">
						var oFCKeditor = new FCKeditor( 'FCKEditor' ) ;
						oFCKeditor.BasePath	= '${root_url}/FCKEditor/';
						oFCKeditor.ToolbarSet = 'Basic';
						oFCKeditor.Value	= '' ;
						oFCKeditor.Create() ;
					</script>					
					<button type="submit">Create</button>
					<button type="reset">Cancel</button>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="create"/>					
					<input type="hidden" name="id" value="${directory.id}"/>
					<input type="hidden" name="type" value="document"/>
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
					<input type="text" name="name" value="${document.name}"/><br/>
					<label for="description">Description</label>
					<textarea name="description" rows="8" cols="auto" id="description">${document.description}</textarea>
  				<label for="keywords">Keywords</label>				
					<input type="text" name="keywords" value="${document.metaData.keywords}"/><br/>
					<label for="content">Content</label>
					<script type="text/javascript">
						var oFCKeditor = new FCKeditor( 'FCKEditor' ) ;
						oFCKeditor.BasePath	= '${root_url}/FCKEditor/';
						oFCKeditor.ToolbarSet = 'Basic';
						oFCKeditor.Value	= '${document_content}' ;
						oFCKeditor.Create() ;
					</script>
					<button type="submit">Update</button>
					<button type="reset">Cancel</button>
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="method" value="update"/>					
					<input type="hidden" name="id" value="${document.id}"/>
					<input type="hidden" name="type" value="node"/>
				</fieldset>
			</form>
		</div>			
	</div>			
</#macro>