FROM 814633283276.dkr.ecr.us-east-1.amazonaws.com/tomcat:8-jdk8
COPY ./build/libs/file.war /usr/local/tomcat/webapps/ROOT.war