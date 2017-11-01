FROM java:8-alpine
MAINTAINER Ed Sweeney <ed@onextent.com>

EXPOSE 8080

RUN mkdir -p /app
COPY target/scala-2.12.3/*.jar /app/

WORKDIR /app

CMD java -jar ./akkaPhantomDemo.jar
# override CMD from your run command, or k8s yaml, or marathon json, etc...

