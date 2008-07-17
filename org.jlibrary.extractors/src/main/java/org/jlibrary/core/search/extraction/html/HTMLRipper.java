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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Attribute;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.BaseHrefTag;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLRipper {

	static Logger logger = LoggerFactory.getLogger(HTMLRipper.class);
	
    private static Parser mParser;
	private static String mSource;
	
	private static HashSet resources;
	private static String resourcesDirectory = "";
	
    protected final int TRANSFER_SIZE = 4096;	
	
	public File crawlFile(File parent, 
				      	  String url) throws ParserException {
		
		if (mParser == null) {
			initParser();
		}
		
		return crawl(parent,url);
	}
	
	public File[] crawlFileWithResources(File parent,
									 	 String url) throws ParserException {

		if (mParser == null) {
			initParser();
		}
		
		ArrayList crawled = new ArrayList();		
		resources.clear();
		resourcesDirectory = getResourcesDirectory(url);
		
		File file = crawl(parent,url);
		crawled.add(file);
		
		if (resources.size() > 0) {
			File directory = new File(parent,resourcesDirectory);
			if (!directory.exists()) {
				directory.mkdir();
			}
			Iterator it = resources.iterator();
			while (it.hasNext()) {
				String resourceURL = (String) it.next();
				crawled.add(crawlResource(directory,resourceURL));
			}
		}
		return (File[])crawled.toArray(new File[]{});
	}
	
	private String getResourcesDirectory(String url) {

		String extension = FileUtils.getExtension(url);
		url = StringUtils.replace(url,extension,"");
		int i = url.lastIndexOf('/');
		url = url.substring(i+1);
		int j = url.lastIndexOf('.');
		return url.substring(j+1);
	}

    protected File crawlResource(File parent, String url) {

        byte[] data;
        InputStream in;
        FileOutputStream out;
        int read;
		File file = null;
        
		if (logger.isDebugEnabled()) {
			logger.debug("[HTMLRipper] processing " + url);
		}
        
		
        try {
			URL source = new URL(url);
			file = new File (parent, makeLocalLink (url,url, ""));
            data = new byte [TRANSFER_SIZE];
            try {
                in = source.openStream ();
                try {
                    out = new FileOutputStream (file);
                    try {
                        while (-1 != (read = in.read (data, 0, data.length)))
                            out.write (data, 0, read);
                    } finally {
                        out.close ();
                    }
                } catch (FileNotFoundException fnfe) {
                	logger.error(fnfe.getMessage(),fnfe);
                } finally  {
                    in.close ();
                }
            } catch (FileNotFoundException fnfe) {
                System.err.println ("broken link " + fnfe.getMessage () + " ignored");
            }
        } catch (MalformedURLException murle) {
        	logger.error(murle.getMessage(),murle);
        } catch (IOException ioe) {
        	logger.error(ioe.getMessage(),ioe);
        }
		return file;
    }	
	
	private File crawl(File parent, 
					   String url) throws ParserException {
		
        //System.out.println ("[HTMLRipper] processing " + url);

		mSource = url;
		NodeList list = null;
		File file = null;
		File dir = null;
		PrintWriter out = null;
		
        try
        {    
            // save the page locally
            file = new File (parent, makeLocalLink (url,url, ""));
            dir = file.getParentFile ();
            if (!dir.exists ())
                dir.mkdirs ();
            else if (!dir.isDirectory ())
            {
                dir = new File (dir.getParentFile (), dir.getName () + ".content");
                if (!dir.exists ())
                    dir.mkdirs ();
                file = new File (dir, file.getName ());
            }
            FileOutputStream fos = null;
            // If the file is known we will download it with html crawler
            if (Types.isBrowsable(Types.getTypeForFile(file.getAbsolutePath()))) {
	            try
	            {
	                // fetch the page and gather the list of nodes
	                mParser.setURL (url);
	                try
	                {
	                    list = new NodeList ();
	                    for (NodeIterator e = mParser.elements (); e.hasMoreNodes (); )
	                        list.add (e.nextNode ()); // URL conversion occurs in the tags
	                }
	                catch (EncodingChangeException ece)
	                {
	                    // fix bug #998195 SiteCatpurer just crashed
	                    // try again with the encoding now set correctly
	                    // hopefully mPages, mImages, mCopied and mFinished won't be corrupted
	                    mParser.reset ();
	                    list = new NodeList ();
	                    for (NodeIterator e = mParser.elements (); e.hasMoreNodes (); )
	                        list.add (e.nextNode ());
	                }	            	
	                fos = new FileOutputStream (file);
	                out = new PrintWriter (fos);
	                for (int i = 0; i < list.size (); i++)
	                    out.print (list.elementAt (i).toHtml ());
	            }
	            catch (FileNotFoundException fnfe)
	            {
	            	logger.error(fnfe.getMessage(),fnfe);
	            }
	            finally {
            		try {
						if (fos != null) {
							fos.close();
						}
						if (out != null) {
							out.close();
						}
					} catch (IOException ioe) {
						logger.error(ioe.getMessage(), ioe);
						throw new ParserException(ioe);
					}
	            }
            } else {
            	InputStream stream = null;
            	try {
                	stream = new URL(url).openStream();
                	fos = new FileOutputStream(file);
                	IOUtils.copy(stream, fos);   		
            	} catch (IOException ioe) {
            		logger.error(ioe.getMessage(),ioe);
            		throw new ParserException(ioe);            		
            	} finally {
            		try {
						if (fos != null) {
							fos.close();
						}
						if (stream != null) {
							stream.close();
						}
					} catch (IOException ioe) {
						logger.error(ioe.getMessage(), ioe);
						throw new ParserException(ioe);
					}
            	}
            }
        }
        catch (ParserException pe)
        {
            String message;
            
            // this exception handling is suboptimal,
            // but it recognizes resources that aren't text/html
            message = pe.getMessage ();
            if ((null != message) && (message.endsWith ("does not contain text")))
            {
                // do nothing
            }
            else
                throw pe;
        }
		return file;
	}
	
	private void initParser() {
		
        PrototypicalNodeFactory factory;

        mParser = new Parser ();
        factory = new PrototypicalNodeFactory ();
        factory.registerTag (new LocalLinkTag ());
        factory.registerTag (new LocalFrameTag ());
        factory.registerTag (new LocalBaseHrefTag ());
        factory.registerTag (new LocalImageTag ());
		factory.registerTag (new LocalMetaLinkTag());
        mParser.setNodeFactory (factory);	
		
		resources = new HashSet();
	}
	
    /**
     * Converts a link to local.
     * A relative link can be used to construct both a URL and a file name.
     * Basically, the operation is to strip off the base url, if any,
     * and then prepend as many dot-dots as necessary to make
     * it relative to the current page.
     * A bit of a kludge handles the root page specially by calling it
     * index.html, even though that probably isn't it's real file name.
     * This isn't pretty, but it works for me.
     * @param link The link to make relative.
     * @param current The current page URL, or empty if it's an absolute URL
     * that needs to be converted.
     * @return The URL relative to the current page.
     */
    protected String makeLocalLink (String source, 
									String link, 
								    String current)
    {
        int i;
        int j;
        String ret;

        link = cleanString(link);
        source = cleanString(source);
        
        if (link.equals (source) || 
            (!source.endsWith ("/") && link.equals (source + "/"))) {
            if (source.endsWith("/")) {
				ret = "index.html";
            } else {
				int k = source.lastIndexOf('/');
				int z = source.indexOf('/');
				if (z == k-1) {
					// handle root urls without end '/' (http://jlibrary.sourceforge.net)
					ret = "index.html";
				} else {
					ret = source.substring(k);
				}
            }
        } else if (link.startsWith (source)
                && (link.length () > source.length ()))
            ret = link.substring (source.length () + 1);
        else
            ret = link; // give up
            
        // make it relative to the current page by prepending "../" for
        // each '/' in the current local path
        if ((null != current)
            && link.startsWith (source)
            && (current.length () > source.length ()))
        {
            current = current.substring (source.length () + 1);
            i = 0;
            while (-1 != (j = current.indexOf ('/', i)))
            {
                ret = "../" + ret;
                i = j + 1;
            }
        }

        int dotIndex = ret.indexOf(".");
        if (dotIndex == -1) {
        	ret = ret + ".html";
        }
        
        return (ret);
    }	
	
    /**
     * Link tag that rewrites the HREF.
     * The HREF is changed to a local target if it matches the source.
     */
    class LocalLinkTag extends LinkTag
    {
		private static final long serialVersionUID = 1L;

		public void doSemanticAction ()
            throws
                ParserException
        {
            String link;

            // get the link
            link = getLink ();
            // check if it needs to be captured
            if (isToBeCaptured (link))
            {
            }
        }
    }

    /**
     * Frame tag that rewrites the SRC URLs.
     * The SRC URLs are mapped to local targets if they match the source.
     */
    class LocalFrameTag extends FrameTag
    {
		private static final long serialVersionUID = 1L;

		public void doSemanticAction ()
            throws
                ParserException
        {
            String link;

            // get the link
            link = getFrameLocation ();
            // check if it needs to be captured
            if (isToBeCaptured (link))
            {
            }
        }
    }

    /**
     * Image tag that rewrites the SRC URL.
     * If resources are being captured the SRC is mapped to a local target if
     * it matches the source, otherwise it is convered to a full URL to point
     * back to the original site.
     */
    class LocalImageTag extends ImageTag
    {
		private static final long serialVersionUID = 1L;

		public void doSemanticAction ()
            throws
                ParserException
        {
            String image = getImageURL ();
			resources.add(image);
			
			int i = image.lastIndexOf('/');
			String imageName = image.substring(i);
			
			setImageURL(resourcesDirectory  + imageName);
        }
    }

    /**
     * Base tag that doesn't show.
     * The toHtml() method is overridden to return an empty string,
     * effectively shutting off the base reference.
     */
    class LocalBaseHrefTag extends BaseHrefTag
    {
		private static final long serialVersionUID = 1L;

		// we don't want to have a base pointing back at the source page
        public String toHtml ()
        {
            return ("");
        }
    }	
	
    /**
     * Returns <code>true</code> if the link is one we are interested in.
     * @param link The link to be checked.
     * @return <code>true</code> if the link has the source URL as a prefix
     * and doesn't contain '?' or '#'; the former because we won't be able to
     * handle server side queries in the static target directory structure and
     * the latter because presumably the full page with that reference has
     * already been captured previously. This performs a case insensitive
     * comparison, which is cheating really, but it's cheap.
     */
    protected boolean isToBeCaptured (String link)
    {
        return (
            link.toLowerCase ().startsWith (mSource.toLowerCase ())
            && (-1 == link.indexOf ("?"))
            && (-1 == link.indexOf ("#")));
    }

	public static void main(String[] args) {
		
		String url = "http://localhost:4277/jlibrary/index.html";
		HTMLRipper ripper = new HTMLRipper();
		try {
			ripper.crawlFileWithResources(new File("/temp"), url);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	class LocalMetaLinkTag extends TagNode {

		private static final long serialVersionUID = 1L;

		private final String[] mIds = new String[] {"LINK"};

	    public LocalMetaLinkTag (){}

		public String[] getIds () {
			return (mIds);
		}

	    public String getRel () {
	        return (getAttribute ("REL"));
	    }

	    public String getHref () {
	        return (getAttribute ("HREF"));
	    }

	    public String getType () {
	        return (getAttribute ("TYPE"));
	    }

	    public String getMedia () {
	        return (getAttribute ("MEDIA"));
	    }
		
		public String getMetaLinkTagName (){
			
			return (getAttribute ("NAME"));
		}

		public void setRel (String rel) {
	        Attribute att;
			att = getAttributeEx ("REL");
	        if (null != att)
				att.setValue (rel);
	        else
	            getAttributesEx ().add (new Attribute ("REL", rel));
		}

	    public void setHref (String href) {
	        Attribute att;
			att = getAttributeEx ("HREF");
	        if (null != att)
				att.setValue (href);
	        else
	            getAttributesEx ().add (new Attribute ("HREF", href));
	    }

		public void setType (String type) {
	        Attribute att;
			att = getAttributeEx ("TYPE");
	        if (null != att)
				att.setValue (type);
	        else
	            getAttributesEx ().add (new Attribute ("TYPE", type));
		}

	    public void setMedia (String media) {
	        Attribute att;
			att = getAttributeEx ("MEDIA");
	        if (null != att)
				att.setValue (media);
	        else
	            getAttributesEx ().add (new Attribute ("MEDIA", media));
	    }		
		
	    public void setMetaTagName (String metaTagName) {
	        Attribute name;
	        name = getAttributeEx ("NAME");
	        if (null != name)
	            name.setValue (metaTagName);
	        else
	            getAttributesEx ().add (new Attribute ("NAME", metaTagName));
	    }
    
	    public void doSemanticAction() throws ParserException {

            String type = getType();
			if (type.equals("text/css")) {
				String url = getPage ().getAbsoluteURL (getHref());
				resources.add(url);
				
				int i = url.lastIndexOf('/');
				String hrefName = url.substring(i);
				setHref(resourcesDirectory + hrefName);
			}
	    }
	}
	
	private String cleanString(String source) {
		
		source = StringUtils.replaceChars(source,'?','_');
		source = StringUtils.replaceChars(source,'&','_');
		
		return source;
	}
}
