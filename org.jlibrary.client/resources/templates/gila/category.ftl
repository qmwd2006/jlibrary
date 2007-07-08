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
    
    <!--leftbar-->
    <@com.leftbar>
         <@leftbar.categories/>
    </@com.leftbar>

    <!--rightbar-->
    <@com.rightbar>
        <@rightbar.favorites/>
    </@com.rightbar>

    <!--body-->
    <@body.listCategory/>

    <!--footer-->
    <@foot.footer/>
</@com.page>
