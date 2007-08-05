<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
  <@head.header/>
  
	<!-- search results -->
 	<p>Search Results</p>
	<#list results as hit>
	  <h1><A style="text-decoration:none" 
		  	   href="${repository_url}${hit.path}">${hit.name}</A></h1>
		<p>${hit.name}</p>
	</#list>
    
  <!--footer-->
  <@foot.footer/>
</@com.page>
