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
    <@form.documentDocuments/>
  </div>

  <!--right bar-->
  <@com.rightbar>
    <div id="secondarycontent">
    	<@rightbar.infoDocument/>
		<@rightbar.resources/>
		<@rightbar.categoriesDocument/>
    </div>
  </@com.rightbar>
    
  <!--footer-->
  <@foot.footer/>
</@com.pageEditor>
