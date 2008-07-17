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
package org.jlibrary.core.search.extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndian;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Powerpoint text extraction class. Most of this code is from OpenCms extractor
 * implementation. 
 * 
 * It needs structure improvements.
 */
public class PowerPointExtractor extends MSOfficeExtractor implements POIFSReaderListener, Extractor {

	static Logger logger = LoggerFactory.getLogger(PowerPointExtractor.class);
	
    /** Windows Cp1252 endocing (western europe) is used as default for single byte fields. */
    protected static final String ENCODING_CP1252 = "Cp1252";

    /** UTF-16 encoding is used for double byte fields. */
    protected static final String ENCODING_UTF16 = "UTF-16";

    /** Event event name for a MS PowerPoint document. */
    protected static final String POWERPOINT_EVENT_NAME = "PowerPoint Document";

    /** PPT text byte atom. */
    protected static final int PPT_TEXTBYTE_ATOM = 4008;

    /** PPT text char atom. */
    protected static final int PPT_TEXTCHAR_ATOM = 4000;

	

    /** The buffer that is written with the content of the PPT. */
    private StringBuffer m_buffer;

    /**
     * Hide the public constructor.<p> 
     */
    public PowerPointExtractor() {

        m_buffer = new StringBuffer(4096);
    }

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
	 * @see org.jlibrary.core.search.extraction.Extractor#extractHeader(java.io.InputStream)
	 */
	public HeaderMetaData extractHeader(InputStream is) throws ExtractionException {
		
		try {
	        POIFSReader reader = new POIFSReader();
	        reader.registerListener(this);
	        reader.read(is);
	        // extract all information
	        return extractMetaInformation();
		} catch (Throwable t) {
			logger.error(t.getMessage(),t);
			throw new ExtractionException(t);
		}
			
	}	
	
    /**
	 * @see org.jlibrary.core.search.extraction.Extractor#extractText(java.io.InputStream)
	 */	
    public String extractText(InputStream in) throws ExtractionException {

        POIFSReader reader = new POIFSReader();
        reader.registerListener(this);
        try {
			reader.read(in);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ExtractionException(e);
		}

        String result = removeControlChars(m_buffer.toString());

        // return the final result
        return result;
    }

    /**
     * @see org.apache.poi.poifs.eventfilesystem.POIFSReaderListener#processPOIFSReaderEvent(org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent)
     */
    public void processPOIFSReaderEvent(POIFSReaderEvent event) {

        try {

            // super implementation handles document summary
        	super.processPOIFSReaderEvent(event);

            // make sue this is a PPT document
            if (!event.getName().startsWith(POWERPOINT_EVENT_NAME)) {
                return;
            }

            DocumentInputStream input = event.getStream();
            byte[] buffer = new byte[input.available()];
            input.read(buffer, 0, input.available());

            for (int i = 0; i < buffer.length - 20; i++) {
                int type = LittleEndian.getUShort(buffer, i + 2);
                int size = (int)LittleEndian.getUInt(buffer, i + 4) + 3;

                String encoding = null;
                switch (type) {
                    case PPT_TEXTBYTE_ATOM:
                        // this pice is single-byte encoded, let's assume Cp1252 since this is most likley
                        // anyone who knows how to find out the "right" encoding - please email me
                        encoding = ENCODING_CP1252;
                    case PPT_TEXTCHAR_ATOM:
                        if (encoding == null) {
                            // this piece is double-byte encoded, use UTF-16
                            encoding = ENCODING_UTF16;
                        }
                        int start = i + 4 + 1;
                        int end = start + size;

                        byte[] buf = new byte[size];
                        System.arraycopy(buffer, start, buf, 0, buf.length);

                        m_buffer.append(new String(buf,encoding));
                        i = end;
                    default:
                // noop                                           
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }
     
    public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Usage: PowerPointExtractor file");
			System.exit(0);
		}
		
		try {
			File file = new File(args[0]);
			if (!file.exists()) {
				System.out.println("The specified file does not exist");
				System.exit(0);
			}
			System.out.println(new PowerPointExtractor().extractText(new FileInputStream(file)));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}    
}
