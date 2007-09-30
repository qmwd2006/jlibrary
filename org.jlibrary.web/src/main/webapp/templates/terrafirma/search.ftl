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
		  	<h3>Search results: </h3>
			</div>
			<div class="content">
				<#list results as hit>
		  		<h2><A style="text-decoration:none" 
			  	   href="${repository_url}${hit.path}">${hit.name}</A> ( ${hit.score*100}% )</h2> 
					<p>${hit.excerpt}</p>
				</#list>
		  </div>
		</div>
  </div>
    
  <!--footer-->
  <@foot.footer/>
</@com.page>
