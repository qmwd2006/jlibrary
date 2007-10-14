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
    <@form.register/>
  </div>

  <!--footer-->
  <@foot.footer/>
</@com.pageEditor>
