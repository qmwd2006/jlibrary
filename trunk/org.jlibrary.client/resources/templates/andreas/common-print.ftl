<#macro page>
<#setting locale="en_EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
		<meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
		<meta name="description" content="jLibrary" />
		<meta name="keywords" content="jlibrary document management repository" />
		<meta name="author" content="Martín Pérez" />
		<link rel="stylesheet" type="text/css" href="${root_url}/resources/andreas01.css" media="screen,projection" />
		<link rel="stylesheet" type="text/css" href="${root_url}/resources/print.css" media="print" />
		<title>jLibrary</title>
  </head>
  <body>
      <#nested>
  </body>
</html>
</#macro>

<#macro leftbar>
  <#nested/>
</#macro>

<#macro rightbar>
  <#nested/>
</#macro>

<#macro directoryRightbar>
  <#nested/>
</#macro>