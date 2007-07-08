package org.jlibrary.cache.input;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.jlibrary.cache.LocalCacheService;
import org.jlibrary.cache.impl.WorkspaceCache;
import org.jlibrary.core.entities.Node;

public class CacheAdapter {

	public static Object getAdapter(Node node) {
		
		WorkspaceCache cache = (WorkspaceCache)
							LocalCacheService.getInstance().getLocalCache();
		IFile file;
		try {
			file = cache.getFile(node);
			return file.getAdapter(IResource.class);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;		
	}	
}
