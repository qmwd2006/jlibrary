<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
  <@head.header/>
  
	<!-- search results -->
	<div id="primarycontent">
	  <div class="post">
	  	<div class="header">
		  	<h3>Log in: </h3>
			</div>
			<div class="content">
				<form action="#">			
				  <p>
					<label>Name</label>
					<input name="username" value="admin_name" type="text" size="30" />
					<label>Password</label>
					<input name="password" value="changeme" type="text" size="30" />
					<input type="hidden" value="${repository.name}"/>
					<br />	
					<input class="button" type="submit" />		
					</p>		
				</form>				
		  </div>
		</div>
  </div>
    
  <!--footer-->
  <@foot.footer/>
</@com.page>
