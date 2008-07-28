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
				<#list results.items as hit>
		  		<h2><a style="text-decoration:none" 
			  	   href="${repository_url}${hit.path?replace("%", "%25")}">${hit.name}</a> ( ${hit.score*100}% )</h2> 
					<p>${hit.excerpt}</p>
				</#list>
		  </div>
				<div class="footer">
					<!--results.init=${results.init},results.end=${results.end},results.size=${results.size}-->
				  <#if results.init != -1 && results.end != -1>
						<#assign interval = results.end - results.init>
						<#assign totalPages = results.size / (interval + 1)>
						<#if 0 < results.init>
							<a href="${root_url}/search?repository=${repository.name}&amp;text=cambio&amp;init=${results.init-interval-1}&end=${results.init-1}">Previous</a> -
						</#if>
						<#if results.init = 0>
						  Page 1 of ${totalPages}
						<#else>
						  Page ${interval / results.init + 1} of ${totalPages}
						</#if>
						<#if results.end < results.size >
						  - <a href="${root_url}/search?repository=${repository.name}&amp;text=cambio&amp;init=${results.end+1}&amp;end=${results.end+interval+1}">Next</a>
						</#if>
					</#if>
				</div>		  
		</div>
  </div>
    
  <!--footer-->
  <@foot.footer/>
</@com.page>
