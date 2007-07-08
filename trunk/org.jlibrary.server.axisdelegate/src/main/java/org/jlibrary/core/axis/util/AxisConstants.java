package org.jlibrary.core.axis.util;


import org.apache.axis.deployment.wsdd.WSDDConstants;

public class AxisConstants {

    public static final String CLIENT_CONFIG_WSDD =
        "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
              "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
        " <transport name=\"local\" pivot=\"java:org.apache.axis.transport.local.LocalSender\"/>\n" +              
        " <transport name=\"java\" pivot=\"java:org.apache.axis.transport.http.JavaSender\"/>\n" +              
        " <transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.CommonsHTTPSender\"/>\n" +                      
        "</deployment>";
}
