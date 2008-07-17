/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, Blandware (represented by
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
package org.jlibrary.client.help;

/**
 * Holds the information for text appearing in the about dialog
 */
public class AboutItem {
	private String text;
	private int[][] linkRanges;
	private String[] hrefs;
/**
 * Creates a new about item
 */
public AboutItem(
	String text,
	int[][] linkRanges,
	String[] hrefs) {
	    
	this.text = text;
	this.linkRanges = linkRanges;
	this.hrefs = hrefs;
}
/**
 * Returns the link ranges (character locations)
 */
public int[][] getLinkRanges() {
	return linkRanges;
}
/**
 * Returns the text to display
 */
public String getText() {
	return text;
}
/**
 * Returns true if a link is present at the given character location
 */
public boolean isLinkAt(int offset) {
	// Check if there is a link at the offset
	for (int i = 0; i < linkRanges.length; i++){
		if (offset >= linkRanges[i][0] && offset < linkRanges[i][0] + linkRanges[i][1]) {
			return true;
		}
	}
	return false;
}
/**
 * Returns the link at the given offset (if there is one),
 * otherwise returns <code>null</code>.
 */
public String getLinkAt(int offset) {
	// Check if there is a link at the offset
	for (int i = 0; i < linkRanges.length; i++){
		if (offset >= linkRanges[i][0] && offset < linkRanges[i][0] + linkRanges[i][1]) {
			return hrefs[i];
		}
	}
	return null;
}
}
