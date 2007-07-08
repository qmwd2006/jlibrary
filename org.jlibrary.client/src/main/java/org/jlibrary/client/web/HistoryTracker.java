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
package org.jlibrary.client.web;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.util.URL;

/**
 * @author martin
 *
 * Track history of opened URLs
 */
public class HistoryTracker {

	private static HashMap history = new HashMap();
	
	private static IAction forwardAction;
	private static IAction backwardAction;
	private static IAction stopAction;
	
	public static void addURL(IEditorPart editor, URL url) {
		
		ArrayList urls = (ArrayList)history.get(editor);
		if (urls == null) {
			urls = new ArrayList();
			history.put(editor,urls);
		} else {
			if (urls.contains(url)) {
				refreshSelection(editor,url);
				return;
			}
		}
		urls.add(url);
		refreshSelection(editor,url);
	}
	
	public static boolean hasNext(IEditorPart editor, URL url) {

		ArrayList urls = (ArrayList)history.get(editor);
		if (urls == null) {
			return false;
		}
		if (!(urls.contains(url))) {
			return false;
		}
		int i = urls.indexOf(url);
		if (i == urls.size()-1) {
			return false;
		}
		return true;
	}
	
	public static boolean hasPrevious(IEditorPart editor, URL url) {

		ArrayList urls = (ArrayList)history.get(editor);
		if (urls == null) {
			return false;
		}
		if (!(urls.contains(url))) {
			return false;
		}
		int i = urls.indexOf(url);
		if (i == 0) {
			return false;
		}
		return true;
	}
	
	public static URL getNext(IEditorPart editor, URL url) {
		
		if (!hasNext(editor,url)) {
			return null;
		}
		ArrayList urls = (ArrayList)history.get(editor);
		int i = urls.indexOf(url);
		return (URL)urls.get(i+1);
	}
	
	public static URL getPrevious(IEditorPart editor, URL url) {
		
		if (!hasNext(editor,url)) {
			return null;
		}
		ArrayList urls = (ArrayList)history.get(editor);
		int i = urls.indexOf(url);
		return (URL)urls.get(i-1);
	}

	public static void remove(IEditorPart editor) {
		
		history.remove(editor);
	}
	
	private static void refreshSelection(IEditorPart editor, URL url) {
		
		if (forwardAction != null) {
			if (!hasNext(editor,url)) {
				forwardAction.setEnabled(false);
			} else {
				forwardAction.setEnabled(true);
			}
		}
		if (backwardAction != null) {
			if (!hasPrevious(editor,url)) {
				backwardAction.setEnabled(false);
			} else {
				backwardAction.setEnabled(true);
			}
		}
	}
	
	/**
	 * @param backwardAction The backwardAction to set.
	 */
	public static void setBackwardAction(IAction backwardAction) {
		HistoryTracker.backwardAction = backwardAction;
	}

	/**
	 * @param forwardAction The forwardAction to set.
	 */
	public static void setForwardAction(IAction forwardAction) {
		HistoryTracker.forwardAction = forwardAction;
	}

	/**
	 * @param stopAction The stopAction to set.
	 */
	public static void setStopAction(IAction stopAction) {
		HistoryTracker.stopAction = stopAction;
	}

	/**
	 * @return
	 */
	public static IAction getStopAction() {
		
		return stopAction;
	}

	/**
	 * @param editor
	 * @param url
	 */
	public static void remove(JLibraryEditor editor, URL url) {

		ArrayList urls = (ArrayList)history.get(editor);
		if (urls == null) {
			return;
		}
		urls.remove(url);
	}

	public static IAction getBackwardAction() {
		return backwardAction;
	}

	public static IAction getForwardAction() {
		return forwardAction;
	}

}
