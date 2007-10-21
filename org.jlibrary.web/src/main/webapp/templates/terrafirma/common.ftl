<#macro page>
<#setting locale="en_EN">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <title>jLibrary Web</title>
    <meta name="keywords" content="${page_keywords}" />
    <meta name="description" content="jLibrary Web Browser" />
    <link rel="stylesheet" type="text/css" href="${root_url}/templates/terrafirma/default.css" />
    <script type="text/javascript" src="${root_url}/js/livevalidation_standalone.compressed.js"></script>
    <script type="text/javascript" src="${root_url}/js/jlibrary_validation.js"></script>
  </head>
 
  <body>
    <#nested>
  </body>
</html>
</#macro>

<#macro pageEditor>
<#setting locale="en_EN">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
    <title>jLibrary Web</title>
    <meta name="keywords" content="${page_keywords}" />
    <meta name="description" content="jLibrary Web Browser" />
    <link rel="stylesheet" type="text/css" href="${root_url}/templates/terrafirma/default.css" />
    <script type="text/javascript" src="${root_url}/livevalidation_standalone.compressed.js"></script>
    <script type="text/javascript" src="${root_url}/FCKEditor/fckeditor.js"></script>
    <script type="text/javascript" src="${root_url}/js/jlibrary_validation.js"></script>
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