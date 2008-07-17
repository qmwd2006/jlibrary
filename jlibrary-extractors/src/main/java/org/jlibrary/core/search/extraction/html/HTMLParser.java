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
package org.jlibrary.core.search.extraction.html;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.lexer.Stream;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TagFindingVisitor;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.search.extraction.ExtractionException;
import org.jlibrary.core.search.extraction.HeaderMetaData;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extracts plain text from HTML. 
 *
 * @author martin
 */
public final class HTMLParser {

	static Logger logger = LoggerFactory.getLogger(HTMLParser.class);
	
	/**
     * Hides the public constructor.<p>
     */
    private HTMLParser() {
        // hides the public constructor
    }
    
    /**
     * Extract the text from a HTML page.<p>
     *
     * @param content the html content
     * @param encoding the encoding of the content
     *
     * @return the extracted text from the page
     * @throws ParserException if the parsing of the HTML failed
     * @throws UnsupportedEncodingException if the given encoding is not supported
     */
    public static String extractText(String content, String encoding) throws ParserException, UnsupportedEncodingException  {

        // we must make sure that the content passed to the parser always is 
        // a "valid" HTML page, i.e. is surrounded by <html><body>...</body></html> 
        // otherwise you will get strange results for some specific HTML constructs
        StringBuilder newContent = new StringBuilder(content.length() + 32);
        
        newContent.append("<html><body>");
        newContent.append(content);
        newContent.append("</body></html>");

        // make sure the Lexer uses the right encoding
        InputStream in = new ByteArrayInputStream(newContent.toString().getBytes(encoding));
        try {
        // use the stream based version to process the results
        return extractText(in, encoding);
        } finally {
        	if (in != null) {
        		try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
					throw new ParserException(e);
				}
        	}
        }
    }
    
    /**
     * Extract the text from an HTML page.<p>
     *
     * @param in the html content input stream
     * @param encoding the encoding of the content
     *
     * @return the extracted text from the page
     * @throws ParserException if the parsing of the HTML failed
     * @throws UnsupportedEncodingException if the given encoding is not supported
     */
    public static String extractText(InputStream in, String encoding) throws ParserException, UnsupportedEncodingException {
        
        Parser parser = new Parser();
        Lexer lexer = new Lexer();
        Page page = new Page(in, encoding);
        lexer.setPage(page);
        parser.setLexer(lexer);
        
        StringBean stringBean = new StringBean();
        // stringBean.setParser(parser);
                       
        parser.visitAllNodesWith(stringBean);
        
        return stringBean.getStrings();      
    }	
	
    public static HeaderMetaData extractHeader(InputStream in, 
    								   		   String encoding) throws ParserException, 
									   						   		   UnsupportedEncodingException {
        Parser parser = new Parser();
        Lexer lexer = new Lexer();
        Page page = new Page(in, encoding);
        lexer.setPage(page);
        parser.setLexer(lexer);
        
        String [] tagsToBeFound = {"META","TITLE"};
        TagFindingVisitor visitor = new TagFindingVisitor (tagsToBeFound);
        parser.visitAllNodesWith (visitor);
        // First tag specified in search
        Node [] allMETATags = visitor.getTags(0);

        HeaderMetaData metadata = new HeaderMetaData();
        
        for (int i = 0; i < allMETATags.length; i++) {
			MetaTag node = (MetaTag)allMETATags[i];
			String tagName = node.getMetaTagName();
			if (tagName == null) {
				continue;
			}
			if (tagName.equalsIgnoreCase("author")) {
				metadata.setAuthor(node.getMetaContent());
			} else if (tagName.equalsIgnoreCase("description")) {
				metadata.setDescription(node.getMetaContent());
			} else if (tagName.equalsIgnoreCase("keywords")) {
				metadata.setKeywords(node.getMetaContent());
			} else if (tagName.equalsIgnoreCase("title")) {
				metadata.setTitle(node.getMetaContent());
			} else if (tagName.equalsIgnoreCase("language")) {
				metadata.setLanguage(node.getMetaContent());
			}
		}
        
        Node [] titleTags = visitor.getTags(1);
        if (titleTags.length > 0) {
        	metadata.setTitle(titleTags[0].toPlainTextString());
        }
        
        return metadata;
    }
    
	public static String[] extractResourcePaths(InputStream in,
												String encoding) throws ParserException, 
																		UnsupportedEncodingException  {

		Parser parser = new Parser();
        Lexer lexer = new Lexer();
		Stream stream = new Stream(in);
        Page page = new Page(stream, encoding);
        lexer.setPage(page);
        parser.setLexer(lexer);

		return extractResourcePaths(parser);
	}
	
	/**
	 * Changes the path from all the references from a file if that references
	 * are pointing to some of the resources passed as parameters
	 * 
	 * @param f File that we want to parse
	 * @param encoding File encoding
	 * @param directory Document's parent directory
	 * @param resources Set of resources from which we want to change their 
	 * references
	 * 
	 * @throws ExtractionException If there is some error during the parsing
	 * process
	 */	
	public static void setLocalPaths(File file, 
									 String encoding,
									 Directory directory,
		 	  						 List resources) 
	 									throws ParserException, 
	 										   UnsupportedEncodingException  {

		try {
			Parser parser = new Parser();
			Lexer lexer = new Lexer();
			FileInputStream fis = new FileInputStream(file);
			Stream stream = new Stream(fis);
			Page page = new Page(stream, encoding);
			lexer.setPage(page);
			parser.setLexer(lexer);
			

			byte[] newContent = setLocalPaths(parser,file,directory,resources);
			fis.close();
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(newContent);
			fos.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new ParserException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new ParserException(e);
		}
	}
	
	private static String[] extractResourcePaths(Parser parser) 
										throws ParserException, 
	   										   UnsupportedEncodingException  {

		ArrayList resources = new ArrayList();

		NodeFilter filter = new NodeClassFilter(ImageTag.class);		
		NodeList list = parser.extractAllNodesThatMatch (filter);
		for (int i = 0; i < list.size (); i++) {
			ImageTag tag = (ImageTag)list.elementAt(i);
			//System.out.println (tag.extractImageLocn());
			String location = tag.extractImageLocn();
			if (location.trim().equals("")) {
				continue;
			}
			location = StringUtils.replace(location,"\"","");
			resources.add(location);
		}
		
		// Extract css stylesheets resources		
		TagFindingVisitor visitor = new TagFindingVisitor (new String[]{"LINK"});
		parser.visitAllNodesWith (visitor);
		Node [] allTags = visitor.getTags(0);
		for (int i = 0; i < allTags.length; i++) {
			TagNode tag = (TagNode)allTags[i];
			String href = tag.getAttribute("href");
			if (href.endsWith(".css")) {
				resources.add(href);
			}
		}
		
		return (String[])resources.toArray(new String[]{});
	}
	
	private static byte[] setLocalPaths(Parser parser,
									    File file,
									    Directory directory,
								 	    List resources) 
										 throws ParserException, 
												UnsupportedEncodingException  {

		
		NodeList list = new NodeList ();
        for (NodeIterator e = parser.elements (); e.hasMoreNodes (); ) {
        	Node node = e.nextNode();
        	processRecursively(parser,directory,resources,node);
        	
        	list.add (node); // URL conversion occurs in the tags
            
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        PrintWriter out = new PrintWriter (baos);
        for (int i = 0; i < list.size (); i++)
            out.print (list.elementAt (i).toHtml ());
        out.close ();
        return baos.toByteArray();
	}

	private static void processRecursively(Parser parser,
										   Directory directory,
										   List resources,
										   Node node) 
										throws ParserException, 
											   UnsupportedEncodingException  {

    	if (node instanceof TagNode) {
	    	TagNode tag = (TagNode)node;
	    	if (tag.getTagName().equalsIgnoreCase("img")) {
	    		ImageTag imageTag = (ImageTag)tag;
				String location = imageTag.getImageURL();
				imageTag.setImageURL(
							changeLocation(location,directory,resources));
	    	} else {
	        	String href = tag.getAttribute("href");
	            if ((href!= null) && href.endsWith(".css"))  {
	            	tag.setAttribute("href",
	            			changeLocation(href,directory,resources));
	            }
	        }
    	}
    	NodeList list = node.getChildren();
    	if (list == null) return;
        for (NodeIterator e = list.elements(); e.hasMoreNodes (); ) {        	
        	processRecursively(parser,directory,resources,e.nextNode());
        }	
	}	
	
	private static String changeLocation(String location, 
									     Directory directory,
									     List resources) {
		
    	String locationName = FileUtils.getFileName(location);
    	
    	Iterator it = resources.iterator();
    	while (it.hasNext()) {
			ResourceNode resource = (ResourceNode) it.next();
			if (resource.getPath().endsWith(locationName)) {
				StringBuilder buffer = new StringBuilder();
				
				StringBuilder docPath = new StringBuilder(directory.getPath());
				StringBuilder resPath = new StringBuilder(resource.getPath());
				
				int k2 = resPath.lastIndexOf("/");
				resPath.delete(k2+1,resPath.length());
				
				// Remove common path
				while ((docPath.length() > 0) &&
					   (resPath.length() > 0) &&
					   (docPath.charAt(0) == resPath.charAt(0))) {
					docPath.deleteCharAt(0);
					resPath.deleteCharAt(0);
				}
				// Now check cases
				if (docPath.length() == 0) {
					// Two options
					// 1: Both, document and resource are at the same level
					// 2: Resource is at a higher level than the document
					//
					// Anyways, references would be ./resource_path
					buffer.append("./");
					buffer.append(resPath.toString());
				} else {
					// Resource is at a lower level than the document
					//
					// The path would be something like ../../resource_path 
					buffer.append("../");
					for (int i=0;i<docPath.length();i++) {
						if (docPath.charAt(i) == '/') {
							buffer.append("../");
						}
					}
					if (buffer.charAt(buffer.length()-1) == '/') {
						buffer.deleteCharAt(buffer.length()-1);
					}
					buffer.append(resPath.toString());
				}				
				buffer.append(locationName);
		    	return buffer.toString();
				
			}
		}		
    	// No changes
    	return location;
	}
	
}