<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>
<#import "leftbar.ftl" as leftbar>
<#import "rightbar.ftl" as rightbar>

<@com.page>

	<!--header-->
  <@head.header/>
  
    <!--leftbar-->
    <@com.leftbar>
      <div id="leftside">           
	    </div>
    </@com.leftbar>
    
    <!--rightbar-->
    <@com.rightbar>
    	<div id="rightside">
    		<@rightbar.search/>
	    </div>
	  </@com.rightbar>
  
	<!-- search results -->
	<div id="content">
	 	<h1>Search Results</h1>
		<#list results as hit>
		  <h2><A style="text-decoration:none" 
			  	   href="${repository_url}${hit.path}">${hit.name}</A></h2><div id="score">${hit.score*100}%</div>
			<p>${hit.excerpt}</p>
		</#list>
  </div>
    
  <!--footer-->
  <@foot.footer/>
</@com.page>
