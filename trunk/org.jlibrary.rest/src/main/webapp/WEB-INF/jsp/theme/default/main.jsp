<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"><head><title>jLibrary</title>
<title><decorator:title default="Welcome!" /></title>
<meta content="text/html; charset=UTF-8" http-equiv="Content-Type">
<meta name="description" content="jLibrary Document Management System">
<meta name="keywords" content="jlibrary enterprise explain environments documents ">
<link rel="stylesheet" href="<%=request.getContextPath()%>/themes/default/styles.css" type="text/css" media="screen">
<style type="text/css">
#container{background:#f1f5f8 url(<%=request.getContextPath()%>/themes/default/resources/alt-img/bodybg-orange.jpg) repeat-x;}
#mainmenu a:hover{background:#f0f0f0 url(<%=request.getContextPath()%>/themes/default/resources/alt-img/menuhover-orange.jpg) top left repeat-x;}
#mainmenu a.current{background:#f0f0f0 url(<%=request.getContextPath()%>/themes/default/resources/alt-img/menuhover-orange.jpg) top left repeat-x;}
</style>
<meta name="ROBOTS" content="INDEX,FOLLOW">
<meta name="REVISIT-AFTER" content="15 days">
<meta name="searchable" content="Yes">
<decorator:head />
</head>
<body>

	<!--header-->
<div id="header">
<div id="container">

<%@ include file="header.jsp" %>

<div id="mainmenu">
<ul>
    	<li><a href="http://jlibrary.sourceforge.net/categories/1/index.html">Developers</a></li>
    	<li><a href="http://jlibrary.sourceforge.net/categories/0/index.html">Help &amp; Support</a></li>
    	<li><a href="http://jlibrary.sourceforge.net/categories/2/index.html">Enterprise</a></li>
    	<li><a href="http://jlibrary.sourceforge.net/categories/4/index.html">Documents &amp; Articles</a></li>
</ul>
</div> 
	    <div id="location">
	        <a href="http://jlibrary.sourceforge.net/index.html">jLibrary</a>  &gt;&gt; <a href="http://jlibrary.sourceforge.net/categories/2/index.html">Enterprise</a>
	    </div>
<div id="wrap">
    
    <!--leftbar-->
      <div id="leftside">
		<h1>Categories</h1>
		<p></p>
 	</div>

    <!--rightbar-->
      <div id="rightside">
             <h1>Favorites</h1>
	  	<p><a href="http://jlibrary.sourceforge.net/4/usecases.html">jLibrary use cases</a></p>
      </div>

    <!--body-->
    <div id="content">
	<decorator:body />
    </div>

    <!--footer-->
<div class="clearingdiv">&nbsp;</div>
</div><!-- wrap -->
</div><!-- container -->

<%@ include file="footer.jsp" %>
 
</div>
</body>
</html>