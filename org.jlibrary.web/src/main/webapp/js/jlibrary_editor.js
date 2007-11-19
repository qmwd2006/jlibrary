window.onload = makeDoubleDelegate(window.onload, WYSIWYGeditor);
function WYSIWYGeditor(){
	var oFCKeditor = new FCKeditor( 'content' ) ;
	oFCKeditor.BasePath	= document.getElementById('baseUrl').value+'/FCKEditor/';
	oFCKeditor.ToolbarSet = 'Basic';
	oFCKeditor.ReplaceTextarea() ;
}