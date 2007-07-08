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
    <@com.rightbar>
	    <@rightbar.infoDocument/>
	    <@rightbar.categoriesDocument/>
	    <@rightbar.relationsDocument/>
	    <@rightbar.notesDocument/>
    </@com.rightbar>

    <!--leftbar-->
    
    <!--body-->
    <@body.content/>

    <!--footer-->
    <@foot.footer/>
</@com.page>
