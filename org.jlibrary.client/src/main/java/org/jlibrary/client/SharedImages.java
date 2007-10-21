/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual 
* contributors as indicated by the @authors tag. See copyright.txt in the
* distribution for a full listing of individual contributors.
* All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.client;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.jlibrary.client.util.NodeUtils;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.util.FileUtils;

/**
 * @author martin
 *
 * Class that will be used to access to shared images
 */
public class SharedImages {
	
	public final static String IMAGE_ABOUT = "about.gif";
	public final static String IMAGE_ABOUT_BIG = "about_big.jpg";
	public final static String IMAGE_ADD_DIRECTORY= "add_directory.gif";
	public final static String IMAGE_ADD_DIRECTORY_DISABLED= "add_directory_disabled.gif";
	public final static String IMAGE_ADD_FAVORITE = "add_fav.gif";
	public final static String IMAGE_ADD_FAVORITE_DISABLED = "add_fav_disabled.gif";
	public final static String IMAGE_ADD_FAVORITE_DEFAULT = "add_fav_def.gif";
	public final static String IMAGE_ADD_FAVORITE_DEFAULT_DISABLED = "add_fav_def_disabled.gif";
	public final static String IMAGE_ADD_RESOURCES = "add_resource.gif";
	public final static String IMAGE_ADD_RESOURCES_DISABLED = "add_resource_disabled.gif";
	public final static String IMAGE_ATTRIBUTE = "attribute.gif";
	public final static String IMAGE_AUTHOR = "author.gif";
	public final static String IMAGE_BACKWARD = "right.gif";
	public final static String IMAGE_BACKWARD_DISABLED = "right_disabled.gif";
	public final static String IMAGE_BOOKMARK = "favorite.gif";
	public final static String IMAGE_CANCEL = "cancel.gif";
	public final static String IMAGE_CATEGORY = "category.gif";
	public final static String IMAGE_CLEAR_CONSOLE = "clear_co.gif";
	public final static String IMAGE_CLOSE_REPOSITORY = "close_rep.gif";	
	public final static String IMAGE_CLOSE_REPOSITORY_DISABLED = "close_rep_disabled.gif";	
	public final static String IMAGE_CONNECT = "connect.png";
	public final static String IMAGE_CONNECT_DISABLED = "connect_disabled.png";
	public final static String IMAGE_CONNECT_WIZARD = "connect_wiz.gif";
	public final static String IMAGE_CONSOLE = "console.gif";
	public final static String IMAGE_CONSOLE_DISABLED = "console_disabled.gif";
	public final static String IMAGE_CONSOLE_HISTORY = "console_history.gif";
	public final static String IMAGE_CONSOLE_HISTORY_DISABLED = "console_history_disabled.gif";
	public final static String IMAGE_COPY = "copy.gif";
	public final static String IMAGE_COPY_HOVER = "copy.gif";
	public final static String IMAGE_COPY_DISABLED = "copy_disabled.gif";
	public final static String IMAGE_CRAWL_MULTIPLE = "crawl_multiple.gif";
	public final static String IMAGE_CRAWL_MULTIPLE_DISABLED = "crawl_multiple_disabled.gif";
	public final static String IMAGE_CUT = "cut.gif";
	public final static String IMAGE_CUT_HOVER = "cut.gif";
	public final static String IMAGE_CUT_DISABLED = "cut_disabled.gif";
	public final static String IMAGE_DELETE = "delete.gif";
	public final static String IMAGE_DELETE_AUTHOR = "delete_author.gif";
	public final static String IMAGE_DELETE_AUTHOR_DISABLED = "delete_author_disabled.gif";
	public final static String IMAGE_DELETE_DISABLED = "delete_disabled.gif";
	public final static String IMAGE_DELETED_DECORATOR = "dec_deleted.gif";
	public final static String IMAGE_DELETE_ALL = "removeAll.gif";
	public final static String IMAGE_DELETE_ALL_DISABLED = "removeAll_disabled.gif";
	public final static String IMAGE_DELETE_GROUP = "delete_group.gif";
	public final static String IMAGE_DELETE_GROUP_DISABLED = "delete_group_disabled.gif";
	public final static String IMAGE_DELETE_ROL = "delete_rol.png";
	public final static String IMAGE_DELETE_ROL_DISABLED = "delete_rol_disabled.png";
	public final static String IMAGE_DELETE_SEARCH_RESULT = "remove.gif";
	public final static String IMAGE_DELETE_SEARCH_RESULT_DISABLED = "remove_disabled.gif";
	public final static String IMAGE_DELETE_USER = "delete_user.png";
	public final static String IMAGE_DELETE_USER_DISABLED = "delete_user_disabled.png";
	public final static String IMAGE_DESCRIPTION = "description.gif";
	public final static String IMAGE_DIRECTORY = "directory.gif";
	public final static String IMAGE_DIRECTORY_UP="directoryUp.gif";
	public final static String IMAGE_DISCONNECT = "disconnect.png";
	public final static String IMAGE_DISCONNECT_DISABLED = "disconnect_disabled.png";
	public final static String IMAGE_EMPTY = "empty.gif";
	public final static String IMAGE_ERROR = "error.gif";
	public final static String IMAGE_EXIT = "close_exit.gif";
	public final static String IMAGE_EXIT_DISABLED = "close_exit_disabled.gif";
	public final static String IMAGE_EXPORT = "export.gif";
	public final static String IMAGE_EXPORT_DISABLED = "export_disabled.gif";
	public final static String IMAGE_EXPORT_HTML = "export_html.png";
	public final static String IMAGE_EXPORT_HTML_DISABLED = "export_html_disabled.png";
	public final static String IMAGE_EXPORT_HTML_WIZARD = "export_html_wiz.gif";
	public final static String IMAGE_FAVORITE = "favorite.gif";
	public final static String IMAGE_FIT = "fit.gif";
	public final static String IMAGE_FIT_DISABLED = "fit_disabled.gif";
	public final static String IMAGE_FORWARD = "left.gif";
	public final static String IMAGE_FORWARD_DISABLED = "left_disabled.gif";
	public final static String IMAGE_GENERIC_WIZARD = "generic_wiz.gif";
	public final static String IMAGE_GROUP = "group.png";
	public final static String IMAGE_GROUP_BOOKMARKS = "group_favorites.gif";	
	public final static String IMAGE_HALF_STAR = "half_star.gif";
	public final static String IMAGE_HEADER_COMPLETE = "header_complete.gif";
	public final static String IMAGE_HISTORY_BOOK= "history_book.gif";
	public final static String IMAGE_HISTORY_LINK = "link.gif";
	public final static String IMAGE_HISTORY_PAGE = "history.gif";
	public final static String IMAGE_HTML = "html.gif";
	public final static String IMAGE_HTML_DISABLED = "html_disabled.gif";
	public final static String IMAGE_IMPORTANCE_HIGH = "imp_high.gif";
	public final static String IMAGE_IMPORTANCE_HIGHEST = "imp_highest.gif";
	public final static String IMAGE_IMPORTANCE_LOW = "imp_low.gif";
	public final static String IMAGE_IMPORTANCE_LOWEST = "imp_lowest.gif";
	public final static String IMAGE_IMPORT_REPOSITORY_WIZARD = "import_rep_wiz.gif";
	public final static String IMAGE_INFORMATION = "information.gif";
	public final static String IMAGE_IMPORT = "import.gif";
	public final static String IMAGE_IMPORT_DISABLED = "import_disabled.gif";
	public final static String IMAGE_JLIBRARY = "jlibrary.gif";
	public final static String IMAGE_JLIBRARY_DISABLED = "jlibrary_disabled.gif";
	public final static String IMAGE_LOAD_CONTENT = "loadContent.gif";
	public final static String IMAGE_LOAD_CONTENT_DISABLED = "loadContent_disabled.gif";
	public final static String IMAGE_LOCK = "lock.gif";
	public final static String IMAGE_LOCK_DECORATOR = "dec_lock.gif";
	public final static String IMAGE_LOGIN = "new_user.png";
	public final static String IMAGE_LOGIN_DISABLED = "new_user_disabled.png";
	public final static String IMAGE_MINUS = "minus.gif";
	public final static String IMAGE_NEW = "new_wiz.gif";	
	public final static String IMAGE_NEW_AUTHOR = "new_author.gif";	
	public final static String IMAGE_NEW_AUTHOR_DISABLED = "new_author_disabled.gif";	
	public final static String IMAGE_NEW_DISABLED = "new_wiz_disabled.gif";	
	public final static String IMAGE_NEW_BOOKMARK = "new_favorite.gif";
	public final static String IMAGE_NEW_BOOKMARK_DISABLED = "new_favorite_disabled.gif";
	public final static String IMAGE_NEW_CATEGORY = "category.gif";
	public final static String IMAGE_NEW_CATEGORY_DISABLED = "category_disabled.gif";
	public final static String IMAGE_NEW_DECORATOR = "dec_new.gif";
	public final static String IMAGE_NEW_DIR_BOOKMARK = "new_dir_favorite.gif";
	public final static String IMAGE_NEW_DIR_BOOKMARK_DISABLED = "new_dir_favorite_disabled.gif";
	public final static String IMAGE_NEW_DIRECTORY = "new_dir.gif";
	public final static String IMAGE_NEW_DIRECTORY_DISABLED = "new_dir_disabled.gif";
	public final static String IMAGE_NEW_DIRECTORY_WIZARD = "new_dir_wiz.gif";
	public final static String IMAGE_NEW_DOCUMENT = "new_doc.gif";
	public final static String IMAGE_NEW_DOCUMENT_DISABLED = "new_doc_disabled.gif";
	public final static String IMAGE_NEW_DOCUMENT_WIZARD = "new_doc_wiz.gif";
	public final static String IMAGE_NEW_GROUP = "new_group.gif";
	public final static String IMAGE_NEW_GROUP_DISABLED = "new_group_disabled.gif";
	public final static String IMAGE_NEW_RESOURCE = "new_res.gif";
	public final static String IMAGE_NEW_RESOURCE_DISABLED = "new_res_disabled.gif";
	public final static String IMAGE_NEW_RESOURCE_WIZARD = "new_res_wiz.gif";
	public final static String IMAGE_NEW_REPOSITORY = "new_rep.gif";
	public final static String IMAGE_NEW_REPOSITORY_DISABLED = "new_rep_disabled.gif";
	public final static String IMAGE_NEW_REPOSITORY_WIZARD = "new_rep_wiz.gif";
	public final static String IMAGE_NEW_ROL = "new_rol.png";
	public final static String IMAGE_NEW_ROL_DISABLED = "new_rol_disabled.png";
	public final static String IMAGE_NEW_USER = "new_user.png";
	public final static String IMAGE_NEW_USER_DISABLED = "new_user_disabled.png";
	public final static String IMAGE_NODE_DIRECTORY = "folder.gif";
	public final static String IMAGE_NODE_DOCUMENT = "document.gif";
	public final static String IMAGE_NODE_REPOSITORY = "repository.gif";
	public final static String IMAGE_NOTE = "note.gif";
	public final static String IMAGE_NOTE_WIZARD = "note_wiz.gif";
	public final static String IMAGE_OPEN = "run.gif";
	public final static String IMAGE_OPEN_AUTHOR = "open_author.gif";
	public final static String IMAGE_OPEN_CATEGORY = "open_category.gif";
	public final static String IMAGE_OPEN_DISABLED = "run_disabled.gif";
	public final static String IMAGE_OPEN_EXTERNAL = "runExternal.gif";
	public final static String IMAGE_OPEN_EXTERNAL_DISABLED = "runExternal_disabled.gif";
	public final static String IMAGE_OPEN_REPOSITORY = "open_rep.gif";        
	public final static String IMAGE_OPEN_REPOSITORY_DISABLED = "open_rep_disabled.gif";        
	public final static String IMAGE_OPEN_SYSTEM = "runSystem.gif";
	public final static String IMAGE_OPEN_SYSTEM_DISABLED = "runSystem_disabled.gif";
	public final static String IMAGE_ORIGINAL = "original.gif";
	public final static String IMAGE_PASTE = "paste.gif";
	public final static String IMAGE_PASTE_HOVER = "paste.gif";
	public final static String IMAGE_PASTE_DISABLED = "paste_disabled.gif";
	public final static String IMAGE_PLUS = "plus.gif";
	public final static String IMAGE_PREFERENCES = "preferences.gif";
	public final static String IMAGE_PREFERENCES_DISABLED = "preferences_disabled.gif";
	public final static String IMAGE_PRODUCT = "product.jpg";
	public final static String IMAGE_PROPERTIES = "properties.gif";
    public final static String IMAGE_REFRESH_REPOSITORY = "refresh.gif";
    public final static String IMAGE_REFRESH_REPOSITORY_DISABLED = "refresh_disabled.gif";
	public final static String IMAGE_RELATION = "relation.gif";
	public final static String IMAGE_REPOSITORY_CLOSED = "repository_closed.gif";
	public final static String IMAGE_RESOURCES = "resources.gif";
	public final static String IMAGE_RESOURCE_DECORATOR = "dec_resource.gif";
	public final static String IMAGE_RESTRICTION = "restriction.gif";
	public final static String IMAGE_RESTORE_VERSION = "version.gif";
	public final static String IMAGE_RESTORE_VERSION_DISABLED = "version_disabled.gif";
	public final static String IMAGE_ROL = "rol.png";
	public final static String IMAGE_ROTATE = "rotate.gif";
	public final static String IMAGE_SAVE = "save.gif";
	public final static String IMAGE_SAVE_ALL = "saveall.gif";
	public final static String IMAGE_SAVE_CONTENT = "saveContent.gif";
	public final static String IMAGE_SAVE_CONTENT_DISABLED = "saveContent_disabled.gif";
	public final static String IMAGE_SAVE_HOVER = "save.gif";
	public final static String IMAGE_SAVE_DISABLED = "save_disabled.gif";	
	public final static String IMAGE_SAVE_ALL_DISABLED = "saveall_disabled.gif";
	public final static String IMAGE_SECURITY = "security.gif";
	public final static String IMAGE_SEARCH = "search.gif";
	public final static String IMAGE_SEARCH_DISABLED = "search_disabled.gif";
	public final static String IMAGE_SEARCH_HISTORY = "search_history.gif";
	public final static String IMAGE_SEARCH_HISTORY_DISABLED = "search_history_disabled.gif";
	public final static String IMAGE_SERVER_PROFILE = "server_profile.gif";
	public final static String IMAGE_SPLASH = "jlibrary.jpg";
	public final static String IMAGE_STAR = "star.gif";
	public final static String IMAGE_STOP_WORK_WITH = "stopWorkWith.gif";
	public final static String IMAGE_STOP_WORK_WITH_DISABLED = "stopWorkWith_disabled.gif";
	public final static String IMAGE_TASK_COMPLETE = "task_complete.gif";
	public final static String IMAGE_TASK_INCOMPLETE = "task_incomplete.gif";
	public final static String IMAGE_TEXT = "txt.png";
	public final static String IMAGE_TIP = "tip.gif";
	public final static String IMAGE_TOOLS = "tools.gif";
	public final static String IMAGE_TOOLS_WIZARD = "tools_wiz.gif";
	public final static String IMAGE_TOOLS_DISABLED = "tools_disabled.gif";
	public final static String IMAGE_UNK = "unk.png";
	public final static String IMAGE_UPDATE_APP_CONF = "config_obj.gif";	
	public final static String IMAGE_UPDATE_APP_CONF_DISABLED = "config_obj_disabled.gif";	
	public final static String IMAGE_UPDATE_APP = "usearch_obj.gif";
	public final static String IMAGE_UPDATE_APP_DISABLED = "usearch_obj_disabled.gif";
	public final static String IMAGE_UPDATE_AUTHOR = "refresh.gif";
	public final static String IMAGE_UPDATE_AUTHOR_DISABLED = "refresh_disabled.gif";
	public final static String IMAGE_USER = "user.png";
	public final static String IMAGE_VERSION_ROOT = "version_root.gif";
	public final static String IMAGE_VERSION_LEAF = "version_leaf.gif";
	public final static String IMAGE_WARNING = "warning.gif";
	public final static String IMAGE_WORK_WITH = "workWith.gif";
	public final static String IMAGE_WORK_WITH_DISABLED = "workWith_disabled.gif";
	public final static String IMAGE_XML = "xml.png";
	public final static String IMAGE_ZOOMIN = "zoomin.gif";
	public final static String IMAGE_ZOOMOUT = "zoomout.gif";

