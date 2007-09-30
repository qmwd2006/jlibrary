<#import "common.ftl" as com>
<#import "header.ftl" as head>
<#import "rightbar.ftl" as rightbar>
<#import "form.ftl" as form>
<#import "footer.ftl" as foot>

<@com.pageEditor>

	<!--header-->
    <@head.header/>
            
    <!--body-->
    <div id="primarycontent">      
    	<@form.documentUpdateForm/>
    </div>
    
    <!-- right bar -->
    <@com.rightbar>
			<div id="secondarycontent">
	      <@rightbar.infoDocument/>
	      <@rightbar.admin/>
	      <@rightbar.resources/>
	      <@rightbar.categoriesDocument/>
	      <@rightbar.relationsDocument/>
    	</div>
    </@com.rightbar>
    
    <!--footer-->
    <@foot.footer/>
</@com.pageEditor>