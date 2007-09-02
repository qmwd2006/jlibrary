<%@ taglib uri="http://fckeditor.net/tags-fckeditor" prefix="FCK" %>
<!--
 * FCKeditor - The text editor for internet
 * Copyright (C) 2003-2005 Frederico Caldeira Knabben
 * 
 * Licensed under the terms of the GNU Lesser General Public License:
 * 		http://www.opensource.org/licenses/lgpl-license.php
 * 
 * For further information visit:
 * 		http://www.fckeditor.net/
 * 
 * File Name: sample02.jsp
 * 	FCKeditor sample file 2.
 * 
 * Version:  2.3
 * Modified: 2005-07-19 13:57:00
 * 
 * File Authors:
 * 		Simone Chiaretta (simo@users.sourceforge.net)
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<title>jLibrary</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="robots" content="noindex, nofollow">
		<script type="text/javascript">
			function FCKeditor_OnComplete( editorInstance )
			{
				window.status = editorInstance.Description ;
			}
		</script>
	</head>
	<body>
		<h1>New Document</h1>
		
		<form action="/upload" method="get" target="_blank">
			
			Title: <input type="text" name="title"/>
			<br/><br/>
			Description: <textarea name="string" rows="5"></textarea>
		  <br/><br/>
			Enter the content of the document you want to create:
			<br/>
			<FCK:editor id="EditorDefault" basePath="/jlibrary/FCKEditor/" toolbarSet="Basic"
				imageBrowserURL="/jlibrary/FCKEditor/editor/filemanager/browser/default/browser.html?Type=Image&Connector=connectors/jsp/connector"
				linkBrowserURL="/jlibrary/FCKEditor/editor/filemanager/browser/default/browser.html?Connector=connectors/jsp/connector"
				flashBrowserURL="/jlibrary/FCKEditor/editor/filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/jsp/connector"
				imageUploadURL="/jlibrary/FCKEditor/editor/filemanager/upload/simpleuploader?Type=Image"
				linkUploadURL="/jlibrary/FCKEditor/editor/filemanager/upload/simpleuploader?Type=File"
				flashUploadURL="/jlibrary/FCKEditor/editor/filemanager/upload/simpleuploader?Type=Flash">
			</FCK:editor>
			<br>
			<input type="submit" value="Submit">
		</form>
	</body>
</html>