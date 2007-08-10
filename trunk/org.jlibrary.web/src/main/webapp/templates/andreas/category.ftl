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
         <@leftbar.categories/>         
      </div>
    </@com.leftbar>

    <!--rightbar-->
    <@com.rightbar>
      <div id="rightside">
        <@rightbar.favorites/> 
        <@rightbar.search/>       
      </div>
    </@com.rightbar>

    <!--body-->
    <div id="content">
    	<@body.listCategory/>
    </div>

    <!--footer-->
    <@foot.footer/>
</@com.page>
