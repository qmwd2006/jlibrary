call mvn install:install-file -DgroupId=org.restlet -DartifactId=restlet -Dversion=1.0.1 -Dpackaging=jar -DgeneratePom=true -Dfile=deps/org.restlet-1.0.1.jar
call mvn install:install-file -DgroupId=org.restlet -DartifactId=restlet-spring -Dversion=1.0.1 -Dpackaging=jar -DgeneratePom=true -Dfile=deps/org.restlet.ext.spring_2.0-1.0.1.jar
call mvn install:install-file -DgroupId=com.noelios -DartifactId=noelios -Dversion=1.0.1 -Dpackaging=jar -DgeneratePom=true -Dfile=deps/com.noelios.restlet-1.0.1.jar
call mvn install:install-file -DgroupId=com.noelios -DartifactId=noelios-servlet -Dversion=1.0.1 -Dpackaging=jar -DgeneratePom=true -Dfile=deps/com.noelios.restlet.ext.servlet_2.4-1.0.1.jar