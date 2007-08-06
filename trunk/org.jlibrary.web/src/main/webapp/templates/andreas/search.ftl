<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
  <@head.header/>
  
	<!-- search results -->
 	<h1>Search Results</h1>
	<#list results as hit>
	  <h2><A style="text-decoration:none" 
		  	   href="${repository_url}${hit.path}">${hit.name}</A></h2>
		<p>${hit.excerpt}</p>
	</#list>
    
  <!--footer-->
  <@foot.footer/>
</@com.page>
