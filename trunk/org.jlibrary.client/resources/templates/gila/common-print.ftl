<#macro page>
<#setting locale="en_EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en-EN">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
    <meta name="author" content="jLibrary" />
    <meta name="generator" content="jLibrary" />

    <link rel="stylesheet" type="text/css" href="${root_url}/resources/gila-print.css" media="print" />

    <title>${repository.name}</title>
  </head>
  <body>
      <#nested>
  </body>
</html>
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