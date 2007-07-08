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
package org.jlibrary.client.ui.editor.forms;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.ui.StatusLine;
import org.jlibrary.client.ui.editor.JLibraryEditor;
import org.jlibrary.client.ui.editor.JLibraryFormPage;
import org.jlibrary.client.ui.editor.URLEditorInput;
import org.jlibrary.client.ui.history.HistoryBook;
import org.jlibrary.client.util.URL;
import org.jlibrary.client.web.HistoryTracker;

public class WebFormPage extends JLibraryFormPage {
	
	private static final String WEB_BROWSER = "WEB_BROWSER";
	private static final String RECENT_LOCATIONS = "RECENT_LOCATIONS";
	private static final int HISTORY_LENGTH = 10;
	
	private Browser browser;
	private CCombo location;
	private FormEditor editor;
	private IDialogSettings settings;
	
	public WebFormPage(JLibraryEditor editor, String title) {
		super(editor, title);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm form) {
		
		Composite body = form.getForm().getBody();
		
		settings = JLibraryPlugin.getDefault().
		getDialogSettings().getSection(WEB_BROWSER);
		if (settings == null) {
			settings = JLibraryPlugin.getDefault().
			getDialogSettings().addNewSection(WEB_BROWSER);
		}
		
		browser = createBrowser(body);		
		initBrowser();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	public void initialize(FormEditor editor) {
		
		super.initialize(editor);
		this.editor = editor;
	}
	
	private void initBrowser() {

		URL url = (URL)((JLibraryEditor)editor).getModel();
		if (url != null) {
			browser.setUrl(url.getURL().toString());
		}
	}
	
	public void setFocus() {
		
		if(browser != null)
			browser.setFocus();
				
	}

		
	private Browser createBrowser(Composite body) {
			
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		body.setLayout(gridLayout);
		
		Label labelAddress = new Label(body, SWT.NONE);
		labelAddress.setText(Messages.getMessage("web_location"));
		labelAddress.setBackground(
				PlatformUI.getWorkbench().getDisplay().getSystemColor(
						SWT.COLOR_WHITE));
				
		location = new CCombo(body, SWT.BORDER);
		GridData data = new GridData();
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		location.setLayoutData(data);

		String[] recentLocations = settings.getArray(RECENT_LOCATIONS);
		if (recentLocations != null) {
			location.setItems(recentLocations);
			//location.select(0);
		}
		location.setText("");
		
		browser = new Browser(body, SWT.NONE);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);

		final IStatusLineManager manager = 
			getEditorSite().getActionBars().getStatusLineManager();
		final IProgressMonitor monitor = manager.getProgressMonitor();
		browser.addProgressListener(new ProgressListener() {

			boolean working = false;
			int workedSoFar;
			
			public void changed(ProgressEvent event) {
//				System.out.println("changed: " + event.current + "/" + event.total);
				if (event.total == 0) return;
				if (!working) {
					if (event.current == event.total) return;
					monitor.beginTask("", event.total); //$NON-NLS-1$
					workedSoFar = 0;
					working = true;
				}
				monitor.worked(event.current - workedSoFar);
				workedSoFar = event.current;
				
				IAction stopAction = HistoryTracker.getStopAction();
				if (stopAction != null) {
					stopAction.setEnabled(true);
				}
			}
			public void completed(ProgressEvent event) {
//				System.out.println("completed: " + event.current + "/" + event.total);
				monitor.done();
				working = false;

				IAction stopAction = HistoryTracker.getStopAction();
				if (stopAction != null) {
					stopAction.setEnabled(false);
				}
				IAction backAction = HistoryTracker.getStopAction();
				if (backAction != null) {
					backAction.setEnabled(true);
				}
			}
		});
		browser.addStatusTextListener(new StatusTextListener() {
			public void changed(StatusTextEvent event) {
//				System.out.println("status: " + event.text);
				StatusLine.setText(event.text);
			}
		});

		
		location.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				urlChanged();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				urlChanged();
			}
		});
		
/*		
		actionBars.setGlobalActionHandler("back", backAction); //$NON-NLS-1$
		actionBars.setGlobalActionHandler("forward", forwardAction); //$NON-NLS-1$
		actionBars.setGlobalActionHandler("stop", stopAction); //$NON-NLS-1$
		actionBars.setGlobalActionHandler("refresh", refreshAction); //$NON-NLS-1$
*/		
		return browser;
		
	}
	
	private void saveLocationText(String location) {
		
		String[] recentDestinations = 
			settings.getArray(RECENT_LOCATIONS);
		List list = null;
		if (recentDestinations == null) {
			list = new ArrayList();
		} else {
			list = new ArrayList(Arrays.asList(recentDestinations));
		}
		if (!list.contains(location)) {
			if (list.size() == HISTORY_LENGTH) {
				list.remove(list.size()-1);
			}
			list.add(0, location);
		}
		
		settings.put(RECENT_LOCATIONS,
					 (String[])list.toArray(new String[]{}));		
	}
	
	/**
	 * @return
	 */
	public Browser getBrowser() {
		
		return browser;
	}

	private void urlChanged() {
		
		String urlText = location.getText();
		if (!urlText.startsWith("http://")) {
			urlText = "http://"+urlText;
		}
		browser.setUrl(urlText);
		try {
			saveLocationText(urlText);
			URL url = new URL(urlText);
			URLEditorInput editor = (URLEditorInput)getEditor().getEditorInput();
			HistoryTracker.addURL(getEditor(), url);
			HistoryBook.getInstance().addHistoryItem(url);
			
			if (editor != null) {
				editor.setURL(url);
			}
			((JLibraryEditor)getEditor()).updateTitle(urlText);
			setPartName(url.getName());
		} catch (MalformedURLException e1) {}
	}

}