    public final static String ICON_EXT_UNK   = "ext/unk.gif";
    public final static String ICON_LANG_UNK   = "lang/unk.gif";
    
    //Deprecated / unused images
    /*
	public final static String IMAGE_DELETE_BOOKMARK = "img.delete.bookmark";
	public final static String IMAGE_DELETE_RELATION = "img.delete.relation";
	public final static String IMAGE_HELP = "help.gif";
	public final static String IMAGE_METADATA = "metadata.gif";
	public final static String IMAGE_NEW_RELATION = "new_relation.gif";
	public final static String IMAGE_NEW_TOOL = "new_wiz.gif";
	public final static String IMAGE_OK = "ok.gif";
	public final static String IMAGE_OO = "oo.png";
	public final static String IMAGE_PDF = "pdf.png";
	public final static String IMAGE_REMOVE_TOOL = "delete.gif";
	public final static String IMAGE_UPDATE_CATEGORY = "updateCategory.gif";
	public final static String IMAGE_UPDATE_GROUP = "refresh.gif";
	public final static String IMAGE_UPDATE_ROL = "refresh.gif";
	public final static String IMAGE_UPDATE_TOOL = "refresh.gif";
	public final static String IMAGE_UPDATE_USER = "refresh.gif";
	*/
	
	
	protected static URL fgIconBaseURL = null;

