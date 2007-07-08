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
        <@leftbar.directories/>
    </@com.leftbar>
    
    <!--rightbar-->
    <@com.directoryRightbar>
	    <@rightbar.infoDirectory/>
	</@com.directoryRightbar>
	
    <!--body-->
    <@body.directoryContent/>
    
    <!--footer-->
    <@foot.footer/>
</@com.page>
