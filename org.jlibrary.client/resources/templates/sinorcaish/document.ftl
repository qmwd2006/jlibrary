<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "leftbar.ftl" as leftbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>
    
    <!--leftbar-->
    <@com.bars>
	    <@leftbar.infoDocument/>
	    <@leftbar.categoriesDocument/>
	    <@leftbar.relationsDocument/>
	    <@leftbar.notesDocument/>
	    <@leftbar.print/>
    </@com.bars>
    
    <!--body-->
    <@com.body>
	    <@body.content/>
	</@com.body>

    <!--footer-->
    <@foot.footer/>
</@com.page>
