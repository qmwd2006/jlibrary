<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "leftbar.ftl" as leftbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>
    
    <!--bars-->
    <@com.bars>
        <@leftbar.directories/>
        <@leftbar.infoDirectory/>
        <@leftbar.print/>
    </@com.bars>

	<!--body-->
    <@com.body>
	    <@body.directoryContent/>
	</@com.body>	
    
    <!--footer-->
    <@foot.footer/>
</@com.page>
