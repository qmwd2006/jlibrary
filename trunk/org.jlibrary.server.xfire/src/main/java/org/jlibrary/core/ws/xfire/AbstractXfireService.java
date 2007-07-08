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
package org.jlibrary.core.ws.xfire;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.exchange.AbstractMessage;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author 33465948M
 *
 */
public class AbstractXfireService {
  
  /**
   * Loggger de clase
   */
  private static Logger logger = LoggerFactory.getLogger(AbstractXfireService.class);
  
  
  
  
  protected XStream xstream = new XStream();
  
  
  
  
  
  
  
  protected byte[] getDocumentContentInContext(MessageContext context) throws IOException {

    logger.info("[getDocumentContentInContext.entrada]:: " + Arrays.asList(new Object[] { context }));

    byte[] realContent = null;

    AbstractMessage message = context.getCurrentMessage();
    logger.debug("[getDocumentContentInContext]:: message: " + message);

    Attachments attachments = message.getAttachments();
    logger.debug("[getDocumentContentInContext]:: attachments: " + attachments);
    if ( attachments == null ){
      logger.info("[getDocumentContentInContext.retorna]:: null");
      return null; 
    }

    int size = attachments.size();
    logger.debug("[getDocumentContentInContext]:: attachments.size(): " + size);
    
    if (size > 0) {
      Iterator it = attachments.getParts();
      while (it.hasNext()) {
        Attachment attachment = (Attachment) it.next();
        InputStream is = attachment.getDataHandler().getInputStream();
        realContent = IOUtils.toByteArray(is);
        is.close();
        break;
      }
    }

    logger.info("[getDocumentContentInContext.retorna]:: " + realContent);
    return realContent;
  }

}
