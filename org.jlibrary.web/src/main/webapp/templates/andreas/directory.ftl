<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "leftbar.ftl" as leftbar>
<#import "rightbar.ftl" as rightbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>
    
    <!--leftbar-->
    <@com.leftbar>
      <div id="leftside">
        <@leftbar.directories/>        
    	<#if !(directory_content="")>
    		<@leftbar.documents/>
    		<@leftbar.search/>
    	</#if>              
	    </div>
    </@com.leftbar>
    
    <!--rightbar-->
    <@com.rightbar>
    	<#if directory_content="">
	    	<div id="rightside">
		      <@rightbar.infoDirectory/>
		      <@rightbar.directoryDocuments/>
		      <@rightbar.search/>
		    </div>
		</#if>
	</@com.rightbar>
	
    <!--body-->
    <#if directory_content="">
    	<div id="content">
    <#else>
    	<div id="content-extended">
    </#if>
      <@body.directoryContent/>
    </div>
    
    <!--footer-->
    <@foot.footer/>
</@com.page>
