FROM java:8

VOLUME /tmp
ADD assets-0.0.1-SNAPSHOT.war /data/app.War
RUN bash -c 'touch /data/app.war'

EXPOSE 9091

CMD java -Djava.security.egd=file:/dev/./urandom -jar /data/app.war