<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "footer.ftl" as foot>
<#import "form.ftl" as form>
<#import "rightbar.ftl" as rightbar>

<@com.page>

	<!--header-->
  <@head.header/>

  <!--content-->
  <div id="primarycontent">
    <@form.directoryUpdateForm/>
  </div>

  <!--right bar-->
  <@com.rightbar>
    <div id="secondarycontent">
    	<@rightbar.infoDirectory/>
    </div>
  </@com.rightbar>
    
  <!--footer-->
  <@foot.footer/>
</@com.page>
