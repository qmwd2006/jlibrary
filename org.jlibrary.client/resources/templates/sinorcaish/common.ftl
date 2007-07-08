<#macro page>
<#setting locale="en_EN">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<head>
  <title>${repository.name}</title>
  <meta name="Author" content="jLibrary" />
  <meta name="Description" content="${repository.description}" />
  <meta name="Language" content="en" />
  <meta name="Generator" content="jLibrary" />
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta http-equiv="Content-Language" content="es" />
  <link rel="StyleSheet" 
  		href="${root_url}/resources/sinorcaish-screen.css" 
  		type="text/css" 
  		media="screen" />
</head>

  <body>
      <#nested>
  </body>
</html>
</#macro>

<#macro bars>
	<#nested/>
</#macro>

<#macro body>
	<#nested/>		
</#macro>

<#macro directoryRightbar>
	<#nested/>		
</#macro>