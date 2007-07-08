<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "leftbar.ftl" as leftbar>
<#import "rightbar.ftl" as rightbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>
    <@head.location/>
    
    <!--bars-->
    <@com.bars>
         <@leftbar.categories/>
         <@rightbar.favorites/>
         <@leftbar.print/>
    </@com.bars>

	<!--body-->
    <@com.body>
	    <@body.listCategory/>
	</@com.body>

    <!--footer-->
    <@foot.footer/>
</@com.page>