	private static boolean init = false;
	
	protected static ImageDescriptor create(String name)
	{
		try
		{
			return ImageDescriptor.createFromURL(makeIconFileURL(name));
		}
		catch (MalformedURLException mue)
		{
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	protected static URL makeIconFileURL(String name) throws MalformedURLException
	{
		return new URL(fgIconBaseURL, name);
	}

	/**
	 * @author nico
	 * 
	 * initImages creates ImageDescriptor to initialize the plugin ImageRegistry.
	 * initImages adds all images defined as static fields IMAGE_SOMETHING or ICON_SOMETHING.
	 * The value of fields must corresponds to the image filename. initImages assumes that the given
	 * filename is relative to plugin-home/resources/icons/
	 * Now, for new images or icons, jLibrary developpers only have to care about adding IMAGE_ fields.
	 */
	private static void initImages()
	{
		fgIconBaseURL = JLibraryPlugin.getDefault().getBundle().getEntry("resources/icons/");

		try
		{
			Field[] fields = SharedImages.class.getDeclaredFields();
			for(int i =0; i < fields.length; i++)
			{
				if(fields[i].getName().startsWith("IMAGE_") || fields[i].getName().startsWith("ICON_"))
				{
					String value = (String)fields[i].get(SharedImages.class);
					putImageDescriptor(value, create(value));
				}
			}
	        init = true;
		}
		catch (Exception e)
		{
			init = false;
		}
	}
        
	public static void putImageDescriptor(String key, ImageDescriptor imgDesc)
	{
		JLibraryPlugin.getDefault().getImageRegistry().put(key,imgDesc);
	}

	public static void putImage(String key, Image image)
	{
		JLibraryPlugin.getDefault().getImageRegistry().put(key,image);
	}

	public static Image getImage(String key) {		
		if (!init) {
			initImages();
		}	
		return JLibraryPlugin.getDefault().getImageRegistry().get(key);
	}
	
	public static ImageDescriptor getImageDescriptor(String key) {		
		if (!init) {
			initImages();
		}				
		return JLibraryPlugin.getDefault().getImageRegistry().getDescriptor(key);
	}

	/**
	 * Returns an icon image for the given document based on document's type.
	 * JLibrary has a set of predefined images for some common doc types like
	 * can be PDF, HTML, etc.
	 * <p>
	 * In case that an icon could be found then it will be added to plugin 
	 * ImageRegitry because resources optimization. Otherwise a default 
	 * unknown type icon would be returned 
	 * 
	 * @param doc Document
	 * @return Image for the document
	 * 
	 * @deprecated Use getImageForNode instead
	 */
	public static Image getImageForDocument(Document doc) {		
		
		return getImageForNode(doc);
	}
	
	/**
	 * Returns an icon image for the given document based on a url's path.
	 * JLibrary has a set of predefined images for some common doc types like
	 * can be PDF, HTML, etc. 
	 * <p>
	 * In case that an icon could be found then it will be added to plugin 
	 * ImageRegitry because resources optimization. Otherwise a default 
	 * unknown type icon would be returned 
	 * 
	 * @param path String path
	 * 
	 * @return Image for the given path
	 */
	public static Image getImageForPath(String path) {
		
		String extension = FileUtils.getExtension(path);
		Image image = getImageForExtension(extension);
                return image;
	}	
	
	/**
	 * Returns an icon image for the given node based on node's type.
	 * 
	 * JLibrary has a set of predefined images for some common node types like
	 * can be PDF, HTML, etc. If this method can find an icon for the node
	 * in these predefined types, then it will ask operating system to find 
	 * an appropiated icon image. 
	 * <p>
	 * In case that an icon could be found then it will be added to plugin 
	 * ImageRegitry because resources optimization. Otherwise a default 
	 * unknown type icon would be returned 
	 * 
	 * @param node Node
	 * @return Image for the node
	 */	
	public static Image getImageForNode(Node node) {		
		
		if (node.isDirectory()) {
			return SharedImages.getImage(SharedImages.IMAGE_NODE_DIRECTORY);
		}
		
		// Return an image based on the node's path
		String extension = FileUtils.getExtension(node.getPath());
		Image image = getImageForExtension(extension);
		
		if (image == null) {
			extension = NodeUtils.getGenericExtension(node);
			image = getImageForExtension(extension);
		}
		
		if (image == null) {
			image = getImage(ICON_EXT_UNK);
		}
		
        return image;
	}	
	
	/**
	 * Returns an icon image for the given file based on file's type.
	 * JLibrary has a set of predefined images for some common doc types like
	 * can be PDF, HTML, etc. 
	 * <p>
	 * In case that an icon could be found then it will be added to plugin 
	 * ImageRegitry because resources optimization. Otherwise a default 
	 * unknown type icon would be returned 
	 * 
	 * @param resource Resource
	 * @return Image for the resource
	 */
	public static Image getImageForFile(File file) {			
		if (file.getName().equals("1.prn")) {
			System.out.println("hey");
		}
		return getImageForPath(file.getAbsolutePath());
	}	

	private static Image getImageForExtension(String extension) {		
		
		if ((extension == null) || (extension.length() == 0)) {
			// No extension files
			return getImage(ICON_EXT_UNK);
		}               
		
		if (extension.equals(".prn") || extension.equals(".aux") || extension.equals(".con")) {
			return getImage(ICON_EXT_UNK);
		}
		
		extension = extension.toLowerCase();
		String iconName = "icon.ext" + extension;
		// look if we have the image cached in the registry
		Image image = getImage(iconName);
		if (image != null) {
			return image;
		}
		else
		{
			String extName = extension.substring(1);
			ImageDescriptor desc = create("ext/"+extName+".gif");
			if(desc.getImageData() == null)
				return getImage(ICON_EXT_UNK);

			putImageDescriptor(iconName, create("ext/"+extName+".gif"));
			Image retImg = getImage(iconName);
			if(retImg == null)
				return getImage(ICON_EXT_UNK);
			return getImage(iconName); 
        }
	}
      
	public static Image getImageForLanguage(String language) {		
		
		if (language == null)
		{
			return getImage(ICON_LANG_UNK);
		}                
		
		String iconName = "icon.lang." + language;
		// look if we have the image cached in the registry
		Image image = getImage(iconName);
		if (image != null) {
			return image;
		}
		else
		{
			ImageDescriptor desc = create("lang/"+language+".gif");
			if(desc.getImageData() == null)
				return getImage(ICON_LANG_UNK);

			putImageDescriptor(iconName, create("lang/"+language+".gif"));
			Image retImg = getImage(iconName);
			if(retImg == null)
				return getImage(ICON_LANG_UNK);
			return getImage(iconName);                        
		}
	}
	
	/**
	 * Returns a greyed image for a node. If the image does not exist on the 
	 * plug-in image registry, then it will create a new grey image and it 
	 * will register it. Greyed images are used for example when you do a 
	 * cut operation over a node. 
	 * 
	 * @param node Node to obtain its grey image.
	 * 
	 * @return Image Greyed image instance.
	 */
	public static Image getGreyedImageForNode(Node node) {
		
		String key = "G";
		if (node.isDirectory()) {
			key+=SharedImages.IMAGE_DIRECTORY;
		} else {
			key+=FileUtils.getExtension(node.getPath());
		}
		
		Image image = JLibraryPlugin.getDefault().getImageRegistry().get(key);
		if (image == null) {
			image = getImageForNode(node);
			Image greyImage = new Image(PlatformUI.getWorkbench().getDisplay(),
										image,
										SWT.IMAGE_GRAY);			
			JLibraryPlugin.getDefault().getImageRegistry().put(key,greyImage);
			return greyImage;
		}
		return image;
	}
}
