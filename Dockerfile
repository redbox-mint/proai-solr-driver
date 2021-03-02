FROM jetty:9.3-alpine
USER root

ENV APPSERVER_HOME /var/lib/jetty
ENV APPSERVER_APPS $APPSERVER_HOME/webapps
ENV APP_HOME /opt/solr-driver

# install unzip
RUN apk add --no-cache unzip bash openjdk8

RUN mkdir -p $APP_HOME
COPY . $APP_HOME/

# unpack war file
RUN mkdir -p $APPSERVER_APPS/ROOT
RUN unzip $APP_HOME/lib/proai.war -d $APPSERVER_APPS/ROOT/

# Build and put jar lib into exploded war file
RUN cd $APP_HOME/ && chmod +x gradlew && ./gradlew shadowJar && cp $APP_HOME/build/libs/proai-solr-driver-fat.jar $APPSERVER_APPS/ROOT/WEB-INF/lib/
RUN cp $APP_HOME/proai.properties $APPSERVER_APPS/ROOT/WEB-INF/classes/
RUN chown -R jetty:jetty $APPSERVER_HOME

USER jetty
CMD ["java","-jar","/usr/local/jetty/start.jar"]
