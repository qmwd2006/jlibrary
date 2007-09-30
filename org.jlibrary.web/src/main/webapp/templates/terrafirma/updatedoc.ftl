<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "rightbar.ftl" as rightbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>
            
    <!--body-->
    <div id="primarycontent">      
    	<@body.content/>
    </div>
    
    <!-- right bar -->
    <@com.rightbar>
			<div id="secondarycontent">
	      <@rightbar.infoDocument/>
	      <@rightbar.admin/>
	      <@rightbar.resources/>
	      <@rightbar.categoriesDocument/>
	      <@rightbar.relationsDocument/>
    	</div>
    </@com.rightbar>
    
    <!--footer-->
    <@foot.footer/>
</@com.page>
