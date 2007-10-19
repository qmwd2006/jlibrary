<#macro header>
<div id="outer">

	<div id="upbg"></div>

	<div id="inner">

		<div id="header">
			<h1><a href="${repository_url}"><span>${repository.name}</span></a> <sup>beta</sup></h1>
			<h2>${repository.description}</h2>
		</div>
	
		<div id="splash"></div>
	
		<div id="menu">
      		<ul>
				<#assign hasCategories=false/>
				<#list repository.categories as category>
  				<#if !category.unknownCategory>
  					<#if !category.parent?exists>
  						<#assign hasCategories=true/>
    					<li><a href="${categories_root_url}/${category.name}">${category.name}</a></li>
    				</#if>
  				</#if>
				</#list>
				<#if !hasCategories> 
					<li>&nbsp;</li>
				</#if>
			</ul>
		  
		  <form method="post" action="${root_url}/search">
				<div id="search">
					<input type="text" class="text" maxlength="64" name="text" />
					<input type="submit" class="submit" value="Search" />
					<input type="hidden" name="repository" value="${repository.name}"/>
					<input type="hidden" name="init" value="0"/>
					<input type="hidden" name="end" value="10"/>
				</div>
			</form>

			<div id="location">
				<p>Location: ${location_url?replace(">/","> &raquo; ")}</p>
			</div>
			
			<div id="userinfo">				
				<#if ticket.user.name="guest">
				  <form id="login" method="post" action="${root_url}/forward?method=login&amp;repository=${repository.name}">
				  <fieldset class="loginheader">
				  	<legend class="loginheader">Login</legend>
				    <label for="username">Name</label>
				    <input type="text" class="text" maxlength="16" name="username" id="username" />
				    <label for="password">Password</label>
				    <input type="password" class="text" maxlength="16" name="password" id="password" />
						<input type="hidden" name="repository" value="${repository.name}"/>
						<input class="submit" type="submit" value="log in"/>
						<a href="${root_url}/forward?method=signin&amp;repository=${repository.name}">sign in</a>
					</fieldset>
				  </form>
				<#else>
					<p>Welcome ${username(ticket.repositoryId,ticket.user.id)} <a href="${root_url}/forward?method=logout&amp;repository=${repository.name}">(log out)</a></p>
				</#if>
			</div>
    </div>
</#macro>