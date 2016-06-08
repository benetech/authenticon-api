FROM frolvlad/alpine-oraclejdk8:slim

MAINTAINER Nima Ansari <nimaa@benetech.org>

VOLUME /tmp
ADD authenticon-api-0.0.1-SNAPSHOT.war app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
