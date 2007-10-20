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
				</fieldset>
			</form>
			<p>The password will be sent to your email address.</p>
		</div>			
	</div>
	<script type="text/javascript">
		var username = new LiveValidation('username',{onlyOnSubmit:true});
		var password = new LiveValidation('password',{onlyOnSubmit:true});
		var email = new LiveValidation('email',{onlyOnSubmit:true});
		var captcha= new LiveValidation('j_captcha_response',{onlyOnSubmit:true});
		username.add(Validate.Presence);
		password.add(Validate.Presence);
		email.add(Validate.Presence);
		email.add(Validate.Email);
		captcha.add(Validate.Presence);
	</script>
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
					<input type="text" id="name" name="name" value="${category.name}"/><br/>
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
					<input type="text" id="name" name="name"/><br/>
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
					<input type="text" id="name" name="name"/><br/>
					<label for="description">Description</label>
					<textarea name="description" rows="8" cols="auto" id="description"></textarea>
  				<label for="keywords">Keywords</label>				
					<input type="text" id="keywords" name="keywords"/><br/>
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
					<input type="text" id="name" name="name" value="${document.name}"/><br/>
					<label for="description">Description</label>
					<textarea name="description" rows="8" cols="auto" id="description">${document.description}</textarea>
  				<label for="keywords">Keywords</label>				
					<input type="text" id="keywords" name="keywords" value="${document.metaData.keywords}"/><br/>
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