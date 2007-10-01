<#macro header>
<div id="outer">

	<div id="upbg"></div>

	<div id="inner">

		<div id="header">
			<h1><A href="${repository_url}"><span>${repository.name}</span></A> <sup>beta</sup></h1>
			<h2>${repository.description}</h2>
		</div>
	
		<div id="splash"></div>
	
		<div id="menu">
      <ul>
				<#list repository.categories as category>
  				<#if !category.unknownCategory>
  					<#if !category.parent?exists>
    					<li><a href="${categories_root_url}/${category.name}">${category.name}</a></li>
    				</#if>
  				</#if>
				</#list>
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
				  <form name="login" method="post" action="${root_url}/forward?method=login&repository=${repository.name}">
				    <label>Name</label>
				    <input type="text" class="text" maxlength="16" name="username" id="username" />
				    <label for="password">Password</label>
				    <input type="password" class="text" maxlength="16" name="password" id="password" />
						<input type="hidden" name="repository" value="${repository.name}"/>
						<input class="submit" type="submit" value="log in"/>
				  </form>
				<#else>
					<p>Welcome ${username(ticket.user.id)} <A href="${root_url}/forward?method=logout&repository=${repository.name}">(log out)</A></p>
				</#if>
			</div>
    </div>
</#macro>