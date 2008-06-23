<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "footer.ftl" as foot>
<#import "form.ftl" as form>
<#import "rightbar.ftl" as rightbar>

<@com.pageEditor>

	<!--header-->
  <@head.header/>

  <!--content-->
  <div id="primarycontent">
    <@form.documentCategories/>
  </div>

  <!--right bar-->
  <@com.rightbar>
    <div id="secondarycontent">
    	<@rightbar.infoDocument/>
		<@rightbar.resources/>
    </div>
  </@com.rightbar>
    
  <!--footer-->
  <@foot.footer/>
</@com.pageEditor>