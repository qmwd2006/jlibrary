<#import "common-print.ftl" as com>
<#import "header.ftl" as head>
<#import "leftbar.ftl" as leftbar>
<#import "rightbar.ftl" as rightbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>    
	<!--body-->
    <@com.body>
	    <@body.directoryContent/>
	</@com.body>
</@com.page>
