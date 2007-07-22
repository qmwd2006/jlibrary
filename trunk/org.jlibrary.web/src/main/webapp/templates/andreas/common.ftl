<#macro page>
<#setting locale="en_EN">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>jLibrary</title>
<meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
<meta name="description" content="jLibrary Document Management System" />
<meta name="keywords" content="${page_keywords}" />
<meta name="author" content="Martín Pérez" />
<link rel="stylesheet" href="${root_url}/templates/andreas/resources/andreas09.css" type="text/css" media="screen" />
<style type="text/css">
#container{background:#f1f5f8 url(${root_url}/resources/alt-img/bodybg-orange.jpg) repeat-x;}
#mainmenu a:hover{background:#f0f0f0 url(${root_url}/resources/alt-img/menuhover-orange.jpg) top left repeat-x;}
#mainmenu a.current{background:#f0f0f0 url(${root_url}/resources/alt-img/menuhover-orange.jpg) top left repeat-x;}
</style>
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