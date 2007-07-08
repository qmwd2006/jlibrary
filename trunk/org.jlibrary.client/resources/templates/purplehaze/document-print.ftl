<#import "common-print.ftl" as com>
<#import "header.ftl" as head>
<#import "rightbar.ftl" as rightbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>   
    <!--body-->
    <@com.body>
	    <@body.contentPrint/>
	</@com.body>
</@com.page>
