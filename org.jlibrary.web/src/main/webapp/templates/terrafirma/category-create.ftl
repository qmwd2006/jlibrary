<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "rightbar.ftl" as rightbar>
<#import "form.ftl" as form>
<#import "footer.ftl" as foot>

<@com.page>

	<!--header-->
    <@head.header/>

    <!--body-->
    <div id="primarycontent">
    	<@form.categoryCreateForm/>
    </div>

	  <!--right bar-->
	  <@com.rightbar>
	    <div id="secondarycontent">
      </div>
    </@com.rightbar>

    <!--footer-->
    <@foot.footer/>
</@com.page>
