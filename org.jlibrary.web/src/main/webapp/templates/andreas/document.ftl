<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "rightbar.ftl" as rightbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>
    
    <!--leftbar-->
    <@com.rightbar>
    	<div id="rightside">
	      <@rightbar.infoDocument/>
	      <@rightbar.resources/>
	      <@rightbar.categoriesDocument/>
	      <@rightbar.relationsDocument/>
	      <@rightbar.search/>
    	</div>
    </@com.rightbar>
        
    <!--body-->
	<div id="contentalt">
    	<@body.content/>
    	<@body.notesDocument/>
	</div>
    <!--footer-->
    <@foot.footer/>
</@com.page>
