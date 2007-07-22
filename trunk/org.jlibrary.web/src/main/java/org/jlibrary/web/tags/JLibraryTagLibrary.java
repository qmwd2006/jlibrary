package org.jlibrary.web.tags;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.sun.facelets.tag.AbstractTagLibrary;

public class JLibraryTagLibrary extends AbstractTagLibrary {

    /** Namespace used to import this library in Facelets pages  */
    public static final String NAMESPACE = "http://jlibrary.sourceforge.net/jsf/core";
    
    /**  Current instance of library. */
    public static final JLibraryTagLibrary INSTANCE = new JLibraryTagLibrary();
    
	public JLibraryTagLibrary() {
		
		super(NAMESPACE);
		
        try {
            Method[] methods = JLibraryContentHelper.class.getMethods();

            for (int i = 0; i < methods.length; i++) {
                if (Modifier.isStatic(methods[i].getModifiers())) {
                    this.addFunction(methods[i].getName(), methods[i]);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}	
}
