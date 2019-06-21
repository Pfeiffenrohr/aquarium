FROM tomcat:8-jre8  
MAINTAINER lpradel

RUN echo "export JAVA_OPTS=\"-Dapp.env=staging\"" > /usr/local/tomcat/bin/setenv.sh  
COPY target/aquarium-0.0.1-SNAPSHOT /usr/local/tomcat/webapps/aquarium.war

CMD ["catalina.sh", "run"] 