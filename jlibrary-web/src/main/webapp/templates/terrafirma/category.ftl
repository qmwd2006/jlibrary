<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "rightbar.ftl" as rightbar>
<#import "body.ftl" as body>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>

    <!--body-->
    <div id="primarycontent">
    	<@body.listCategory/>
    </div>

	  <!--right bar-->
	  <@com.rightbar>
	    <div id="secondarycontent">
        <@rightbar.favorites/>
        <@rightbar.actionsCategory/> 
      </div>
    </@com.rightbar>

    <!--footer-->
    <@foot.footer/>
</@com.page>
