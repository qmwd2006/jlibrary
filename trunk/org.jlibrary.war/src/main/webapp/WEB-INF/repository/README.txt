TOMCAT INSTALLATION
-------------------------------------------------------------------------------

The only requisite to install jLibrary server as a .war in your system is to 
copy the jaas.config file that is on this directory to %CATALINA_HOME%/conf 
directory.

If you already have a jaas.config file, add the following lines:

Jackrabbit {
org.apache.jackrabbit.core.security.SimpleLoginModule required anonymousId="anonymous";
};

And finally you must tell to tomcat where is the jaas.config file if you haven't done yet. This can 
be done in several ways from your catalina file. For example adding the following line to the 
java command, or to the CATALINA_OPTS environment variable:

-Djava.security.auth.login.config=%CATALINA_HOME%/conf/jaas.config

or also

-Djava.security.auth.login.config=../conf/jaas.config