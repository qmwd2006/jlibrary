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
package org.jlibrary.client.ui.history;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.jlibrary.client.JLibraryPlugin;
import org.jlibrary.client.Messages;
import org.jlibrary.client.util.BigDate;
import org.jlibrary.client.util.URL;
import org.jlibrary.core.config.ConfigException;
import org.jlibrary.core.config.JLibraryProperties;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class HistoryBook implements Serializable{
	
	static Logger logger = LoggerFactory.getLogger(HistoryBook.class);
	
	static final long serialVersionUID = -20050423L;

	private ArrayList pages = new ArrayList();	
	private static HistoryBook instance;

	private HistoryPage pageToday;
	private HistoryPage pageYesterday;
	private HistoryPage pageWeek;
	private HistoryPage pageMonth;
	private HistoryPage pageThreeMonths;

	private HistoryPage pageEver;
	
	public void addPage(HistoryPage page) {
		
		pages.add(page);
	}
	
	public void removePage(HistoryPage page) {
		
		pages.remove(page);
	}
	
	public Collection getPages() {
		
		return pages;
	}
	
	public HistoryBook createHistoryBook() {
		
		HistoryBook book = new HistoryBook();
		
		book.pageToday = new HistoryPage();
		book.pageToday.setDescription(Messages.getMessage("history_today"));

		book.pageYesterday = new HistoryPage();
		book.pageYesterday.setDescription(Messages.getMessage("history_yesterday"));		
		
		book.pageWeek = new HistoryPage();
		book.pageWeek.setDescription(Messages.getMessage("history_week"));		
		
		book.pageMonth = new HistoryPage();
		book.pageMonth.setDescription(Messages.getMessage("history_month"));		

		book.pageThreeMonths = new HistoryPage();
		book.pageThreeMonths.setDescription(Messages.getMessage("history_three_months"));
		
		book.pageEver = new HistoryPage();
		book.pageEver.setDescription(Messages.getMessage("history_ever"));
				
		book.addPage(book.pageToday);
		book.pageToday.setBook(book);
		book.addPage(book.pageYesterday);
		book.pageYesterday.setBook(book);
		book.addPage(book.pageWeek);
		book.pageWeek.setBook(book);
		book.addPage(book.pageMonth);
		book.pageMonth.setBook(book);
		book.addPage(book.pageThreeMonths);
		book.pageThreeMonths.setBook(book);
		book.addPage(book.pageEver);
		book.pageEver.setBook(book);

		return book;
	}
	
	private void updateDates() {
		
		checkDays(pageToday,pageYesterday,1);
		checkDays(pageYesterday,pageWeek,2);
		checkDays(pageWeek,pageMonth,7);
		checkDays(pageMonth,pageThreeMonths,30);
		checkDays(pageThreeMonths,pageEver,90);
	}
	
	private void checkDays(HistoryPage currentPage, HistoryPage nextPage, int days) {
		
		Date today = new Date();
		Iterator it = currentPage.getItems().iterator();
		while (it.hasNext()) {
			History history = (History) it.next();
			Date date = history.getDate();
			int difference = getDifference(today,date);
			if (difference >= days) {
				it.remove();
				history.setPage(nextPage);
				nextPage.addItem(history);
			}
		}		
	}
	
	public void addHistoryItem(URL url) {
		
		if (url == null) {
			return;
		}
		// look for the url
		History urlHistory = new History();
		urlHistory.setUrl(url.getName());
		urlHistory.setDate(new Date());
		urlHistory.setPage(pageToday);
		
		pageToday.addItem(urlHistory);
					
		saveHistory();		
		HistoryView.refresh();
	}
	
	private int getDifference(Date today, Date date) {
		
		Calendar calendar = Calendar.getInstance();
		int todayYear = calendar.get(Calendar.YEAR);
		int todayMonth = calendar.get(Calendar.MONTH);
		int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
		
		calendar.setTime(date);
		int dateYear = calendar.get(Calendar.YEAR);
		int dateMonth = calendar.get(Calendar.MONTH);
		int dateDay = calendar.get(Calendar.DAY_OF_MONTH);
		
		
		BigDate bdToday = new BigDate(todayYear, todayMonth, todayDay);
		BigDate bdDate = new BigDate(dateYear, dateMonth, dateDay);
		
		int[] age = BigDate.age(bdDate, bdToday);
		
		return age[0]*365 + age[1]*12 + age[2];
	}
	
	private static HistoryBook initHistoryBook() throws ConfigException {
		
		// Load history from disk
        String home = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_HOME);  
		File f = new File(home,".jlibrary");
        f.mkdirs();
		if (!f.exists()) {
			throw new ConfigException(".jlibrary directory don't found");
		}
		XStream xstream = new XStream();
		ClassLoader clientClassLoader = 
			JLibraryPlugin.getDefault().getClass().getClassLoader();
		xstream.setClassLoader(clientClassLoader);
		
		File file = null;
		FileReader reader = null;
		try {
			file = new File(f, ".history-registry.xml");
			if (!file.exists()) {
				instance = new HistoryBook().createHistoryBook();
				instance.saveHistory();
				return instance;
			}
			reader = new FileReader(file);
			instance = (HistoryBook) xstream.fromXML(reader);
			instance.updateDates();
			return instance;
		} catch (Exception e) {
			// Backup the corrupted file and remove it
			String backupName = file.getName();
			String extension = FileUtils.getExtension(file.getName());
			backupName = StringUtils.replace(backupName, extension, "");
			backupName += new SimpleDateFormat("yyyy-MM-dd").format(new Date())
					+ ".bak";
			File backupFile = new File(backupName);
			try {
				org.apache.commons.io.FileUtils.copyFile(file, backupFile);
				file.delete();
			} catch (IOException e1) {
				logger.error(e1.getMessage(), e);
			}
			throw new ConfigException(e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	public void saveHistory() {

		//String home = System.getProperty("user.home");
		String home = JLibraryProperties.getProperty(JLibraryProperties.JLIBRARY_HOME);  
		
		File f = new File(home,".jlibrary");
        f.mkdirs();
		if (!f.exists()) {
			
			logger.info(".jlibrary directory don't found");
			return;
		}

		XStream xstream = new XStream();
		ClassLoader clientClassLoader = 
			JLibraryPlugin.getDefault().getClass().getClassLoader();
		xstream.setClassLoader(clientClassLoader);
		
		try {
			xstream.toXML(this,new FileWriter(new File(f,".history-registry.xml")));
		} catch (IOException e) {
            logger.error(e.getMessage(),e);
		}
	}
	
	public static HistoryBook getInstance() {
		
		if (instance == null) {
			try {
				instance = initHistoryBook();
			} catch (ConfigException e) {
				
                logger.error(e.getMessage(),e);
			}
		}
		return instance;
	}
	
	public String toString() {
		
		return Messages.getMessage("history_recent");
	}
}
