FROM dockerfile/java:oracle-java8

VOLUME /tmp
ADD assets-0.0.1-SNAPSHOT.jar /data/app.jar
RUN bash -c 'touch /data/app.jar'

EXPOSE 9091

CMD java -Djava.security.egd=file:/dev/./urandom -jar /data/app.jar