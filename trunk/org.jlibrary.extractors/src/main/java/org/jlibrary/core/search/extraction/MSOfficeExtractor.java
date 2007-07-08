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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.Section;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;

/**
 * @author martin
 *
 * Base class for office extractors.
 * 
 * Good code from OpenCMS
 */
public class MSOfficeExtractor {

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

    /** The summary of the POI document. */
    private DocumentSummaryInformation m_documentSummary;

    /** The summary of the POI document. */
    private SummaryInformation m_summary;

    /** A buffer in case the input stream must be read more then once. */
    protected byte[] m_inputBuffer;    
        
    /**
     * @see org.apache.poi.poifs.eventfilesystem.POIFSReaderListener#processPOIFSReaderEvent(org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent)
     */
    public void processPOIFSReaderEvent(POIFSReaderEvent event) {

    	//TODO: CHANGE FROM RECENT 2.5 POI CONSTANTS SummaryInformation.DOCUMENT_STREAM_NAME, and
    	// DocumentSummaryInformation.DOCUMENT_STREAM_NAME
        try {
            if (event.getName().startsWith("\005SummaryInformation")) {
                m_summary = (SummaryInformation)PropertySetFactory.create(event.getStream());
                return;
            }
            if (event.getName().startsWith("\005DocumentSummaryInformation")) {
                m_documentSummary = (DocumentSummaryInformation)PropertySetFactory.create(event.getStream());
                return;
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Returns a map with the extracted meta information from the document.<p>
     * 
     * @return a map with the extracted meta information from the document
     */
    protected HeaderMetaData extractMetaInformation() {

        HeaderMetaData metadata = new HeaderMetaData();
        String meta;
        if (m_summary != null) {
            // can't use convenience methods on summary since they can't deal with multiple sections
            Section section = (Section)m_summary.getSections().get(0);

            meta = (String)section.getProperty(PropertyIDMap.PID_TITLE);
            if ((meta != null) && !meta.equals("")) {
                metadata.setTitle(meta);
                metadata.setDescription(meta);
            }
            meta = (String)section.getProperty(PropertyIDMap.PID_KEYWORDS);
            if ((meta != null) && !meta.equals("")) {
                metadata.setKeywords( meta);
            }
            meta = (String)section.getProperty(PropertyIDMap.PID_SUBJECT);
            if ((meta != null) && !meta.equals("")) {
                metadata.setDescription(meta);
            }
            meta = (String)section.getProperty(PropertyIDMap.PID_COMMENTS);
            if ((meta != null) && !meta.equals("")) {
            	// Not handled
            }
            // extract other available meta information
            meta = (String)section.getProperty(PropertyIDMap.PID_AUTHOR);
            if ((meta != null) && !meta.equals("")) {
                metadata.setAuthor(meta);
            }
            Date date;
            date = (Date)section.getProperty(PropertyIDMap.PID_CREATE_DTM);
            if ((date != null) && (date.getTime() > 0)) {
            	// Not handled            
            }
            date = (Date)section.getProperty(PropertyIDMap.PID_LASTSAVE_DTM);
            if ((date != null) && (date.getTime() > 0)) {
            	// Not handled
            }
        }
        if (m_documentSummary != null) {
            // can't use convenience methods on document since they can't deal with multiple sections
            Section section = (Section)m_documentSummary.getSections().get(0);

            // extract available meta information from document summary
            meta = (String)section.getProperty(PropertyIDMap.PID_COMPANY);
            if ((meta != null) && !meta.equals("")) {
            	// Not handled
            }
            meta = (String)section.getProperty(PropertyIDMap.PID_MANAGER);
            if ((meta != null) && !meta.equals("")) {
            	// Not handled
            }
            meta = (String)section.getProperty(PropertyIDMap.PID_CATEGORY);
            if ((meta != null) && !meta.equals("")) {
            	// Not handled
            }
        }

        return metadata;
    }

    protected String removeControlChars(String content) {

        char[] chars = content.toCharArray();
        StringBuffer result = new StringBuffer(chars.length);
        boolean wasUnwanted = false;
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];

            int type = Character.getType(ch);
            switch (type) {

                // punctuation
                case Character.CURRENCY_SYMBOL:
                case Character.CONNECTOR_PUNCTUATION:
                case Character.FINAL_QUOTE_PUNCTUATION:
                case Character.INITIAL_QUOTE_PUNCTUATION:
                case Character.DASH_PUNCTUATION:
                case Character.START_PUNCTUATION:
                case Character.END_PUNCTUATION:
                case Character.OTHER_PUNCTUATION:
                // letters
                case Character.OTHER_LETTER:
                case Character.MODIFIER_LETTER:
                case Character.UPPERCASE_LETTER:
                case Character.TITLECASE_LETTER:
                case Character.LOWERCASE_LETTER:
                // digits
                case Character.DECIMAL_DIGIT_NUMBER:
                // spaces
                case Character.SPACE_SEPARATOR:
                    result.append(ch);
                    wasUnwanted = false;
                    break;

                // line separators
                case Character.LINE_SEPARATOR:
                    result.append('\n');
                    wasUnwanted = true;
                    break;

                // symbols
                case Character.MATH_SYMBOL:
                case Character.OTHER_SYMBOL:
                // other stuff:
                case Character.CONTROL:
                case Character.COMBINING_SPACING_MARK:
                case Character.ENCLOSING_MARK:
                case Character.FORMAT:
                case Character.LETTER_NUMBER:
                case Character.MODIFIER_SYMBOL:
                case Character.NON_SPACING_MARK:
                case Character.PARAGRAPH_SEPARATOR:
                case Character.PRIVATE_USE:
                case Character.SURROGATE:
                case Character.UNASSIGNED:
                case Character.OTHER_NUMBER:
                default:
                    if (!wasUnwanted) {
                        result.append('\n');
                        wasUnwanted = true;
                    }
            }
        }

        return result.toString();
    }
    
    public InputStream getStreamCopy(InputStream in) throws IOException {

        if (m_inputBuffer != null) {
            return new ByteArrayInputStream(m_inputBuffer);
        }

        // read the input stream fully and copy it to a byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        int c = 0;
        while (true) {
            c = in.read();
            if (c < 0) {
                break;
            }
            out.write(c);
        }
        m_inputBuffer = out.toByteArray();

        // now return a reader from the byte array
        return new ByteArrayInputStream(m_inputBuffer);
    }

}
