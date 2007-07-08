<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "rightbar.ftl" as rightbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>
    <@head.location/>
    
    <!--rightbar-->
    <@com.bars>
	    <@rightbar.infoDocument/>
	    <@rightbar.categoriesDocument/>
	    <@rightbar.relationsDocument/>
	    <@rightbar.notesDocument/>
	    <@rightbar.print/>
    </@com.bars>

    <!--leftbar-->
    
    <!--body-->
    <@com.body>
	    <@body.content/>
	</@com.body>

    <!--footer-->
    <@foot.footer/>
</@com.page>
