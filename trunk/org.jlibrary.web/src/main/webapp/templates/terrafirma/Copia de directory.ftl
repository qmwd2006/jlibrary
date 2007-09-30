<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "footer.ftl" as foot>
<#import "body.ftl" as body>
<#import "rightbar.ftl" as rightbar>

<@com.page>

	<!--header-->
  <@head.header/>

  <!--content-->
  <div id="primarycontent">
    <@body.directoryContent/>
  </div>

  <!--right bar-->
  <@com.rightbar>
    <div id="secondarycontent">
    	<@rightbar.infoDirectory/>
      <@rightbar.directories/>        
      <@rightbar.actions/>
    </div>
  </@com.rightbar>
    
  <!--footer-->
  <@foot.footer/>
</@com.page>
