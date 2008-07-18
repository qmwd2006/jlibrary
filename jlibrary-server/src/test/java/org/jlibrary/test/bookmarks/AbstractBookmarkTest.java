/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
* Andrey Grebnev), and individual contributors as indicated by the
* @authors tag. See copyright.txt in the distribution for a full listing of
* individual contributors. All rights reserved.
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
package org.jlibrary.test.bookmarks;

import java.util.Iterator;

import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.User;
import org.jlibrary.test.AbstractRepositoryTest;

/**
 * Base test class for bookmarks testing
 * 
 * @author mpermar
 *
 */
public class AbstractBookmarkTest extends AbstractRepositoryTest {

	protected static Bookmark testRootBookmark;
	protected static Bookmark testParentBookmark;
	protected static Bookmark testChildBookmark;
	
	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
	}
	
	protected int findAllUserBookmarks(User user) {
		
		int bkmks = user.getFilteredBookmarks().size();
		
		Iterator it = user.getFilteredBookmarks().iterator();
		while (it.hasNext()) {
			Bookmark bmk = (Bookmark)it.next();
			System.out.println("Bookmark found " + bmk );
			bkmks += findAllBookmarks(bmk);
		}
		return bkmks;
	}	
	
	protected int findAllBookmarks(Bookmark bookmark) {
		
		int bkmks = bookmark.getBookmarks().size();
		
		Iterator it = bookmark.getBookmarks().iterator();
		while (it.hasNext()) {
			Bookmark bmk = (Bookmark)it.next();
			System.out.println("Bookmark found " + bmk );
			bkmks += findAllBookmarks(bmk);
		}
		return bkmks;
	}
}