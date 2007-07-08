<#macro page>
<#setting locale="en_EN">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
    <meta name="jLibrary" content="jLibrary" />
    <link rel="stylesheet" type="text/css" href="${root_url}/resources/purplehaze-print.css" title="Purple Haze Stylesheet" />

    <title>${repository.name}</title>
  </head>

  <body>
      <div id="top"></div>
      <#nested>
  </body>
</html>
</#macro>

<#macro body>
	<#nested/>		
</#macro>

<#macro leftbar>
	<div id="side-bar">
		<#nested/>
	</div>
</#macro>

<#macro rightbar>
	<div id="side-bar">
		<#nested/>
		  <div class="rightSideBar">
		    <span class="sideBarText">Print</span>
		    <A href="${print_file}"><img src="${root_url}/resources/print.gif" border="0"/></A>
		  </div>		
	</div>
</#macro>

<#macro directoryRightbar>
	<#if directory_content="">
		<div id="side-bar">
			<#nested/>
			  <div class="rightSideBar">
			    <span class="sideBarText">Print</span>
			    <A href="${print_file}"><img src="${root_url}/resources/print.gif" border="0"/></A>
			  </div>		
		</div>
	</#if>
</#macro>