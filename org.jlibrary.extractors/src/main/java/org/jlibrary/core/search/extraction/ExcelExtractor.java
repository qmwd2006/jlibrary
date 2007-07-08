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
package org.jlibrary.core.search.extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martín
 *
 * Excel text extraction class. Most of this code is from OpenCms extractor
 * implementation. 
 * 
 * It needs structure improvements.
 */
public class ExcelExtractor extends MSOfficeExtractor implements POIFSReaderListener, Extractor {

	static Logger logger = LoggerFactory.getLogger(ExcelExtractor.class);
	
    /**
     * Hide the public constructor.<p> 
     */
    public ExcelExtractor() {}

    /**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractText(java.io.File)
	 */
	public String extractText(File f) throws ExtractionException {
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			return extractText(fis);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);			
		} catch (ExtractionException ee) {
			throw ee;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new ExtractionException(e);
				}
			}
		}
	}
	
	/**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.InputStream)
	 */
	public HeaderMetaData extractHeader(InputStream is) throws ExtractionException {
		
		try {
	        POIFSReader reader = new POIFSReader();
	        reader.registerListener(this);
	        reader.read(getStreamCopy(is));
	
	        // extract all information
	        return extractMetaInformation();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}
			
	}	
	
	/**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.File)
	 */
	public HeaderMetaData extractHeader(File f) throws ExtractionException {
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			return extractHeader(fis);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);			
		} catch (ExtractionException ee) {
			throw ee;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new ExtractionException(e);
				}
			}
		}
	}
	
    /**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractText(java.io.InputStream)
	 */	
    public String extractText(InputStream in) throws ExtractionException {

        String result;
		try {
			result = extractTableContent(getStreamCopy(in));
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}
        result = removeControlChars(result);

        return result;
    	
    }

    /**
     * Extracts the text from the Excel table content.<p>
     * 
     * @param in the document input stream
     * @return the extracted text
     * @throws IOException if something goes wring
     */
    protected String extractTableContent(InputStream in) throws IOException {

        HSSFWorkbook excelWb = new HSSFWorkbook(in);
        StringBuffer result = new StringBuffer(4096);

        int numberOfSheets = excelWb.getNumberOfSheets();

        for (int i = 0; i < numberOfSheets; i++) {
            HSSFSheet sheet = excelWb.getSheetAt(i);
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            if (numberOfRows > 0) {

                if ((excelWb.getSheetName(i) != null) && !excelWb.getSheetName(i).equals("")) {
                    // append sheet name to content
                    if (i > 0) {
                        result.append("\n\n");
                    }
                    result.append(excelWb.getSheetName(i).trim());
                    result.append(":\n\n");
                }

                Iterator rowIt = sheet.rowIterator();
                while (rowIt.hasNext()) {
                    HSSFRow row = (HSSFRow)rowIt.next();
                    if (row != null) {
                        boolean hasContent = false;
                        Iterator it = row.cellIterator();
                        while (it.hasNext()) {
                            HSSFCell cell = (HSSFCell)it.next();
                            String text = null;
                            try {
                                switch (cell.getCellType()) {
                                    case HSSFCell.CELL_TYPE_BLANK:
                                    case HSSFCell.CELL_TYPE_ERROR:
                                        // ignore all blank or error cells
                                        break;
                                    case HSSFCell.CELL_TYPE_NUMERIC:
                                        text = Double.toString(cell.getNumericCellValue());
                                        break;
                                    case HSSFCell.CELL_TYPE_BOOLEAN:
                                        text = Boolean.toString(cell.getBooleanCellValue());
                                        break;
                                    case HSSFCell.CELL_TYPE_STRING:
                                    default:
                                        text = cell.getStringCellValue();
                                        break;
                                }
                            } catch (Exception e) {
                                // ignore this cell
                            }
                            if ((text!=null) && !text.equals("")) {
                                result.append(text.trim());
                                result.append(' ');
                                hasContent = true;
                            }
                        }
                        if (hasContent) {
                            // append a newline at the end of each row that has content                            
                            result.append('\n');
                        }
                    }
                }
            }
        }

        return result.toString();
    }      
}
