<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>jLibrary repositories</title>
<link href="templates/terrafirma/static.css" rel="stylesheet" type="text/css" />
</head>

<body>
	<div id="header">
		<h1><a href="http://www.jlibrary.org">jLibrary</a></h1>
		<h2>Open Source Document Management System from your Desktop ... and the Web.</h2>
	</div>
	
	<div id="outer">
		<div id="inner">
			<h2>jLibrary repositories</h2>
			<p>You can find below a list with all the repositories available on this server. Click on each icon to 
				 be forwarded to the main page of each repository.</p>
			
			<%
			  // Would be better to do this using JSTL
			  java.util.List repositories = (java.util.List)request.getAttribute("repositories");
			  java.util.Iterator it = repositories.iterator();
			  while (it.hasNext()) {
			  org.jlibrary.core.entities.RepositoryInfo info = (org.jlibrary.core.entities.RepositoryInfo)it.next();
			  out.println("<h3><a href=\""+ request.getContextPath() +"/repositories/" + info.getName()+"\">" + info.getName() + "</a></h3>");
			  }
			%>			
		</div>
	</div>
	<div id="footer">
		<p>This demo has been written by Daniel Latorre and Martin Perez. Created with <a href="http://www.jlibrary.org">jLibrary</a>.</p>
	</div>
</body>
</html>