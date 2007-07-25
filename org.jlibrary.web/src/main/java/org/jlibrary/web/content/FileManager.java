package org.jlibrary.web.content;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.jlibrary.web.Messages;

public class FileManager {
	Logger log=Logger.getLogger(FileManager.class);
	private UploadedFile file;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
    	log.debug("setando file");
        this.file = file;
    }

    public String processMyFile() {
    	log.debug("procesando");
        try {
            InputStream in = new BufferedInputStream(
            		file.getInputStream());
            log.debug("file:"+file.getName());
            return "content$saved";
        } catch (IOException e) {
        	Messages.setMessageError(e);
            e.printStackTrace();
            return null;
        }
    }
}
