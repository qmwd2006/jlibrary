window.onload = function(){
	var oFCKeditor = new FCKeditor( 'content' ) ;
	oFCKeditor.BasePath	= document.getElementById('baseUrl').value+'/FCKEditor/';
	oFCKeditor.ToolbarSet = 'Basic';
	oFCKeditor.ReplaceTextarea() ;
}